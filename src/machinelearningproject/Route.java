package machinelearningproject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import static machinelearningproject.GeneticAlgorithm.MAXCAPACITY;

/**
 * This class represents one of several routes that make up a potential solution
 *
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
    public Route() {
        route = new ArrayList<>();
        load = 0;
        Date startTime = new Date(Dataset.MINTIME);
        completeTime = Calendar.getInstance();
        completeTime.setTime(startTime);
    }

    public Route(Route r) {
        route = new ArrayList<>();
        for (Customer c : r.getRoute()) {
            route.add(c);
        }
        load = r.load; //how come i can do this? isnt it private...
        completeTime = r.completeTime; //how come i can do this? isnt it private...
    }

    /**
     * Adds the given customer to the route, if the vehicle has sufficient
     * capacity
     *
     * @param c Customer to attempt to add
     * @return True if customer is successfully added, false if adding the
     * customer would overload the vehicle
     */
    public boolean tryAdd(Customer c) {
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
        if (!route.isEmpty()) {
            //Get previous stop on the route
            Customer prevC = route.get(route.size() - 1);

            //Find out the time after going from prevC to c
            Date tripDuration = prevC.timeTo(c);
            newTime.add(Calendar.MILLISECOND, (int) tripDuration.getTime());

            if (newTime.after(windowEnd)) {
                inTime = false;
            }
        }

        //If driver arrives before time window, they need to wait
        if (newTime.before(windowStart)) {
            newTime.setTime(windowStart.getTime());
        }

        if (inTime && !makesRouteOverCapacity(c)) {
            route.add(c);
            load += c.getDemand();
            completeTime.setTime(newTime.getTime());
            return true;
        } else {
            return false;
        }
    }

    /**
     * Sees if the customer will fit in the route without making it for over
     * capacity
     *
     * @param c The customer to see if it fits
     * @return Whether the customer makes the route over capacity or not
     */
    public boolean makesRouteOverCapacity(Customer c) {
        if (load + c.getDemand() <= MAXCAPACITY) {
            return false;
        } else {
            return true;
        }
    }

    public Route findOptimalLocationForCustomer(Customer c) {

        //Store all customers in the route in another array
        ArrayList<Route> potentialRoutes = new ArrayList<>();
        ArrayList<Customer> customersInRoute = new ArrayList<>();
        for (Customer cInRoute : route) {
            customersInRoute.add(cInRoute);
        }

        //Create a route with the customer c placed at every index
        int index = 0;
        boolean insertedNewCustomer = false;
        for (int i = 0; i < route.size() + 1; i++) {
            Route r = new Route();
            for (int j = 0; j < route.size() + 1; j++) {
                boolean success = false;

                //If we are inserting the new customer at this index
                if (j == index) {
                    success = r.tryAdd(c);
                    insertedNewCustomer = true;
                } else if (insertedNewCustomer) {
                    success = r.tryAdd(customersInRoute.get(j - 1));
                } else {
                    success = r.tryAdd(customersInRoute.get(j));
                }

                //If it didnt succeed in adding to this route, then this route wont work
                if (!success) {
                    break;
                }
            }

            //Reset that the customer was inserted, and increase the index where
            //to place the new customer 
            insertedNewCustomer = false;
            index++;

            //Dont add routes which dont have all customers
            if (r.getRoute().size() == route.size() + 1) {
                potentialRoutes.add(r);
            }
        }

//        for (Route r : potentialRoutes) {
//            for (Customer w : r.getRoute()) {
//                System.out.print(w.getIndex() + ",");
//            }
//            System.out.println(" ");
//        }

        if (potentialRoutes.size() == 0) {
            return null;
        } else if (potentialRoutes.size() == 1) {
            return potentialRoutes.get(0);
            
        } else {
            Route bestRoute = new Route();
            int currentCost = 0;
            int bestCost = Integer.MAX_VALUE;
            for (Route r : potentialRoutes) {
                currentCost = r.getCost();
//                System.out.println("Cost :" + currentCost);
                if (r.getCost() < bestCost) {
                    bestCost = currentCost;
                    bestRoute = r;
                }
            }
            return bestRoute;
//            for (Customer w : route) {
//                System.out.print(w.getIndex() + ",");
//            }
//            System.out.println("Cost:" + this.getCost());
          
        }
    }

    /**
     * Returns the cost of the route by getting the distance between each
     * customer and summing them together
     *
     * @return Cost of the route
     */
    public int getCost() {
        int cost = 0;

        //Distance from depot to first customer
        cost += route.get(0).distanceTo(GeneticAlgorithm.DATASET.getDepot());

        //Distance between each customer in the route
        for (int i = 0; i < route.size() - 1; i++) {
            Customer currentCustomer = route.get(i);
            Customer nextCustomer = route.get(i + 1);

            //Calculate the distance between the current customer and the next one
            cost += currentCustomer.distanceTo(nextCustomer);
        }

        //Distance from last customer to depot
        cost += route.get(route.size() - 1).distanceTo(GeneticAlgorithm.DATASET.getDepot());

        return cost;
    }

    /**
     * Gets the list of all the customers in the route
     *
     * @return List of customers in route
     */
    public List<Customer> getRoute() {
        return route;
    }

    /**
     * Removes the given customer from the route
     *
     * @param c The customer to remove
     */
    public void removeCustomer(Customer c) {
        route.remove(c);
    }

}
