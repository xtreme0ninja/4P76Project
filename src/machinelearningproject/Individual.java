package machinelearningproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import static machinelearningproject.GeneticAlgorithm.DATASET;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This class represents one "individual" in the population. This individual is
 * a potential solution to the problem
 *
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
    public Individual() {
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

    /**
     * Create a new individual with the given chromosome
     *
     * @param dna An array of customers to use as the chromosome
     */
    public Individual(Customer[] dna) {
        chromosome = dna;
        buildRoutes();
    }
    
    public Individual(Individual toCopy){
        routes = new ArrayList<>();
        for(Route r : toCopy.getRoutes()){
            Route toAdd = new Route(r);
            routes.add(toAdd);
        }
        chromosome = toCopy.chromosome;
    }

    /**
     * Gets the list of all the routes in the individual
     *
     * @return The list of routes
     */
    public List<Route> getRoutes() {
        return routes;
    }

    public int getNumCustomersInRoutes() {
        int num = 0;
        for (Route r : routes) {
            for (Customer c : r.getRoute()) {
                num++;
            }
        }
        return num;
    }

    private void buildRoutes() {
        routes = new ArrayList<>();
        Route r = new Route();
        for (Customer customer : chromosome) {
            boolean added = r.tryAdd(customer);
            if (!added) {
                routes.add(r);
                updateCost(r);
                r = new Route();
                r.tryAdd(customer);
            }
        }
        routes.add(r);
    }

    /**
     * Removes the customers in the given route from the chromosome
     *
     * @param r The route which holds the customers to remove
     */
    public Route removeCustomersFromChromosomeOnRoute(Route inputRoute, int numBefore) {

        //Need to deep copy the input route so if the route exists fully in this
        //individual it doesnt remove the customers from the input route aswell
        Route r = new Route(inputRoute);
        
        //Need to save the customer to delete from the route
        //and the index of the route which its being deleted from
        ArrayList<Customer> toDelete = new ArrayList<Customer>();
        ArrayList<Integer> routeToDeleteFrom = new ArrayList<Integer>();
        
        //Find all customers that are to be deleted and which route they belong to
        for (Customer c : r.getRoute()) {
            for (int i = 0; i < routes.size(); i++) {
                Route r2 = routes.get(i);
                for (Customer c2 : r2.getRoute()) {
                    if (c.equals(c2)) {
                        toDelete.add(c2);
                        routeToDeleteFrom.add(i);
                    }
                }
            }
        }
        //Delete all the customers that were given to function
        for (int i = 0; i < toDelete.size(); i++) {
            Customer cDelete = toDelete.get(i);
            int indexOfRoute = routeToDeleteFrom.get(i);
            routes.get(indexOfRoute).removeCustomer(cDelete);
        }
        checkForEmptyRoutes();
        
        return r;
    }
    
    /**
     * Rebuilds the chromosome using the customers location in the 
     * routes as the order for the chromosome
     */
    
    public void rebuildChromosomeBasedOnRoutes(){
        int index = 0;
        Customer[] newChromosome = new Customer[DATASET.getSize()];
        for(Route r : routes){
            for(Customer c: r.getRoute()){
                newChromosome[index++] = c;
            }
        }
        chromosome = newChromosome;
    }

    /**
     * Checks to see if there are any empty routes in the current individual and
     * will remove them if there are.
     */
    private void checkForEmptyRoutes() {

        ArrayList<Route> routesToDelete = new ArrayList<Route>();
        for (Route r : routes) {
            if (r.getRoute().size() <= 0) {
                routesToDelete.add(r);
            }
        }
        routes.removeAll(routesToDelete);

    }

    @Override
    public String toString() {
        String s = "Chromosome: (";
        for (int i = 0; i < chromosome.length; i++) {
            s += chromosome[i].getIndex();
            if (i != chromosome.length - 1) {
                s += ", ";
            }
        }
        s += ")";
        return s;
    }

    /**
     * Returns the number of routes needed for the given solution. This is also
     * the number of drivers needed for this solution
     *
     * @return The number of routes in the solution
     */
    public int getNumRoutes() {
        return routes.size();
    }

    /**
     * Returns the cost of all the routes in the individual summed together
     *
     * @return The cost of the individual
     */
    public int getCost() {
        return cost;
    }

    /**
     * Updates the cost of the individual by adding the cost of the given route
     *
     * @param r Routes which cost to add
     */
    private void updateCost(Route r) {
        cost += r.getCost();
    }

    /**
     * Gets the current Pareto rank
     *
     * @return The pareto rank of the individual
     */
    public int getParetoRank() {
        return paretoRank;
    }

    /**
     * Sets the pareto rank of the individual
     *
     * @param paretoRank the pareto rank to set
     */
    public void setParetoRank(int paretoRank) {
        this.paretoRank = paretoRank;
    }

    /**
     * Adds the route to the individual
     *
     * @param r Route to add
     */
    public void addRoute(Route r) {
        routes.add(r);
    }

    public Customer[] getChromosome() {
        return chromosome;
    }
}
