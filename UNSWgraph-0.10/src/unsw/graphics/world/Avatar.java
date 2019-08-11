package unsw.graphics.world;

import java.awt.Color;
import java.io.IOException;

import com.jogamp.newt.event.KeyEvent;
import com.jogamp.newt.event.KeyListener;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL3;

import unsw.graphics.CoordFrame3D;
import unsw.graphics.Shader;
import unsw.graphics.Texture;
import unsw.graphics.geometry.Point3D;
import unsw.graphics.geometry.TriangleMesh;

public class Avatar implements KeyListener {
	private TriangleMesh dolphins;
    
    private Point3D position;
    private float yRotation;
    private float xRotation;
    
    private Terrain terrain;
    private int distance;
	
	public Avatar(Terrain terrain) {
		try {
			dolphins = new TriangleMesh("res/models/dolphins.ply", true, true);
		} catch (IOException e) {
			e.printStackTrace();
		}
		this.position = new Point3D(0, terrain.altitude(0, 0), 0);
		distance = 1;
		this.terrain = terrain;
		yRotation = 0;
		xRotation = 0;
	}
	
	public void draw(GL3 gl) {
		CoordFrame3D frame = CoordFrame3D.identity();
		frame = frame.translate(position)
				.rotateY(yRotation - 90)
				.rotateX(- 90)
		.scale(0.003f, 0.003f, 0.003f);
		dolphins.init(gl);
	    Texture dolphinTex = new Texture(gl,"res/textures/dolphin.bmp","bmp", true);
		
    	Shader.setInt(gl, "tex", 1);
    	gl.glActiveTexture(GL.GL_TEXTURE1);
    	gl.glBindTexture(GL.GL_TEXTURE_2D, dolphinTex.getId());
    	Shader.setPenColor(gl, Color.WHITE);
    	dolphins.draw(gl, frame);
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
//		if(keyCode == KeyEvent.VK_T) {
//			System.out.println("torch");
//			torch = !torch;
//		}
		
	}
	
	private void moveForward() {
		float x = (float) Math.sin(-1*Math.toRadians(yRotation));
		float z = (float) (-1*Math.cos(Math.toRadians(yRotation)));
		float y = terrain.altitude(x + position.getX(), z + position.getZ())
				- position.getY() + distance;
		this.position = this.position.translate(x, y, z);
	}

	private void moveBackward() {
		float x = (float) Math.sin(Math.toRadians(yRotation));
		float z = (float) Math.cos(Math.toRadians(yRotation));
		float y = terrain.altitude(x + position.getX(), z + position.getZ())
				- position.getY() + distance;
		this.position = this.position.translate(x, y, z);
	}
	
	private void turnLeft() {
		this.yRotation = yRotation + 5.0f;
	}
	
	private void turnRight() {
		this.yRotation = yRotation - 5.0f;
		
	}
	
	public Point3D getPosition() {
		return position;
	}
	
	public float getRotation() {
		return yRotation;
	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}
}
