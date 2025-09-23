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
		this.position = new Point3D(
            Math.round(position.getX() * 1000) / 1000.0,
            Math.round(position.getY() * 1000) / 1000.0,
            Math.round(position.getZ() * 1000) / 1000.0
        );
	}

    public double getX() {
        return position.getX();
    }

    public void setX(double x) {
        this.position = new Point3D(Math.round(x * 1000) / 1000.0, position.getY(), position.getZ());
    }

    public double getY() {
        return position.getY();
    }

    public void setY(double y) {
        this.position = new Point3D(position.getX(), Math.round(y * 1000) / 1000.0, position.getZ());
    }

    public double getZ() {
        return position.getZ();
    }

    public void setZ(double z) {
        this.position = new Point3D(position.getX(), position.getY(), Math.round(z * 1000) / 1000.0);
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
        double tempX;
        double tempY;
        double tempZ;
        double x = point.getX() - position.getX();
        double y = point.getY() - position.getY();
        double z = point.getZ() - position.getZ();
        // rotate around y axis
        tempX = x * Math.cos(orientation.y()) - z * Math.sin(orientation.y());
        tempZ = x * Math.sin(orientation.y()) + z * Math.cos(orientation.y());
        x = tempX;
        z = tempZ;
        // rotate around x axis
        tempY = y * Math.cos(orientation.x()) - z * Math.sin(orientation.x());
        tempZ = y * Math.sin(orientation.x()) + z * Math.cos(orientation.x());
        y = tempY;
        z = tempZ;
        // rotate around z axis
        tempX = x * Math.cos(orientation.z()) - y * Math.sin(orientation.z());
        tempY = x * Math.sin(orientation.z()) + y * Math.cos(orientation.z());
        x = tempX;
        y = tempY;

        return new Point3D(x, y, z);
    }

    public Point3D convertToWorldCoordinates(Point3D point) {
        double tempX;
        double tempY;
        double tempZ;
        double x = point.getX();
        double y = point.getY();
        double z = point.getZ();
        // rotate back around z axis
        tempX = x * Math.cos(-orientation.z()) - y * Math.sin(-orientation.z());
        tempY = x * Math.sin(-orientation.z()) + y * Math.cos(-orientation.z());
        x = tempX;
        y = tempY;
        // rotate back around x axis
        tempY = y * Math.cos(-orientation.x()) - z * Math.sin(-orientation.x());
        tempZ = y * Math.sin(-orientation.x()) + z * Math.cos(-orientation.x());
        y = tempY;
        z = tempZ;
        // rotate back around y axis
        tempX = x * Math.cos(-orientation.y()) - z * Math.sin(-orientation.y());
        tempZ = x * Math.sin(-orientation.y()) + z * Math.cos(-orientation.y());
        x = tempX;
        z = tempZ;

        x = x + position.getX();
        y = y + position.getY();
        z = z + position.getZ();

        return new Point3D(x, y, z);
    }
}
