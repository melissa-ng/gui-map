import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;

import java.awt.*;

public class MapLine {
    private Coordinate start;
    private Coordinate end;
    private Color color;

    public MapLine(Coordinate start, Coordinate end, Color color) {
        this.start = start;
        this.end = end;
        this.color = color;
    }

    public void paint(Graphics g, Point ppx, Point ppy, double scale) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setStroke(new BasicStroke(3));
        g2.setColor(color);
        Point pp1 = new Point(ppx.x + (int) ((start.getLon() - ppy.x) * scale), ppx.y + (int) ((ppy.y - start.getLat()) * scale));
        Point pp2 = new Point(ppx.x + (int) ((end.getLon() - ppy.x) * scale), ppx.y + (int) ((ppy.y - end.getLat()) * scale));
        g2.drawLine(pp1.x, pp1.y, pp2.x, pp2.y);
    }

	public void setColor(Color red) {
		color = red;
		// TODO Auto-generated method stub
		
	}
}
