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
    private final int index; //Index of this customer
    final static int SPEED = 1000; //Travel speed in units/hour
    
    /**
     * Creates new customer with given parameters
     * @param x X-coordinate of customer
     * @param y Y-coordinate of customer
     * @param qty Quantity of goods customer wants delivered
     * @param start Start time of delivery time window
     * @param end End time of delivery time window
     * @param i Index of this customer
     */
    public Customer(int x, int y, int qty, Date start, Date end, int i){
        location = new Point(x, y);
        demand = qty;
        timeWindow = new TimeInterval(start, end);
        index = i;
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
     * Get the index of this customer
     * @return Customer's index
     */
    public int getIndex(){
        return index;
    }
    
    /**
     * Get the travel time to the given point
     * @param p Point to find travel time to
     * @return The time it takes to travel to the given point
     */
    public Date timeTo(Point p){
        double hours = location.distance(p) / SPEED; //Number of hours of travel
        long duration = (long)(60*60*1000*hours); //Number of milliseconds of travel
        return new Date(duration);
    }
    
    /**
     * Get the travel time to the given customer
     * @param c Customer to find travel time to
     * @return The time it takes to travel to the given customer
     */
    public Date timeTo(Customer c){
        return timeTo(c.getLocation());
    }
    
    /**
     * Get the distance to the given point
     * @param p Point to measure distance to
     * @return Distance between the points
     */
    public double distanceTo(Point p){
        return location.distance(p);
    }
    
    /**
     * Get the distance to the given customer
     * @param c Customer to measure distance to
     * @return Distance between the customers
     */
    public double distanceTo(Customer c){
        return distanceTo(c.getLocation());
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
