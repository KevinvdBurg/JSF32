/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf31kochfractalfx;

import calculate.*;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.Scanner;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.canvas.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.input.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author Nico Kuijpers
 */
public class JSF31KochFractalFX extends Application {
    
    private boolean gui = true;
    private String binaryFilePath = "binaryKoch.ser";
    private String textFilePath = "textKoch.txt";
    
    // Zoom and drag
    private double zoomTranslateX = 0.0;
    private double zoomTranslateY = 0.0;
    private double zoom = 1.0;
    private double startPressedX = 0.0;
    private double startPressedY = 0.0;
    private double lastDragX = 0.0;
    private double lastDragY = 0.0;

    // Koch manager
    // TO DO: Create class KochManager in package calculate
    private KochManager kochManager;
    
    // Current level of Koch fractal
    private int currentLevel = 1;
    
    // Labels for level, nr edges, calculation time, and drawing time
    private Label labelLevel;
    private Label labelNrEdges;
    private Label labelNrEdgesText;
    private Label labelCalc;
    private Label labelCalcText;
    private Label labelDraw;
    private Label labelDrawText;
    private Label labelProgressLeft;
    private final Label labelProgressLeftNrEdge = new Label();
    private Label labelProgressBottom;
    private final Label labelProgressBottomNrEdge = new Label();
    private Label labelProgressRight;
    private final Label labelProgressRightNrEdge = new Label();
    public ProgressBar ProgressBottomBar;
    public ProgressBar ProgressRightBar;
    public ProgressBar ProgressLeftBar;
    
    
    // Koch panel and its size
    private Canvas kochPanel;
    private final int kpWidth = 500;
    private final int kpHeight = 500;
    
    Button buttonIncreaseLevel;
    Button buttonDecreaseLevel;
    
    @Override
    public void start(Stage primaryStage) throws IOException, ClassNotFoundException {
        
        // Define grid pane
        GridPane grid;
        grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        // For debug purposes
        // Make de grid lines visible
        // grid.setGridLinesVisible(true);
        
        // Drawing panel for Koch fractal
        kochPanel = new Canvas(kpWidth,kpHeight);
        grid.add(kochPanel, 0, 3, 25, 1);
        
        // Labels to present number of edges for Koch fractal
        labelNrEdges = new Label("Nr edges:");
        labelNrEdgesText = new Label();
        grid.add(labelNrEdges, 0, 0, 4, 1);
        grid.add(labelNrEdgesText, 3, 0, 22, 1);
        
        // Labels to present time of calculation for Koch fractal
        labelCalc = new Label("Calculating:");
        labelCalcText = new Label();
        grid.add(labelCalc, 0, 1, 4, 1);
        grid.add(labelCalcText, 3, 1, 22, 1);
        
        // Labels to present time of drawing for Koch fractal
        labelDraw = new Label("Drawing:");
        labelDrawText = new Label();
        grid.add(labelDraw, 0, 2, 4, 1);
        grid.add(labelDrawText, 3, 2, 22, 1);
        
        // Label to present current level of Koch fractal
        labelLevel = new Label("Level: " + currentLevel);
        grid.add(labelLevel, 0, 6);
        
        // Button to increase level of Koch fractal
        buttonIncreaseLevel = new Button();
        buttonIncreaseLevel.setText("Increase Level");
        buttonIncreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                increaseLevelButtonActionPerformed(event);
            }
        });
        buttonIncreaseLevel.setVisible(false);
        grid.add(buttonIncreaseLevel, 3, 6);

        // Button to decrease level of Koch fractal
        buttonDecreaseLevel = new Button();
        buttonDecreaseLevel.setText("Decrease Level");
        buttonDecreaseLevel.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                decreaseLevelButtonActionPerformed(event);
            }
        });
        buttonDecreaseLevel.setVisible(false);
        grid.add(buttonDecreaseLevel, 5, 6);
        
        // Button to fit Koch fractal in Koch panel
        Button buttonFitFractal = new Button();
        buttonFitFractal.setText("Fit Fractal");
        buttonFitFractal.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                fitFractalButtonActionPerformed(event);
            }
        });
        grid.add(buttonFitFractal, 14, 6);
        
        // Add mouse clicked event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_CLICKED,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    kochPanelMouseClicked(event);
                }
            });
        
        // Add mouse pressed event to Koch panel
        kochPanel.addEventHandler(MouseEvent.MOUSE_PRESSED,
            new EventHandler<MouseEvent>() {
                @Override
                public void handle(MouseEvent event) {
                    kochPanelMousePressed(event);
                }
            });
        
        // Add mouse dragged event to Koch panel
        kochPanel.setOnMouseDragged(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                kochPanelMouseDragged(event);
            }
        });
        
        // Labels progress Left
        labelProgressLeft = new Label("Progress Left:");
        labelProgressLeftNrEdge.setText("Nr. Edges:");
        ProgressLeftBar = new ProgressBar();
        ProgressLeftBar.setProgress(0.0f);
        grid.add(labelProgressLeft, 0, 8, 4, 1);
        grid.add(ProgressLeftBar, 4, 8, 22, 1);
        grid.add(labelProgressLeftNrEdge, 8, 8, 22, 1);
        
        
        // Labels progress Bottom
        labelProgressBottom = new Label("Progress Bottom:");
        labelProgressBottomNrEdge.setText("Nr. Edges:");
        ProgressBottomBar = new ProgressBar();
        ProgressBottomBar.setProgress(0.0f);
        grid.add(labelProgressBottom, 0, 9, 4, 1); 
        grid.add(ProgressBottomBar, 4, 9, 22, 1);
        grid.add(labelProgressBottomNrEdge, 8, 9, 22, 1);
        
        // Labels progress Right
        labelProgressRight = new Label("Progress Right:");
        labelProgressRightNrEdge.setText("Nr. Edges:");
        ProgressRightBar = new ProgressBar();
        ProgressRightBar.setProgress(0.0f);
        grid.add(labelProgressRight, 0, 10, 4, 1);
        grid.add(ProgressRightBar, 4, 10, 22, 1);
        grid.add(labelProgressRightNrEdge, 8, 10, 22, 1);
        
        
        // Create Koch manager and set initial level
        resetZoom();
        kochManager = new KochManager(this);
        
        // Create the scene and add the grid pane
        Group root = new Group();
        Scene scene = new Scene(root, kpWidth+50, kpHeight+370);
        root.getChildren().add(grid);
        
        // Define title and assign the scene for main window
        primaryStage.setTitle("Koch Fractal");
        primaryStage.setScene(scene);
        
        
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.print("Insert 1 for read and 0 for write: ");
        int mode = scanner.nextInt();
        if(mode == 1)
        {
            System.out.println();
            System.out.print("Insert 1 for binary and 0 for text: ");
            mode = scanner.nextInt();
            if(mode == 1)
                readBinary();
            else
                readText();
            
            primaryStage.show();
        }
        else
            requestKochFractal();
    }
    
    public void requestKochFractal() throws IOException
    {
        Scanner scanner = new Scanner(System.in);
        System.out.println();
        System.out.print("insert kochfractal level: ");
        int level = scanner.nextInt();
        
        this.kochManager.changeLevel(level);
    }
    
    public void readText() throws IOException, ClassNotFoundException
    {
    
    }
    
    public void readBinary() throws IOException, ClassNotFoundException
    {
        KochData sContent=null;
        byte [] buffer =null;
        File a_file = new File(binaryFilePath);
        
        if(a_file.exists() && !a_file.isDirectory()) { 
            try
            {
                FileInputStream fis = new FileInputStream(binaryFilePath);
                int length = (int)a_file.length();
                buffer = new byte [length];
                fis.read(buffer);
                fis.close();
            }
            catch(IOException e)
            {
                e.printStackTrace();
            }
            ByteArrayInputStream bis = new ByteArrayInputStream(buffer);
            ObjectInput in = new ObjectInputStream(bis);
            sContent = (KochData)in.readObject();
            
            this.kochManager.setEdgeList(sContent.getEdges());
            this.kochManager.setKochFractal(sContent.getFractal());
            this.requestDrawEdges();
        }
    }

    public void writeEdgesToBinary()
    {
        KochData kd = new KochData(kochManager.getEdgeList(), kochManager.getKochFractal());
        
       //serialize the List
        try (
          OutputStream file = new FileOutputStream(binaryFilePath);
          OutputStream buffer = new BufferedOutputStream(file);
          ObjectOutput output = new ObjectOutputStream(buffer);
        ){
          output.writeObject(kd);
        }  
        catch(IOException ex){
            System.err.println("Cannot perform output." + ex);
        }
        System.out.println("Wrote to binary.");

    }

    public void writeEdgesToText()
    {
        System.out.println("Wrote to text.");
    }
    
    
    public void clearKochPanel() {
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        gc.clearRect(0.0,0.0,kpWidth,kpHeight);
        gc.setFill(Color.BLACK);
        gc.fillRect(0.0,0.0,kpWidth,kpHeight);
    }
    
    public void drawEdge(Edge e) {
        // Graphics
        GraphicsContext gc = kochPanel.getGraphicsContext2D();
        
        // Adjust edge for zoom and drag
        Edge e1 = edgeAfterZoomAndDrag(e);
        
        // Set line color
        gc.setStroke(e1.getColor());
        
        // Set line width depending on level
        if (currentLevel <= 3) {
            gc.setLineWidth(2.0);
        }
        else if (currentLevel <=5 ) {
            gc.setLineWidth(1.5);
        }
        else {
            gc.setLineWidth(1.0);
        }
        
        // Draw line
        gc.strokeLine(e1.X1,e1.Y1,e1.X2,e1.Y2);
    }
    
    public void setTextNrEdges(final String text) {
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                labelNrEdgesText.setText(text);
            }
        });
    }
    
    public void setLeftEdgeNr(final int text) {
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                labelProgressLeftNrEdge.setText("Nr. Edges: " + text);
            }
        });
    }
    
    public void setBottomEdgeNr(final int text) {
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                labelProgressBottomNrEdge.setText("Nr. Edges: " + text);
            }
        });
    }
    
    public void setRightEdgeNr(final int text) {
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                labelProgressRightNrEdge.setText("Nr. Edges: " + text);
            }
        });
    }
    
    public void setTextCalc(final String text) {
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                labelCalcText.setText(text);
            }
        });
    }
    
    public void setTextDraw(final String text) {
        
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                labelDrawText.setText(text);
            }
        });
    }
    
    
    public void requestDrawEdges() {
        Platform.runLater(new Runnable(){
            @Override
            public void run() {
                kochManager.drawEdges();
            }
        });
    }
    
    private void increaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel < 12) {
            
            // resetZoom();
            currentLevel++;
            labelLevel.setText("Level: " + currentLevel);
            kochManager.changeLevel(currentLevel);
        }
    } 
    
    private void decreaseLevelButtonActionPerformed(ActionEvent event) {
        if (currentLevel > 1) {
            
            // resetZoom();
            currentLevel--;
            labelLevel.setText("Level: " + currentLevel);
            kochManager.changeLevel(currentLevel);
        }
    } 

    private void fitFractalButtonActionPerformed(ActionEvent event) {
        resetZoom();
        kochManager.drawEdges();
    }
    
    private void kochPanelMouseClicked(MouseEvent event) {
        if (Math.abs(event.getX() - startPressedX) < 1.0 && 
            Math.abs(event.getY() - startPressedY) < 1.0) {
            double originalPointClickedX = (event.getX() - zoomTranslateX) / zoom;
            double originalPointClickedY = (event.getY() - zoomTranslateY) / zoom;
            if (event.getButton() == MouseButton.PRIMARY) {
                zoom *= 2.0;
            } else if (event.getButton() == MouseButton.SECONDARY) {
                zoom /= 2.0;
            }
            zoomTranslateX = (int) (event.getX() - originalPointClickedX * zoom);
            zoomTranslateY = (int) (event.getY() - originalPointClickedY * zoom);
            kochManager.drawEdges();
        }
    }                                      

    private void kochPanelMouseDragged(MouseEvent event) {
        zoomTranslateX = zoomTranslateX + event.getX() - lastDragX;
        zoomTranslateY = zoomTranslateY + event.getY() - lastDragY;
        lastDragX = event.getX();
        lastDragY = event.getY();
        kochManager.drawEdges();
    }

    private void kochPanelMousePressed(MouseEvent event) {
        startPressedX = event.getX();
        startPressedY = event.getY();
        lastDragX = event.getX();
        lastDragY = event.getY();
    }                                                                        

    private void resetZoom() {
        int kpSize = Math.min(kpWidth, kpHeight);
        zoom = kpSize;
        zoomTranslateX = (kpWidth - kpSize) / 2.0;
        zoomTranslateY = (kpHeight - kpSize) / 2.0;
    }

    private Edge edgeAfterZoomAndDrag(Edge e) {
        return new Edge(
                e.X1 * zoom + zoomTranslateX,
                e.Y1 * zoom + zoomTranslateY,
                e.X2 * zoom + zoomTranslateX,
                e.Y2 * zoom + zoomTranslateY,
                e.hue,
                e.saturation,
                e.brightness);
    }

    public Label getlabelCountLeft(){
        return labelProgressLeftNrEdge;
    }
    
    public Label getlabelCountBottom(){
        return labelProgressBottomNrEdge;
    }
        
    public Label getlabelCountRight(){
        return labelProgressRightNrEdge;
    }
    /**
     * The main() method is ignored in correctly deployed JavaFX application.
     * main() serves only as fallback in case the application can not be
     * launched through deployment artifacts, e.g., in IDEs with limited FX
     * support. NetBeans ignores main().
     *
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
