import java.util.ArrayList;
import java.io.Serializable;
import javafx.geometry.Point3D;

public record Triangle(Vertex p1, Vertex p2, Vertex p3) implements Serializable {
	public static Triangle createAndRegister(Vertex p1, Vertex p2, Vertex p3, ArrayList<Triangle> list) {
		Triangle triangle = new Triangle(p1, p2, p3);
		p1.addConnectedTriangle(triangle);
		p2.addConnectedTriangle(triangle);
		p3.addConnectedTriangle(triangle);
		list.add(triangle);
		return triangle;
	}

	public Vertex[] getVertices() {
		return new Vertex[] {p1, p2, p3};
	}

	public Point3D getCenter() {
		double xAverage = (p1.getX() + p2.getX() + p3.getX()) / 3.0;
		double yAverage = (p1.getY() + p2.getY() + p3.getY()) / 3.0;
		double zAverage = (p1.getZ() + p2.getZ() + p3.getZ()) / 3.0;
		return new Point3D(xAverage, yAverage, zAverage);
	}
}