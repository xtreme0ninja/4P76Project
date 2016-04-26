package machinelearningproject;

import java.awt.Point;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * This class represents a set of customers and a depot
 * @author James Paterson, 5140082
 */
public class Dataset {
    private Customer[] customers; //Array of customers in this dataset
    private Point depot; //Location of the depot
    final static int BOUND = 2500;
    final static int MAXDEMAND = 5;
    int capacity = 10;
    long minTime = new GregorianCalendar(1970,0,1,0,0,0).getTime().getTime();
    long maxTime = new GregorianCalendar(1970,0,1,23,59,0).getTime().getTime();
    
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
     * Creates a new dataset from the given file
     * @param relDirectory Directory the file is located in
     * @param fileIndex Index of the file, files should be named VRPi.txt where i is the index
     */
    public Dataset(String relDirectory, int fileIndex){
        File directory = new File(relDirectory);
        File file = new File(directory, "VRP"+fileIndex+".txt");
        
        loadDataset(file);
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
            Date t1 = new Date(minTime + (long)rand.nextInt((int)maxTime-(int)minTime));
            Date t2 = new Date(minTime + (long)rand.nextInt((int)maxTime-(int)minTime));
            //Use the smaller of these times as the start of the time window, and the larger as the end
            Date start = (t1.before(t2)) ? t1 : t2;
            Date end = (t1.before(t2)) ? t2 : t1;
            
            customers[i] = new Customer(x, y, demand, start, end, i);
        }
    }
    
    /**
     * Loads data from the given file
     * @param from File to load data from
     */
    private void loadDataset(File from){
        try {
            //Get contents of file
            List<String> lines = Files.readAllLines(from.toPath());
            
            //Create depot
            Scanner sc = new Scanner(lines.get(0));
            sc.useDelimiter(" ");
            depot = new Point(sc.nextInt(), sc.nextInt());
            capacity = sc.nextInt();
            int sTime = sc.nextInt();
            int sHour = sTime/60;
            int sMinute = sTime - (60*sHour);
            minTime = new GregorianCalendar(1970,0,1,sHour,sMinute,0).getTime().getTime();
            
            int eTime = sc.nextInt();
            int eHour = eTime/60;
            int eMinute = eTime - (60*eHour);
            maxTime = new GregorianCalendar(1970,0,1,eHour,eMinute,0).getTime().getTime();
            
            customers = new Customer[lines.size()-1];
            //Create customers
            for (int i = 0; i < lines.size()-1; i++) {
                sc = new Scanner(lines.get(i+1));
                int x = sc.nextInt();
                int y = sc.nextInt();
                int demand = sc.nextInt();
                
                int startTime = sc.nextInt();
                int startHour = startTime/60;
                int startMinute = startTime - (60*startHour);
                Date t1 = new GregorianCalendar(1970,0,1,startHour,startMinute,0).getTime();
                
                int endTime = sc.nextInt();
                int endHour = endTime/60;
                int endMinute = endTime - (60*endHour);
                Date t2 = new GregorianCalendar(1970,0,1,endHour,endMinute,0).getTime();
                
                customers[i] = new Customer(x, y, demand, t1, t2, i);
            }
            
        } catch (IOException ex) {
            Logger.getLogger(Dataset.class.getName()).log(Level.SEVERE, null, ex);
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
