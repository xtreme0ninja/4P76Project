package machinelearningproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static machinelearningproject.GeneticAlgorithm.DATASET;

/**
 * This class represents one "individual" in the population. This individual is a potential solution to the problem
 * @author James Paterson, 5140082
 * @author Riley Davidson, 5291398
 */
public class Individual {
    private Customer[] chromosome;
    private List<Route> routes;
    private int cost;
    private int paretoRank;
    
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
                updateCost(r);
                r = new Route();
                r.tryAdd(customer);
            }
        }
        routes.add(r);
        System.out.println("Route completed");
        System.out.println("Cost of the route is :" + cost);
        System.out.println("Finished. Created " + getNumRoutes() + " routes.");
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
    
    /**
     * Returns the number of routes needed for the given solution. This is also
     * the number of drivers needed for this solution
     * @return The number of routes in the solution
     */
    public int getNumRoutes(){
        return routes.size();
    }

 
    /**
     * Returns the cost of all the routes in the individual summed together
     * @return The cost of the individual
     */
    public int getCost(){
       return cost;
    }
    
    
    /**
     * Updates the cost of the individual by adding the cost of the given route
     * @param r Routes which cost to add
     */
    private void updateCost(Route r){
        cost += r.getCost();
    }
    
    /**
     * Gets the current Pareto rank
     * @return The pareto rank of the individual
     */
     public int getParetoRank() {
        return paretoRank;
    }

     /**
      * Sets the pareto rank of the individual
      * @param paretoRank the pareto rank to set
      */
    public void setParetoRank(int paretoRank) {
        this.paretoRank = paretoRank;
    }
}
