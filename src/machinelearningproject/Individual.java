package machinelearningproject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
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

    /**
     * Gets the list of all the routes in the individual
     *
     * @return The list of routes
     */
    public List<Route> getRoutes() {
        return routes;
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
    public void removeCustomersFromChromosomeOnRoute(Route r) {
        Iterator<Customer> iter = r.getRoute().iterator();
        while(iter.hasNext()){
            Customer c = iter.next();
            for (Customer custInChromosome : chromosome) {
                if (c.equals(custInChromosome)) {
                    //If this customer exist in the chromosome, remove it
                    //chromosome = ArrayUtils.removeElement(chromosome, custInChromosome);

                    //We then need to remove the customer from any routes that are currently holding it
                    for (Route r2 : routes) {
                        Iterator<Customer> iter2 = r2.getRoute().iterator();
                        while(iter2.hasNext()){
                            Customer c2 = iter2.next();
                            if (c2.equals(c)) {
                                r2.removeCustomer(c);
                                break;
                            }
                        }
                    }
                }
            }
        }
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
     * @param r Route to add
     */
    public void addRoute(Route r){
        routes.add(r);
    }

    public Customer[] getChromosome() {
        return chromosome;
    }
}
