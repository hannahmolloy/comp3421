package unsw.graphics.world;

import java.io.IOException;

import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

/**
 * COMMENT: Comment Tree 
 *
 * @author malcolmr
 */
public class Tree {
	
	private TriangleMesh tree;
    private Point3D position;
    
    public Tree(float x, float y, float z) {
        position = new Point3D(x, y, z);
    }
    
    public Point3D getPosition() {
        return position;
    }
    
    public void draw(GL3 gl, CoordFrame3D frame) {
    	try {
			tree = new TriangleMesh("res/models/tree.ply", true);
			tree.init(gl);
			tree.draw(gl, frame);
		} catch (IOException e) {
			e.printStackTrace();
		}
    }
}
