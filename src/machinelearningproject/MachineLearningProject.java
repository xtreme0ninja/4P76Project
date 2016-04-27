package machinelearningproject;

import java.awt.Point;
import java.util.List;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.ScatterChart;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.FlowPane;
import javafx.stage.Stage;


/**
 * This class creates a graph output of the VRP run
 * @author James Paterson, 5140082
 * @Author Riley Davidson, 5291398
 */
public class MachineLearningProject extends Application {

    /**
     * Main method which gets called at the start of program execution. First
     * it runs the Genetic algorithm, then uses its output to populate the graph
     * @param stage Stage to draw the graph on
     */
    @Override
    public void start(Stage stage) {
        GeneticAlgorithm GApmc = new GeneticAlgorithm(1);
        GApmc.evolve();
        
        GeneticAlgorithm GAbcrc = new GeneticAlgorithm(2);
        GAbcrc.evolve();
        
        Point depot = GeneticAlgorithm.DATASET.getDepot();
        
        /***************************************************
         * Start graph logic
         **************************************************/
        stage.setTitle("VRP with GA's");
        FlowPane backdrop = new FlowPane();
        
        //Get the all the solutions from each crossover method
        Individual[] allIndividualsPMC = GApmc.getPopulation();
        Individual[] allIndividualsBCRC = GAbcrc.getPopulation();
        
        //Since it sorts based on pareto rank, the best individuals are at the start
        Individual bestIndividualPMC = allIndividualsPMC[0];
        Individual bestIndividualBCRC = allIndividualsBCRC[0];
        
        //Create the graphs of the best individuals
        LineChart lcPMC = createGraph(depot,bestIndividualPMC, "Partially Matched Crossover");
        LineChart lcBCRC = createGraph(depot,bestIndividualBCRC, "Best Cost Route Crossover");
        
        //Add the linecharts to the stage and show it
        backdrop.getChildren().add(lcPMC);
        backdrop.getChildren().add(lcBCRC);
        
        Scene scene = new Scene(backdrop, 1100, 400);
        stage.setScene(scene);
        stage.show();
    }
    
    
    /**
     * Creates a graph of the given individuals VRP output.
     * @param depot The start point of the routes
     * @param toGraph The individual holding the routes to graph
     * @param crossoverType Which crossover type was used to produce this solution
     * @return The graph of the individuals solution
     */
    private LineChart createGraph(Point depot, Individual toGraph,String crossoverType){
        
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number,Number> lineChart = new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("X Location");
        yAxis.setLabel("Y Location");
        
        //Keeps track of how many routes there are and which route were currently graphing
        int currentRoute = 1;
        
        //Since it sorts based on pareto rank, the best individuals are at the start
        Individual bestIndividual = toGraph;
        List<Route> allRoutes = bestIndividual.getRoutes();
        
        //Total cost of the best individual
        int totalCost = 0;
        
        //For each route
        for (Route r : allRoutes) {
            
            //Add this routes cost to the total cost
            totalCost += r.getCost();
            
            //Create a new series
            XYChart.Series series = new XYChart.Series();
            
            //First point is the depot (This should be accounted for in code, but its not)
            series.setName("Route #" + currentRoute);
            series.getData().add(new XYChart.Data(depot.x, depot.y));
            
            //For each customer in the route, graph it
            for (Customer c: r.getRoute()) {
                Point p = c.getLocation();
                double x = p.getX();
                double y = p.getY();
                series.getData().add(new XYChart.Data(x, y));
            }
            
            //Last point is the depot
            series.getData().add(new XYChart.Data(depot.x, depot.y));
            
            //Add that series to the chart
            lineChart.getData().add(series);
            currentRoute++;
        }
        
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        
        lineChart.setTitle(crossoverType + "- Cost: " + totalCost);
        
        System.out.println(crossoverType + " output");
        System.out.println("The cost of the best route:" + totalCost + " and it used " + allRoutes.size() + " drivers");
        System.out.println("------------------------------------");
        
        return lineChart;
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
