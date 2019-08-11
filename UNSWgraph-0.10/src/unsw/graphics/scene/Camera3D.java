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
import unsw.graphics.world.Avatar;
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
public class Camera3D implements KeyListener{
    
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
    private Avatar avatar;
    
//    public Camera3D(SceneObject parent, Point3D position, Terrain terrain) {
//    	super();
//        this.terrain = terrain;
//        this.position = position;
//        near = 1.0f;
//        far = 100.0f;
//        fieldOfView = 60.0f;
//    }

    public Camera3D(Terrain terrain, Avatar avatar) {
    	super();
        this.avatar = avatar;
        this.terrain = terrain;
        this.position = getCameraPosition();
        near = 1.0f;
        far = 100.0f;
        fieldOfView = 60.0f;
        zoom = 0.1f;
        thirdPerson = false;
        distance = 2;
        cameraDirection = new Vector3(0,0,1);
    }

    public void reshape(int width, int height) {
        myAspectRatio = (1f * width) / height;            
    }

    public float getAspectRatio() {
        return myAspectRatio;
    }
    
	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();

		if(key == KeyEvent.VK_3) {
			changeThirdPerson();
		}
	}

	
	private void changeThirdPerson() {
		if (!thirdPerson) {
			distance = 3;
			moveBackward();
			moveBackward();
			this.position.translate(0, distance, 0);
		} else {
			distance = 1;
			moveForward();
			moveForward();
			this.position.translate(0, distance, 0);
		}
		thirdPerson = !thirdPerson;
	}
	
	private void moveForward() {
		float x = 0.25f * (float) Math.sin(-1*Math.toRadians(avatar.getRotation()));
		float z = 0.25f * (float) (-1*Math.cos(Math.toRadians(avatar.getRotation())));
		float y = terrain.altitude(x + position.getX(), z + position.getZ())
				- position.getY() + distance;
		this.position = this.position.translate(x, y, z);
	}

	private void moveBackward() {
		float x = 0.25f * (float) Math.sin(Math.toRadians(avatar.getRotation()));
		float z = 0.25f * (float) Math.cos(Math.toRadians(avatar.getRotation()));
		float y = terrain.altitude(x + position.getX(), z + position.getZ())
				- position.getY() + distance;
		this.position = this.position.translate(x, y, z);
	}
	
	public Point3D getCameraPosition() {
		return thirdPerson ? getThirdPersonPosition() : getFirstPersonPosition();
	}
	
	private Point3D getFirstPersonPosition() {
		position = avatar.getPosition();
		moveForward();
		moveForward();
		return this.position;
	}

	private Point3D getThirdPersonPosition() {
		position = avatar.getPosition();
		moveBackward();
		moveBackward();
		moveBackward();
		
		return this.position;
	}

	public float getCameraYRot() {
		yRotation = avatar.getRotation();
		return this.yRotation;
	}
	
	public float getCameraXRot() {
		return this.xRotation;
	}
	

	private void lookUp() {
		this.xRotation = xRotation + 5.0f;
	}
	
	private void lookDown() {
		this.xRotation = xRotation - 5.0f;
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