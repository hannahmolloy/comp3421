package unsw.graphics.world;

import java.util.ArrayList;
import java.util.List;

import com.jogamp.opengl.GL3;

import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;



/**
 * COMMENT: Comment HeightMap 
 *
 * @author malcolmr
 */
public class Terrain {

    private int width;
    private int depth;
    private float[][] altitudes;
    private List<Tree> trees;
    private List<Road> roads;
    private Vector3 sunlight;
    private TriangleMesh mesh;

    /**
     * Create a new terrain
     *
     * @param width The number of vertices in the x-direction
     * @param depth The number of vertices in the z-direction
     */
    public Terrain(int width, int depth, Vector3 sunlight) {
        this.width = width;
        this.depth = depth;
        altitudes = new float[width][depth];
        trees = new ArrayList<Tree>();
        roads = new ArrayList<Road>();
        this.sunlight = sunlight;
    }
    
    public void init(GL3 gl) {
    	mesh = createMesh();
    	mesh.init(gl);
    }

	public List<Tree> trees() {
        return trees;
    }

    public List<Road> roads() {
        return roads;
    }

    public Vector3 getSunlight() {
        return sunlight;
    }

    /**
     * Set the sunlight direction. 
     * 
     * Note: the sun should be treated as a directional light, without a position
     * 
     * @param dx
     * @param dy
     * @param dz
     */
    public void setSunlightDir(float dx, float dy, float dz) {
        sunlight = new Vector3(dx, dy, dz);      
    }

    /**
     * Get the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public double getGridAltitude(int x, int z) {
        return altitudes[x][z];
    }

    /**
     * Set the altitude at a grid point
     * 
     * @param x
     * @param z
     * @return
     */
    public void setGridAltitude(int x, int z, float h) {
        altitudes[x][z] = h;
    }

    /**
     * Get the altitude at an arbitrary point. 
     * Non-integer points should be interpolated from neighbouring grid points
     * 
     * @param x
     * @param z
     * @return
     */
    public float altitude(float x, float z) {
    	// ignore if out of the bounds 
        float altitude = 0;
    	
    	if((x > this.width - 1 || x < 0) || (z < 0) || z > this.depth - 1){
    		return altitude;
    	}
    	// enclose the point in a square made of the floors and ceilings
        int xmin = (int) Math.floor(x);
        int xmax = (int) Math.ceil(x);
        int zmin = (int) Math.floor(z);
        int zmax = (int) Math.ceil(z);
        float hypotenuse = xmin + zmax - z;
        
        // on an integer/vertex, so return
        if(xmin == xmax && zmin == zmax) {
        	return (float) getGridAltitude(xmin, zmin);
        }
        
        // else linear interpolation to calculate the altitude 
        if(xmin == x || xmax == x) {
        	altitude = lerpZ(z, zmin, zmax, x, x);
        } else if(zmin == z || zmax == z) {
        	altitude = lerpX(x, xmin, xmax, z, z);
        } else if(x < hypotenuse) {
        	altitude = blerp(x, (float)xmin, (float)xmin, (float)xmax, z, 
        			(float)zmax, (float)zmin, (float)zmin, hypotenuse);
        } else {
        	altitude = blerp(x, (float)xmax, (float)xmax, (float)xmin, z, 
        			(float)zmin, (float) zmax, (float) zmax, hypotenuse);
        }
		
        return altitude;
    }
    
    // linear interpolation with a given X
    private float lerpX(float x, float x1, float x2, float z1, float z2) {
        return (float) (((x - x1) / (x2 - x1)) * getGridAltitude((int)x2, (int)z2) +
          ((x2 - x) / (x2 - x1)) * getGridAltitude((int)x1, (int)z1));
    }
    
    // linear interpolation with a given Z
    private float lerpZ(float z, float z1, float z2, float x1, float x2) {
        return (float) (((z - z1) / (z2 - z1)) * getGridAltitude((int)x2, (int)z2) +
          ((z2 - z) / (z2 - z1)) * getGridAltitude((int)x1, (int)z1));
    }
    
    // bilinear interpolation
    private float blerp(float x, float x1, float x2, float x3,
            float z, float z1, float z2, float z3, float hypotenuse) {
    	return ((x - x1) / (hypotenuse - x1)) * lerpZ(z, z1, z3, x1, x3) +
    			((hypotenuse - x) / (hypotenuse - x1)) * lerpZ(z, z1, z2, x1, x2);
    }


    /**
     * Add a tree at the specified (x,z) point. 
     * The tree's y coordinate is calculated from the altitude of the terrain at that point.
     * 
     * @param x
     * @param z
     */
    public void addTree(float x, float z) {
        float y = altitude(x, z);
        Tree tree = new Tree(x, y, z);
        trees.add(tree);
    }


    /**
     * Add a road. 
     * 
     * @param x
     * @param z
     */
    public void addRoad(float width, List<Point2D> spine) {
        Road road = new Road(width, spine);
        roads.add(road);        
    }

    
    /*
     * create a function that creates the triangle mesh
     */
    private TriangleMesh createMesh() {
    	List<Point3D> points = new ArrayList<Point3D>();
    	List<Integer> indices = new ArrayList<Integer>();
    	float row;
    	float col;
    	
    	for (row = 0; row < depth; row++) {
    		for (col = 0; col < width; col++) {
    			/* for each square of points 
    			 * two triangles need to be made
    			 * 	topLeft  	topRight
						   +-----+
						   |    /|
						   |  /  |
						   |/    |
						   +-----+
					bottomLeft	 bottomRight
    			 */

    			points.add(new Point3D(row, (float)altitude(row, col), col));
    			
    			if (row < depth - 1 && col < width - 1) {
    				int topLeft = (int) (row * width + col);
        			int topRight = (int) (row * width + col + 1);
        			int bottomLeft = (int) ((row + 1) * width + col);
        			int bottomRight = (int) ((row + 1) * width + col + 1);
        			
        			indices.add(new Integer(topLeft));
        			indices.add(new Integer(bottomLeft));
        			indices.add(new Integer(topRight));
        			
        			indices.add(new Integer(bottomLeft));
        			indices.add(new Integer(bottomRight));
        			indices.add(new Integer(topRight));
    			}
    		}
    	}

		return new TriangleMesh(points, indices, true);
	}
    
    public void draw(GL3 gl) {
    	mesh.draw(gl);
    }
}
