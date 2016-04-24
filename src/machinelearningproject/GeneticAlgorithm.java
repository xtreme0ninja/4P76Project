package machinelearningproject;

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
    final static int NUMGENERATIONS = 25;
    private final static int CROSSMETHOD = 1; //1 for PMX
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
    }
    
    /**
     * Evolve the population
     */
    public void evolve(){
        for (int i = 0; i < NUMGENERATIONS; i++) {
            population = paretoRank(population);
            
            System.out.println("Before");
            for (int j = 0; j < POPSIZE; j++) {
                System.out.println("Pareto Rank: " + population[j].getParetoRank() + " Cost :" + population[j].getCost() + " Num Routes :" + population[j].getNumRoutes());
            }

            Individual[] parents = tournamentSelection(4);

            Individual[] children = crossover(parents);
            
            population = children;
        }
        
        population = paretoRank(population);
        System.out.println("===========================================");
        System.out.println("After");
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
    
    /**
     * Tournament selection with elitism
     * @param k Tournament set size
     * @return Set of individuals to use as parents for the next generation
     */
    private Individual[] tournamentSelection(int k){
        Individual[] parents = new Individual[population.length];
        Random rand = new Random();
        
        //Elitism - keep the best individual from the previous generation
        parents[0] = population[0];
        
        for (int i = 1; i < population.length; i++) {
            int bestIndex = population.length-1;
            for (int j = 0; j < k; j++) {
                int selection = rand.nextInt(population.length);
                if(selection < bestIndex){
                    bestIndex = selection;
                }
            }
            parents[i] = population[bestIndex];
        }
        
        return parents;
    }
    
    /**
     * Performs a crossover operation
     * @param parents Set of individuals to do crossover on
     * @return The children derived from the given parents
     */
    private Individual[] crossover(Individual[] parents){
        if(CROSSMETHOD == 1){
            return crossoverPMX(parents);
        }
        return null;
    }
    
    /**
     * Performs a partially mapped crossover
     * @param parents Set of individuals to do crossover on
     * @return The children derived from the given parents
     */
    private Individual[] crossoverPMX(Individual[] parents){
        Random rand = new Random();
        Individual[] children = new Individual[population.length];
        
        //Gets children for two parents at a time
        for (int i = 0; i < parents.length; i+=2) {
            //Parents
            Customer[] parent1 = parents[i].getChromosome();
            Customer[] parent2 = parents[i+1].getChromosome();
            
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
                boolean in1 = false;
                boolean in2 = false;
                
                //Check for conflict
                for (int k = cutPoint1; k < cutPoint2; k++) {
                    if(parent1[j].getIndex() == child1[k].getIndex()){
                        in1 = true;
                    }
                    if(parent2[j].getIndex() == child2[k].getIndex()){
                        in2 = true;
                    }
                }
                
                if(!in1){
                    child1[j] = parent1[j];
                } else {
                    child1[j] = (Customer)map1to2.get(parent1[j]);
                }
                
                if(!in2){
                    child2[j] = parent2[j];
                } else {
                    child2[j] = (Customer)map2to1.get(parent2[j]);
                }
            }
            
            //Area after second cutpoint is moved to opposite child if no conflict, if conflict use mapping
            for (int j = cutPoint2; j < DATASET.getSize(); j++) {
                boolean in1 = false;
                boolean in2 = false;
                
                //Check for conflict
                for (int k = cutPoint1; k < cutPoint2; k++) {
                    if(parent1[j].getIndex() == child1[k].getIndex()){
                        in1 = true;
                    }
                    if(parent2[j].getIndex() == child2[k].getIndex()){
                        in2 = true;
                    }
                }
                
                if(!in1){
                    child1[j] = parent1[j];
                } else {
                    child1[j] = (Customer)map1to2.get(parent1[j]);
                }
                
                if(!in2){
                    child2[j] = parent2[j];
                } else {
                    child2[j] = (Customer)map2to1.get(parent2[j]);
                }
            }
            
            children[i] = new Individual(child1);
            children[i+1] = new Individual(child2);
        }
        
        return children;
    }
}
