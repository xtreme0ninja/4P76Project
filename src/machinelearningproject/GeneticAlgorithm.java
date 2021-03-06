package machinelearningproject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import org.apache.commons.lang3.ArrayUtils;

/**
 * This class represents the overall GA
 *
 * @author James Paterson, 5140082
 * @author Riley Davidson, 5291398
 */
public class GeneticAlgorithm {

    final static int MAXCAPACITY = 10;
    final static Dataset DATASET = new Dataset("Data", 7);
    final static int POPSIZE = 100;
    final static int NUMGENERATIONS = 100;
    private int CROSSMETHOD = 2; //1 for PMX , 2 for BCRC
    private int longOrShort = 0; //0 for neither , 1 for longest, 2 for shortest
    private final static int MUTATEMETHOD = 2; //1 for random swap, 2 for route inversion
    private final static double CROSSRATE = 0.8;
    private final static double MUTATERATE = 0.2;
    private Individual[] population;

    public GeneticAlgorithm(int crossover, int BCRCtype) {
        longOrShort = BCRCtype;
        CROSSMETHOD = crossover;
        population = new Individual[POPSIZE];
        //Initialize the population
        for (int i = 0; i < POPSIZE; i++) {
            population[i] = new Individual();
        }
    }

    /**
     * Evolve the population
     */
    public void evolve() {
        for (int i = 0; i < NUMGENERATIONS; i++) {
            population = paretoRank(population);

//            System.out.println("Before");
//            for (int j = 0; j < POPSIZE; j++) {
//                System.out.println("Pareto Rank: " + population[j].getParetoRank() + " Cost :" + population[j].getCost() + " Num Routes :" + population[j].getNumRoutes());
//            }
            Individual[] parents = tournamentSelection(4);

            Individual[] children = crossover(parents);

            children = mutate(children);

            population = children;
        }

        population = paretoRank(population);
//        System.out.println("===========================================");
//        System.out.println("After");
//        for (int i = 0; i < POPSIZE; i++) {
//            System.out.println("Pareto Rank: " + population[i].getParetoRank() + " Cost :" + population[i].getCost() + " Num Routes :" + population[i].getNumRoutes());
//        }
    }

    /**
     * Determines the pareto ranking of the population
     *
     * @param pop The population to get the pareto ranking of
     */
    private Individual[] paretoRank(Individual[] pop) {
        int currentRank = 1;
        int N = pop.length;
        int M = pop.length;
        Individual[] newPopulation = new Individual[POPSIZE];
        int currentIndex = 0;
        while (N != 0) { //process entire population
            for (int i = 0; i < M; i++) { //find memebers in current rank
                if (!isDominated(pop[i], pop)) {   //if the current individual isnt dominated
                    pop[i].setParetoRank(currentRank);
                }
            }
            for (int i = 0; i < M; i++) {
                if (pop[i].getParetoRank() == currentRank) {
                    ///remove pop[i] from population and add it to the returned population
                    newPopulation[currentIndex] = pop[i];
                    currentIndex++;
                    pop = ArrayUtils.removeElement(pop, pop[i]);
                    M--;
                    N--;
                }
            }
            currentRank++;
            M = N;
        }
        return newPopulation;
    }

    /**
     * Checks whether the given individual is dominated by any of the
     * individuals in the given array
     *
     * @param currentIndividual The individual to compare
     * @param pop The population of individuals to compare to
     * @return Whether this individual is dominated or not
     */
    private boolean isDominated(Individual currentIndividual, Individual[] pop) {
        boolean betterCost = false;
        boolean betterNumRoutes = false;

        for (Individual i : pop) {
            betterCost = false;
            betterNumRoutes = false;

            //Check to see if the member in the population is better at an objective
            if (i.getCost() < currentIndividual.getCost()) {
                betterCost = true;
            } else if (i.getNumRoutes() < currentIndividual.getNumRoutes()) {
                betterNumRoutes = true;
            }

            //If its found that its better than one of the objectives
            if (betterCost || betterNumRoutes) {
                if (betterCost) {
                    //If it has a better number of routes or the same, then its dominated
                    if (i.getNumRoutes() <= currentIndividual.getNumRoutes()) {
                        return true;
                    }
                } else if (betterNumRoutes) {
                    //If it has a better cost or the same, then its dominated
                    if (i.getCost() <= currentIndividual.getCost()) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * Gets the current population of individuals
     *
     * @return The current population of individuals
     */
    public Individual[] getPopulation() {
        return population;
    }

    /**
     * Tournament selection with elitism
     *
     * @param k Tournament set size
     * @return Set of individuals to use as parents for the next generation
     */
    private Individual[] tournamentSelection(int k) {
        Individual[] parents = new Individual[population.length];
        Random rand = new Random();

        //Elitism - keep the best individual from the previous generation
        parents[0] = population[0];

        for (int i = 1; i < population.length; i++) {
            int bestIndex = population.length - 1;
            for (int j = 0; j < k; j++) {
                int selection = rand.nextInt(population.length);
                if (selection < bestIndex) {
                    bestIndex = selection;
                }
            }
            parents[i] = population[bestIndex];
        }

        return parents;
    }

    /**
     * Performs a crossover operation
     *
     * @param parents Set of individuals to do crossover on
     * @return The children derived from the given parents
     */
    private Individual[] crossover(Individual[] parents) {
        if (CROSSMETHOD == 1) {
            return crossoverPMX(parents);
        } else if (CROSSMETHOD == 2) {
            return bestCostRouteCrossover(parents, longOrShort);
        }
        return null;
    }

    /**
     * Performs a partially mapped crossover
     *
     * @param parents Set of individuals to do crossover on
     * @return The children derived from the given parents
     */
    private Individual[] crossoverPMX(Individual[] parents) {
        Random rand = new Random();
        Individual[] children = new Individual[parents.length];

        //Gets children for two parents at a time
        for (int i = 0; i < parents.length; i += 2) {
            if (rand.nextDouble() < CROSSRATE) {
                //Parents
                Customer[] parent1 = parents[i].getChromosome();
                Customer[] parent2 = parents[i + 1].getChromosome();

                //Generate cut points
                int num1 = rand.nextInt(DATASET.getSize() + 1);
                int num2 = rand.nextInt(DATASET.getSize() + 1);
                int cutPoint1 = Integer.min(num1, num2);
                int cutPoint2 = Integer.max(num1, num2);

                //Init children
                Customer[] child1 = new Customer[DATASET.getSize()];
                Customer[] child2 = new Customer[DATASET.getSize()];

                //For conflicts in making children
                Map map1to2 = new HashMap();
                Map map2to1 = new HashMap();

                //Area between cutpoints is moved to opposite child
                for (int j = cutPoint1; j < cutPoint2; j++) {
                    child1[j] = parent2[j];
                    child2[j] = parent1[j];

                    //Add to mapping for conflicts
                    map1to2.put(child1[j], child2[j]);
                    map2to1.put(child2[j], child1[j]);
                }

                //Area before first cutpoint is moved to opposite child if no conflict, if conflict use mapping
                for (int j = 0; j < cutPoint1; j++) {
                    child1[j] = assignWithMapping(parent1[j], child1, map1to2, cutPoint1, cutPoint2);
                    child2[j] = assignWithMapping(parent2[j], child2, map2to1, cutPoint1, cutPoint2);
                }

                //Area after second cutpoint is moved to opposite child if no conflict, if conflict use mapping
                for (int j = cutPoint2; j < DATASET.getSize(); j++) {
                    child1[j] = assignWithMapping(parent1[j], child1, map1to2, cutPoint1, cutPoint2);
                    child2[j] = assignWithMapping(parent2[j], child2, map2to1, cutPoint1, cutPoint2);
                }

                children[i] = new Individual(child1);
                children[i + 1] = new Individual(child2);
            } else {
                children[i] = parents[i];
                children[i + 1] = parents[i + 1];
            }
        }

        return children;
    }

    /**
     * Finds the correct customer to insert via PMX
     *
     * @param toAssign Customer in parent to be assigned
     * @param into Child chromosome being inserted into, should already have
     * section between cut points assigned
     * @param mapping Mapping function to handle conflicts with area between cut
     * points
     * @param cp1 First cut point
     * @param cp2 Second cut point
     * @return The customer to be inserted
     */
    private Customer assignWithMapping(Customer toAssign, Customer[] into, Map mapping, int cp1, int cp2) {
        boolean conflict = true;
        Customer toCheck = toAssign;
        while (conflict) {
            conflict = false;
            //Check for conflict
            for (int k = cp1; k < cp2; k++) {
                if (toCheck.getIndex() == into[k].getIndex()) {
                    //If there's a conflict, get the customer the conflicting customer maps to
                    toCheck = (Customer) mapping.get(toCheck);
                    k = cp2;
                    //Check for conflicts with this new customer
                    conflict = true;
                }
            }
        }
        return toCheck;
    }

    /**
     * Performs the best route crossover on the given parents and returns the
     * children
     *
     * @param parents Parents to do crossover on
     * @return The children of the parents
     */
    private Individual[] bestCostRouteCrossover(Individual[] parents, int longestOrShortest) {
        Random rand = new Random();
        Individual[] children = new Individual[parents.length];

        //Gets children for two parents at a time
        for (int i = 0; i < parents.length; i += 2) {

            //Parents
            Individual parent1 = parents[i];
            Individual parent2 = parents[i + 1];

            //Init children to be the same as thier parents
            Individual child1 = new Individual(parent1);
            Individual child2 = new Individual(parent2);

            if (rand.nextDouble() < CROSSRATE) {

                //Get the routes to remove from the opposite parents child
                Route parent1Route = new Route();
                Route parent2Route = new Route();
                if (longestOrShortest == 0) {
                    parent1Route = parent1.getRoutes().get(rand.nextInt(parent1.getNumRoutes()));
                    parent2Route = parent2.getRoutes().get(rand.nextInt(parent2.getNumRoutes()));
                } else if (longestOrShortest == 1) {
                    parent1Route = parent1.getMostCustomerRoute();
                    parent2Route = parent2.getMostCustomerRoute();
                } else if (longestOrShortest == 2) {
                    parent1Route = parent1.getLeastCustomerRoute();
                    parent2Route = parent2.getLeastCustomerRoute();
                }

                //Remove the customers in the chosen route from the other parents child
                parent2Route = child1.removeCustomersFromChromosomeOnRoute(parent2Route, parent2Route.getRoute().size());
                parent1Route = child2.removeCustomersFromChromosomeOnRoute(parent1Route, parent1Route.getRoute().size());

                //Add the removed customers back into route in the optimal location
                addRoutesCustomersToIndividual(parent2Route, child1);
                addRoutesCustomersToIndividual(parent1Route, child2);

                //Set the chromosome of the customer the same as the routes
                child1.rebuildChromosomeBasedOnRoutes();
                child2.rebuildChromosomeBasedOnRoutes();

            }

            //Assign the children to the childs created
            children[i] = child1;
            children[i + 1] = child2;

        }
        return children;
    }

    /**
     * Adds the givens routes customers to the given individuals routes
     *
     * @param toAdd The route with customers to add
     * @param toAddTo The individual which will have customers added to its
     * routes
     */
    private void addRoutesCustomersToIndividual(Route toAdd, Individual toAddTo) {

        //If the route is over capacity or not
        boolean overCapacity = false;

        for (Customer c : toAdd.getRoute()) {
            //Used to tell if we've added a customer back in
            boolean added = false;

            //Maps routes with their old costs before the customer as inserted
            HashMap oldCosts = new HashMap();

            //Holds all potential routes with a new customer added
            ArrayList<Route> custAddedRoutes = new ArrayList<>();

            //Get the routes where this customer can fit, and save the best cost route
            //with the customer added in the list
            for (Route r : toAddTo.getRoutes()) {
                overCapacity = r.makesRouteOverCapacity(c);
                if (!overCapacity) {
                    Route routeWithCustomerAdded = r.findOptimalLocationForCustomer(c);
                    if (routeWithCustomerAdded != null) {
                        added = true;
                        custAddedRoutes.add(routeWithCustomerAdded);
                        oldCosts.put(routeWithCustomerAdded, r);
                    }
                }
            }

            //If we found a route where the customer belongs, find the route
            //which result in the least additional cost
            if (added) {
                if (custAddedRoutes.size() == 1) {//if theres only one route, add it
                    toAdd = custAddedRoutes.get(0);
                    replaceRouteInIndividual(oldCosts, toAdd, toAddTo);
                } else {
                    //Find the route which results in the least additional cost
                    int bestCost = Integer.MAX_VALUE;
                    Route toUse = new Route();
                    for (Route r : custAddedRoutes) {
                        Route routeWithoutCustomerAdded = (Route) (oldCosts.get(r));
                        int withoutCustomerCost = routeWithoutCustomerAdded.getCost();
                        int difference = r.getCost() - withoutCustomerCost;
                        if (difference < bestCost) {
                            bestCost = difference;
                            toUse = r;
                        }
                    }

                    //Replace the old route in toAddTo with the route toUse
                    replaceRouteInIndividual(oldCosts, toUse, toAddTo);
                }
            } else { //If the customer didnt fit into any route, then add it to a new route
                Route r2 = new Route();
                r2.tryAdd(c);
                toAddTo.addRoute(r2);
            }

        }
    }

    /**
     * Replaces the route in the given individual which is equal to the route in
     * toUse, but the route in toUse has an additional customer on the route.
     *
     * @param oldCosts Map to get the cost of the old route
     * @param toUse Route to be added to the individual
     * @param toAddTo Individual holding the old route whcih will be replaced
     */
    private void replaceRouteInIndividual(HashMap oldCosts, Route toUse, Individual toAddTo) {
        Route routeWithoutCustomerAdded = (Route) (oldCosts.get(toUse));
        for (int i = 0; i < toAddTo.getRoutes().size(); i++) {
            Route r = toAddTo.getRoutes().get(i);
            int cost = r.getCost();
            if (r.equals(routeWithoutCustomerAdded)) {
                toAddTo.getRoutes().set(i, toUse);
            }
        }
    }

    /**
     * Performs a mutation operation
     *
     * @param parents Set of individuals to perform mutation on
     * @return Mutated individuals
     */
    private Individual[] mutate(Individual[] parents) {
        if (MUTATEMETHOD == 1) {
            return mutateSwap(parents);
        } else if (MUTATEMETHOD == 2) {
            return mutateRouteInversion(parents);
        }
        return null;
    }

    /**
     * Performs a swap mutation, switching the position of two customers in the
     * chromosome
     *
     * @param parents Set of individuals to perform mutation on
     * @return Mutated individuals
     */
    private Individual[] mutateSwap(Individual[] parents) {
        Individual[] mutated = new Individual[parents.length];
        Random rand = new Random();

        for (int i = 0; i < parents.length; i++) {
            if (rand.nextDouble() < MUTATERATE) {
                int point1 = rand.nextInt(DATASET.getSize());
                int point2 = rand.nextInt(DATASET.getSize());

                Customer[] dna = parents[i].getChromosome();
                Customer temp = dna[point1];
                dna[point1] = dna[point2];
                dna[point2] = temp;

                mutated[i] = new Individual(dna);
            } else {
                mutated[i] = parents[i];
            }
        }
        return mutated;
    }

    /**
     * Performs a constrained route inversion mutation. One route of length 2 or
     * 3 is chosen at random to have its direction reversed.
     *
     * @param parents Set of individuals to perform mutation on
     * @return Mutated individuals
     */
    private Individual[] mutateRouteInversion(Individual[] parents) {
        Individual[] mutated = new Individual[parents.length];
        Random rand = new Random();

        for (int i = 0; i < parents.length; i++) {
            if (rand.nextDouble() < MUTATERATE) {
                List<Route> routes = parents[i].getRoutes();

                //Don't consider routes with too few or too many customers, ideal is 2 to 3
                //Route with 1 customer won't change after inversion
                //Route with more than 3 customers has a high chance of time windows not lining up, causing split into multiple new routes
                List<Route> trimmedRoutes = new ArrayList<>();
                for (Route route : routes) {
                    if (route.getLength() > 1 && route.getLength() < 4) {
                        trimmedRoutes.add(route);
                    }
                }

                if (trimmedRoutes.size() > 0) {
                    //If there are viable routes to reverse, choose one at random
                    Route selectedRoute = trimmedRoutes.get(rand.nextInt(trimmedRoutes.size()));

                    //Get the start and end points of the route on the chromosome
                    int routeStart = 0;
                    for (int j = 0; j < routes.size(); j++) {
                        if (routes.get(j) == selectedRoute) {
                            j = routes.size();
                        } else {
                            routeStart += routes.get(j).getLength();
                        }
                    }
                    int routeEnd = routeStart + selectedRoute.getLength();

                    //Reverse the selected route
                    List<Customer> routeCustomers = new ArrayList<>(selectedRoute.getRoute());
                    Collections.reverse(routeCustomers);

                    //Put together new chromosome with the reversed route
                    Customer[] dna = parents[i].getChromosome();
                    for (int j = routeStart; j < routeEnd; j++) {
                        dna[j] = routeCustomers.get(j - routeStart);
                    }

                    //Add to mutated list
                    mutated[i] = new Individual(dna);
                } else {
                    //No routes of correct length, no mutation is done
                    mutated[i] = parents[i];
                }
            } else {
                mutated[i] = parents[i];
            }
        }

        return mutated;
    }
}
