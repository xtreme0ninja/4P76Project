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
 */
public class Route {
    private List<Customer> route;
    private int load;
    private Calendar completeTime;
    
    /**
     * Initializes the route
     */
    public Route(){
        System.out.println("Starting a new route");
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
        System.out.println("Attempting to add Customer #" + c.getIndex());
        System.out.println("Current route load: " + load + "/" + MAXCAPACITY + "; Demand: " + c.getDemand() + "; Expected load: " + (load+c.getDemand()) + "/" + MAXCAPACITY);
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
            
            //Debug output stuff
            SimpleDateFormat sf = new SimpleDateFormat("HH:mm");
            String newTimeS = sf.format(newTime.getTime());
            String windowStartS = sf.format(windowStart.getTime());
            String windowEndS = sf.format(windowEnd.getTime());
            String completeTimeS = sf.format(completeTime.getTime());
            
            System.out.println("Current time: " + completeTimeS + "; Expected time: " + newTimeS + "; Time window start: " + windowStartS + "; Time window end: " + windowEndS);
            
            if(newTime.after(windowEnd)){
                inTime = false;
            }
        } else {
            System.out.println("Don't need to check time constraints");
        }
        
        //If driver arrives before time window, they need to wait
        if(newTime.before(windowStart)){
            newTime.setTime(windowStart.getTime());
        }
        
        if(inTime && load + c.getDemand() <= MAXCAPACITY){
            route.add(c);
            load += c.getDemand();
            completeTime.setTime(newTime.getTime());
            System.out.println("Succeeded");
            return true;
        } else {
            System.out.println("Failed");
            return false;
        }
    }
}
