import javax.swing.*;
import java.awt.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

public class BirthdayCard extends JPanel {
    private static final int FPS = 60;

    private static Object3db[] objects = new Object3db[13];

    private static int p = -10;
    private static int y;

    private static int cx = 0;
    private static int cy = 4;
    private static int cz = -7;

    private static List<Face> allFaces = new ArrayList<Face>();

    public static double minDist = Double.MAX_VALUE;
    public static double maxDist = Double.MIN_VALUE;

    private static int frame;
    private static final String MESSAGE = "Happy birthday, Ceci!";

    public BirthdayCard() {
        setPreferredSize(new Dimension(640, 480));
        setDoubleBuffered(true);
    }

    public void paintComponent(Graphics _g) {
        Graphics2D g = (Graphics2D) _g;
        g.setColor(new Color(0, 0, 60));
        g.fillRect(0, 0, getWidth(), getHeight());

        g.setColor(new Color(0, 0, 120));
        for(int x = frame % 120 - 120; x < getWidth() + frame % 120; x += 120) {
            for(int y = frame % 120 - 120; y < getHeight() + frame % 120; y += 120) {
                g.fillOval(x, y, 30, 30);
                g.fillOval(x + 60, y + 60, 30, 30);
            }
        }

        g.setColor(Color.white);

        minDist = Double.MAX_VALUE;
        maxDist = Double.MIN_VALUE;

        allFaces.clear();

        for(Object3db o : objects) if(o != null) {
            allFaces.addAll(o.calculateScreenPosition(cx, cy, cz, p, y));
            o.ry = (o.ry + 1) % 360;
        }

        try {
            Collections.sort(allFaces);
        } catch(Exception e) { /* I HATE TIM AND HIS SORT */ }

        for(int k = 0; k < allFaces.size(); k++) {
            Face f = allFaces.get(k);

            double normDist = 1 - ((f.distance - minDist) / (maxDist - minDist));
            int red = (int) (((f.color >> 16) & 0xff) * normDist);
            int green = (int) (((f.color >> 8) & 0xff) * normDist);
            int blue = (int) ((f.color & 0xff) * normDist);

            int col = 0xff000000 | red << 16 | green << 8 | blue;

            g.setColor(new Color(col));
            g.fillPolygon(f.x, f.y, f.x.length);
        }

        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        drawColorfulString(g, MESSAGE, 60);
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_OFF);
        frame++;
    }

    public void drawColorfulString(Graphics g, String s, int by) {
        g.setFont(new Font(Font.SERIF, Font.PLAIN, 48));
        int width = g.getFontMetrics().stringWidth(s);

        final Color[] colors = { new Color(255, 255, 200), new Color(200, 255, 255), new Color(255, 200, 255) };
        for(int k = 0; k < s.length(); k++) {
            int y = by + (int) (10 * Math.sin(frame * Math.PI / 48 + k * Math.PI / 12));

            Color c = colors[k % colors.length];
            g.setColor(c);
            char ch = s.charAt(k);
            int x = getWidth() / 2 - width / 2 + g.getFontMetrics().stringWidth(s.substring(0, k));
            g.drawString(String.valueOf(ch), x, y);
        }
    }

    public Object3db genCylinder(double radius, double height, int r, int g, int b, boolean fill) {
        Object3db cyl = new Object3db();
        final double resolution = Math.PI / 48;
        int f = 0;

        for(double ang = 0; ang < 2 * Math.PI; ang += resolution) {
            double c1 = Math.cos(ang), c2 = Math.cos(ang + resolution);
            double s1 = Math.sin(ang), s2 = Math.sin(ang + resolution);

            cyl.addVertex(radius * c1, height / 2, radius * s1);
            cyl.addVertex(radius * c2, height / 2, radius * s2);
            cyl.addVertex(radius * c2, -height / 2, radius * s2);
            cyl.addVertex(radius * c1, -height / 2, radius * s1);

            double shade = 0.25 + 0.375 * (1 + Math.cos(ang + Math.PI / 2));
            int sr = (int) (r * shade);
            int sg = (int) (g * shade);
            int sb = (int) (b * shade);

            int col = 0xff000000 | sr << 16 | sg << 8 | sb;

            cyl.addFace(col, f, f + 1, f + 2, f + 3);
            f += 4;
        }

        if(fill) {
            int[] circle = new int[96];
            for(int k = 0; k < 96; k++)
                circle[k] = k * 4;
            cyl.addFace(0xff000000 | r << 16 | g << 8 | b, circle);
        }

        return cyl;
    }

    public static void main(String[] args) {
        System.setProperty("java.util.Arrays.useLegacyMergeSort", "true");
        JFrame frame = new JFrame();
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setResizable(false);

        BirthdayCard bc = new BirthdayCard();

        // plate
        objects[0] = bc.genCylinder(2.5, 0.2, 200, 200, 200, true);
        objects[0].oy = -1.5;
        // cake cylinder
        objects[1] = bc.genCylinder(2, 3, 255, 255, 200, true);

        // candles
        objects[2] = bc.genCylinder(0.05, 1.5, 255, 0, 0, false);
        objects[2].ox = -1;
        objects[2].oy = 4;

        objects[3] = bc.genCylinder(0.05, 1.5, 0, 255, 0, false);
        objects[3].oz = 1;
        objects[3].oy = 4;

        objects[4] = bc.genCylinder(0.05, 1.5, 0, 0, 255, false);
        objects[4].ox = 1;
        objects[4].oy = 4;

        objects[5] = bc.genCylinder(0.05, 1.5, 255, 255, 255, false);
        objects[5].oz = -1;
        objects[5].oy = 4;

        objects[6] = new Object3db();
        objects[6].addVertex(-1.1, 4.75, 0);
        objects[6].addVertex(-0.9, 4.75, 0);
        objects[6].addVertex(-1, 5.25, 0);
        objects[6].addFace(0xffffff00, 0, 1, 2);

        objects[7] = new Object3db();
        objects[7].addVertex(1.1, 4.75, 0);
        objects[7].addVertex(0.9, 4.75, 0);
        objects[7].addVertex(1, 5.25, 0);
        objects[7].addFace(0xffffff00, 0, 1, 2);

        objects[8] = new Object3db();
        objects[8].addVertex(0, 4.75, -1.1);
        objects[8].addVertex(0, 4.75, -0.9);
        objects[8].addVertex(0, 5.25, -1);
        objects[8].addFace(0xffffff00, 0, 1, 2);

        objects[9] = new Object3db();
        objects[9].addVertex(0, 4.75, 1.1);
        objects[9].addVertex(0, 4.75, 0.9);
        objects[9].addVertex(0, 5.25, 1);
        objects[9].addFace(0xffffff00, 0, 1, 2);

        objects[10] = bc.genCylinder(1.7, 0.35, 255, 0, 255, true);
        objects[10].oy = 1.6;
        objects[11] = bc.genCylinder(2.25, 0.35, 0, 255, 255, true);
        objects[11].oy = -1.1;

        objects[12] = bc.genCylinder(1.5, 2, 255, 255, 200, true);
        objects[12].oy = 2.25;

        frame.add(bc);
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        try {
            while(true) {
                bc.repaint();
                Thread.sleep(1000 / FPS);
            }
        } catch(InterruptedException e) {
            e.printStackTrace();
            Thread.currentThread().interrupt();
        }
    }
}
