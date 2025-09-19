import java.io.Serializable;

public record Rotation(double x, double y, double z) implements Serializable {
    private static final double PI2 = 2 * Math.PI;

    /** 
     * Between 0 and 2 pi radians. A value beyond will wrap around
     */
    public Rotation(double x, double y, double z) {
        this.x = x % PI2;
        this.y = y % PI2;
        this.z = z % PI2;
    }
}
