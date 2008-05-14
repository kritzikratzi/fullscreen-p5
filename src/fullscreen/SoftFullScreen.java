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
import java.awt.Frame;
import java.awt.GraphicsDevice;

import processing.core.PApplet;

/**
 * FullScreen support for processing. 
 * 
 * For a detailed reference see http://www.superduper.org/processing/fullscreen_api/
 * 
 * - setFullScreen( true | false ) 
 *   goes to / leaves fullscreen mode
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

public class SoftFullScreen extends FullScreenBase{
	// We use this frame to go to fullScreen mode...
	Frame fsFrame = new Frame(); 
	GraphicsDevice fsDevice = fsFrame.getGraphicsConfiguration().getDevice();
	
	//AWTEventListener fsKeyListener;
	
	// the first time wait until the frame is displayed
	boolean fsIsInitialized; 
	
	// Daddy...
	PApplet dad; 
	
	
	
	/**
	 *  
	 */
	public SoftFullScreen( PApplet dad ){
		super( dad ); 
		this.dad = dad; 
		fsFrame.setTitle( "FullScreen" ); 
		fsFrame.setUndecorated( true ); 
		fsFrame.setBackground( Color.black ); 
		fsFrame.setLayout( null ); 
		//fsFrame.setSize( fsDevice.getDisplayMode().getWidth(), fsDevice.getDisplayMode().getHeight() );
		fsFrame.setSize( dad.width, dad.height ); 
		
		registerFrame( fsFrame ); 
	}
	
	
	/**
	 * Are we in FullScreen mode? 
	 *
	 * @returns true if so, yes if not
	 */
	public boolean isFullScreen(){
		return fsFrame.isVisible();  
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
		// If it's called from setup we wait until the applet initialized properly
		/*if( dad.frameCount == 0 && fullScreen == true ){
			new FSWaitForInitThread().start(); 
			
			return; 
		}*/
		
		
		
		if( fullScreen == isFullScreen() ){
			// no change required! 
			return; 
		}
		else if( fullScreen ){
			if( available() ){
				// remove applet from processing frame and attach to fsFrame
				fsFrame.setVisible( false );
				/*dad.frame.setSize(
					dad.width + dad.frame.insets().left + dad.frame.insets().right, 
					dad.height + dad.frame.insets().top + dad.frame.insets().bottom 
				);*/
				//fsFrame.setSize( fsDevice.getDisplayMode().getWidth(), fsDevice.getDisplayMode().getHeight() );
				fsFrame.setSize( dad.width, dad.height ); 

				
				fsFrame.add( dad ); 
				dad.requestFocus(); 
				
				if( dad.platform == dad.MACOSX ){
					new NativeOSX().setVisible( false ); 
				}
				
				fsFrame.setVisible( true ); 
				fsFrame.setLocation( 0, 0 ); 
				dad.setLocation( ( fsFrame.getWidth() - dad.width ) / 2, ( fsFrame.getHeight() - dad.height ) / 2 ); 
				
				return; 
			}
			else{
				System.err.println( "FullScreen API: Fullscreen mode not available" ); 
				return; 
			}
		}
		else{
			// remove applet from fsFrame and attach to processing frame
			System.out.println( "hide" ); 
			fsFrame.removeAll(); 
			dad.frame.add( dad ); 
			dad.setLocation( dad.frame.insets().left, dad.frame.insets().top );
			
			// processing.core.fullscreen_texturehelper.update( dad );
			if( dad.platform == dad.MACOSX ){
				new NativeOSX().setVisible( true );
			}
			
			fsFrame.setVisible( false ); 
			dad.frame.setVisible( true ); 
			dad.requestFocus(); 
			
			return; 
		}
	}


	@Override
	public void setResolution( int xRes, int yRes ) {
		System.err.println( "Changing resolution is not supported in SoftFullScreen mode. " ); 
		System.err.println( "Use the normal FullScreen mode to make use of that functionality.  " ); 
	}


}
