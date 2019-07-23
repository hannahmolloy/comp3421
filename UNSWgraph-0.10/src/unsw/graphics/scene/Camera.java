package unsw.graphics.scene;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix3;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.geometry.Point2D;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.world.Terrain;

/**
 * The camera is a SceneObject that can be moved, rotated and scaled like any other, as well as
 * attached to any parent in the scene tree.
 * 
 * TODO: You need to implement the setView() method.
 *
 * @author malcolmr
 * @author Robert Clifton-Everest
 */
public class Camera extends SceneObject implements KeyListener{
    
    /**
     * The aspect ratio is the ratio of the width of the window to the height.
     */
    private float myAspectRatio;

    private float near;
    private float far;
    private float fieldOfView;
    
    private int width;
    private int height;
    
    private Point3D position;
    private float yRotation;
    
    private Terrain terrain;
    
    public Camera(SceneObject parent) {
    	super();
        this.terrain = terrain;
        this.position = position;
        near = 1.0f;
        far = 100.0f;
        fieldOfView = 60.0f;
    }

    public Camera(Terrain terrain, Point3D position) {
    	super();
        this.terrain = terrain;
        this.position = position;
        near = 1.0f;
        far = 100.0f;
        fieldOfView = 60.0f;
    }

    public void setView(GL3 gl) {
        // TODO compute a view transform to account for the cameras aspect ratio
        
        // TODO apply further transformations to account for the camera's global position, 
        // rotation and scale
        
        // TODO set the view matrix to the computed transform
    	
    	// Matrix3 matrix = Matrix3.scale(1/myAspectRatio, 1);
        
    	// float myScale = getGlobalScale();
    	// float myAngle = getGlobalRotation();
    	// Point2D myPos = getGlobalPosition();
    	
    	
    	// matrix = matrix.multiply(Matrix3.scale(1/myScale, 1/myScale))
    	// 		.multiply(Matrix3.rotation(-myAngle))
    	// 		.multiply(Matrix3.translation(-myPos.getX(), -myPos.getY()));
    	
    	// /*
    	//  * TODO for 3d need to rotate around all three axis
    	//  */

     //    Shader.setViewMatrix(gl, matrix);
        	
    	CoordFrame3D frame = CoordFrame3D.identity();
		frame = frame.rotateY(-yRotation);
		frame = frame.translate(-1*position.getX(),-1*position.getY(),-1*position.getZ());
		
		Shader.setViewMatrix(gl, frame.getMatrix());
    	
//    	float top, bottom, left, right;
//    	
//    	if (width > height) {
//			float aspect = (1.0f * width) / height;
//			top = 1.0f;
//			bottom = -1.0f;
//			left = -aspect;
//			right = aspect;
//		} else {
//			float aspect = (1.0f * height) / width;
//			top = aspect;
//			bottom = -aspect;
//			left = -1.0f;
//			right = 1.0f;
//		}

    }

    public void reshape(int width, int height) {
        myAspectRatio = (1f * width) / height;            
    }

    /**
     * Transforms a point from camera coordinates to world coordinates. Useful for things like mouse
     * interaction
     * 
     * @param x
     * @param y
     * @return
     */
    public Point2D fromView(float x, float y) {
        Matrix3 mat = Matrix3.translation(getGlobalPosition())
                .multiply(Matrix3.rotation(getGlobalRotation()))
                .multiply(Matrix3.scale(getGlobalScale(), getGlobalScale()))
                .multiply(Matrix3.scale(myAspectRatio, 1));
        return mat.multiply(new Vector3(x,y,1)).asPoint2D();
    }

    public float getAspectRatio() {
        return myAspectRatio;
    }

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		
		if(key == KeyEvent.VK_UP) {
			moveForward();
		}
		if(key == KeyEvent.VK_DOWN) {
			moveBackward();
		}
		if(key == KeyEvent.VK_LEFT) {
			turnLeft();
		}
		if(key == KeyEvent.VK_RIGHT) {
			turnRight();
		}
		
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
	
	private void moveForward() {
		float x = (float) Math.sin(-1*Math.toRadians(yRotation));
		float z = (float) (-1*Math.cos(-1*Math.toRadians(yRotation)));
		float y = terrain.altitude(x + position.getX(), z + position.getY())
				- position.getY() + 1;
		position.translate(x, y, z);
	}

	private void moveBackward() {
		float x = (float) Math.sin(-1*Math.toRadians(yRotation));
		float z = (float) Math.cos(-1*Math.toRadians(yRotation));
		float y = terrain.altitude(x + position.getX(), z + position.getY())
				- position.getY() + 1;
		position.translate(x, y, z);
	}
	
	private void turnLeft() {
		yRotation = yRotation + 5.0f;
	}
	
	private void turnRight() {
		yRotation = yRotation - 5.0f;
		
	}
}
