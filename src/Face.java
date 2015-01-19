public class Face implements Comparable<Face> {
    public int[] indices;

    public int[] x;
    public int[] y;

    public int color;
    public double distance;

    public int compareTo(Face other) {
        return other.distance > distance ? 1 : -1;
    }
}
