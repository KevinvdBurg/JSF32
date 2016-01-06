package waarombytecode;

/**
 * Voorbeeld met 2 implementaties om TOS te laten zien. Gebruik bytecode
 * viewer om verschil in gegenereerde bytecode te laten zien.
 * @author erik
 */
public class StackVoorbeeld {

    private int[] intArray;

    /**
     * maak array aan die 100 ints kan bevatten
     */
    public StackVoorbeeld() {
        this.intArray = new int[100];
    }

    /**
     * return top of stack, implementatie 1: hele methode is mutual exclusive
     */
    public synchronized int top1() {
        int returnvalue;
        // evt. andere statements

        // het uiteindelijke mutual exclusive deel
        returnvalue = this.intArray[0];
        // nog meer evt. andere statements

        return returnvalue;
    }

    /**
     * return top of stack, implementatie 2: alleen relevante deel van
     * methode is mutual exclusive.
     */
    public int top2() {
        int returnvalue;
        // evt. andere statements

        // het uiteindelijke mutual exclusive deel
        synchronized (this) {
            returnvalue = this.intArray[0];
        }
        // nog meer evt. andere statements

        return returnvalue;
    }
}
