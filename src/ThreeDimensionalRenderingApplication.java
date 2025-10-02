import java.io.*;

import javafx.application.Application;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.control.MenuBar;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;

public class ThreeDimensionalRenderingApplication extends Application {
	private ThreeDimensionalScene mainScene;

	public static void main(String[] args) throws Exception {
		launch(args);
	}

	@Override
	public void start(Stage primaryStage) {
		mainScene = new ThreeDimensionalScene();
		BorderPane backPane = new BorderPane(mainScene);

		MenuBar menuBar = new MenuBar();
		backPane.setTop(menuBar);
		Menu fileMenu = new Menu("File");
		MenuItem openMenuItem = new MenuItem("Open");
		MenuItem saveMenuItem = new MenuItem("Save");
		//Menu editMenu = new Menu("Edit");
		//MenuItem copyMenuItem = new MenuItem("Copy");
		//MenuItem cutMenuItem = new MenuItem("Cut");
		//MenuItem pasteMenuItem = new MenuItem("Paste");
		Menu sceneMenu = new Menu("Scene");
		MenuItem clearMenuItem = new MenuItem("Clear Scene");
		MenuItem cubeMenuItem = new MenuItem("Create Cube");
		MenuItem squareMenuItem = new MenuItem("Create Square");
		MenuItem triangleMenuItem = new MenuItem("Create Triangle");
		menuBar.getMenus().addAll(fileMenu, sceneMenu);
		fileMenu.getItems().addAll(openMenuItem, saveMenuItem);
		//editMenu.getItems().addAll(copyMenuItem, cutMenuItem, pasteMenuItem);
		sceneMenu.getItems().addAll(clearMenuItem, cubeMenuItem, squareMenuItem, triangleMenuItem);

		FileChooser.ExtensionFilter objectFilter = new FileChooser.ExtensionFilter("Data Files (*.dat)", "*.dat");
		openMenuItem.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Open 3D Object");
			fileChooser.getExtensionFilters().add(objectFilter);
			File file = fileChooser.showOpenDialog(primaryStage);
			if (file != null) {
				openScene(file);
			}
		});
		saveMenuItem.setOnAction(event -> {
			FileChooser fileChooser = new FileChooser();
			fileChooser.setTitle("Save 3D Object");
			fileChooser.getExtensionFilters().add(objectFilter);
			fileChooser.setInitialFileName("untitled.dat");
			File file = fileChooser.showSaveDialog(primaryStage);
			if (file != null) {
				saveScene(file);
			}
		});
		clearMenuItem.setOnAction(event -> mainScene.clearScene(true));
		cubeMenuItem.setOnAction(event -> openScene(new File("default_objects/cube.dat")));
		squareMenuItem.setOnAction(event -> openScene(new File("default_objects/square.dat")));
		triangleMenuItem.setOnAction(event -> openScene(new File("default_objects/triangle.dat")));

		Scene scene = new Scene(backPane, 700, 500);
		primaryStage.setTitle("3D Renderer");
		primaryStage.setScene(scene);
		primaryStage.show();
	}

	void openScene(File file) {
		if (!file.exists()) return;
		try {
			ObjectInputStream input = new ObjectInputStream(new BufferedInputStream(new FileInputStream(file)));
			mainScene = (ThreeDimensionalScene)input.readObject();
			input.close();
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}

	void saveScene(File file) {
		try {
			ObjectOutputStream output = new ObjectOutputStream(new BufferedOutputStream(new FileOutputStream(file)));
			output.writeObject(mainScene);
			output.close();
		}
		catch (Exception ex) {
			System.out.println(ex.getMessage());
			ex.printStackTrace();
		}
	}
}

