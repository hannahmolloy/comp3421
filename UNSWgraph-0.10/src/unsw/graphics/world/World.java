package unsw.graphics.world;

import java.awt.Color;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import javafx.scene.Camera;
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
    private Camera3D camera;
    private Avatar dolphins;

    public World(Terrain terrain) {
    	super("Assignment 2", 800, 600);
        this.terrain = terrain;
    	dolphins = new Avatar(terrain);
    	camera = new Camera3D(terrain, dolphins);
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
		getWindow().addKeyListener(dolphins);
		
	}

	@Override
	public void display(GL3 gl) {
		super.display(gl);

		Matrix4 viewMatrix = Matrix4.rotationY(-camera.getCameraYRot())
    			.multiply(Matrix4.translation(-camera.getCameraPosition().getX(), -camera.getCameraPosition().getY(), -camera.getCameraPosition().getZ()));
    	
    	Matrix4 projMatrix = Matrix4.perspective(60, 1, 0.1f, 100);
        
    	Shader.setViewMatrix(gl, viewMatrix);
    	Shader.setProjMatrix(gl, projMatrix);
    	
		if(camera.isTorchOn()) {
			setBackground(Color.black);
			
			Vector3 torchPos = new Vector3(camera.getCameraPosition().getX(), camera.getCameraPosition().getY(), camera.getCameraPosition().getZ());
			System.out.println(camera.getCameraPosition().getX() + " "+ camera.getCameraPosition().getY() + " " + camera.getCameraPosition().getZ());
			
			Shader.setInt(gl,  "torch", 1);
			
			Shader.setVector3D(gl, "torchlightPos", torchPos);
			Shader.setColor(gl, "ambientIntensity", new Color(0.1f, 0.1f, 0.1f));
			
			// Set the material properties
			Shader.setColor(gl, "ambientCoeff", Color.white);
			Shader.setColor(gl, "diffuseCoeff", new Color(0.2f, 0.2f, 0.2f));
			Shader.setColor(gl, "specularCoeff", new Color(0.4f, 0.4f, 0.4f));
			Shader.setFloat(gl, "phongExp", 16f);
			Shader.setColor(gl, "torchlightIntensity", Color.white);
			
		} else {
			setBackground(Color.white);
			
			Shader.setInt(gl,  "torch", 0);
			
			Shader.setVector3D(gl, "sunlightPos", terrain.getSunlight());
			Shader.setColor(gl, "ambientIntensity", new Color(0.2f, 0.2f, 0.2f));
			
			// Set the material properties
			Shader.setColor(gl, "ambientCoeff", Color.WHITE);
			Shader.setColor(gl, "diffuseCoeff", new Color(0.6f, 0.6f, 0.6f));
			Shader.setColor(gl, "specularCoeff", new Color(0.4f, 0.4f, 0.4f));
			Shader.setFloat(gl, "phongExp", 16f);
			Shader.setColor(gl, "sunlightIntensity", Color.WHITE);
		}
		
    	try {
			terrain.draw(gl);
	    	if (camera.inThirdPerson()) dolphins.draw(gl);
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
}
