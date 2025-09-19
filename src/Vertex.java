import java.util.ArrayList;
import java.io.Serializable;
import javafx.geometry.Point3D;

public class Vertex extends Point3D implements Serializable {
	private final ArrayList<Triangle> connectedTriangles = new ArrayList<>();

	public Vertex(double x, double y, double z) {
		super(x, y, z);
	}

	public static Vertex createAndRegister(double x, double y, double z, ArrayList<Vertex> list) {
		Vertex vertex = new Vertex(x, y, z);
		list.add(vertex);
		return vertex;
	}

	public void addConnectedTriangle(Triangle triangle) {
		connectedTriangles.add(triangle);
	}

	public ArrayList<Triangle> getConnectedTriangles() {
		return connectedTriangles;
	}
}