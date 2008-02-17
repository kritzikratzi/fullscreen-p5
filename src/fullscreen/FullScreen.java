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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.GraphicsDevice;
import java.awt.Frame; 
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

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
 *
 *
 * TODO: Mouselisteners 
 */

public class FullScreen{
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
	
	// nasty nasty
	int superfuckingNastyhack = 0; 
	
	/**
	 * 
	 */
	public FullScreen( PApplet dad ){
		fsDevice = dad.frame.getGraphicsConfiguration().getDevice();
		
		if( dad.g.getClass().getName().equals( "processing.opengl.PGraphicsOpenGL" ) ){
			System.err.println( "FullScreen API: Warning, OPENGL Support is experimental! " ); 
			System.err.println( "Keep checking http://www.superduper.org/processing/fullscreen_api/ for updates!" );
		}
		
		this.dad = dad; 
		dad.registerKeyEvent( this );
		dad.registerDraw( this ); 
		dad.frame.addKeyListener( new FSKeyListener( this ) ); 
		dad.frame.addWindowListener( new FSWindowListener() );
		
		if( dad.width > 0 ){
			setResolution( dad.width, dad.height );
		}
		
	}
	
	
	/**
	 * Are we in FullScreen mode? 
	 *
	 * @returns true if so, yes if not
	 */
	boolean isFullScreen(){
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
	 * Enters fullscreen mode
	 * 
	 * @returns true on success
	 */
	public boolean enter(){
		return setFullScreen( true ); 
	}
	
	
	/**
	 * Leaves fullscreen mode
	 * 
	 * @returns true on success
	 */
	public boolean leave(){
		return setFullScreen( false ); 
	}
	
	
	/**
	 * Enters/Leaves fullScreen mode. 
	 *
	 * @param fullScreen true or false
	 * @returns true on success
	 */
	public boolean setFullScreen( boolean fullScreen ){
		// If it's called from setup we wait until the applet initialized properly
		if( dad.frameCount == 0 && fullScreen == true ){
			new FSWaitForInitThread().start(); 
			
			return true; 
		}
		
		
		
		if( fullScreen == isFullScreen() ){
			// no change required! 
			return true; 
		}
		else if( fullScreen ){
			// go to fullScreen mode...
			
			if( available() ){
				// reshape frame and get it all excited about going fs
				dad.frame.dispose(); 
				dad.frame.setUndecorated( true ); 
				dad.frame.setSize( fsDevice.getDisplayMode().getWidth(), fsDevice.getDisplayMode().getHeight() );
//				dad.frame.setSize(
//					dad.width + dad.frame.insets().left + dad.frame.insets().right, 
//					dad.height + dad.frame.insets().top + dad.frame.insets().bottom 
//				); 
				dad.frame.setVisible( true ); 
				fsDevice.setFullScreenWindow( dad.frame ); 
				
				dad.requestFocus(); 
				
				// set default resolution...
				setResolution( 0, 0 ); 
				
				// update texture space
				processing.core.fullscreen_texturehelper.update( dad ); 
				
				// Tell the sketch about the resolution change
				Helper.notifySketch( dad ); 
				
				return true; 
			}
			else{
				System.err.println( "FullScreen API: Fullscreen mode not available" ); 
				return false; 
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
			System.out.println( "show" ); 
	
			processing.core.fullscreen_texturehelper.update( dad );
			
			// Tell the sketch about the resolution change
			Helper.notifySketch( dad ); 
			
			return true; 
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
	public boolean setResolution( int xRes, int yRes ){
		if( xRes > 0 && yRes > 0 ){
			fsResolutionX = xRes; 
			fsResolutionY = yRes; 
		}
		
		
		// only change in fullscreen mode
		if( !isFullScreen() ){
			return false; 
		}
		
		
		// Change resolution only if values are somehow meaningfull
		if( fsResolutionX <= 0 || fsResolutionY <= 0 ){
			dad.setLocation( ( fsDevice.getDisplayMode().getWidth() - dad.width ) / 2, ( fsDevice.getDisplayMode().getHeight() - dad.height ) / 2 ); 
			return false; 
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
			return false; 
		}
	
	
		// Wait until we are in fullScreen exclusive mode..
		try{
			fsDevice.setDisplayMode( theMode ); 
			dad.frame.setSize( fsResolutionX, fsResolutionY ); 
		}
		catch( Exception e ){
			System.err.println( "FullScreen API: Failed to go to fullScreen mode" ); 
			e.printStackTrace(); 
			return false; 
		}
	
		dad.setLocation( ( fsDevice.getDisplayMode().getWidth() - dad.width ) / 2, ( fsDevice.getDisplayMode().getHeight() - dad.height ) / 2 ); 
		return true; 
	}
	
	
	final static int fsControlKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	static KeyEvent lastEvent = null; 
	public void keyEvent( KeyEvent e ){
		if( e.equals( lastEvent ) ){
			return; 
		}
		
		lastEvent = e; 
		
		// Catch the ESC key if in fullscreen mode
		if( e.getKeyCode() == KeyEvent.VK_ESCAPE ){
			if( isFullScreen() ){
				if( e.getID() == KeyEvent.KEY_RELEASED ){
					superfuckingNastyhack = 1;  
				}
			}
		}
		
		// catch the CMD+F combination (ALT+ENTER or CTRL+F for windows)
		else if( e.getID() == KeyEvent.KEY_PRESSED ){
			if( ( e.getKeyCode() == e.VK_F && e.getModifiers() == fsControlKey ) ||
					( dad.platform == dad.WINDOWS && e.getKeyCode() == e.VK_ENTER && e.getModifiers() == e.VK_ALT ) ){
				// toggle fullscreen! 
				superfuckingNastyhack = 2;  
			}
		}
	}
	
	class FSKeyListener extends KeyAdapter{
		FullScreen fs; 
		public FSKeyListener( FullScreen fs ){
			this.fs = fs; 
		}
		
		public void keyPressed( KeyEvent e ){
			fs.keyEvent( e ); 
		}
	}
	
	/**
	 * A window listener for the fullscreen window, that 
	 * calls the exit() function of processing when the window 
	 * is closed (using alt+f4, apple+w, or whatever)
	 */
	class FSWindowListener extends WindowAdapter{
		public void windowClosing( WindowEvent e ){
			// let processing exit! 
			dad.exit(); 
		}
	}
	
	
	
	/**
	 * A thread that invokes the setFullScreen() functionality delayed, 
	 * in case it's called from setup()
	 */
	class FSWaitForInitThread extends Thread{
		public void run(){
			while( dad.frameCount < 5 ){
				try{
					Thread.sleep( 1000 ); 
				}
				catch( Exception e ){
					System.err.println( "FullScreen API: Failed to go to fullscreen mode" ); 
					return; 
				}
			}
			
			if( !setFullScreen( true ) ){
				System.err.println( "FullScreen API: Failed to go to fullscreen mode" );
			}
		}
	}
	
	/**
	 * In draw..
	 */
	public void draw(){
		if( superfuckingNastyhack == 0 ){
			return; 
		}
		else if( superfuckingNastyhack == 1 ){
			setFullScreen( false );
			superfuckingNastyhack = 0; 
		}
		else if( superfuckingNastyhack == 2 ){
			setFullScreen( !isFullScreen() );
			superfuckingNastyhack = 0; 
		}
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
	
}
