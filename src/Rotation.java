import java.io.Serializable;

public record Rotation(double x, double y, double z) implements Serializable {
    public static final double PI2 = 2 * Math.PI;
    public static final double DEGREES_TO_RADIANS = PI2 / 360;

    /** 
     * Between 0 and 2 pi radians. A value beyond will wrap around
     */
    public Rotation(double x, double y, double z) {
        this.x = x % PI2;
        this.y = y % PI2;
        this.z = z % PI2;
    }

    public static Rotation RotationByDegrees(double x, double y, double z) {
        return new Rotation(x * DEGREES_TO_RADIANS, y * DEGREES_TO_RADIANS, z * DEGREES_TO_RADIANS);
    }

    public Rotation add(Rotation other) {
        return add(other.x(), other.y(), other.z());
    }

    public Rotation add(double x, double y, double z) {
        return new Rotation(this.x + x, this.y + y, this.z + z);
    }
}
