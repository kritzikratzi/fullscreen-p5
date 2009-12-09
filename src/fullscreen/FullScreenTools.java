package fullscreen;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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
}
