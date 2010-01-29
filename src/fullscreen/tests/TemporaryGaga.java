package fullscreen.tests;

import java.nio.ByteBuffer;

import javax.media.opengl.DefaultGLCapabilitiesChooser;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLDrawable;
import javax.media.opengl.GLEventListener;
import javax.swing.JFrame;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import fullscreen.SoftFullScreen;

public class TemporaryGaga {

	public static void main( String args[]) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		final Demo.Simple demo = new Demo.Simple( 800, 800, PApplet.OPENGL );
		SoftFullScreen fs = new SoftFullScreen( demo );
		/*fs.setScreens( 
			0, 0, 0, 400, 600, 
			1, 400, 0, 400, 600 
		);*/
		
		PGraphicsOpenGL g = (PGraphicsOpenGL) demo.g;
		final GLContext context = g.getContext();
		GLDrawable drawable = context.getGLDrawable();
		JFrame copy = new JFrame( "copy" );
		
		final ByteBuffer buf = ByteBuffer.allocate( 1000000 ); 
		final GLCanvas canvas = new GLCanvas(
			context.getGLDrawable().getChosenGLCapabilities(), 
			new DefaultGLCapabilitiesChooser(), 
			context, 
			copy.getGraphicsConfiguration().getDevice()
		); 
		canvas.setSize( 100, 100 );
		canvas.setAutoSwapBufferMode( true ); 
		canvas.addGLEventListener(new GLEventListener() {
			
			@Override
			public void reshape(GLAutoDrawable arg0, int arg1, int arg2, int arg3,
					int arg4) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void init(GLAutoDrawable arg0) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void displayChanged(GLAutoDrawable arg0, boolean arg1, boolean arg2) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void display(GLAutoDrawable drawable) {
				drawable.getGL().glDrawPixels( 100, 100, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf );
			}
		});
		demo.registerPost( new XYZ(){
			@Override
			public void postImpl() {
				GL otherGL = context.getGL();
				GL gl = canvas.getGL(); 
				//context.setSynchronized( false );
				detainContext( canvas.getContext() ); 
				detainContext( context ); 
				
				otherGL.glReadPixels( 50, 50, 100, 100, GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buf );
				//releaseContext( canvas.getContext() );
		        gl.glClearColor(0.0f, 0.0f, 0.0f, 0.0f);
		        gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);    //Clear the buffers
				gl.glRasterPos2i( 0, 0 ); 
				buf.rewind(); 
				for( int i = 0; i < 100; i++ ){
					buf.array()[i] = (byte)(120*Math.random());
				}
				//context.setSynchronized( true ); 
				System.out.println( buf.array()[10] + "/" ); 
				//releaseContext( canvas.getContext() );
				canvas.display(); 
				}
		} ); 
		
		copy.getContentPane().add( canvas ); 
		copy.pack(); 
		copy.setVisible( true ); 
	}
	
	public static abstract class XYZ{
		public void post(){
			postImpl(); 
		}
		
		public abstract void postImpl(); 
	}
	
	
	  public static void detainContext( GLContext context ) {
		    try {
		      while (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
//		        System.out.println("Context not yet current...");
//		        new Exception().printStackTrace(System.out);
//		        Thread.sleep(1000);
		        Thread.sleep(10);
		      }
		    } catch (InterruptedException e) {
		      e.printStackTrace();
		    }
		  }


		  /**
		   * Release the context, otherwise the AWT lock on X11 will not be released
		   */
		  public static void releaseContext( GLContext context ) {
		    context.release();
		  }
	
}