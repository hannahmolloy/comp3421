package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Line3D;
import unsw.graphics.geometry.LineStrip2D;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Road 
 *
 * @author malcolmr
 */
public class Road {

    private List<Point2D> controlPoints;
    private float width;
    private TriangleMesh mesh;
    private Texture tex;
    private Integer numSlices;
    private float altitude;
    private List<Point3D> points;
    
    /**
     * Create a new road with the specified spine 
     *
     * @param width
     * @param spine
     */
    public Road(float width, List<Point2D> spine, float altitude) {
        this.width = width;
        this.controlPoints = spine;
        this.altitude = altitude;
    	this.points = new ArrayList<Point3D>();
        numSlices = 10;
        mesh = createMesh();
    }

    private TriangleMesh createMesh() {
    	float t;

    	List<Point3D> extendedPoints = new ArrayList<Point3D>();
    	List<Integer> indices = new ArrayList<Integer>();
    	List<Point2D> texCoords = new ArrayList<Point2D>();
    	
    	for (int segment = 0; segment < size(); segment++) {
    		t = 0;
    		// for each segment slice the line into 10 slices and 
//    		for (int slices = 0; slices < numSlices; slices++) {
//    			t = (float)slices/numSlices;
//    			Point2D p = point(t);
//    			Point3D pLeft = new Point3D(p.getX() - width, altitude, p.getY());
//    			Point3D pRight = new Point3D(p.getX() + width,altitude,  p.getY());
//
//    			points.add(pLeft);
//    			points.add(new Point3D(p.getX(), altitude, p.getY()));
//    			points.add(pRight);
//
//    			if (slices < numSlices - 1) {
//    				int topLeft = segment + (slices+1)*3;
//    				int topMiddle = segment + (slices+1)*3 + 1;			//	tL		tM		tR
//    				int topRight = segment + (slices+1)*3 + 2;			//		+---+---+
//    				int bottomLeft = segment + slices*3;				//		| / | / |
//    				int bottomMiddle = segment + slices*3 + 1;			//		+---+---+
//    				int bottomRight = segment + slices*3 + 2;			//	bL		bM		bR
//    				
//    				indices.add(new Integer(bottomLeft));
//    				indices.add(new Integer(topLeft));
//    				indices.add(new Integer(topMiddle));
//    				
//    				indices.add(new Integer(bottomLeft));
//    				indices.add(new Integer(topMiddle));
//    				indices.add(new Integer(bottomMiddle));
//    				
//    				indices.add(new Integer(bottomMiddle));
//    				indices.add(new Integer(topMiddle));
//    				indices.add(new Integer(topRight));
//    				
//    				indices.add(new Integer(topRight));
//    				indices.add(new Integer(bottomRight));
//    				indices.add(new Integer(bottomMiddle));
//    			}
//    		}
    		
    		for (int slices = 0; slices < numSlices; slices++) {
    			t = (float)slices/numSlices;
    			Point2D p = point(t);
    			points.add(new Point3D (p.getX(), altitude, p.getY()));
    			//points.add(p);
    			System.out.println(t + " " + p.getX() + " " + p.getY());
    		}
    		
    		int index = 0;
    		for (Point3D p : points) {

    			Point3D p1 = points.get(index+1);
    			
    			Vector3 tangent = new Vector3(p1.getX() - p.getX(), altitude, p1.getY() - p.getY());
    			
    			Vector3 normal = tangent.normalize();	// fix this
    			
    			Point3D pLeft = p.translate(normal);
    			Point3D pRight = p.translate(normal.negate());
    			
    			extendedPoints.add(pLeft);
    			extendedPoints.add(pRight);	

    			indices.add(index*2);
    			indices.add(index*2 + 1);
    			indices.add((index+1)*2);

    			indices.add(index*2);
    			indices.add((index+1)*2);
    			indices.add((index+1)*2 + 1);
    			
    			index++;
    		}
    	}

		return new TriangleMesh(extendedPoints, indices, true);
	}

	/**
     * The width of the road.
     * 
     * @return
     */
    public double width() {
        return width;
    }
    
    /**
     * Get the number of segments in the curve
     * 
     * @return
     */
    public int size() {
        return controlPoints.size() / 3;
    }

    /**
     * Get the specified control point.
     * 
     * @param i
     * @return
     */
    public Point2D controlPoint(int i) {
        return controlPoints.get(i);
    }
    
    /**
     * Get a point on the spine. The parameter t may vary from 0 to size().
     * Points on the kth segment take have parameters in the range (k, k+1).
     * 
     * @param t
     * @return
     */
    public Point2D point(float t) {
        int i = (int)Math.floor(t);
        t = t - i;
        
        i *= 3;
        
        Point2D p0 = controlPoints.get(i++);
        Point2D p1 = controlPoints.get(i++);
        Point2D p2 = controlPoints.get(i++);
        Point2D p3 = controlPoints.get(i++);
        

        float x = b(0, t) * p0.getX() + b(1, t) * p1.getX() + b(2, t) * p2.getX() + b(3, t) * p3.getX();
        float y = b(0, t) * p0.getY() + b(1, t) * p1.getY() + b(2, t) * p2.getY() + b(3, t) * p3.getY();        
        
        return new Point2D(x, y);
    }
    
    /**
     * Calculate the Bezier coefficients
     * 
     * @param i
     * @param t
     * @return
     */
    private float b(int i, float t) {
        
        switch(i) {
        
        case 0:
            return (1-t) * (1-t) * (1-t);

        case 1:
            return 3 * (1-t) * (1-t) * t;
            
        case 2:
            return 3 * (1-t) * t * t;

        case 3:
            return t * t * t;
        }
        
        // this should never happen
        throw new IllegalArgumentException("" + i);
    }
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	//tex = new Texture(gl, "res/textures/road.bmp","bmp", true);
    	Shader.setPenColor(gl, Color.YELLOW);
    	mesh.init(gl);
    	mesh.draw(gl);
//    	LineStrip2D line = new LineStrip2D(points);
//    	line.draw(gl);
    }
}
