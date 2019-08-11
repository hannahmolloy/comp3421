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
public class Camera3D extends SceneObject implements KeyListener{
    
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
    private float xRotation;
    private Vector3 cameraDirection;
    private float zoom;
    
    private Terrain terrain;
    private boolean torch;
    private boolean thirdPerson;
    private int distance;
    
//    public Camera3D(SceneObject parent, Point3D position, Terrain terrain) {
//    	super();
//        this.terrain = terrain;
//        this.position = position;
//        near = 1.0f;
//        far = 100.0f;
//        fieldOfView = 60.0f;
//    }

    public Camera3D(Terrain terrain, Point3D position) {
    	super();
        this.terrain = terrain;
        this.position = position;
        near = 1.0f;
        far = 100.0f;
        fieldOfView = 60.0f;
        zoom = 0.1f;
        thirdPerson = false;
        distance = 2;
        cameraDirection = new Vector3(0,0,1);
    }

    public void setView(GL3 gl) {
        // TODO compute a view transform to account for the cameras aspect ratio
        
        // TODO apply further transformations to account for the camera's global position, 
        // rotation and scale
        
        // TODO set the view matrix to the computed transform
    	
    	CoordFrame3D frame = CoordFrame3D.identity();
		frame = frame.rotateY(-yRotation);
		frame = frame.translate(-1*position.getX(),-1*position.getY() + 0.05f,-1*position.getZ());
		
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
        Matrix3 mat = Matrix3.translation(this.getPosition())
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
		if(key == KeyEvent.VK_E) {
			zoomIn();
		}
		if(key == KeyEvent.VK_D) {
			zoomOut();
		}
		if(key == KeyEvent.VK_3) {
			changeThirdPerson();
		}
		if(key == KeyEvent.VK_T) {
			System.out.println("torch");
			torch = !torch;
		}
		
	}

	
	private void changeThirdPerson() {
		if (!thirdPerson) {
			distance = 4;
			moveBackward();
			moveBackward();
			this.position.translate(0, distance, 0);
		} else {
			distance = 2;
			moveForward();
			moveForward();
			this.position.translate(0, distance, 0);
		}
		thirdPerson = !thirdPerson;
	}
	
	private void moveForward() {
		float x = 0.25f * (float) Math.sin(-1*Math.toRadians(yRotation));
		float z = 0.25f * (float) (-1*Math.cos(Math.toRadians(yRotation)));
		float y = terrain.altitude(x + position.getX(), z + position.getZ())
				- position.getY() + distance;
		this.position = this.position.translate(x, y, z);
	}

	private void moveBackward() {
		float x = 0.25f * (float) Math.sin(Math.toRadians(yRotation));
		float z = 0.25f * (float) Math.cos(Math.toRadians(yRotation));
		float y = terrain.altitude(x + position.getX(), z + position.getZ())
				- position.getY() + distance;
		this.position = this.position.translate(x, y, z);
	}
	
	private void turnLeft() {
		this.yRotation = yRotation + 5.0f;
		cameraDirection = new Vector3(-1*(float)Math.sin(Math.toRadians(yRotation)), 0, -1*(float)Math.cos(Math.toRadians(yRotation)));
		System.out.println(cameraDirection.getX() + " " + cameraDirection.getY() + " " + cameraDirection.getZ());
	}
	
	private void turnRight() {
		this.yRotation = yRotation - 5.0f;
		cameraDirection = new Vector3(-1*(float)Math.sin(Math.toRadians(yRotation)), 0, -1*(float)Math.cos(Math.toRadians(yRotation)));
		System.out.println(cameraDirection.getX() + " " + cameraDirection.getY() + " " + cameraDirection.getZ());
		
	}

	
	private void zoomIn() {
		this.zoom = this.zoom + 0.05f;
	}
	
	private void zoomOut() {
		if(this.zoom - 0.05f <= 0.1f) {
			this.zoom = 0.1f;
		}
		else {
			this.zoom = this.zoom - 0.05f;
		}	
	}
	
	public Point3D getCameraPosition() {
		return this.position;
	}
	
	public float getCameraYRot() {
		return this.yRotation;
	}
	public float getCameraXRot() {
		return this.xRotation;
	}
	public float getZoom() {
		return this.zoom;
	}
	public boolean getTorch() {
		return this.torch;
	}
	public Vector3 getCameraDir() {
		return this.cameraDirection;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}