package src;

import javafx.animation.PathTransition;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Animate extends Application {
    private Group root = new Group();
    private int radius = 3;


    public static void main(String[] args) {

        //processing the file from the parameter
        RequestResponseParser.run("C:/Users/Aletta/Desktop/sniffngo/MERESEK/FinalCulomnizationCPY2.csv", "final_culomnization_out.csv", "final_culomnization_history.csv");


       /* HistoryHolder.getInstance().addHistoryEntryToMac("ali", new LatLng(15, 15));
        HistoryHolder.getInstance().addHistoryEntryToMac("belaa", new LatLng(35, 35));
        HistoryHolder.getInstance().addHistoryEntryToMac("pittyhu", new LatLng(105, 95));

        HistoryHolder.getInstance().addHistoryEntryToMac("ali", new LatLng(555, 95));
        HistoryHolder.getInstance().addHistoryEntryToMac("ali", new LatLng(205, 300));*/

        launch();
    }

    @Override
    public void start(Stage stage) {
        //Drawing a Circle
        Scene scene = new Scene(root, 600, 300);

        HashMap<String, ArrayList<LatLng>> history = HistoryHolder.getInstance().getHistory();


        for (Map.Entry<String, ArrayList<LatLng>> stringLatLngEntry : history.entrySet()) {


            ArrayList<LatLng> value = stringLatLngEntry.getValue();
            if (value.size() > 2) {
                radius = 10;
            } else if (value.size() > 5) {
                radius = 15;
            }


            Circle circle = new Circle(radius, new Color(new Random(255).nextFloat(), 0, new Random(255).nextFloat(), 0.6));


            //setting the given history list
            getParameters().getRaw();
            //Setting the position of the circle
            circle.setCenterX(value.get(0).getLatitude());
            circle.setCenterY(value.get(0).getLongitude());

            //Setting the radius of the circle
            circle.setRadius(8.30f);

            //  Color color = getColor();
            // System.out.println("SZIN "+color.toString());
            //circle.setFill(color);

            circle.setStrokeWidth(2);
            circle.setStroke(new Color(0, 0, 0.9, 0.8));

            //Creating a Path
            Path path = new Path();

            MoveTo moveTo = new MoveTo(value.get(0).getLatitude(), value.get(0).getLongitude());

            circle.setRadius(radius);
            System.out.println(" kor " + stringLatLngEntry.getKey() + " " + value.get(0).getLongitude() + " " + value.get(0).getLatitude() + " " + radius);

            //Adding all the elements to the path
            path.getElements().add(moveTo);

            int i = value.size();
            int counter = 1;

            while (i > 1) {
                path.getElements().addAll(new LineTo(value.get(counter).getLatitude() * 4, value.get(counter).getLongitude() * 4));
                i--;
                System.out.println(value.size() + " " + counter + " n " + value.get(counter).toString());
                counter++;
            }

            PathTransition pathTransition = new PathTransition();

            pathTransition.setDuration(Duration.millis(8000));

            pathTransition.setNode(circle);

            //Setting the path for the transition
            pathTransition.setPath(path);


            //Setting the cycle count for the transition
            // pathTransition.setCycleCount(50);

            pathTransition.setAutoReverse(true);

            pathTransition.play();

            root.getChildren().add(circle);

            stage.setTitle("MAC");

        }
        stage.setScene(scene);
        stage.show();
    }

    private Color getColor() {

        return new Color(new Random(255).nextFloat(), new Random(255).nextFloat(), new Random(255).nextFloat(), 0.6);
    }
}
