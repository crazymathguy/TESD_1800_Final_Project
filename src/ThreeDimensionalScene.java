import java.io.Serializable;
import java.util.ArrayList;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Pos;
import javafx.stage.Stage;
import javafx.scene.Cursor;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.layout.Pane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.control.TextField;
import javafx.scene.control.Label;
import javafx.scene.control.Button;

public class ThreeDimensionalScene extends Application implements Serializable {
	// Conversion factor from world coordinates to screen coordinates
	private static final double WORLD_TO_SCREEN_CONVERSION = 175;
		// (700 / 4): 700 is the width of the window, 4.0 is the width of the screen in world coordinates, i.e. 1 unit is 175 pixels
	// private static final Point3D ORIGIN = new Point3D(0, 0, 0);

	private final Camera camera = new Camera(new Point3D(3.0, 2.0, -6.0), Rotation.RotationByDegrees(-20.0, -30.0, 0.0), 3.0); // camera
	private int renderMode = 1;
	//private int tool = 1;

	private transient Pane mainPane;
	private transient Line xAxis; // Line representing the X axis
	private transient Line yAxis; // Line representing the Y axis
	private transient Line zAxis; // Line representing the Z axis

	private transient double lastX; // Last mouse X position during drag
	private transient double lastY; // Last mouse Y position during drag
	private transient int dragging; // Is the mouse currently dragging?
	private transient boolean pane; // Is the side pane currently open?

	// Store points and triangles in the scene
	private final ArrayList<Vertex> points = new ArrayList<>();
	private final ArrayList<Triangle> triangles = new ArrayList<>();

	@Override
	public void start(Stage primaryStage) {
		pane = false;
		mainPane = new Pane();
		mainPane.setStyle("-fx-background-color:rgb(70, 80, 80);");

		BorderPane backPane = new BorderPane(mainPane);
		VBox pointControls = new VBox();
		pointControls.setSpacing(20);
		pointControls.setPadding(new Insets(2));
		pointControls.setAlignment(Pos.TOP_LEFT);
		pointControls.setOpacity(1);
		pointControls.setStyle("-fx-background-color:rgb(40, 50, 50);");
		
		Button exitButton = new Button("x");
		exitButton.setOnAction(_ -> {
			backPane.setRight(null);
			pane = false;
			mainPane.requestFocus();
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
				pane = false;
				mainPane.requestFocus();
			}
			if (!newValue.matches("(\\-?\\d*\\.?\\d*)")) {
				xCoordinate.setText(oldValue);
				return;
			}
		});
		xCoordinate.focusedProperty().addListener((_, _, newV) -> {
			if (newV) {
				xCoordinate.selectAll();
			} else {
				xCoordinate.setText(Double.valueOf(xCoordinate.getText()).toString());
				camera.setX(Double.parseDouble(xCoordinate.getText()));
				draw();
			}
		});
		yCoordinate.textProperty().addListener((_, oldValue, newValue) -> {
			if (newValue.contains("p")) {
				backPane.setRight(null);
				pane = false;
				mainPane.requestFocus();
			}
			if (!newValue.matches("(\\-?\\d*\\.?\\d*)")) {
				yCoordinate.setText(oldValue);
				return;
			}
		});
		yCoordinate.focusedProperty().addListener((_, _, newV) -> {
			if (newV) {
				yCoordinate.selectAll();
			} else {
				yCoordinate.setText(Double.valueOf(yCoordinate.getText()).toString());
				camera.setY(Double.parseDouble(yCoordinate.getText()));
				draw();
			}
		});
		zCoordinate.textProperty().addListener((_, oldValue, newValue) -> {
			if (newValue.contains("p")) {
				backPane.setRight(null);
				pane = false;
				mainPane.requestFocus();
			}
			if (!newValue.matches("(\\-?\\d*\\.?\\d*)")) {
				zCoordinate.setText(oldValue);
				return;
			}
		});
		zCoordinate.focusedProperty().addListener((_, _, newV) -> {
			if (newV) {
				zCoordinate.selectAll();
			} else {
				zCoordinate.setText(Double.valueOf(zCoordinate.getText()).toString());
				camera.setZ(Double.parseDouble(zCoordinate.getText()));
				draw();
			}
		});

		mainPane.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case UP -> camera.setFocalLength(camera.getFocalLength() + 0.1);
				case DOWN -> camera.setFocalLength(camera.getFocalLength() - 0.1);
				case P -> {
					if (pane) {
						backPane.setRight(null);
						pane = false;
					} else {
						backPane.setRight(pointControls);
						pane = true;
					}
				}
				case DIGIT0 -> renderMode = (renderMode + 1) % 2;
				case DIGIT1 -> createTestSquare();
				case DIGIT2 -> createTestTriangle();
				case DIGIT3 -> createTestCube();
				//case Q -> tool = 1;
				//case W -> tool = 2;
				//case E -> tool = 3;
				default -> {}
			}
			draw();
		});
		ChangeListener<Number> windowSizeListener = (_, _, _) -> draw();
		mainPane.widthProperty().addListener(windowSizeListener);
		mainPane.heightProperty().addListener(windowSizeListener);

		mainPane.setOnScroll(event -> {
			double newZ = camera.getZ() - event.getDeltaY() / WORLD_TO_SCREEN_CONVERSION;
			camera.setZ(newZ);
			zCoordinate.setText(Double.toString(newZ));
			draw();
		});
		mainPane.setOnMousePressed(event -> {
			if (mainPane.getCursor() == Cursor.DEFAULT) return;
			mainPane.setCursor(Cursor.CLOSED_HAND);
			lastX = event.getX();
			lastY = event.getY();
			dragging = 1;
		});
		mainPane.setOnMouseDragged(event -> {
			if (dragging != 1) return;
			double camX = camera.getX() + (lastX - event.getX()) / WORLD_TO_SCREEN_CONVERSION;
			double camY = camera.getY() - (lastY - event.getY()) / WORLD_TO_SCREEN_CONVERSION;
			camera.setPosition(new Point3D(camX, camY, camera.getZ()));
			lastX = event.getX();
			lastY = event.getY();
			xCoordinate.setText(Double.toString(camera.getX()));
			yCoordinate.setText(Double.toString(camera.getY()));
			zCoordinate.setText(Double.toString(camera.getZ()));
			draw();
		});
		mainPane.setOnMouseReleased(_ -> {
			if (dragging != 1) return;
			mainPane.setCursor(Cursor.HAND);
			dragging = 0;
		});

		Scene scene = new Scene(backPane, 700, 500);
		primaryStage.setTitle("3D Renderer");
		primaryStage.setScene(scene);
		primaryStage.show();
		mainPane.requestFocus();
		mainPane.setCursor(Cursor.HAND);

		clearScene(true);

		// Test objects
		// pressing 'q' will draw the square, pressing 'w' will draw the triangle, and 'e' the cube
	}

	void createTestCube() {
		clearScene(false);
		Vertex v1 = Vertex.createAndRegister(-1, -1, 1, points);
		Vertex v2 = Vertex.createAndRegister(-1, -1, -1, points);
		Vertex v3 = Vertex.createAndRegister(1, -1, -1, points);
		Vertex v4 = Vertex.createAndRegister(1, -1, 1, points);
		Vertex v5 = Vertex.createAndRegister(-1, 1, 1, points);
		Vertex v6 = Vertex.createAndRegister(-1, 1, -1, points);
		Vertex v7 = Vertex.createAndRegister(1, 1, -1, points);
		Vertex v8 = Vertex.createAndRegister(1, 1, 1, points);
		Triangle.createAndRegister(v1, v2, v3, triangles);
		Triangle.createAndRegister(v1, v3, v4, triangles);
		Triangle.createAndRegister(v1, v2, v5, triangles);
		Triangle.createAndRegister(v2, v5, v6, triangles);
		Triangle.createAndRegister(v2, v3, v6, triangles);
		Triangle.createAndRegister(v3, v6, v7, triangles);
		Triangle.createAndRegister(v3, v4, v8, triangles);
		Triangle.createAndRegister(v3, v7, v8, triangles);
		Triangle.createAndRegister(v1, v4, v5, triangles);
		Triangle.createAndRegister(v4, v5, v8, triangles);
		Triangle.createAndRegister(v5, v6, v7, triangles);
		Triangle.createAndRegister(v5, v8, v7, triangles);
		draw();
	}

	void createTestSquare() {
		clearScene(false);
		Vertex p1 = Vertex.createAndRegister(0, -1, -1, points);
		Vertex p2 = Vertex.createAndRegister(0, -1, 1, points);
		Vertex p3 = Vertex.createAndRegister(0, 1, 1, points);
		Vertex p4 = Vertex.createAndRegister(0, 1, -1, points);
		Triangle.createAndRegister(p1, p2, p3, triangles);
		Triangle.createAndRegister(p1, p3, p4, triangles);
		draw();
	}

	void createTestTriangle() {
		clearScene(false);
		Vertex p1 = Vertex.createAndRegister(-1, -1, 0, points);
		Vertex p2 = Vertex.createAndRegister(1, -1, 0, points);
		Vertex p3 = Vertex.createAndRegister(0, 1, 0, points);
		Triangle mainTriangle = Triangle.createAndRegister(p1, p2, p3, triangles);
		draw();
		drawPoint(mainTriangle.getCenter());
	}

	void clearScene(boolean draw) {
		points.clear();
		triangles.clear();
		if (draw) draw();
	}

	void draw() {
		mainPane.getChildren().clear();
		// Update axis positions
		Point2D right = project(new Point3D(100, 0, 0));
		Point2D left = project(new Point3D(-100, 0, 0));
		Point2D up = project(new Point3D(0, 100, 0));
		Point2D down = project(new Point3D(0, -100, 0));
		Point2D forward = project(new Point3D(0, 0, 100));
		Point2D back = project(new Point3D(0, 0, Math.clamp(camera.getZ() + 0.001, -100.0, 100.0)));
		xAxis = right == null || left == null ? null : new Line(right.getX(), right.getY(), left.getX(), left.getY());
		yAxis = up == null || down == null ? null : new Line(up.getX(), up.getY(), down.getX(), down.getY());
		zAxis = forward == null || back == null ? null : new Line(forward.getX(), forward.getY(), back.getX(), back.getY());
		if (xAxis != null) {
			xAxis.setStroke(new Color(1, 0, 0, 0.5));
			mainPane.getChildren().add(xAxis);
		}
		if (yAxis != null) {
			yAxis.setStroke(new Color(0, 1, 0, 0.5));
			mainPane.getChildren().add(yAxis);
		}
		if (zAxis != null) {
			zAxis.setStroke(new Color(0, 0, 1, 0.2));
			mainPane.getChildren().add(zAxis);
		}
		// Redraw all triangles
		drawAllTriangles();
		if (!pane) mainPane.requestFocus();
	}

	@SuppressWarnings("unchecked")
	void drawAllTriangles() {
		if (triangles.isEmpty()) return;
		ArrayList<Triangle> sortedTriangles = (ArrayList<Triangle>)triangles.clone();
		if (renderMode > 0) {
			// Sort triangles based on distance
			sortedTriangles.clear();
			ArrayList<Double> distances = new ArrayList<>();
			distances.add(Double.NEGATIVE_INFINITY);
			for (Triangle triangle : triangles) {
				double distance = camera.getPosition().distance(triangle.getCenter());
				int currentSize = distances.size();
				for (int i = 0; i < currentSize; i++) {
					if (distance > distances.get(i)) {
						distances.add(i, distance);
						sortedTriangles.add(i, triangle);
					}
				}
				if (distances.isEmpty()) {
					distances.add(distance);
					sortedTriangles.add(triangle);
				}
			}
		}
		for (Triangle triangle : sortedTriangles) {
			drawTriangle(triangle);
		}
	}

	void drawTriangle(Triangle triangle) {
		Polygon polygon = createPolygonFromTriangle(triangle);
		if (polygon == null) return;
		switch (renderMode) {
			case 0 -> {
				polygon.setFill(null);
				polygon.setStroke(Color.DARKGRAY);
			}
			case 1 -> {
				polygon.setFill(Color.DARKGRAY);
				polygon.setStroke(Color.WHITE);
			}
		}
		mainPane.getChildren().add(polygon);
		for (Vertex v : triangle.getVertices()) {
			drawPoint(v);
		}
		polygon.setOnMouseEntered(_ -> {
			if (dragging == 3) {
				dragging = 2;
				return;
			}
			if (renderMode > 0) {
				polygon.setFill(Color.LIGHTGRAY);
				mainPane.setCursor(Cursor.DEFAULT);
			}
		});
		polygon.setOnMouseClicked(null);
		polygon.setOnMousePressed(_ -> {
			dragging = 2;
		});
		polygon.setOnMouseReleased(_ -> {
			if (dragging < 2) return;
			if (dragging == 3) {
				polygon.setFill(Color.DARKGRAY);
				mainPane.setCursor(Cursor.HAND);
			}
			dragging = 0;
		});
		polygon.setOnMouseExited(_ -> {
			if (dragging == 2) {
				dragging = 3;
				return;
			}
			if (renderMode > 0) {
				polygon.setFill(Color.DARKGRAY);
				mainPane.setCursor(Cursor.HAND);
			}
		});
	}

	void drawPoint(Point3D point) {
		Point2D projectedPoint = project(point);
		if (projectedPoint == null) return;
		Circle pixel = new Circle(projectedPoint.getX(), projectedPoint.getY(), 3, Color.BLACK);
		pixel.setFill(Color.BLACK);
		mainPane.getChildren().add(pixel);
	}

	Polygon createPolygonFromTriangle(Triangle triangle) {
		Vertex[] vertices = triangle.getVertices();
		Point2D[] projectedPoints = {project(vertices[0]), project(vertices[1]), project(vertices[2])};
		if (projectedPoints[0] == null && projectedPoints[1] == null && projectedPoints[2] == null) return null;
		Polygon polygon = new Polygon();

		// clip edges between points in front and points behind the camera
		for (int i = 0; i < 3; i++) {
			int previous = (i + 2) % 3;
			int next = (i + 1) % 3;
			if (projectedPoints[i] == null) {
				double z = camera.convertToCameraCoordinates(vertices[i]).getZ();
				Point3D interpolated1 = projectedPoints[previous] == null ? null : vertices[i].interpolate(vertices[previous], (z - (camera.getZ() + 0.001)) / (z - camera.convertToCameraCoordinates(vertices[previous]).getZ()));
				Point3D interpolated2 = projectedPoints[next] == null ? null : vertices[i].interpolate(vertices[next], (z - (camera.getZ() + 0.001)) / (z - camera.convertToCameraCoordinates(vertices[next]).getZ()));
				Point2D clipped1 = project(interpolated1);
				if (clipped1 != null) polygon.getPoints().addAll(clipped1.getX(), clipped1.getY());
				Point2D clipped2 = project(interpolated2);
				if (clipped2 != null) polygon.getPoints().addAll(clipped2.getX(), clipped2.getY());
			} else {
				polygon.getPoints().addAll(projectedPoints[i].getX(), projectedPoints[i].getY());
			}
		}
		return polygon;
	}

	Point2D project(Point3D point) {
		if (point == null) return null;
		Point3D convertedPoint = camera.convertToCameraCoordinates(point);
		if (convertedPoint.getZ() < 0.001) return null;
		double x = camera.getFocalLength() / convertedPoint.getZ() * convertedPoint.getX() * WORLD_TO_SCREEN_CONVERSION + mainPane.getWidth() / 2;
		double y = camera.getFocalLength() / convertedPoint.getZ() * -convertedPoint.getY() * WORLD_TO_SCREEN_CONVERSION + mainPane.getHeight() / 2;
		return new Point2D(x, y);
	}

	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
