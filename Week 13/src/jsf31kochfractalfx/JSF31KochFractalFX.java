/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package jsf31kochfractalfx;

import calculate.*;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInput;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.RandomAccessFile;
import java.io.Reader;
import java.io.StringReader;
import java.io.Writer;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.util.Base64;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
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
import org.apache.commons.lang.SerializationUtils;
import org.apache.commons.lang.StringUtils;

/**
 *
 * @author Nico Kuijpers
 */
public class JSF31KochFractalFX extends Application {
    
    private String binaryFilePath = "binaryKoch.ser";
    private String notBufferedbinaryFilePath = "binaryKochNotBuffered.ser";
    private String textFilePath = "textKoch.txt";
    private String bufferedTextFilePath = "textKochBuffered.txt";
    private String notBufferedTextFilePath = "textKochNotBuffered.txt";
    private String mappedkochPath = "mappedKoch.dat";
    private int fileSize = 10485760; //10MB
    
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
        System.out.print("Insert 0 for write, 1 for read: ");
        int mode = scanner.nextInt();
        if(mode == 1)
        {
            System.out.println();
            System.out.print("Insert 0 for text,  1 for binary, 2 for mapped : ");
            mode = scanner.nextInt();
            System.out.println();
            System.out.print("Insert 0 for not buffered, 1 for buffered : ");
            if(mode == 0)
            {
                mode = scanner.nextInt();
                if(mode == 1)
                    readTextBuffered();
                else if (mode == 0)
                  readTextNotBuffered(); 
                else
                    System.err.println("Not Found! Quiting application...");
            }
            else if(mode == 1)
            {
                mode = scanner.nextInt();
                if(mode == 1)
                    readBinaryBuffered();
                else if (mode == 0)
                    readBinaryNotBuffered();
                else
                    System.err.println("Not Found! Quiting application...");
                    System.exit(0);
                    
            }
            else if(mode == 2)
            {
                readMapped();
            }
            else{
                 System.err.println("Not Found! Quiting application...");
                 System.exit(0);
            }
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
    
    //------------ Read ------------\\
    public void readTextBuffered() throws IOException, ClassNotFoundException
    {
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Read Text Bufferd");
        
        BufferedReader reader = null;

        try {
            File file = new File(bufferedTextFilePath);
            reader = new BufferedReader(new FileReader(file));

            String content = "";
            String line;
            while ((line = reader.readLine()) != null) {
                content += line;
            }
            reader.close();

            KochData kochData = (KochData)SerializationUtils.deserialize(Base64.getDecoder().decode(content));
            
            this.kochManager.setEdgeList(kochData.getEdges());
            this.kochManager.setKochFractal(kochData.getFractal());
            this.labelLevel.setText("Level: " + kochData.getFractal().getLevel());
            this.requestDrawEdges();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        timeStamp.setEnd("Stop - Read Text Bufferd");
        System.out.println(timeStamp.toString());
        
    }
    
    public void readTextNotBuffered() throws IOException, ClassNotFoundException
    {
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Read Text Bufferd");
        
        try {
            String content = "";
            InputStream inputStream = new FileInputStream(notBufferedTextFilePath);
            Reader inputStreamReader = new InputStreamReader(inputStream);

            int data = inputStreamReader.read();
            while(data != -1){
                char theChar = (char) data;
                content += theChar;
                data = inputStreamReader.read();
            }

            inputStreamReader.close();

            KochData kochData = (KochData)SerializationUtils.deserialize(Base64.getDecoder().decode(content));
            
            this.kochManager.setEdgeList(kochData.getEdges());
            this.kochManager.setKochFractal(kochData.getFractal());
            this.labelLevel.setText("Level: " + kochData.getFractal().getLevel());
            this.requestDrawEdges();

        } catch (IOException e) {
            e.printStackTrace();
        }
        
        timeStamp.setEnd("Stop - Read Text Bufferd");
        System.out.println(timeStamp.toString());
        
    }
    
    public void readBinaryBuffered() throws IOException, ClassNotFoundException
    {
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Read Binary Not Bufferd");
        
        KochData sContent=null;
        byte [] buffer =null;
        File a_file = new File(binaryFilePath);
        
        if(a_file.exists() && !a_file.isDirectory()) { 
            try
            {
                DataInputStream fis = new DataInputStream(new FileInputStream(binaryFilePath));
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
            this.labelLevel.setText("Level: " + sContent.getFractal().getLevel());
            this.requestDrawEdges();
        }
        
        timeStamp.setEnd("Stop - Read Text Bufferd");
        System.out.println(timeStamp.toString());
    }
    
    public void readBinaryNotBuffered() throws IOException, ClassNotFoundException
    {
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Read Binary Not Bufferd");
        
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
            this.labelLevel.setText("Level: " + sContent.getFractal().getLevel());
            this.requestDrawEdges();
        }
        
        timeStamp.setEnd("Stop - Read Text Bufferd");
        System.out.println(timeStamp.toString());
    }

    //------------ Write ------------\\
    public void writeEdgesToBinaryBufferd()
    {
        KochData kd = new KochData(kochManager.getEdgeList(), kochManager.getKochFractal());
        
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Write Binary Bufferd"); 
        
        
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
        timeStamp.setEnd("Stop - Write Binary Bufferd");
        System.out.println(timeStamp.toString());
    }
    
    public void writeEdgesToBinaryNotBufferd()
    {
        KochData kd = new KochData(kochManager.getEdgeList(), kochManager.getKochFractal());
        
        TimeStamp timeStamp = new TimeStamp();
        
        timeStamp.setBegin("Start - Write Binary Not Bufferd");
       //serialize the List
        try (
          OutputStream file = new FileOutputStream(notBufferedbinaryFilePath);
          ObjectOutput output = new ObjectOutputStream(file);
        ){
            output.writeObject(kd);
        }  
        catch(IOException ex){
            System.err.println("Cannot perform output." + ex);
        }
        timeStamp.setEnd("Stop - Write Binary Not Bufferd");
        
        System.out.println(timeStamp.toString());
    }

    public void writeEdgesToTextBuffered()
    {
        KochData kd = new KochData(kochManager.getEdgeList(), kochManager.getKochFractal());
        byte[] serialized = SerializationUtils.serialize(kd);
        String s = Base64.getEncoder().encodeToString(serialized);
        TimeStamp timeStamp = new TimeStamp();
        

        FileWriter fw = null;
        
        timeStamp.setBegin("Start - Write Text Bufferd");
        try {
            File file = new File(bufferedTextFilePath);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }
            
            fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            
            bw.write(s);
            
            

            bw.close();
            fw.close();
            //System.out.println("Wrote buffered to text.");
        } catch (IOException ex) {
            Logger.getLogger(JSF31KochFractalFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        timeStamp.setEnd("Stop - Write Text Bufferd");
        System.out.println(timeStamp.toString());
    }
    
     public void writeEdgesToTextNotBuffered()
    {
        KochData kd = new KochData(kochManager.getEdgeList(), kochManager.getKochFractal());
        byte[] serialized = SerializationUtils.serialize(kd);
        String s = Base64.getEncoder().encodeToString(serialized);
        
        TimeStamp timeStamp = new TimeStamp();
        
        
        FileWriter fw = null;
        timeStamp.setBegin("Start - Write Text Not Bufferd");
        try {
            File file = new File(notBufferedTextFilePath);
            // if file doesnt exists, then create it
            if (!file.exists()) {
                file.createNewFile();
            }

            FileWriter w = new FileWriter(file);
            w.write(s);
            w.close();
            
            //System.out.println("Wrote Not buffered to text.");
        } catch (IOException ex) {
            Logger.getLogger(JSF31KochFractalFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        timeStamp.setEnd("Stop - Write Text Not Bufferd");
        System.out.println(timeStamp.toString());
    }
     
     //------------ Mapped ------------\\
     public void readMapped(){
        byte[] serialized = new byte[fileSize];
        
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Read Mapped");
        try {
          RandomAccessFile memoryMappedFile = new RandomAccessFile(mappedkochPath, "rw");
          MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
          out.get(serialized);
          KochData kochData = (KochData)SerializationUtils.deserialize(serialized);

          this.kochManager.setEdgeList(kochData.getEdges());
          this.kochManager.setKochFractal(kochData.getFractal());
          this.labelLevel.setText("Level: " + kochData.getFractal().getLevel());
          this.requestDrawEdges();

        } catch (Exception ex) {
            Logger.getLogger(JSF31KochFractalFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        timeStamp.setEnd("Stop - Read Mapped");
        System.out.println(timeStamp.toString());
     }
     
     public void writeMapped(){
        KochData kd = new KochData(kochManager.getEdgeList(), kochManager.getKochFractal());
        byte[] serialized = SerializationUtils.serialize(kd);
        
        TimeStamp timeStamp = new TimeStamp();
        timeStamp.setBegin("Start - Write Mapped");
        try
        {
           RandomAccessFile memoryMappedFile = new RandomAccessFile(mappedkochPath, "rw");
           MappedByteBuffer out = memoryMappedFile.getChannel().map(FileChannel.MapMode.READ_WRITE, 0, fileSize);
           out.put(serialized);

        } catch (Exception ex)
        {
            Logger.getLogger(JSF31KochFractalFX.class.getName()).log(Level.SEVERE, null, ex);
        }
        timeStamp.setEnd("Stop - Write Mapped");
        System.out.println(timeStamp.toString());
         
     }
     
     
    
    //------------ Interface ------------\\
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
