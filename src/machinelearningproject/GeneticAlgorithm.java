package machinelearningproject;

/**
 * This class represents the overall GA
 * @author James Paterson, 5140082
 */
public class GeneticAlgorithm {
    final static int MAXCAPACITY = 10;
    final static Dataset DATASET = new Dataset(10);
    final static int POPSIZE = 1;
    private Individual[] population;
    
    public GeneticAlgorithm() {
        population = new Individual[POPSIZE];
        for (int i = 0; i < POPSIZE; i++) {
            population[i] = new Individual();
            //System.out.println(population[i]);
        }
    }
}
