package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.scene.Camera3D;
import unsw.graphics.geometry.Point3D;

import com.jogamp.newt.event.KeyAdapter;
import com.jogamp.newt.event.KeyEvent;

/**
 * COMMENT: Comment Game 
 *
 * @author malcolmr
 */
public class World extends Application3D {

    private Terrain terrain;
    private Point3D cameraPos;
    private float cameraRot;
    private Camera3D camera;
    private float zoom;
    private float aspectRatio;
    private int rotateX, rotateY, rotateZ;

    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        aspectRatio = 1;
    	cameraPos = new Point3D(3,5,15);
    	zoom = 1.0f;
    	rotateX = 0;
    	rotateY = 0;
    	rotateZ = 0;
    	camera = new Camera3D(terrain, cameraPos);
    }
   
    /**
     * Load a level file and display it.
     * 
     * @param args - The first argument is a level file in JSON format
     * @throws FileNotFoundException
     */
    public static void main(String[] args) throws FileNotFoundException {
        Terrain terrain = LevelIO.load(new File(args[0]));
        World world = new World(terrain);
        world.start();
    }
    
    @Override
    /**
     * Have not properly implemented the camera and movement but this is the current standby, 
     * implemented this at the start and then forgot it still needed to be done properly, 
     * will complete for Milestone 2 
     */
	public void init(GL3 gl) {
		super.init(gl);
		terrain.init(gl);
		//Shader shader = new Shader(gl, "shaders/vertex_phong.glsl", "shaders/fragment_phong.glsl");
		Shader shader = new Shader(gl, "shaders/vertex_tex_phong.glsl", "shaders/fragment_tex_phong.glsl");
		shader.use(gl);
		
		getWindow().addKeyListener(camera);
		
//		getWindow().addKeyListener(new KeyAdapter() {
//          @Override
//          public void keyPressed(KeyEvent ev) {
//        	  
//              if (ev.getKeyCode() == KeyEvent.VK_LEFT)
//                  cameraPos = cameraPos.translate(-0.1f/zoom, 0, 0);
//              else if (ev.getKeyCode() == KeyEvent.VK_RIGHT)
//                  cameraPos = cameraPos.translate(0.1f/zoom, 0, 0);
//              else if (ev.getKeyCode() == KeyEvent.VK_UP)
//                  cameraPos = cameraPos.translate(0, 0.1f/zoom, 0);
//              else if (ev.getKeyCode() == KeyEvent.VK_DOWN)
//                  cameraPos = cameraPos.translate(0, -0.1f/zoom, 0);
//              else if (ev.getKeyCode() == KeyEvent.VK_SPACE)
//                  zoom *= 1.05;
//              else if (ev.getKeyCode() == KeyEvent.VK_Z)
//            	  zoom /= 1.05;
//              else if (ev.getKeyCode() == KeyEvent.VK_A)
//            	  rotateY += 10;
//              else if (ev.getKeyCode() == KeyEvent.VK_D)
//            	  rotateY -= 10;
//              else if (ev.getKeyCode() == KeyEvent.VK_W)
//            	  rotateX -= 10;
//              else if (ev.getKeyCode() == KeyEvent.VK_S)
//            	  rotateX += 10;
//              else if (ev.getKeyCode() == KeyEvent.VK_E)
//            	  rotateZ -= 10;
//              else if (ev.getKeyCode() == KeyEvent.VK_R)
//            	  rotateZ += 10;
//          }
//		});
		
	}

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		
		updateCamera();
		
		//terrain.init(gl);
		Matrix4 viewMatrix = Matrix4.scale(1/aspectRatio, 1, 1)
				.multiply(Matrix4.scale(1/zoom, 1/zoom, 1))
				//.multiply(Matrix4.rotationX(-rotateX))
				.multiply(Matrix4.rotationY(-cameraRot))
				//.multiply(Matrix4.rotationZ(-rotateZ))
    			.multiply(Matrix4.translation(-cameraPos.getX(), -cameraPos.getY() + 1, -cameraPos.getZ()));
    	
    	Matrix4 projMatrix = Matrix4.perspective(60, 1, 1, 100);
        
    	Shader.setViewMatrix(gl, viewMatrix);
    	Shader.setProjMatrix(gl, projMatrix);
    	
		Shader.setVector3D(gl, "lightPos", terrain.getSunlight());
		Shader.setColor(gl, "lightIntensity", Color.WHITE);
		Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
		
		 // Set the material properties
		Shader.setColor(gl, "ambientCoeff", Color.WHITE);
		Shader.setColor(gl, "diffuseCoeff", new Color(0.5f, 0.5f, 0.5f));
		Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
		Shader.setFloat(gl, "phongExp", 16f);
		Shader.setColor(gl, "sunlightIntensity", Color.YELLOW);
		
		Shader.setPenColor(gl, Color.GREEN);
		
    	terrain.draw(gl);
	}

	@Override
	public void destroy(GL3 gl) {
		super.destroy(gl);
		
	}
	
	@Override
	public void reshape(GL3 gl, int width, int height) {
        super.reshape(gl, width, height);
        Shader.setProjMatrix(gl, Matrix4.perspective(60, width/(float)height, 1, 100));
	}
	
	public void updateCamera() {
		this.cameraPos = camera.getCameraPosition();
		this.cameraRot = camera.getCameraYRot();
	}
}
