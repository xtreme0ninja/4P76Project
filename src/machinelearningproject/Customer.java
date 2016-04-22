package machinelearningproject;

import java.awt.Point;
import java.util.Date;

/**
 * This class represents a customer being delivered to
 * @author James Paterson, 5140082
 */
public class Customer {
    private final Point location; //Location of the customer
    private final int demand; //Amount of goods the customer wants delivered
    private final TimeInterval timeWindow; //Window of time that the customer can accept deliveries
    
    /**
     * Creates new customer with given parameters
     * @param x X-coordinate of customer
     * @param y Y-coordinate of customer
     * @param qty Quantity of goods customer wants delivered
     * @param start Start time of delivery time window
     * @param end End time of delivery time window
     */
    public Customer(int x, int y, int qty, Date start, Date end){
        location = new Point(x, y);
        demand = qty;
        timeWindow = new TimeInterval(start, end);
    }
    
    /**
     * Get the customer's location
     * @return A copy of the internal representation of the customer's location
     */
    public Point getLocation(){
        return (Point)location.clone();
    }
    
    /**
     * Get the customer's demand
     * @return The quantity of items the customer wants delivered
     */
    public int getDemand(){
        return demand;
    }
    
    /**
     * Get the interval of delivery
     * @return The time window that the customer can accept deliveries
     */
    public TimeInterval getInterval(){
        return timeWindow;
    }
    
    /**
     * String representation of a customer
     * @return String representation of customer
     */
    @Override
    public String toString(){
        return "Customer: {Location: (" + location.x + ", " + location.y + "), Demand: " + demand + ", Time window: " + timeWindow + "}";
    }
}
