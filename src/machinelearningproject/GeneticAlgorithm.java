package machinelearningproject;

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
    final static int POPSIZE = 10;
    final static int NUMGENERATIONS = 1;
    private Individual[] population;

    public GeneticAlgorithm() {
        population = new Individual[POPSIZE];
        int lowestCost = Integer.MAX_VALUE;
        int indexOfBest = 0;

        //Initialize the population
        for (int i = 0; i < POPSIZE; i++) {
            population[i] = new Individual();
            //getting the initial lowest cost route
            if (population[i].getCost() < lowestCost) {
                lowestCost = population[i].getCost();
                indexOfBest = i;
            }
        }
                //debugging info
//        System.out.println("The best fitness was at index " + indexOfBest + " with a cost of : " + lowestCost);
//        System.out.println("The best individual had " + population[indexOfBest].getNumRoutes() + " routes");

        population = paretoRank(population);
        
        for (int i = 0; i < POPSIZE; i++) {
            System.out.println("Pareto Rank: " + population[i].getParetoRank() + " Cost :" + population[i].getCost() + " Num Routes :" + population[i].getNumRoutes());
        }


    }

    /**
     * Determines the pareto ranking of the population
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
     * Checks whether the given individual is dominated by any of the individuals
     * in the given array
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
            }else if (i.getNumRoutes() < currentIndividual.getNumRoutes()){
                betterNumRoutes = true;
            }
            
            //If its found that its better than one of the objectives
            if(betterCost || betterNumRoutes){
                if(betterCost){
                    //If it has a better number of routes or the same, then its dominated
                    if (i.getNumRoutes() <= currentIndividual.getNumRoutes()){
                        return true;
                    }
                }else if(betterNumRoutes){
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
     * @return The current population of individuals
     */
     public Individual[] getPopulation() {
        return population;
    }
}
