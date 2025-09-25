import java.io.Serializable;

public record Rotation(double x, double y, double z) implements Serializable {
    public static final double PI2 = 2 * Math.PI;

    /** 
     * Between 0 and 2 pi radians. A value beyond will wrap around
     */
    public Rotation(double x, double y, double z) {
        x = x % PI2;
        if (x < 0) x += PI2;
        this.x = x;
        y = y % PI2;
        if (y < 0) y += PI2;
        this.y = y;
        z = z % PI2;
        if (z < 0) z += PI2;
        this.z = z;
    }

    public static Rotation RotationByDegrees(double x, double y, double z) {
        return new Rotation(Math.toRadians(x), Math.toRadians(y), Math.toRadians(z));
    }

    public Rotation add(Rotation other) {
        return add(other.x(), other.y(), other.z());
    }

    public Rotation add(double x, double y, double z) {
        return new Rotation(this.x + x, this.y + y, this.z + z);
    }
}
