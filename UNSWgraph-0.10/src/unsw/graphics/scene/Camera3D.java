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
    
    private Point3D position;
    private float yRotation;
    private float xRotation;
    private Vector3 cameraDirection;
    
    private Terrain terrain;
    private boolean torch;
    private boolean thirdPerson;
    private int distance;
    private Avatar avatar;

    public Camera3D(Terrain terrain, Avatar avatar) {
    	super();
        this.avatar = avatar;
        this.terrain = terrain;
        this.position = getCameraPosition();
        thirdPerson = false;
        distance = 2;
        cameraDirection = new Vector3(0,0,-1);
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
		if(key == KeyEvent.VK_T) {
			torch = !torch;
		}
		if(key == KeyEvent.VK_LEFT) {
			turnLeft();
		}
		if(key == KeyEvent.VK_RIGHT) {
			turnRight();
		}
	}

	private void changeThirdPerson() {
		if (!thirdPerson) {
			distance = 3;
		} else {
			distance = 1;
		}
		thirdPerson = !thirdPerson;
	}

	private void moveBackward() {
		float x = (float) Math.sin(Math.toRadians(avatar.getRotation()));
		float z = (float) Math.cos(Math.toRadians(avatar.getRotation()));
		float y = terrain.altitude(x + position.getX(), z + position.getZ())
				- position.getY() + distance;
		this.position = this.position.translate(x, y, z);
	}
	
	private void turnLeft() {
		this.yRotation = avatar.getRotation();
		cameraDirection = new Vector3(-1*(float)Math.sin(Math.toRadians(yRotation)), 0, -1*(float)Math.cos(Math.toRadians(yRotation)));
	}
	
	private void turnRight() {
		this.yRotation = avatar.getRotation();
		cameraDirection = new Vector3(-1*(float)Math.sin(Math.toRadians(yRotation)), 0, -1*(float)Math.cos(Math.toRadians(yRotation)));
	}
	
	public Point3D getCameraPosition() {
		return thirdPerson ? getThirdPersonPosition() : getFirstPersonPosition();
	}
	
	private Point3D getFirstPersonPosition() {
		position = avatar.getPosition();
		
		return this.position;
	}

	private Point3D getThirdPersonPosition() {
		position = avatar.getPosition();
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
	
	public boolean inThirdPerson() {
		return thirdPerson;
	}
	
	public boolean isTorchOn() {
		return this.torch;
	}
	
	public Vector3 getCameraDir() {
		cameraDirection = new Vector3(getCameraPosition().getX(), getCameraPosition().getY(), getCameraPosition().getZ());
		return this.cameraDirection;
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
		// TODO Auto-generated method stub
		
	}
}