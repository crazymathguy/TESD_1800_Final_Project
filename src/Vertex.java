import java.io.IOException;
import java.io.Serializable;

import java.util.ArrayList;
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

	private void writeObject(java.io.ObjectOutputStream out) throws IOException {
		out.defaultWriteObject();
		out.writeDouble(getX());
		out.writeDouble(getY());
		out.writeDouble(getZ());
	}

	private void readObject(java.io.ObjectInputStream in) throws IOException, ClassNotFoundException{
		in.defaultReadObject();
		this.x = in.readDouble();
		this.y = in.readDouble();
		this.z = in.readDouble();
		
	}
}