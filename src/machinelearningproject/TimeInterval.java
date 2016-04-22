package machinelearningproject;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * This class represents an interval of time
 * @author James Paterson, 5140082
 */
public class TimeInterval {
    private final Date startTime; //Start time of the interval
    private final Date endTime; //End time of the interval
    
    /**
     * Creates a time interval between the given start and end times
     * @param start Desired start time
     * @param end Desired end time
     */
    public TimeInterval(Date start, Date end){
        startTime = start;
        endTime = end;
    }
    
    /**
     * Returns the start time of this interval
     * @return A copy of the start time
     */
    public Date getStart(){
        return (Date)startTime.clone();
    }
    
    /**
     * Returns the end time of this interval
     * @return A copy of the end time
     */
    public Date getEnd(){
        return (Date)endTime.clone();
    }
    
    /**
     * Checks if the given time is within the interval
     * @param t Time to check
     * @return True if the time is within the interval, false is not
     */
    public boolean inInterval(Date t){
        return (startTime.compareTo(t) <= 0) && (endTime.compareTo(t) >= 0);
    }
    
    /**
     * String representation of the time interval
     * @return String representation of interval
     */
    @Override
    public String toString(){
        SimpleDateFormat df = new SimpleDateFormat("HH:mm");
        return df.format(startTime) + " - " + df.format(endTime);
    }
}
