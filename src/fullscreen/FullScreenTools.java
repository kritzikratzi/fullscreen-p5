package fullscreen;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Rectangle;
import java.nio.IntBuffer;

import javax.media.opengl.GL;

import processing.core.PApplet;

/**
 * A bunch of useful functions. 
 * 
 * @author hansi
 *
 */
public class FullScreenTools {

	
	/**
	 * Finds a graphics device with a certain number. 
	 * If that device doesn't exist the graphics device belonging to the 
	 * first screen in the system will be returned.
	 *  
	 * @param screenNr The number of the screen
	 * @return The graphics device
	 */
	public static GraphicsDevice getScreenDevice( int screenNr ){
		GraphicsDevice[] devices = devices();
		if( screenNr >= devices.length ){
			System.err.println( "FullScreen API: You requested to use screen nr. " + screenNr + ", " ); 
			System.err.println( "however, there are only " + devices.length + " screens in your environment. " ); 
			System.err.println( "Continuing with screen nr. 0" );
			screenNr = 0; 
		}
		
		return devices[screenNr]; 
	}
	
	/**
	 * Returns all screen devices in the system
	 */
	public static GraphicsDevice[] devices(){
		return GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices(); 
	}

	/**
	 * Checks whether an applet inherits from PGraphicsOpenGL
	 * 
	 * @param dad The applet
	 * @return true, or false. 
	 */
	public static boolean isGL( PApplet dad ){
		Class<?> clazz = dad.g.getClass(); 
		while( clazz != null  ){
			if( clazz.getName().equals( "processing.opengl.PGraphicsOpenGL" ) )
				return true; 
			
			clazz = clazz.getSuperclass(); 
		}
		
		return false; 
	}
	
	/**
	 * Creates a new texture of a certain width and height. 
	 * 
	 * mostly taken from: 
	 * http://processing.org/discourse/yabb2/YaBB.pl?num=1181343531
	 * 
	 * @return The texture id
	 */
	public static int createTexture( GL gl, int width, int height ){
		int[] pixels = new int[width*height];

		for (int i=0;i < pixels.length; i++)
			pixels[i] = 0x00FF00FF + i;
		

		IntBuffer pixBuffer = IntBuffer.wrap( pixels );
		pixBuffer.rewind();
		int[] id = new int[1]; 
		
		gl.glGenTextures( 1, id, 0 );
		gl.glEnable( GL.GL_TEXTURE_2D );
		gl.glPixelStorei(GL.GL_UNPACK_ALIGNMENT, 1);
		gl.glBindTexture(GL.GL_TEXTURE_2D, id[0] );
		gl.glTexImage2D( GL.GL_TEXTURE_2D, 0, 4, width, height, 0, GL.GL_BGRA, GL.GL_UNSIGNED_BYTE, pixBuffer );
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER, GL.GL_LINEAR);
		gl.glTexParameterf(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER, GL.GL_LINEAR);
		gl.glTexEnvf(GL.GL_TEXTURE_ENV, GL.GL_TEXTURE_ENV_MODE, GL.GL_REPLACE);
		gl.glDisable(GL.GL_TEXTURE_2D);
		
		return id[0]; 
	}
	
	/**
	 * Copies a certain rectangle from the screen into a texture
	 */
	public static void takeScreenshot( GL gl, int targetTexID, int x, int y, int w, int h ){
		gl.glEnable( GL.GL_TEXTURE_2D );
		gl.glBindTexture( GL.GL_TEXTURE_2D, targetTexID );
		
		//gl.glReadBuffer( GL.GL_FRONT ); 
		//gl.glCopyTexImage2D( GL.GL_TEXTURE_2D, 0, GL.GL_RGBA, x, y, w, h, 0 );
		gl.glCopyTexSubImage2D( GL.GL_TEXTURE_2D, 0, 0, 0, x, y, w, h );
		gl.glDisable( GL.GL_TEXTURE_2D );		
	}

	/**
	 * Draw a texture
	 */
	public static void drawTexture( GL gl, int texID, float x, float y, float w, float h ){
		gl.glDisable( GL.GL_DEPTH_TEST );
		gl.glEnable( GL.GL_TEXTURE_2D );
		gl.glBindTexture( GL.GL_TEXTURE_2D, texID ); 

		gl.glBegin( GL.GL_QUADS );
		gl.glTexCoord2f( 0.0f, 0.0f ); gl.glVertex2f(   x, y );
		gl.glTexCoord2f( 1.0f,   0f ); gl.glVertex2f(   x+w, y );
		gl.glTexCoord2f( 1.0f, 1.0f ); gl.glVertex2f(   x+w, y+h );
		gl.glTexCoord2f( 0.0f, 1.0f ); gl.glVertex2f(   x, y+h );
		gl.glEnd();

		gl.glDisable( GL.GL_TEXTURE_2D );
		gl.glEnable( GL.GL_DEPTH_TEST );
	}


	/**
	 * Exact inside fit, maintaining aspect ratio
	 */
	public static Rectangle fit( int width, int height, int targetWidth, int targetHeight ){
		
		int newWidth = width * targetWidth/width; 
		int newHeight = height * targetWidth/width;
		
		if( newHeight > targetHeight ){
			newWidth = width * targetHeight/height; 
			newHeight = height * targetHeight/height;
		}
		
		return new Rectangle( ( targetWidth-newWidth)/2, ( targetHeight-newHeight)/2, newWidth, newHeight ); 
	}
	
	
	
	// blub... 
	private void setWindowSplittingEnabled( boolean state ){}
	private void setBackground( int rgb ){}
	private void setScalingMethod( int region, String method ){}
	private void setEntersFullScreen( int region, boolean yes ){}
	public static class Preset{
		public static FullScreen onScreen( PApplet dad, int ... screen ){
			return null;
		}
		
		public static FullScreen onActiveScreen( PApplet dad ){
			return null; 
		}
		
		public static FullScreen controller( PApplet dad, int controlX, int controlY, int controlW, int controlH ){
			return null;
		}
		
		public static FullScreen stretch( PApplet dad ){
			return null; 
		}
	}
}