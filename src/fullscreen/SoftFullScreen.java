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

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import processing.core.PApplet;
import processing.core.PConstants;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;

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
	/** 
	 * This frame is used when the application is in fullscreen mode. 
	 * It's only public so you can easily hack around, if you use it 
	 * please don't complain if things change. 
	 */
	public Frame fsFrame; 
	GraphicsDevice fsDevice;
	
	//AWTEventListener fsKeyListener;
	
	// the first time wait until the frame is displayed
	boolean fsIsInitialized; 

	// are we in kiosk mode? (OS X only)
	boolean kioskMode;
	
	// Daddy...
	PApplet dad; 
	
	
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
    this( dad, screenNr, false );
  }
	
	/**
	 * Creates a new softfullscreen object on a specific screen 
	 * (numbering starts at 0) optionally in kiosk mode.
	 * 
	 * @param dad The parent sketch (usually "this")
	 * @param screenNr The screen number. 
   * @param kioskMode Enable/disable kiosk mode (OS X only)
	 */
	public SoftFullScreen( PApplet dad, int screenNr, boolean kioskMode ){
		super( dad ); 
		this.dad = dad;
		setKioskMode( kioskMode ); 
		
		GraphicsDevice[] devices = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices();
		if( screenNr >= devices.length ){
			System.err.println( "FullScreen API: You requested to use screen nr. " + screenNr + ", " ); 
			System.err.println( "however, there are only " + devices.length + " screens in your environment. " ); 
			System.err.println( "Continuing with screen nr. 0" );
			screenNr = 0; 
		}
		
		fsDevice = devices[screenNr];
		WindowListener listener = new WindowAdapter(){
			public void windowDeiconified( WindowEvent w ){
				if( isFullScreen() && PApplet.platform == PConstants.MACOSX ){
					new JAppleMenuBar().setVisible( false, SoftFullScreen.this.kioskMode );
				}
			}
		};
		
		fsFrame = new Frame( fsDevice.getDefaultConfiguration() );
		fsFrame.addWindowListener(listener);
		fsFrame.setTitle( dad.frame == null? "":dad.frame.getTitle() );
		fsFrame.setIconImage( dad.frame.getIconImage() );
		fsFrame.setUndecorated( true ); 
		fsFrame.setBackground( Color.black ); 
		fsFrame.setLayout( null ); 
		fsFrame.setSize( 
			Math.max( fsDevice.getDisplayMode().getWidth(), dad.width ), 
			Math.max( fsDevice.getDisplayMode().getHeight(), dad.height )
		);
		
		registerFrame( fsFrame ); 
	}
	
	/**
	 * Are we in FullScreen mode? 
	 *
	 * @return true if so, yes if not
	 */
	public boolean isFullScreen(){
		return fsFrame.isVisible();  
	}
	
	/**
	 * Allow for minimizing the frame
	 */
	public void minimize(){
		if( isFullScreen() ){
			if( PApplet.platform == PConstants.MACOSX ){
				new JAppleMenuBar().setVisible( true );
			}
			fsFrame.setState( Frame.ICONIFIED );
		}
		else{
			dad.frame.setState( Frame.ICONIFIED );
		}
	}
	
	/**
	 * Restores the frame after it has been minimized. 
	 * If it wasn't minimized this doesn't do much! 
	 */
	public void restore(){
		if( isFullScreen() ){
			fsFrame.setState( Frame.NORMAL ); 
			
			if( PApplet.platform == PConstants.MACOSX ){
				new JAppleMenuBar().setVisible( false, kioskMode );
			}
		}
		else{
			dad.frame.setState( Frame.NORMAL ); 
		}
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
	public void setFullScreen( final boolean fullScreen ){
		new DelayedAction( 2 ){
			public void action(){
				setFullScreenImpl( fullScreen ); 
			}
		};
	}
	
	@SuppressWarnings("deprecation")
	private void setFullScreenImpl( boolean fullScreen ){
		if( fullScreen == isFullScreen() ){
			// no change required! 
			return; 
		}
		else if( fullScreen ){
			if( available() ){
				// remove applet from processing frame and attach to fsFrame
				dad.frame.setVisible( false ); 
				fsFrame.add( dad ); 
				
				if( PApplet.platform == PConstants.MACOSX ){
					new JAppleMenuBar().setVisible( false, kioskMode ); 
				}
				
				fsFrame.setVisible( true ); 
				fsFrame.setLocation( fsDevice.getDefaultConfiguration().getBounds().getLocation() );
				
				boolean usesEntireScreen = fsDevice.getDefaultConfiguration().getBounds().getSize().equals( new Dimension( dad.width, dad.height ) );
				int appleDriversSuck = PApplet.platform == PConstants.MACOSX && usesEntireScreen? 1:0;  
				dad.setLocation( ( fsFrame.getWidth() - dad.width ) / 2, ( fsFrame.getHeight() - dad.height ) / 2 - appleDriversSuck ); 
				fsFrame.setExtendedState( Frame.MAXIMIZED_BOTH );		
				
				GLDrawableHelper.reAllocate( this ); 
				GLTextureUpdateHelper.update( this ); 
				
				requestFocus();
				notifySketch( dad );
				
				return; 
			}
			else{
				System.err.println( "FullScreen API: Fullscreen mode not available" ); 
				return; 
			}
		}
		else{
			// remove applet from fsFrame and attach to processing frame
			fsFrame.setVisible( false ); 
			fsFrame.removeAll(); 
			dad.frame.add( dad ); 
			dad.setLocation( dad.frame.insets().left, dad.frame.insets().top );
			
			// processing.core.fullscreen_texturehelper.update( dad );
			if( PApplet.platform == PConstants.MACOSX ){
				new JAppleMenuBar().setVisible( true );
			}
			
			dad.frame.setVisible( true ); 
			
			GLDrawableHelper.reAllocate( this ); 
			GLTextureUpdateHelper.update( this ); 
			
			requestFocus();
			notifySketch( dad ); 
			
			return; 
		}
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
	 * Changes kiosk mode, this will only apply once you (re-)enter fullscreen mode. 
	 */
	public void setKioskMode( boolean kioskMode ){
		this.kioskMode = kioskMode;
		
		if( kioskMode && PApplet.platform != PConstants.MACOSX ){
			System.err.println( "Warning: Kiosk Mode only works on Mac OS X. " ); 
			System.err.println( "         Continuing without kiosk mode. " ); 
		}
	}
}
