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

	public Point3D barycentricCoordinates(double lamda1, double lamda2, double lamda3) {
		double sum = lamda1 + lamda2 + lamda3;
		if (sum < 0.001) return null;
		lamda1 /= sum;
		lamda2 /= sum;
		lamda3 /= sum;
		double x = p1.getX() * lamda1 + p2.getX() * lamda2 + p3.getX() * lamda3;
		double y = p1.getY() * lamda1 + p2.getY() * lamda2 + p3.getY() * lamda3;
		double z = p1.getZ() * lamda1 + p2.getZ() * lamda2 + p3.getZ() * lamda3;
		return new Point3D(x, y, z);
	}

	public Point3D getCenter() {
		return barycentricCoordinates(1, 1, 1);
	}
}