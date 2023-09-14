
import java.awt.BasicStroke;
import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Stroke;
import java.awt.Transparency;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TimerTask;
import java.util.Timer;
import java.awt.Color;

import javax.imageio.ImageIO;
import javax.swing.*;

import org.openstreetmap.gui.jmapviewer.Coordinate;
import org.openstreetmap.gui.jmapviewer.JMapViewer;
import org.openstreetmap.gui.jmapviewer.MapMarkerDot;
import org.openstreetmap.gui.jmapviewer.MapPolygonImpl;
import org.openstreetmap.gui.jmapviewer.tilesources.OsmTileSource;

public class Driver {
	// submission

	// Declare class data
	private static final String[] ANIMATION_TIMES = { "15 seconds", "30 seconds", "60 seconds", "90 seconds" };
	private static final int[] ANIMATION_TIME_VALUES = { 15, 30, 60, 90 };

	private static JComboBox<String> animationTimeComboBox;
	private static JCheckBox includeStopsCheckBox = new JCheckBox("Include Stops");
	private static JButton playButton;
	private static Timer timer;
	private static TimerTask placeMarker;
	private static JMapViewer mapViewer;
	private static Image arrowHead;
	
	public static double calculateAngle(Coordinate c1, Coordinate c2) {
	    double lat1 = Math.toRadians(c1.getLat());
	    double lat2 = Math.toRadians(c2.getLat());
	    double lon1 = Math.toRadians(c1.getLon());
	    double lon2 = Math.toRadians(c2.getLon());

	    double y = Math.sin(lon2 - lon1) * Math.cos(lat2);
	    double x = Math.cos(lat1) * Math.sin(lat2) - Math.sin(lat1) * Math.cos(lat2) * Math.cos(lon2 - lon1);
	    double bearing = Math.atan2(y, x);

	    return Math.toDegrees(bearing);
	}

	
	public static void main(String[] args) throws FileNotFoundException, IOException {
		//SwingUtilities.invokeLater(() -> {
			// create a new JMapViewer instance
			mapViewer = new JMapViewer();
			mapViewer.setTileSource(new OsmTileSource.TransportMap());
			mapViewer.setDisplayPosition(1600, 3250, 5);

			// create a new map marker at a specific coordinate
			Coordinate coordinate = new Coordinate(47.6062, -122.3321);
			MapMarkerDot marker = new MapMarkerDot(coordinate);

			// add the marker to the map
			mapViewer.addMapMarker(marker);

			// create a new JFrame and add the map viewer to it
			JFrame frame = new JFrame("Project 5 - Melissa Ng");
			frame.setSize(800, 600);
			frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

			// create the top panel with the JComboBox and JCheckBox
			JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
			animationTimeComboBox = new JComboBox<>(ANIMATION_TIMES);
			topPanel.add(animationTimeComboBox);
			topPanel.add(includeStopsCheckBox);
			frame.getContentPane().add(topPanel, BorderLayout.NORTH);
			playButton = new JButton("Play");
			arrowHead = ImageIO.read(new File("arrow.png"));
			playButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					mapViewer.removeAllMapPolygons();
					if (includeStopsCheckBox.isSelected()) {
						
						int index = animationTimeComboBox.getSelectedIndex();
						int time = 15;
						if (index == 1) {
							time = 30;
						}
						else if (index == 2) {
							time = 60;
						}
						else if (index == 3) {
							time = 90;
						}
						
						try {
							TripPoint.readFile("triplog.csv");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						
						List<TripPoint> trip = new ArrayList<TripPoint>(TripPoint.getTrip());
						
						int interval = time * 1000 / trip.size();
						long interval1 = interval;
						long delay = 0;
						
						timer = new Timer();
						List<Coordinate> coordinates = new ArrayList<>();
						Coordinate c = new Coordinate(trip.get(0).getLat(), trip.get(0).getLon());
						coordinates.add(c);
						trip.remove(0);


						
						TimerTask todo = new TimerTask() {
							public void run() {
							mapViewer.removeAllMapMarkers();
							Coordinate c1 = new Coordinate(trip.get(0).getLat(), trip.get(0).getLon());
							Coordinate c2 = new Coordinate(trip.get(1).getLat(), trip.get(1).getLon());
							
							//double angle = Math.atan2(trip.get(1).getLat() - trip.get(0).getLat(), trip.get(1).getLon() - trip.get(0).getLon());

							
//					        Graphics2D g2d = rotatedImage.createGraphics();
//							AffineTransform transform = new AffineTransform();
//							transform.rotate(angle);
//							arrowHead.setTransform(transform);
//							
//							double rotationRequired = Math.toRadians (angle);
//							double locationX = 100 / 2;
//							double locationY = 100 / 2;
//							AffineTransform tx = AffineTransform.getRotateInstance(rotationRequired, locationX, locationY);
//							AffineTransformOp op = new AffineTransformOp(tx, AffineTransformOp.TYPE_BILINEAR);

							
							
							BufferedImage arrowImage = null;
							try {
								arrowImage = ImageIO.read(new File("arrow.png"));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

					        // Create a new BufferedImage to draw the rotated arrow onto
					        BufferedImage rotatedImage = new BufferedImage(
					                arrowImage.getWidth(), arrowImage.getHeight(), arrowImage.getType());

					        // Rotate the arrow image
					        Graphics2D g2d = rotatedImage.createGraphics();
					        double angle = calculateAngle(c1, c2); // Example angle calculation
					        AffineTransform transform = new AffineTransform();
					        transform.translate(rotatedImage.getWidth()/2, rotatedImage.getHeight()/2);
					        transform.rotate(Math.toRadians(angle));
					        transform.translate(-rotatedImage.getWidth()/2, -rotatedImage.getHeight()/2);
					        g2d.drawImage(arrowImage, transform, null);
					        g2d.dispose();

					        // Create an IconMarker using the rotated arrow image
					        Image arrowIcon = rotatedImage;
					        

							
							IconMarker marker = new IconMarker(c1, arrowIcon);
							trip.remove(0);
							
							mapViewer.addMapMarker(marker);
							
							List<Coordinate> coordinates = new ArrayList<>();
							coordinates.add(c1);
					        coordinates.add(c2);
					        coordinates.add(c2);
					        MapPolygonImpl line = new MapPolygonImpl(coordinates);
					        line.setStroke(new BasicStroke(2));
					        line.setColor(Color.red);
					        line.setBackColor(null);
					        mapViewer.addMapPolygon(line);
							}
						};
						timer.scheduleAtFixedRate(todo, delay, interval1);
					}
					if (!includeStopsCheckBox.isSelected()) {
						int index = animationTimeComboBox.getSelectedIndex();
						int time = 15;
						if (index == 1) {
							time = 30;
						}
						else if (index == 2) {
							time = 60;
						}
						else if (index == 3) {
							time = 90;
						}
						
						try {
							TripPoint.readFile("triplog.csv");
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						try {
							TripPoint.h1StopDetection();
						} catch (IOException e1) {
							// TODO Auto-generated catch block
							e1.printStackTrace();
						}
						List<TripPoint> movingTrip = new ArrayList<TripPoint>(TripPoint.getMovingTrip());
						int interval = time * 1000 / movingTrip.size();
						long interval1 = interval;
						long delay = 0;
						
						timer = new Timer();
						List<Coordinate> coordinates = new ArrayList<>();
						Coordinate c = new Coordinate(movingTrip.get(0).getLat(), movingTrip.get(0).getLon());
						coordinates.add(c);
						movingTrip.remove(0);
						
						TimerTask todo = new TimerTask() {
							public void run() {
							mapViewer.removeAllMapMarkers();
							Coordinate c1 = new Coordinate(movingTrip.get(0).getLat(), movingTrip.get(0).getLon());
							Coordinate c2 = new Coordinate(movingTrip.get(1).getLat(), movingTrip.get(1).getLon());
							
							BufferedImage arrowImage = null;
							try {
								arrowImage = ImageIO.read(new File("arrow.png"));
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}

					        // Create a new BufferedImage to draw the rotated arrow onto
					        BufferedImage rotatedImage = new BufferedImage(
					                arrowImage.getWidth(), arrowImage.getHeight(), arrowImage.getType());

					        // Rotate the arrow image
					        Graphics2D g2d = rotatedImage.createGraphics();
					        double y;
					        double angle = calculateAngle(c1, c2); // Example angle calculation
					        AffineTransform transform = new AffineTransform();
					        transform.translate(rotatedImage.getWidth()/2, rotatedImage.getHeight()/2);
					        transform.rotate(Math.toRadians(angle));
					        transform.translate(-rotatedImage.getWidth()/2, -rotatedImage.getHeight()/2);
					        g2d.drawImage(arrowImage, transform, null);
					        g2d.dispose();

					        // Create an IconMarker using the rotated arrow image
					        Image arrowIcon = rotatedImage;

							
							IconMarker marker = new IconMarker(c1, arrowIcon);							
							movingTrip.remove(0);
							
							mapViewer.addMapMarker(marker);
							
							List<Coordinate> coordinates = new ArrayList<>();
							coordinates.add(c1);
					        coordinates.add(c2);
					        coordinates.add(c2);
					        MapPolygonImpl line = new MapPolygonImpl(coordinates);
					        line.setStroke(new BasicStroke(2));
					        line.setColor(Color.red);
					        line.setBackColor(null);
					        mapViewer.addMapPolygon(line);
					        
							}
						};
						
						timer.scheduleAtFixedRate(todo, delay, interval1);
					}
				}
			});
			topPanel.add(playButton);

			// add the map viewer to the center of the frame
			frame.getContentPane().add(mapViewer, BorderLayout.CENTER);
			frame.setVisible(true);

			mapViewer.setTileSource(new OsmTileSource.TransportMap());
		//});

		// Read file and call stop detection
		
		// Set up frame, include your name in the title

		// Set up Panel for input selections

		// Play Button

		// CheckBox to enable/disable stops
		includeStopsCheckBox.setSelected(false);
		includeStopsCheckBox.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					// Checkbox is checked
					includeStopsCheckBox.setSelected(true);

				} else {
					// Checkbox is unchecked
					includeStopsCheckBox.setSelected(false);

				}
			}
		});

		// ComboBox to pick animation time
		JComboBox<Integer> animationTimeComboBox = new JComboBox<>(new Integer[] { 15, 30, 60, 90 });
		animationTimeComboBox.setSelectedIndex(0); // select the first option by default

		// Add all to top panel

		// Set up mapViewer

		// Add listeners for GUI components

		// Set the map center and zoom level

		

	}

	// Animate the trip based on selections from the GUI components
}
