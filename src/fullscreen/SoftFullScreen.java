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

import japplemenubar.JAppleMenuBar;

import java.awt.Frame;
import java.util.Vector;

import javax.swing.JFrame;

import com.sun.awt.AWTUtilities;

import processing.core.PApplet;
import processing.core.PConstants;

/**
 *  Creates a new softfullscreen object. <br>
 *  
 *  This will use undecorated frames to bring your sketch to the screen. <br>
 *  The advantages are: 
 *  
 *  <ul>
 *    <li>You can create a sketch that spans across multiple monitors easily</li>
 *  </ul>
 *  
 *  The drawbacks are: 
 *  <ul>
 *    <li>You cannot change resolution</li>
 *    <li>Screensaver must be disabled manually</li>
 *    <li>Notifications and other kinds of annoying popups might just show up on top of your sketch</li>
 *  </ul>
 */

public class SoftFullScreen extends FullScreenBase{
	// We use this frame to go to fullScreen mode...
	//AWTEventListener fsKeyListener;
	
	// the first time wait until the frame is displayed
	boolean fsIsInitialized; 
	
	// Daddy...
	PApplet dad; 
	
	private boolean isFullScreen = false; 
	Vector<FullScreenFrame> frames = new Vector<FullScreenFrame>(); 
	
	// Can we already switch back? 
	private boolean canSwitch = true; 
	private float originalAlpha; 
	private float fullscreenAlpha; 
	
	/**
	 * Creates a new softfullscreen object. 
	 * 
	 * @param dad The parent sketch (aka "this")
	 */
	public SoftFullScreen( PApplet dad ){
		this( dad, 0 ); 
	}
	
	/**
	 * Creates a new softfullscreen object on a specific screen 
	 * (numbering starts at 0)
	 * 
	 * @param dad The parent sketch (usually "this")
	 * @param screenNr The screen number. 
	 */
	public SoftFullScreen( PApplet dad, int screenNr ){
		super( dad ); 
		this.dad = dad;
		setScreens( screenNr, 0, 0, dad.width, dad.height );
	}
	
	/**
	 * Sets the crop-regions...
	 * @param numbers screen 1, x1, y1, width1, height1, screen 2, x2, y2, width2, height2, etc.   
	 */
	public void setScreens( int ... numbers ){
		if( numbers.length % 5 != 0 ){
			System.err.println( "FullScreen API: You defined the screen-regions, but the input format doesn't match. " );
			System.err.println( "Use like this: " ); 
			System.err.println( "fs.setScreens( screen-nr1, x1, y1, width1, height1, screen-nr2, width2, height2" );
			System.err.println( "" ); 
			System.err.println( "e.g. fs.setScreens( 0, 0, 0, 800, 600 ); // applet on screen 0" ); 
			System.err.println( "e.g. fs.setScreens( 1, 0, 0, 800, 600 ); // applet on screen 1" ); 
			System.err.println( "e.g. fs.setScreens( 0, 0, 0, 400, 600,  1, 400, 0, 400, 600 ); // half on screen 0, half on screen 1" ); 
			System.err.println( "" );
			System.err.println( "Your call is ignored, please fix this! " ); 
			return; 
		}
		
		for( Frame frame : frames ){
			unregisterFrame( frame ); 
			frame.dispose(); 
		}
		
		frames.clear();
		
		for( int i = 0; i < numbers.length; i += 5 ){
			int screenNr = numbers[i]; 
			int x = numbers[i+1]; 
			int y = numbers[i+2]; 
			int width = numbers[i+3]; 
			int height = numbers[i+4];
			
			FullScreenFrame frame = new FullScreenFrame( dad, screenNr, x, y, width, height );
			registerFrame( frame ); 
			frames.add( frame ); 
		}
	}
	
	
	/**
	 * Are we in FullScreen mode?
	 *
	 * @return true if so, yes if not
	 */
	public boolean isFullScreen(){
		return isFullScreen; 
	}
	
	
	/**
	 * FullScreen is only available is applications, not in applets! 
	 *
	 * @return true if fullScreen mode is available
	 */
	public boolean available(){
		return dad.frame != null;
	}
	
	
	/**
	 * Enters/Leaves fullScreen mode. 
	 *
	 * @param fullScreen true or false
	 */
	public void setFullScreen( boolean fullScreen ){
		new DelayedModeChange( fullScreen );  
	}
	
	
	@SuppressWarnings("deprecation")
	private synchronized void setFullScreenImpl( boolean fullScreen ){
		if( fullScreen == isFullScreen() ){
			// no change required! 
			return; 
		}
		else if( fullScreen ){
			if( available() ){
				// remove applet from processing frame and attach to fsFrame
				//dad.frame.setVisible( false );  
				
				if( PApplet.platform == PConstants.MACOSX ){
					new JAppleMenuBar().setVisible( false );
				}
				
				for( JFrame frame : frames ){
					AWTUtilities.setWindowOpacity( frame, 0 );
					frame.setVisible( true ); 
					frame.requestFocus(); 
				}
				
				//GLDrawableHelper.reAllocate( this ); 
				//GLTextureUpdateHelper.update( this ); 
				fullscreenAlpha = 1; 
				originalAlpha = 0; 
				
				notifySketch( dad );
				isFullScreen = true; 
			}
			else{
				System.err.println( "FullScreen API: Fullscreen mode not available" );
				return; 
			}
		}
		else{
			// remove applet from fsFrame and attach to processing frame
			//for( JFrame frame : frames ){
			//	frame.setVisible( false ); 
			//}
			
			AWTUtilities.setWindowOpacity( dad.frame, 0 ); 
			dad.frame.setVisible( true ); 
			dad.requestFocus();
			
			// processing.core.fullscreen_texturehelper.update( dad );
			if( PApplet.platform == PConstants.MACOSX ){
				new JAppleMenuBar().setVisible( true );
			}
			
			
			//GLDrawableHelper.reAllocate( this ); 
			//GLTextureUpdateHelper.update( this ); 
			fullscreenAlpha = 0; 
			originalAlpha = 1; 
			
			notifySketch( dad ); 
			isFullScreen = false; 
		}
		
		canSwitch = false; 
		new Thread(){
			public void run(){
				float origA = 1-originalAlpha;
				float fullA = 1-fullscreenAlpha;
				
				while( Math.abs( origA - originalAlpha ) > 0.02 ){
					origA += ( originalAlpha - origA )/5f;
					fullA += ( fullscreenAlpha - fullA )/5f;
					AWTUtilities.setWindowOpacity( dad.frame, origA );
					for( JFrame frame : frames ){
						AWTUtilities.setWindowOpacity( frame, fullA ); 
					}
					
					try{
						Thread.sleep( 20 );
					}
					catch( InterruptedException e ){
						e.printStackTrace(); 
						break; 
					}
				}
				
				AWTUtilities.setWindowOpacity( dad.frame, originalAlpha);
				for( JFrame frame : frames ){
					AWTUtilities.setWindowOpacity( frame, fullscreenAlpha ); 
				}
				
				//if( originalAlpha == 0 ) dad.frame.setVisible( false ); 
				if( fullscreenAlpha == 0 ){
					for( JFrame frame : frames ){
						frame.setVisible( false );  
					}
				}
				
				canSwitch = true; 
			}
		}.start(); 
	}


	/**
	 * Setting resolution is not possible with the SoftFullScreen object. 
	 */
	@Override
	public void setResolution( int xRes, int yRes ) {
		System.err.println( "Changing resolution is not supported in SoftFullScreen mode. " ); 
		System.err.println( "Use the normal FullScreen mode to make use of that functionality.  " ); 
	}

	
	/**
	 * A sweet little helper. 
	 */
	public class DelayedModeChange{
		private boolean state; 
		private int skippedFrames = 0; 
		
		public DelayedModeChange( boolean state ){
			this.state = state;
			dad.registerPost( this ); 
		}
		
		public void post(){
			skippedFrames ++; 
			
			if( skippedFrames >= 2 && canSwitch ){
				setFullScreenImpl( state );
				dad.unregisterPost( this );
			}
		}
	}
}
