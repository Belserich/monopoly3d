import java.util.Random;

public class SeedRandomTest {
    
    /**
     * Hier einmal ein Ausschnitt aus der Beschreibung der Klasse Random: If two instances of Random are created with the same seed, and the same sequence of method calls
     * is made for each, they will generate and return identical sequences of numbers. In order to guarantee this property, particular algorithms are specified for the class Random.
     * Java implementations must use all the algorithms shown here for the class Random, for the sake of absolute portability of Java code.
     * However, subclasses of class Random are permitted to use other algorithms, so long as they adhere to the general contracts for all the methods.
     *
     * Die Ausgabe lautet:
     * 3
     * 4
     * 1
     * 3
     * 892128508
     */
    public static void main(String[] args)
    {
        Random rng = new Random(1);
        
        System.out.println(rng.nextInt(6));
        System.out.println(rng.nextInt(6));
        System.out.println(rng.nextInt(6));
        System.out.println(rng.nextInt(6));
        
        System.out.println(rng.nextInt());
    }
    
}
