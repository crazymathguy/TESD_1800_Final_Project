import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
//import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
//import javafx.scene.shape.Polygon;
//import javafx.scene.shape.Circle;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class ThreeDimensionalRendering extends Application {
    @Override
    public void start(Stage primaryStage) {
        Rectangle square = new Rectangle(0, 0, 100, 100);
        square.setFill(Color.DARKGREY);
        StackPane mainPane = new StackPane(square);

        BorderPane backPane = new BorderPane(mainPane);
        VBox pointControls = new VBox();
        pointControls.setSpacing(20);
        pointControls.setPadding(new Insets(2));
        pointControls.setAlignment(Pos.TOP_LEFT);
        pointControls.setOpacity(1);
        pointControls.setStyle("-fx-background-color:rgb(55, 70, 70);");
        backPane.setRight(pointControls);
        backPane.setStyle("-fx-background-color:rgb(40, 50, 50);");
        backPane.setOnKeyPressed(event -> {
            if (event.getCode().getChar().equals("P")) {
                backPane.setRight(backPane.getRight() == null ? pointControls : null);
            }
        });
        
        Button exitButton = new Button("x");
        exitButton.setOnAction(_ -> {
            backPane.setRight(null);
            backPane.requestFocus();
        });
        exitButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(150, 150, 150); -fx-font-size: 12;");
        exitButton.setOnMouseEntered(_ -> exitButton.setStyle("-fx-background-color: rgb(50, 60, 60); -fx-text-fill: rgb(150, 150, 150); -fx-font-size: 12;"));
        exitButton.setOnMouseExited(_ -> exitButton.setStyle("-fx-background-color: transparent; -fx-text-fill: rgb(150, 150, 150); -fx-font-size: 12;"));
        exitButton.setAlignment(Pos.TOP_RIGHT);

        Label xLabel = new Label("X:");
        Label yLabel = new Label("Y:");
        Label zLabel = new Label("Z:");
        TextField xCoordinate = new TextField("0.0");
        TextField yCoordinate = new TextField("0.0");
        TextField zCoordinate = new TextField("0.0");
        HBox xBox = new HBox(xLabel, xCoordinate);
        HBox yBox = new HBox(yLabel, yCoordinate);
        HBox zBox = new HBox(zLabel, zCoordinate);
        xLabel.setTextFill(Color.WHITE);
        yLabel.setTextFill(Color.WHITE);
        zLabel.setTextFill(Color.WHITE);
        xBox.setAlignment(Pos.CENTER);
        yBox.setAlignment(Pos.CENTER);
        zBox.setAlignment(Pos.CENTER);
        xBox.setSpacing(10);
        yBox.setSpacing(10);
        zBox.setSpacing(10);
        xBox.setPadding(new Insets(10, 10, 0, 10));
        yBox.setPadding(new Insets(0, 10, 0, 10));
        zBox.setPadding(new Insets(0, 10, 0, 10));
        xCoordinate.setPrefWidth(100);
        yCoordinate.setPrefWidth(100);
        zCoordinate.setPrefWidth(100);
        xCoordinate.setStyle("-fx-background-color: rgb(70, 80, 80); -fx-text-fill: white; -fx-border-color: transparent;");
        yCoordinate.setStyle("-fx-background-color: rgb(70, 80, 80); -fx-text-fill: white; -fx-border-color: transparent;");
        zCoordinate.setStyle("-fx-background-color: rgb(70, 80, 80); -fx-text-fill: white; -fx-border-color: transparent;");
        pointControls.getChildren().addAll(exitButton, xBox, yBox, zBox);

        xCoordinate.textProperty().addListener((_, oldValue, newValue) -> {
            if (newValue.contains("p")) {
                backPane.setRight(null);
                backPane.requestFocus();
            }
            if (newValue.matches("(\\-?\\d*\\.?\\d*)")) return;
            xCoordinate.setText(oldValue);
        });
        xCoordinate.focusedProperty().addListener((_, _, newV) -> {
            if (newV) {
                xCoordinate.selectAll();
            } else {
                xCoordinate.setText(Double.valueOf(xCoordinate.getText()).toString());
            }
        });
        yCoordinate.textProperty().addListener((_, oldValue, newValue) -> {
            if (newValue.contains("p")) {
                backPane.setRight(null);
                backPane.requestFocus();
            }
            if (newValue.matches("(\\-?\\d*\\.?\\d*)")) return;
            yCoordinate.setText(oldValue);
        });
        yCoordinate.focusedProperty().addListener((_, _, newV) -> {
            if (newV) {
                yCoordinate.selectAll();
            } else {
                yCoordinate.setText(Double.valueOf(yCoordinate.getText()).toString());
            }
        });
        zCoordinate.textProperty().addListener((_, oldValue, newValue) -> {
            if (newValue.contains("p")) {
                backPane.setRight(null);
                backPane.requestFocus();
            }
            if (newValue.matches("(\\-?\\d*\\.?\\d*)")) return;
            zCoordinate.setText(oldValue);
        });
        zCoordinate.focusedProperty().addListener((_, _, newV) -> {
            if (newV) {
                zCoordinate.selectAll();
            } else {
                zCoordinate.setText(Double.valueOf(zCoordinate.getText()).toString());
            }
        });

        Scene scene = new Scene(backPane, 700, 500);
        primaryStage.setTitle("3D Renderer");
        primaryStage.setScene(scene);
        primaryStage.show();
        backPane.requestFocus();
    }

    public static void main(String[] args) throws Exception {
        launch(args);
    }
}
