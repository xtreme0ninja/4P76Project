package machinelearningproject;

import java.util.Arrays;
import java.util.Collections;
import static machinelearningproject.GeneticAlgorithm.DATASET;

/**
 * This class represents one "individual" in the population. This individual is a potential solution to the problem
 * @author James Paterson, 5140082
 */
public class Individual {
    private Customer[] chromosome;
    
    /**
     * Create a new individual with a random chromosome
     */
    public Individual(){
        Customer[] customers = DATASET.getCustomers();
        chromosome = new Customer[DATASET.getSize()];
        
        //Assign customers to chromosome in random order
        Integer[] arr = new Integer[customers.length];
        for (int i = 0; i < arr.length; i++) {
            arr[i] = i;
        }
        Collections.shuffle(Arrays.asList(arr));
        
        for (int i = 0; i < arr.length; i++) {
            chromosome[i] = customers[arr[i]];
        }
    }
    
    @Override
    public String toString(){
        String s = "Chromosome: (";
        for (int i = 0; i < chromosome.length; i++) {
            s += chromosome[i].getIndex();
            if(i != chromosome.length-1){
                s += ", ";
            }
        }
        s += ")";
        return s;
    }
}
