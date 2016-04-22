package machinelearningproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static machinelearningproject.GeneticAlgorithm.DATASET;

/**
 * This class represents one "individual" in the population. This individual is a potential solution to the problem
 * @author James Paterson, 5140082
 */
public class Individual {
    private Customer[] chromosome;
    private List<Route> routes;
    
    /**
     * Create a new individual with a random chromosome
     */
    public Individual(){
        Customer[] customers = DATASET.getCustomers();
        chromosome = new Customer[DATASET.getSize()];
        
        //Assign customers to chromosome in random order
        Integer[] arr = new Integer[customers.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));
        
        for (int i = 0; i < arr.length; i++) {
            chromosome[i] = customers[arr[i]];
        }
        
        buildRoutes();
    }
    
    private void buildRoutes(){
        routes = new ArrayList<>();
        Route r = new Route();
        for (Customer customer : chromosome) {
            boolean added = r.tryAdd(customer);
            if(!added){
                routes.add(r);
                System.out.println("Route completed");
                r = new Route();
                r.tryAdd(customer);
            }
        }
        routes.add(r);
        System.out.println("Route completed");
        System.out.println("Finished. Created " + routes.size() + " routes.");
    }
    
    @Override
    public String toString(){
        String s = "Chromosome: (";
        for (int i = 0; i < chromosome.length; i++) {
            s += chromosome[i].getIndex();
            if(i != chromosome.length-1){
                s += ", ";
            }
        }
        s += ")";
        return s;
    }
}
