/*
  Part of the Processing Fullscreen API

  Copyright (c) 2006-08 Hansi Raber

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
*/
package fullscreen;

import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import processing.core.PApplet;

/**
 * FullScreen support for processing. 
 * 
 * For a detailed reference see http://www.superduper.org/processing/fullscreen_api/
 * 
 * - setFullScreen( true | false ) 
 *   goes to / leaves fullscreen mode
 *
 * - setResolution( x, y ) 
 *   sets the resolution 
 * 
 * - createFullScreenKeyBindings() 
 *   links ctrl+f (or apple+f for macintosh) to enter/leave fullscreen mode
 *
 * WARNING: This package conflicts with the processing "present" option. If you want
 * fullscreen from the start use like this: 
 * 
 * void setup(){
 *   fs.setFullScreen( true );               // get fullscreen exclusive mode
 *   fs.setResolution( 640, 480 );           // change resolution to 640, 480
 * }
 * 
 * LIMITATIONS: 
 * - The size of the sketch can not be changed, when your sketch is
 *   smaller than the screen it will be centered. 
 * - The ESC key exits the sketch, this is processing standard. 
 * - Requires min. Java 1.4 to be installed work
 * - Only works for applications (not for applets)
 * 
 * by hansi, http://www.superduper.org,  http://www.fabrica.it
 */

public class FullScreen extends FullScreenBase {
	// We use this frame to go to fullScreen mode...
	GraphicsDevice fsDevice;
	
	//AWTEventListener fsKeyListener;
	
	// desired x/y resolution
	int fsResolutionX, fsResolutionY; 
	
	// the first time wait until the frame is displayed
	boolean fsIsInitialized; 
	
	// Daddy...
	PApplet dad;
	
	// Refresh rate
	private int refreshRate; 
	
	/**
	 * Create a new fullscreen object
	 */
	public FullScreen( final PApplet dad, int screenNr ){
		super( dad ); 
		this.dad = dad; 
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if( screenNr >= devices.length ){
			System.err.println( "FullScreen API: You requested to use screen nr. " + screenNr + ", " ); 
			System.err.println( "however, there are only " + devices.length + " screens in your environment. " ); 
			System.err.println( "Continuing with screen nr. 0" );
			screenNr = 0; 
		}
		
		fsDevice = devices[screenNr]; 
		
		
		if( dad.width > 0 ){
			setResolution( dad.width, dad.height );
		}
	}
	
	/**
	 * Create a fullscreen object based on a specific screen
	 */
	public FullScreen( PApplet dad ){
		this( dad, 0 ); 
	}
	
	
	/**
	 * Are we in FullScreen mode? 
	 *
	 * @returns true if so, yes if not
	 */
	public boolean isFullScreen(){
		return fsDevice.getFullScreenWindow() == dad.frame; 
	}
	
	
	/**
	 * FullScreen is only available is applications, not in applets! 
	 *
	 * @returns true if fullScreen mode is available
	 */
	public boolean available(){
		return dad.frame != null;
	}
	

	/**
	 * Enters/Leaves fullScreen mode. 
	 *
	 * @param fullScreen true or false
	 * @returns true on success
	 */
	public void setFullScreen( boolean fullScreen ){
		if( fullScreen == isFullScreen() ){
			// no change required! 
			return; 
		}
		else if( fullScreen ){
			// go to fullScreen mode...
			
			if( available() ){
				// reshape frame and get it all excited about going fs
				dad.frame.dispose(); 
				dad.frame.setUndecorated( true ); 
				dad.frame.setSize( fsDevice.getDisplayMode().getWidth(), fsDevice.getDisplayMode().getHeight() );
				dad.frame.setVisible( true ); 
				fsDevice.setFullScreenWindow( dad.frame ); 
				
				dad.requestFocus(); 
				
				// set default resolution...
				setResolution( 0, 0 ); 
				
				// Tell the sketch about the resolution change
				notifySketch( dad ); 
				
				return; 
			}
			else{
				System.err.println( "FullScreen API: Fullscreen mode not available" ); 
				return; 
			}
		}
		else{
			fsDevice.setFullScreenWindow( null );
			dad.frame.dispose(); 
			dad.frame.setUndecorated( false ); 
			dad.frame.setVisible( true ); 
			dad.setLocation( dad.frame.insets().left, dad.frame.insets().top );
			dad.frame.setSize(
				dad.width + dad.frame.insets().left + dad.frame.insets().right, 
				dad.height + dad.frame.insets().top + dad.frame.insets().bottom 
			); 
			dad.requestFocus(); 
	
			// Tell the sketch about the resolution change
			notifySketch( dad ); 
			
			return; 
		}
	}
	
	
	/**
	 * Change display resolution. Only sets the resolution, use 
	 * setFullScreen( true ) to go to fullscreen mode! 
	 *
	 * If you're not in fullscreen mode it memorizes the resolution and sets
	 * it the next time you go in fullscreen mode
	 *
	 * @returns true if resolution change succeeded, false if not
	 */
	public void setResolution( int xRes, int yRes ){
		if( xRes > 0 && yRes > 0 ){
			fsResolutionX = xRes; 
			fsResolutionY = yRes; 
		}
		
		
		// only change in fullscreen mode
		if( !isFullScreen() ){
			return; 
		}
		
		
		// Change resolution only if values are somehow meaningfull
		if( fsResolutionX <= 0 || fsResolutionY <= 0 ){
			dad.setLocation( ( fsDevice.getDisplayMode().getWidth() - dad.width ) / 2, ( fsDevice.getDisplayMode().getHeight() - dad.height ) / 2 ); 
			return; 
		}
		
		DisplayMode modes[ ] = fsDevice.getDisplayModes(); 
		DisplayMode theMode = null; 
	
		for( int i = 0; i < modes.length; i++ ){
			
			if( modes[ i ].getWidth() == fsResolutionX && modes[ i ].getHeight() == fsResolutionY ){
				if( refreshRate == 0 || refreshRate == modes[i].getRefreshRate() ){
					theMode = modes[ i ];
				}
			}
		}
	
	
		// Resolution not supported? 
		if( theMode == null ){
			System.err.println( "FullScreen API: Display mode not supported: " + fsResolutionX + "x" + fsResolutionY ); 
			dad.setLocation( ( fsDevice.getDisplayMode().getWidth() - dad.width ) / 2, ( fsDevice.getDisplayMode().getHeight() - dad.height ) / 2 ); 
			return; 
		}
	
	
		// Wait until we are in fullScreen exclusive mode..
		try{
			fsDevice.setDisplayMode( theMode ); 
			dad.frame.setSize( fsResolutionX, fsResolutionY ); 
		}
		catch( Exception e ){
			System.err.println( "FullScreen API: Failed to go to fullScreen mode" ); 
			e.printStackTrace(); 
			return; 
		}
	
		dad.setLocation( ( fsDevice.getDisplayMode().getWidth() - dad.width ) / 2, ( fsDevice.getDisplayMode().getHeight() - dad.height ) / 2 ); 
	}
	
	
	/**
	 * Returns the current refresh rate
	 */
	public int getRefreshRate(){
		return fsDevice.getDisplayMode().getRefreshRate();  
	}
	
	/**
	 * Sets the refresh rate
	 */
	public void setRefreshRate( int rate ){
		this.refreshRate = rate; 
	}
	
	/**
	 * List resolution for this screen
	 */
	public Dimension[] getResolutions(){
		return getResolutions( fsDevice ); 
	}
	
	/**
	 * List resolutions for a graphics device
	 */
	public static Dimension[] getResolutions( GraphicsDevice device ){
		DisplayMode modes[] = device.getDisplayModes();
		
		// count the number of different resolutions... 
		int found = 0; 
		Dimension resultTemp[] = new Dimension[modes.length]; 
		for( int i = 0; i < modes.length; i++ ){
			for( int j = i; j < modes.length; j++ ){
				if(    modes[i].getWidth() != modes[j].getWidth() 
					&& modes[i].getHeight() != modes[j].getHeight()
					&& modes[i].getBitDepth() != 8 
					&& modes[i].getBitDepth() != 16
				){
					// looks good! 
					resultTemp[found] = new Dimension( modes[i].getWidth(), modes[i].getHeight() );
					found ++; 
					break; 
				}
			}
		}
		
		Dimension result[] = new Dimension[found]; 
		System.arraycopy( resultTemp, 0, result, 0, found );
		
		return result; 
	}
	
	/**
	 * Get a list of available screen resolutions
	 */
	public static Dimension[] getResolutions( int screenNr ){
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if( screenNr >= devices.length ){
			System.err.println( "FullScreen API: You requested the resolutions of screen nr. " + screenNr + ", " ); 
			System.err.println( "however, there are only " + devices.length + " screens in your environment. " ); 
			System.err.println( "Continuing with screen nr. 0" );
			screenNr = 0; 
		}
		
		return getResolutions( devices[screenNr] );  
	}
	
	/**
	 * Get a list of refresh rates available for a resolution
	 */
	public int[] getRefreshRates( int xRes, int yRes ){
		DisplayMode modes[] = fsDevice.getDisplayModes(); 
		int resultTemp[] = new int[modes.length]; 
		int found = 0; 
		
		for( int i = 0; i < modes.length; i++ ){
			if(    modes[i].getWidth() == xRes 
			    && modes[i].getHeight() == yRes 
			    && modes[i].getBitDepth() != 8 
			    && modes[i].getBitDepth() != 16 
			){
				resultTemp[found] = modes[i].getRefreshRate(); 
				found ++;
			}
		}
		
		int result[] = new int[found]; 
		System.arraycopy( resultTemp, 0, result, 0, found );
		
		return result; 
	}

}
