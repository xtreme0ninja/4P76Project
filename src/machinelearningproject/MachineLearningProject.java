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
        GeneticAlgorithm GA = new GeneticAlgorithm();
        Point depot = GeneticAlgorithm.DATASET.getDepot();
        
        /***************************************************
         * Start graph logic
         **************************************************/
        stage.setTitle("VRP with GA's");
        final NumberAxis xAxis = new NumberAxis();
        final NumberAxis yAxis = new NumberAxis();
        final LineChart<Number,Number> lineChart =
                new LineChart<>(xAxis, yAxis);
        xAxis.setLabel("X Location");
        yAxis.setLabel("Y Location");
        lineChart.setTitle("Output of best");

        int currentRoute = 1;
        Individual[] allIndividuals = GA.getPopulation();
        //Since it sorts based on pareto rank, the best individuals are at the start
        Individual bestIndividual = allIndividuals[0];
        List<Route> allRoutes = bestIndividual.getRoutes();
        
        
        //For each route
        for (Route r : allRoutes) {
            
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
        
        //Add the linechart to the stage and show it
        lineChart.setAxisSortingPolicy(LineChart.SortingPolicy.NONE);
        Scene scene = new Scene(lineChart, 900, 700);
        stage.setScene(scene);
        stage.show();
    }
    
    
    public static void main(String[] args) {
        launch(args);
    }
    
}
