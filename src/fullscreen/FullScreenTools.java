package fullscreen;

import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

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
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if( screenNr >= devices.length ){
			System.err.println( "FullScreen API: You requested to use screen nr. " + screenNr + ", " ); 
			System.err.println( "however, there are only " + devices.length + " screens in your environment. " ); 
			System.err.println( "Continuing with screen nr. 0" );
			screenNr = 0; 
		}
		
		return devices[screenNr]; 
	}
}
