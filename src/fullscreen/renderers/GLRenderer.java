package fullscreen.renderers;

import java.awt.Dimension;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;

import javax.media.opengl.DefaultGLCapabilitiesChooser;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLContext;
import javax.media.opengl.GLEventListener;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import fullscreen.FullScreenTools;

public class GLRenderer extends GLCanvas implements Renderer{
	private static final long serialVersionUID = 1L;

	// Parent applet and according graphics object
	PApplet dad; 
	PGraphicsOpenGL g; 
	
	// x, y, width, height
	private int x; 
	private int y; 
	private int width; 
	private int height; 
	
	// where is the sketch position? 
	private Rectangle boundaries = new Rectangle( 0, 0, 1, 1 ); 
	
	// Our rendering-duplicate
	private int texID;

	public GLRenderer( final PApplet dad, GLContext context, final int x, final int y, final int width, final int height ){
		super( 
				context.getGLDrawable().getChosenGLCapabilities(), 
				new DefaultGLCapabilitiesChooser(), 
				context, 
				GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice()
		);
		dad.registerPost( this ); 
		setSize( width, height );
		
		this.dad = dad; 
		this.g = (PGraphicsOpenGL) dad.g;
		this.x = x; 
		this.y = y; 
		this.width = width; 
		this.height = height;
		
		
		addGLEventListener(new GLEventListener() {
			@Override
			public void reshape( GLAutoDrawable arg0, int arg1, int arg2, int arg3, int arg4 ){ }
			
			@Override
			public void init( GLAutoDrawable drawable ){ }
			
			@Override
			public void displayChanged( GLAutoDrawable arg0, boolean arg1, boolean arg2 ){ }
			
			@Override
			public void display(GLAutoDrawable drawable) {
				GL gl = drawable.getGL();
		        gl.glClearColor( 0f, 0f, 0f, 1f );
		        gl.glClear( GL.GL_COLOR_BUFFER_BIT );
		        
		        gl.glColor3f( 1f, 1f, 1f );
				boundaries = FullScreenTools.fit( width, height, getWidth(), getHeight() );
				
				FullScreenTools.drawTexture( 
					g.gl, 
					texID, 
					-1+2f*boundaries.x/getWidth(), 
					-1+2f*boundaries.y/getHeight(), 
					+2f*boundaries.width/getWidth(),
					+2f*boundaries.height/getHeight()
				); 
			}
		});	
	}
	
	
	public void post() {
		detainContext( g.getContext() ); 

		if( texID == 0 ){
			texID = FullScreenTools.createTexture( g.gl, width, height );
		}
		
		FullScreenTools.takeScreenshot( g.gl, texID, x, dad.height-y-height, width, height );
		display();

		releaseContext( g.getContext() );
	}
	

	/**
	 * Detain opengl context
	 * 
	 * @param context
	 */
	public static void detainContext( GLContext context ) {
		try {
			int i = 0; 
			while (context.makeCurrent() == GLContext.CONTEXT_NOT_CURRENT) {
				i++; 
				if( i > 10 ){
					System.out.println("Context not yet current...");
					Thread.sleep(1000);
				}
				//		        new Exception().printStackTrace(System.out);
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
	
	
	@Override
	public Dimension getMinimumSize() {
		return new Dimension( width, height ); 
	}

	@Override
	public Dimension getPreferredSize() {
		return getMinimumSize(); 
	}


	@Override
	public Rectangle sketchBoundaries(){
		return boundaries; 
	}


	@Override
	public void die() {
		dad.unregisterPost( this ); 
	}
}
