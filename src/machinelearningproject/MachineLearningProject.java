package machinelearningproject;

import java.awt.Point;


/**
 *
 * @author James
 */
public class MachineLearningProject {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Dataset ds = new Dataset(10);
        Point depot = ds.getDepot();
        System.out.println("Depot: {Location: (" + depot.x + ", " + depot.y + ")}");
        Customer[] customers = ds.getCustomers();
        for (Customer customer : customers) {
            System.out.println(customer);
        }
        new GeneticAlgorithm();
    }
    
}
