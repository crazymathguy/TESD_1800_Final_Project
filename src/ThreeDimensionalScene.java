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

record Triangle(Vertex p1, Vertex p2, Vertex p3) {
	static Triangle createAndRegister(Vertex p1, Vertex p2, Vertex p3, ArrayList<Triangle> triangles) {
		Triangle triangle = new Triangle(p1, p2, p3);
		p1.addConnectedTriangle(triangle);
		p2.addConnectedTriangle(triangle);
		p3.addConnectedTriangle(triangle);
		triangles.add(triangle);
		return triangle;
	}

	Vertex[] getVertices() {
		return new Vertex[] {p1, p2, p3};
	}
}

class Vertex extends Point3D implements Serializable {
	private final ArrayList<Triangle> connectedTriangles = new ArrayList<>();

	Vertex(double x, double y, double z) {
		super(x, y, z);
	}

	void addConnectedTriangle(Triangle triangle) {
		connectedTriangles.add(triangle);
	}

	ArrayList<Triangle> getConnectedTriangles() {
		return connectedTriangles;
	}
}

public class ThreeDimensionalScene extends Application {
	// Conversion factor from world coordinates to screen coordinates
	private static final double WORLD_TO_SCREEN_CONVERSION = 175;
		// (700 / 4): 700 is the width of the window, 4.0 is the width of the screen in world coordinates, i.e. 1 unit is 175 pixels
	// private static final Point3D ORIGIN = new Point3D(0, 0, 0);

	private Point3D camera = new Point3D(0.0, 0.0, -6.0); // camera position
	private double focalLength = 3.0; // distance from the camera to the projection plane

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
				camera = new Point3D(Double.parseDouble(xCoordinate.getText()), camera.getY(), camera.getZ());
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
				camera = new Point3D(camera.getX(), Double.parseDouble(yCoordinate.getText()), camera.getZ());
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
				camera = new Point3D(camera.getX(), camera.getY(), Double.parseDouble(zCoordinate.getText()));
				draw();
			}
		});

		mainPane.setOnKeyPressed(event -> {
			switch (event.getCode()) {
				case UP -> {
					focalLength += 0.1;
					draw();
				}
				case DOWN -> {
					focalLength -= 0.1;
					if (focalLength < 0.1) focalLength = 0.1;
					draw();
				}
				case P -> {
					if (pane) {
						backPane.setRight(null);
						pane = false;
					} else {
						backPane.setRight(pointControls);
						pane = true;
					}
				}
				case Q -> createTestSquare();
				case W -> createTestTriangle();
				case E -> createTestCube();
				default -> {}
			}
		});
		ChangeListener<Number> windowSizeListener = (_, _, _) -> draw();
		mainPane.widthProperty().addListener(windowSizeListener);
		mainPane.heightProperty().addListener(windowSizeListener);

		mainPane.setOnScroll(event -> {
			camera = new Point3D(camera.getX(), camera.getY(), camera.getZ() - event.getDeltaY() / 100.0);
			zCoordinate.setText(Double.toString(Math.round(camera.getZ() * 1000) / 1000.0));
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
			camera = new Point3D(camX, camY, camera.getZ());
			lastX = event.getX();
			lastY = event.getY();
			xCoordinate.setText(Double.toString(Math.round(camera.getX() * 1000) / 1000.0));
			yCoordinate.setText(Double.toString(Math.round(camera.getY() * 1000) / 1000.0));
			zCoordinate.setText(Double.toString(Math.round(camera.getZ() * 1000) / 1000.0));
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

		clearScene();

		// Test objects
		// pressing 'q' will draw the square, pressing 'w' will draw the triangle, and 'e' the cube
	}

	void createTestCube() {
		clearScene();
		Vertex v1 = new Vertex(-1, -1, 1);
		Vertex v2 = new Vertex(-1, -1, -1);
		Vertex v3 = new Vertex(1, -1, -1);
		Vertex v4 = new Vertex(1, -1, 1);
		Vertex v5 = new Vertex(-1, 1, 1);
		Vertex v6 = new Vertex(-1, 1, -1);
		Vertex v7 = new Vertex(1, 1, -1);
		Vertex v8 = new Vertex(1, 1, 1);
		points.add(v1);
		points.add(v4);
		points.add(v5);
		points.add(v8);
		points.add(v2);
		points.add(v3);
		points.add(v6);
		points.add(v7);
		Triangle t9 = Triangle.createAndRegister(v1, v4, v8, triangles);
		Triangle t10 = Triangle.createAndRegister(v1, v5, v8, triangles);
		Triangle t1 = Triangle.createAndRegister(v1, v2, v3, triangles);
		Triangle t2 = Triangle.createAndRegister(v1, v3, v4, triangles);
		Triangle t3 = Triangle.createAndRegister(v1, v2, v6, triangles);
		Triangle t4 = Triangle.createAndRegister(v1, v5, v6, triangles);
		Triangle t7 = Triangle.createAndRegister(v3, v4, v8, triangles);
		Triangle t8 = Triangle.createAndRegister(v3, v7, v8, triangles);
		Triangle t11 = Triangle.createAndRegister(v5, v6, v7, triangles);
		Triangle t12 = Triangle.createAndRegister(v5, v8, v7, triangles);
		Triangle t5 = Triangle.createAndRegister(v2, v3, v6, triangles);
		Triangle t6 = Triangle.createAndRegister(v3, v6, v7, triangles);
		drawTriangle(t9);
		drawTriangle(t10);
		drawTriangle(t1);
		drawTriangle(t2);
		drawTriangle(t3);
		drawTriangle(t4);
		drawTriangle(t7);
		drawTriangle(t8);
		drawTriangle(t11);
		drawTriangle(t12);
		drawTriangle(t5);
		drawTriangle(t6);
	}

	void createTestSquare() {
		clearScene();
		Vertex p1 = new Vertex(0, -1, -1);
		points.add(p1);
		Vertex p2 = new Vertex(0, -1, 1);
		points.add(p2);
		Vertex p3 = new Vertex(0, 1, 1);
		points.add(p3);
		Vertex p4 = new Vertex(0, 1, -1);
		points.add(p4);
		Triangle t1 = Triangle.createAndRegister(p1, p2, p3, triangles);
		drawTriangle(t1);
		Triangle t2 = Triangle.createAndRegister(p1, p3, p4, triangles);
		drawTriangle(t2);
	}

	void createTestTriangle() {
		clearScene();
		Vertex p1 = new Vertex(-1, -1, 0);
		points.add(p1);
		Vertex p2 = new Vertex(1, -1, 0);
		points.add(p2);
		Vertex p3 = new Vertex(0, 1, 0);
		points.add(p3);
		Triangle mainTriangle = Triangle.createAndRegister(p1, p2, p3, triangles);
		drawTriangle(mainTriangle);
	}

	void clearScene() {
		points.clear();
		triangles.clear();
		draw();
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
		// Redraw all elements
		for (Triangle triangle : triangles) {
			drawTriangle(triangle);
		}
		if (!pane) mainPane.requestFocus();
		pane = true;
	}

	Point2D project(Point3D point) {
		if (point == null) return null;
		if (point.getZ() < camera.getZ() + 0.001) return null;
		double x = focalLength / (point.getZ() - camera.getZ()) * (point.getX() - camera.getX()) * WORLD_TO_SCREEN_CONVERSION + mainPane.getWidth() / 2;
		double y = focalLength / (point.getZ() - camera.getZ()) * -(point.getY() - camera.getY()) * WORLD_TO_SCREEN_CONVERSION + mainPane.getHeight() / 2;
		return new Point2D(x, y);
	}

	void drawPoint(Vertex point) {
		Point2D projectedPoint = project(point);
		if (projectedPoint == null) return;
		Circle pixel = new Circle(projectedPoint.getX(), projectedPoint.getY(), 3, Color.BLACK);
		pixel.setFill(Color.BLACK);
		mainPane.getChildren().add(pixel);
	}

	void drawTriangle(Triangle triangle) {
		Polygon polygon = createPolygonFromTriangle(triangle);
		if (polygon == null) return;
		polygon.setStroke(Color.WHITE);
		polygon.setFill(Color.DARKGRAY);
		mainPane.getChildren().add(polygon);
		drawPoint(triangle.p1());
		drawPoint(triangle.p2());
		drawPoint(triangle.p3());
		polygon.setOnMouseEntered(_ -> {
			if (dragging == 3) {
				dragging = 2;
				return;
			}
			polygon.setFill(Color.LIGHTGRAY);
			mainPane.setCursor(Cursor.DEFAULT);
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
			polygon.setFill(Color.DARKGRAY);
			mainPane.setCursor(Cursor.HAND);
		});
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
				double z = vertices[i].getZ();
				Point3D interpolated1 = projectedPoints[previous] == null ? null : vertices[i].interpolate(vertices[previous], (z - (camera.getZ() + 0.01)) / (z - vertices[previous].getZ()));
				Point3D interpolated2 = projectedPoints[next] == null ? null : vertices[i].interpolate(vertices[next], (z - (camera.getZ() + 0.01)) / (z - vertices[next].getZ()));
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

	public static void main(String[] args) throws Exception {
		launch(args);
	}
}
