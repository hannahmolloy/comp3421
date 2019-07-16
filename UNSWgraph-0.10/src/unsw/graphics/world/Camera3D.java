package unsw.graphics.world;

import com.jogamp.opengl.GL3;

import unsw.graphics.Matrix4;
import unsw.graphics.Shader;
import unsw.graphics.Vector4;
import unsw.graphics.geometry.Point3D;

public class Camera3D {
//    public Camera3D() {
//    	myAspectRatio = 1;
//    	cameraPos = new Point3D(0,0,0);
//    	zoom = 0.5f;    	
//    }
//    
//    public void init(GL3 gl) {
////    	getWindow().addKeyListener(new KeyAdapter() {
////            @Override
////            public void keyPressed(KeyEvent ev) {
////                if (ev.getKeyCode() == KeyEvent.VK_LEFT)
////                    cameraPos = cameraPos.translate(-0.02f/zoom, 0, 0);
////                else if (ev.getKeyCode() == KeyEvent.VK_RIGHT)
////                    cameraPos = cameraPos.translate(0.02f/zoom, 0, 0);
////                else if (ev.getKeyCode() == KeyEvent.VK_UP)
////                    cameraPos = cameraPos.translate(0, 0.02f/zoom, 0);
////                else if (ev.getKeyCode() == KeyEvent.VK_DOWN)
////                    cameraPos = cameraPos.translate(0, -0.02f/zoom, 0);
////                else if (ev.getKeyCode() == KeyEvent.VK_SPACE)
////                    zoom *= 1.01;
////            }
////        });
//    }
//
//    public void setView(GL3 gl) {
//        // TODO compute a view transform to account for the cameras aspect ratio
//    	Matrix4 viewMatrix = Matrix4.scale(1/myAspectRatio, 1, 1);
//        
//        // TODO apply further transformations to account for the camera's global position, 
//        // rotation and scale
//    	viewMatrix = viewMatrix
//    			.multiply(Matrix4.scale(1/zoom, 1/zoom, 1))
//    			.multiply(Matrix4.translation(-cameraPos.getX(), -cameraPos.getY(), -cameraPos.getZ()));
//    	
//    	// TODO compute the projection matrix to get to CVV coords
//    	Matrix4 projMatrix = Matrix4.perspective(60, 1, 1, 100);
//        
//        // TODO set the view and projection matrices to the computed transforms
//    	Shader.setViewMatrix(gl, viewMatrix);
//    	Shader.setProjMatrix(gl, projMatrix);
//    }
//    
//    public void reshape(int width, int height) {
//      //  myAspectRatio = (1f * width) / height;            
//    }
//
//    /**
//     * Transforms a point from camera coordinates to world coordinates. Useful for things like mouse
//     * interaction
//     * 
//     * @param x
//     * @param y
//     * @return
//     */
//    public Point3D fromView(float x, float y, float z) {
////        Matrix4 mat = Matrix4.translation(getGlobalPosition())
////                .multiply(Matrix4.rotation(getGlobalRotation()))
////                .multiply(Matrix4.scale(getGlobalScale(), getGlobalScale()))
////                .multiply(Matrix4.scale(myAspectRatio, 1));
//        return Matrix4.identity().multiply(new Vector4(x,y,z,1)).asPoint3D();
//    }
}
