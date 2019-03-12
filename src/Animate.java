package src;

import javafx.animation.*;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public class Animate extends Application {
    private Group root = new Group();
    private int radius = 3;
    private Timeline timeline;
    private AnimationTimer animationTimer;


    public static void main(String[] args) {

        //processing the file from the parameter
        RequestResponseParser.run("C:/Users/Aletta/Desktop/sniffngo/MERESEK/FinalCulomnization.csv", "final_culomnization_out.csv", "final_culomnization_history.csv");

        launch();
    }

    @Override
    public void start(Stage stage) {
        //Drawing a Circle
        Scene scene = new Scene(root, 900, 500);

        HashMap<String, ArrayList<LatLng>> history = HistoryHolder.getInstance().getHistoryMAP();


        for (final Map.Entry<String, ArrayList<LatLng>> stringLatLngEntry : history.entrySet()) {


            for (LatLng latLng : stringLatLngEntry.getValue()) {
                System.out.println(stringLatLngEntry.getKey() + " " +  latLng.getDate());

            }

            ArrayList<LatLng> value = stringLatLngEntry.getValue();
            if (value.size() == 2) {
                radius = 2;
            } else if (value.size() > 3 && value.size() < 5) {
                radius = 5;
            } else if (value.size() >= 5) {
                radius = 10;
            }


            Circle circle = new Circle(radius, new Color(new Random(255).nextFloat(), 0, new Random(255).nextFloat(), 0.6));
            final Text text = new Text(stringLatLngEntry.getKey());
            text.setStroke(Color.BLACK);
            //create a layout for circle with text inside
            final StackPane stack = new StackPane();
            stack.getChildren().addAll(circle, text);
            stack.setLayoutX(value.get(0).getLatitude());
            stack.setLayoutY(value.get(0).getLongitude());
            root.getChildren().add(stack);

            timeline = new Timeline();
            timeline.setCycleCount(Timeline.INDEFINITE);
            timeline.setAutoReverse(true);


            //setting the given history list
            getParameters().getRaw();
            //Setting the position of the circle
            circle.setCenterX(value.get(0).getLatitude());
            circle.setCenterY(value.get(0).getLongitude());

            //Setting the radius of the circle
            circle.setRadius(8.30f);

            circle.setStrokeWidth(2);
            circle.setStroke(new Color(0, 0, 0.9, 0.8));

            //Creating a Path
            Path path = new Path();

            MoveTo moveTo = new MoveTo(value.get(0).getLatitude(), value.get(0).getLongitude());

            circle.setRadius(radius);
            //  System.out.println(" kor " + stringLatLngEntry.getKey() + " " + value.get(0).getLongitude() + " " + value.get(0).getLatitude() + " " + radius);

            //Adding all the elements to the path
            path.getElements().add(moveTo);

            int i = value.size();
            int counter = 1;

            while (i > 1) {
                path.getElements().addAll(new LineTo(value.get(counter).getLatitude() * 10, value.get(counter).getLongitude() * 10));
                i--;
                //    System.out.println(value.size() + " " + counter + " n " + value.get(counter).toString());
                counter++;
            }

            PathTransition pathTransition = new PathTransition();

            pathTransition.setDuration(Duration.millis(28000));

            pathTransition.setNode(stack);

            //Setting the path for the transition
            pathTransition.setPath(path);

            pathTransition.setAutoReverse(true);

            pathTransition.play();

            animationTimer = new AnimationTimer() {
                @Override
                public void handle(long l) {
                    //   text.setText(i.toString());
                    // i++;
                }
            };

            KeyValue keyValueX = new KeyValue(stack.scaleXProperty(), 1);
            KeyValue keyValueY = new KeyValue(stack.scaleYProperty(), 1);

            //create a keyFrame, the keyValue is reached at time 2s
            Duration duration = Duration.millis(2000);
            //one can add a specific action when the keyframe is reached
            EventHandler onFinished = new EventHandler<ActionEvent>() {
                public void handle(ActionEvent t) {
                    for (LatLng latLng : stringLatLngEntry.getValue()) {
                        latLng.getDate();
                    }
                    //  stack.setTranslateX(java.lang.Math.random()*200-100);
                    //reset counter
                }
            };

            KeyFrame keyFrame = new KeyFrame(duration, onFinished, keyValueX, keyValueY);

            //add the keyframe to the timeline
            timeline.getKeyFrames().add(keyFrame);

            timeline.play();
            animationTimer.start();
            stage.setTitle("MAC");

        }
        stage.setScene(scene);
        stage.show();
    }

    private Color getColor() {

        return new Color(new Random(255).nextFloat(), new Random(255).nextFloat(), new Random(255).nextFloat(), 0.6);
    }
}
