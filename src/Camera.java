import java.io.Serializable;
import javafx.geometry.Point3D;

public class Camera implements Serializable {
    private Point3D position;
    private Rotation orientation;
    private double focalLength;

    public Camera(Point3D position, Rotation orientation, double focalLength) {
        this.position = position;
        this.orientation = orientation;
        this.focalLength = focalLength;
    }

    public Point3D getPosition() {
        return position;
    }

	public void setPosition(Point3D position) {
		this.position = position;
	}

    public double getX() {
        return position.getX();
    }

    public void setX(double x) {
        this.position = new Point3D(x, position.getY(), position.getZ());
    }

    public double getY() {
        return position.getY();
    }

    public void setY(double y) {
        this.position = new Point3D(position.getX(), y, position.getZ());
    }

    public double getZ() {
        return position.getZ();
    }

    public void setZ(double z) {
        this.position = new Point3D(position.getX(), position.getY(), z);
    }

    public Rotation getOrientation() {
        return orientation;
    }

    public void setOrientation(Rotation orientation) {
        this.orientation = orientation;
    }

    public double getFocalLength() {
        return focalLength;
    }

    public void setFocalLength(double focalLength) {
        this.focalLength = focalLength;
        if (focalLength < 0.1) this.focalLength = 0.1;
    }

    public Point3D convertToCameraCoordinates(Point3D point) {
        Point3D convertedPoint = new Point3D(point.getX() - position.getX(), point.getY() - position.getY(), point.getZ() - position.getZ());
        return convertedPoint;
    }
}
