package machinelearningproject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static machinelearningproject.GeneticAlgorithm.MAXCAPACITY;

/**
 * This class represents one of several routes that make up a potential solution
 * @author James Paterson, 5140082
 * @author Riley Davidson, 5291398
 */
public class Route {
    private List<Customer> route;
    private int load;
    private Calendar completeTime;
    
    /**
     * Initializes the route
     */
    public Route(){
        route = new ArrayList<>();
        load = 0;
        Date startTime = new Date(Dataset.MINTIME);
        completeTime = Calendar.getInstance();
        completeTime.setTime(startTime);
    }
    
    /**
     * Adds the given customer to the route, if the vehicle has sufficient capacity
     * @param c Customer to attempt to add
     * @return True if customer is successfully added, false if adding the customer would overload the vehicle
     */
    public boolean tryAdd(Customer c){
        //Check if time window constraint holds
        boolean inTime = true;
        Calendar newTime = Calendar.getInstance();
        newTime.setTime(completeTime.getTime());
        
        //Get a Calendar representation of the start of c's time window
        Calendar windowStart = Calendar.getInstance();
        windowStart.setTime(c.getInterval().getStart());

        //Get a Calendar representation of the end of c's time window
        Calendar windowEnd = Calendar.getInstance();
        windowEnd.setTime(c.getInterval().getEnd());
        
        //Can always add if c is the first customer on the route
        if(!route.isEmpty()){
            //Get previous stop on the route
            Customer prevC = route.get(route.size()-1);
            
            //Find out the time after going from prevC to c
            Date tripDuration = prevC.timeTo(c);
            newTime.add(Calendar.MILLISECOND, (int)tripDuration.getTime());
            
            if(newTime.after(windowEnd)){
                inTime = false;
            }
        }
        
        //If driver arrives before time window, they need to wait
        if(newTime.before(windowStart)){
            newTime.setTime(windowStart.getTime());
        }
        
        if(inTime && load + c.getDemand() <= MAXCAPACITY){
            route.add(c);
            load += c.getDemand();
            completeTime.setTime(newTime.getTime());
            return true;
        } else {
            return false;
        }
    }
    
    
    /**
     * Returns the cost of the route by getting the distance between each customer
     * and summing them together
     * 
     * @return Cost of the route
     */
    public int getCost(){
        int cost = 0;
        
        //Distance from depot to first customer
        cost += route.get(0).distanceTo(GeneticAlgorithm.DATASET.getDepot());
        
        //Distance between each customer in the route
        for(int i = 0 ; i < route.size() - 1 ; i++){
            Customer currentCustomer = route.get(i);
            Customer nextCustomer  = route.get(i+1);
            
            //Calculate the distance between the current customer and the next one
            cost += currentCustomer.distanceTo(nextCustomer);
        }
        
        //Distance from last customer to depot
        cost += route.get(route.size()-1).distanceTo(GeneticAlgorithm.DATASET.getDepot());
        
        return cost;
    }
    
    /**
     * Gets the list of all the customers in the route
     * @return List of customers in route
     */
    public List<Customer> getRoute() {
        return route;
    }

    /**
     * Removes the given customer from the route
     * @param c The customer to remove
     */    
    public void removeCustomer(Customer c){
        route.remove(c);
    }
    
}
