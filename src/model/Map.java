package model;
import java.awt.Color;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileReader;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilderFactory;

import com.sun.javafx.image.IntPixelGetter;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;

public class Map {

    public static final Color blockColor = new Color(255,255,0);

	private static final String mapDataXMLFileName = ".\\img\\map.xml";

	private static NodeList maps;
	private static int currentMap;
	
	private static String mapFileName;
	private static BufferedImage bufferedImage;
	private static Point startPoint;
	private static double startDirection;

	static {
		startPoint = new Point();
		currentMap = 0;
		try {
			Document mapXML = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(
					new InputSource(new FileReader(mapDataXMLFileName)));
			mapXML.getDocumentElement().normalize();
			maps = mapXML.getElementsByTagName("map");
	 		changeMapTo(0);           
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/* map selection methods */
	public static void changeToNextMap(){
		if(currentMap != maps.getLength() -1)
			changeMapTo(++currentMap);
	}
	public static void changeToPrevMap(){
		if(currentMap != 0)
			changeMapTo(--currentMap);
	}
	private static void changeMapTo(int index){
		try {
 	       for(Node child = maps.item(index).getFirstChild(); child!=null; child=child.getNextSibling()){
 	            if(child.getNodeName().equals("filename")) mapFileName = child.getTextContent();
 	            if(child.getNodeName().equals("startx")) startPoint.x = Integer.parseInt(child.getTextContent());
 	            if(child.getNodeName().equals("starty")) startPoint.y = Integer.parseInt(child.getTextContent());
 	            if(child.getNodeName().equals("direction")) startDirection = Integer.parseInt(child.getTextContent())*Math.PI /180;
 	        }
			bufferedImage = ImageIO.read(new File(mapFileName));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/* map selection methods*/


	public static Point getStartPoint(){return startPoint;}
	public static double getStartDirection(){return startDirection;}
	public static Color getColorValue(Point point) throws ArrayIndexOutOfBoundsException{
		return new Color(bufferedImage.getRGB(point.x, point.y));
	}
	

	public static double getBrightness(Point point) {
        try {
            Color color = new Color(bufferedImage.getRGB(point.x, point.y));
            return Color.RGBtoHSB(color.getRed(), color.getGreen(), color.getBlue(), null)[2];
        } catch (ArrayIndexOutOfBoundsException e) {
            return 0;
        }
    }
    public static int getDist(Point pos, double sensorDirection) {
	    int dist = 0;
	    Color color;
	    try{
	        while(true){
	            color = getColorValue(new Point(
	                    (int)(pos.x + dist*Math.cos(sensorDirection)),
                        (int)(pos.y + dist*Math.sin(sensorDirection))));

	            if(color.equals(blockColor))
	                return dist;
	            else dist++;
            }
        }catch (ArrayIndexOutOfBoundsException e){
	        return Integer.MAX_VALUE;
        }
    }
	public static BufferedImage getBufferedImage(){return bufferedImage;}

}
