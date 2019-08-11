package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.Application3D;
import unsw.graphics.CoordFrame3D;
import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Vector3;
import unsw.graphics.scene.Camera3D;
import unsw.graphics.geometry.Point3D;

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
    //private Avatar dolphins;

    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
        aspectRatio = 1;
    	cameraPos = new Point3D(5,5,15);
    	zoom = 1.0f;
    	camera = new Camera3D(terrain, cameraPos);
    	//dolphins = new Avatar();
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
		Shader shader = new Shader(gl, "shaders/vertex_tex_night.glsl", "shaders/fragment_tex_night.glsl");
		shader.use(gl);
		
		getWindow().addKeyListener(camera);
		
	}

	@Override
	public void display(GL3 gl) {
		super.display(gl);
		
		updateCamera();
		
		Matrix4 viewMatrix = Matrix4.scale(1/aspectRatio, 1, 1)
				.multiply(Matrix4.scale(1/zoom, 1/zoom, 1))
				.multiply(Matrix4.rotationY(-cameraRot))
    			.multiply(Matrix4.translation(-cameraPos.getX(), -cameraPos.getY() + 1, -cameraPos.getZ()));
		
		Matrix4 projMatrix = Matrix4.perspective(60, 1, 0.1f, 100);
		
		Shader.setViewMatrix(gl, viewMatrix);
    	Shader.setProjMatrix(gl, projMatrix);
    	
		if(camera.getTorch()) {
			
			setBackground(Color.black);
			
			Vector3 torchPos = new Vector3(camera.getCameraPosition().getX(), camera.getCameraPosition().getY(), camera.getCameraPosition().getZ());
			System.out.println(camera.getCameraPosition().getX() + " "+ camera.getCameraPosition().getY() + " " + camera.getCameraPosition().getZ());
			
			Shader.setInt(gl,  "torch", 1);
			
			Shader.setVector3D(gl, "lightPos", torchPos);
			Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
			
			 // Set the material properties
			Shader.setColor(gl, "ambientCoeff", Color.white);
			Shader.setColor(gl, "diffuseCoeff", new Color(0.3f, 0.3f, 0.3f));
			Shader.setColor(gl, "specularCoeff", new Color(0.5f, 0.5f, 0.5f));
			Shader.setFloat(gl, "phongExp", 16f);
			Shader.setColor(gl, "torchlightIntensity", Color.white);
			
		}
		else {
			
			setBackground(Color.white);
			
			Shader.setInt(gl,  "torch", 0);
			
			Shader.setVector3D(gl, "lightPos", terrain.getSunlight());
			Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
			
			 // Set the material properties
			Shader.setColor(gl, "ambientCoeff", Color.WHITE);
			Shader.setColor(gl, "diffuseCoeff", new Color(0.6f, 0.6f, 0.6f));
			Shader.setColor(gl, "specularCoeff", new Color(0.8f, 0.8f, 0.8f));
			Shader.setFloat(gl, "phongExp", 16f);
			Shader.setColor(gl, "sunlightIntensity", Color.YELLOW);
		}
		
    	try {
			terrain.draw(gl);
	    	//dolphins.draw(gl, CoordFrame3D.identity().translate(5, 1, 5).rotateX(-90).scale(0.003f, 0.003f, 0.003f));
		} catch (IOException e) {
			e.printStackTrace();
		}
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
