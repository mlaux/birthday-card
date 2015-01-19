import java.util.ArrayList;
import java.util.List;

public class Object3db {
    public List<Double> vx = new ArrayList<Double>();
    public List<Double> vy = new ArrayList<Double>();
    public List<Double> vz = new ArrayList<Double>();

    public List<Face> faces = new ArrayList<Face>();

    public double ox;
    public double oy;
    public double oz;

    public int rx;
    public int ry;

    public void addVertex(double x, double y, double z) {
        vx.add(x);
        vy.add(y);
        vz.add(z);
    }

    public void addFace(int color, int... indices) {
        Face f = new Face();
        f.indices = indices;
        f.color = color;
        faces.add(f);
    }

    public List<Face> calculateScreenPosition(int cx, int cy, int cz, int pitch, int yaw) {
        double pr = Math.toRadians(pitch);
        double yr = Math.toRadians(yaw);

        double rxr = Math.toRadians(rx);
        double ryr = Math.toRadians(ry);

        double ps = Math.sin(pr), pc = Math.cos(pr);
        double ys = Math.sin(yr), yc = Math.cos(yr);

        double rxs = Math.sin(rxr), rxc = Math.cos(rxr);
        double rys = Math.sin(ryr), ryc = Math.cos(ryr);

        for(int k = 0; k < faces.size(); k++) {
            Face f = faces.get(k);

            int[] verts = f.indices;
            if(f.x == null) {
                f.x = new int[verts.length];
                f.y = new int[verts.length];
            }

            double sumz = 0;

            for(int v = 0; v < verts.length; v++) {
                double _x = vx.get(verts[v]);
                double _y = vy.get(verts[v]);
                double _z = vz.get(verts[v]);

                _x += ox;
                _y += oy;
                _z += oz;

                double x = _x * ryc + _z * rys;
                double y = _y;
                double z = _z * ryc - _x * rys;

                x -= cx;
                y -= cy;
                z -= cz;

                double tz = y * ps + z * pc;
                double ty = y * pc - z * ps;

                double tx = x * yc + tz * ys;
                tz = tz * yc - x * ys;

                if(tz <= 0.01)
                    continue;

                sumz += tz;

                double sx =  320 * (tx / tz) + 320;
                double sy = -240 * (ty / tz) + 240;

                f.x[v] = (int) sx;
                f.y[v] = (int) sy;
            }

            f.distance = sumz / verts.length;

            if(f.distance > BirthdayCard.maxDist)
                BirthdayCard.maxDist = f.distance;
            if(f.distance < BirthdayCard.minDist)
                BirthdayCard.minDist = f.distance;
        }

        return faces;
    }
}
