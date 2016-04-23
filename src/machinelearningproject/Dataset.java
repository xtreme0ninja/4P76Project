package machinelearningproject;

import java.awt.Point;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Random;

/**
 * This class represents a set of customers and a depot
 * @author James Paterson, 5140082
 */
public class Dataset {
    private Customer[] customers; //Array of customers in this dataset
    private Point depot; //Location of the depot
    final static int BOUND = 2500;
    final static long MINTIME = new GregorianCalendar(1970,0,1,8,0,0).getTime().getTime();
    final static long MAXTIME = new GregorianCalendar(1970,0,1,20,0,0).getTime().getTime();
    final static int MAXDEMAND = 5;
    
    /**
     * Creates a new dataset with random positions
     * @param size Number of customers in the dataset
     */
    public Dataset(int size){
        genDataset(size, new Random());
    }
    
    /**
     * Creates a new dataset with random positions, based off given seed
     * @param size Number of customers in the dataset
     * @param seed Seed to be used for random generation
     */
    public Dataset(int size, long seed){
        genDataset(size, new Random(seed));
    }
    
    /**
     * Generates dataset with given Random object and number of customers
     * @param size Number of customers in the dataset
     * @param rand Random object for random number gen
     */
    private void genDataset(int size, Random rand){
        //Create depot
        depot = new Point(rand.nextInt(BOUND), rand.nextInt(BOUND));
        
        //Create customers
        customers = new Customer[size];
        for (int i = 0; i < size; i++) {
            //Customer coordinates
            int x = rand.nextInt(BOUND);
            int y = rand.nextInt(BOUND);
            
            //Customer demand
            int demand = rand.nextInt(MAXDEMAND-1)+1;
            
            //Generate two random times between the min and max times
            Date t1 = new Date(MINTIME + (long)rand.nextInt((int)MAXTIME-(int)MINTIME));
            Date t2 = new Date(MINTIME + (long)rand.nextInt((int)MAXTIME-(int)MINTIME));
            //Use the smaller of these times as the start of the time window, and the larger as the end
            Date start = (t1.before(t2)) ? t1 : t2;
            Date end = (t1.before(t2)) ? t2 : t1;
            
            customers[i] = new Customer(x, y, demand, start, end, i);
        }
    }
    
    /**
     * Get the depot's location
     * @return A copy of the internal representation of the depot's location
     */
    public Point getDepot(){
        return (Point)depot.clone();
    }
    
    /**
     * Get all the customers in this data set
     * @return Array of customers
     */
    public Customer[] getCustomers(){
        return customers;
    }
    
    /**
     * Get the size of the dataset
     * @return The number of customers in the dataset
     */
    public int getSize(){
        return customers.length;
    }
}
