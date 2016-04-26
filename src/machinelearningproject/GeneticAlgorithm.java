package machinelearningproject;

import java.util.ArrayList;
import java.util.HashMap;
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
    final static Dataset DATASET = new Dataset(10);
    final static int POPSIZE = 100;
    final static int NUMGENERATIONS = 80;
    private final static int CROSSMETHOD = 2; //1 for PMX , 2 for BCRC
    private final static int MUTATEMETHOD = 1; //1 for random swap
    private final static double CROSSRATE = 0.8;
    private final static double MUTATERATE = 0;
    private Individual[] population;

    public GeneticAlgorithm() {
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
            return bestCostRouteCrossover(parents);
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
    private Individual[] bestCostRouteCrossover(Individual[] parents) {
        Random rand = new Random();
        Individual[] children = new Individual[parents.length];

        //Gets children for two parents at a time
        for (int i = 0; i < parents.length; i += 2) {

            //Parents
            Individual parent1 = parents[i];
            Individual parent2 = parents[i + 1];

            //Init children to be the same as thier parents
            Individual child1 = parent1;
            Individual child2 = parent2;

            //Get the routes to remove from the opposite parents child
            Route parent1Route = parent1.getRoutes().get(rand.nextInt(parent1.getNumRoutes()));
            Route parent2Route = parent2.getRoutes().get(rand.nextInt(parent2.getNumRoutes()));

            //Remove the customers in the chosen route from the other parents child
            parent2Route = child1.removeCustomersFromChromosomeOnRoute(parent2Route, parent2Route.getRoute().size());
            parent1Route = child2.removeCustomersFromChromosomeOnRoute(parent1Route, parent1Route.getRoute().size());

            //Add the removed customers back into route in the optimal location
            addRoutesCustomersToIndividual(parent2Route, child1);
            addRoutesCustomersToIndividual(parent1Route, child2);

            //Assign the children to the childs created
            children[i] = child1;
            children[i + 1] = child2;

            //Set the chromosome of the customer the same as the routes, is that important???
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
                        oldCosts.put(routeWithCustomerAdded, r.getCost());
                    }
                }
            }

            //If we found a route where the customer belongs, find the route
            //which result in the least additional cost
            if (added) {
                if (custAddedRoutes.size() == 1) {//if theres only one route, add it
                    toAdd = custAddedRoutes.get(0);
                    replaceRouteInIndividual(oldCosts,toAdd,toAddTo);
                } else {
                    //Find the route which results in the least additional cost
                    int bestCost = Integer.MAX_VALUE;
                    Route toUse = new Route();
                    for (Route r : custAddedRoutes) {
                        int withoutCustomerCost = (int) oldCosts.get(r);
                        int difference = r.getCost() - withoutCustomerCost;
                        if (difference < bestCost) {
                            bestCost = difference;
                            toUse = r;
                        }
                    }
                    
                    //Replace the old route in toAddTo with the route toUse
                    replaceRouteInIndividual(oldCosts,toUse,toAddTo);
                }
            } else { //If the customer didnt fit into any route, then add it to a new route
                Route r2 = new Route();
                r2.tryAdd(c);
                toAddTo.addRoute(r2);
            }
            
        }
    }
    
    /**
     * Replaces the route in the given individual which is equal to the route in toUse,
     * but the route in toUse has an additional customer on the route.
     * @param oldCosts Map to get the cost of the old route
     * @param toUse Route to be added to the individual
     * @param toAddTo Individual holding the old route whcih will be replaced
     */
    
    private void replaceRouteInIndividual(HashMap oldCosts, Route toUse, Individual toAddTo){
        int costOfRoute = (int) oldCosts.get(toUse);
        boolean done = false;
        for (int i = 0; i < toAddTo.getRoutes().size(); i++) {
            Route r = toAddTo.getRoutes().get(i);
            int cost = r.getCost();
            if (costOfRoute == cost) {
                toAddTo.getRoutes().set(i, toUse);
                done = true;
            }
        }
    }
    

    private Individual[] mutate(Individual[] parents) {
        if (MUTATEMETHOD == 1) {
            return mutateSwap(parents);
        }
        return null;
    }

    private Individual[] mutateSwap(Individual[] parents) {
        Individual[] children = new Individual[parents.length];
        Random rand = new Random();

        for (int i = 0; i < parents.length; i++) {
            if (rand.nextDouble() < MUTATERATE) {
                int point1 = rand.nextInt(DATASET.getSize());
                int point2 = rand.nextInt(DATASET.getSize());

                Customer[] dna = parents[i].getChromosome();
                Customer temp = dna[point1];
                dna[point1] = dna[point2];
                dna[point2] = temp;

                children[i] = new Individual(dna);
            } else {
                children[i] = parents[i];
            }
        }
        return children;
    }
}
