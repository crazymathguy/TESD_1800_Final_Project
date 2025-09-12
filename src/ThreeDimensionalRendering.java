import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.geometry.Pos;
import javafx.geometry.Insets;
import javafx.scene.paint.Color;
//import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.control.TextField;

public class ThreeDimensionalRendering extends Application {
    @Override
    public void start(Stage primaryStage) {
        Rectangle square = new Rectangle(0, 0, 100, 100);
        square.setFill(Color.LIGHTGREY);
        StackPane mainPane = new StackPane(square);

        BorderPane backPane = new BorderPane(mainPane);
        VBox pointControls = new VBox();
        pointControls.setSpacing(20);
        pointControls.setPadding(new Insets(10));
        pointControls.setAlignment(Pos.CENTER);
        backPane.setRight(pointControls);
        
        TextField xCoordinate = new TextField("0");
        TextField yCoordinate = new TextField("0");
        TextField zCoordinate = new TextField("0");
        pointControls.getChildren().addAll(xCoordinate, yCoordinate, zCoordinate);

        Scene scene = new Scene(backPane, 700, 500);
        primaryStage.setTitle("3DRender");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
