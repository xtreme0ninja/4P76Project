package machinelearningproject;

import java.util.ArrayList;
import java.util.List;
import static machinelearningproject.GeneticAlgorithm.MAXCAPACITY;

/**
 * This class represents one of several routes that make up a potential solution
 * @author James Paterson, 5140082
 */
public class Route {
    private List<Customer> route;
    private int load;
    
    /**
     * Initializes the route
     */
    public Route(){
        System.out.println("Starting a new route");
        route = new ArrayList<>();
        load = 0;
    }
    
    /**
     * Adds the given customer to the route, if the vehicle has sufficient capacity
     * @param c Customer to attempt to add
     * @return True if customer is successfully added, false if adding the customer would overload the vehicle
     */
    public boolean tryAdd(Customer c){
        System.out.println("Attempting to add Customer #" + c.getIndex() + " (demand: " + c.getDemand() + ") to route with load of " + load + "/" + MAXCAPACITY);
        if(load + c.getDemand() <= MAXCAPACITY){
            route.add(c);
            load += c.getDemand();
            System.out.println("Succeeded");
            return true;
        } else {
            System.out.println("Failed");
            return false;
        }
    }
}
