/*
  Part of the Processing Fullscreen API

  The MIT License

  Copyright (c) 2011, hansi raber.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/
package fullscreen;


import java.awt.Color;
import java.awt.Dimension;
import java.awt.DisplayMode;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;

import processing.core.PApplet;

/**
 *  Creates a new fullscreen object. <br>
 *  
 *  This will use <a href="http://java.sun.com/docs/books/tutorial/extra/fullscreen/index.html" target="_blank">fullscreen exclusive mode</a>
 *  to bring your sketch to the screen. <br>
 *  The advantages are: 
 *  
 *  <ul>
 *    <li>Notifications from your OS will not be on top of your sketch</li>
 *    <li>The screensaver will be disabled automatically</li>
 *  </ul>
 *  
 *  The drawbacks are: 
 *  <ul>
 *    <li>It's hard to use two screens</li>
 *  </ul>
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
	private PApplet dad;
	
	// Refresh rate
	private int refreshRate; 
	
	// A frame for going fullscreen
	private Frame fsFrame; 
	
	/**
	 * Create a fullscreen object based on a specific screen
	 * 
	 * @param dad Your sketch
	 * @param screenNr The screen number in a multi-monitor system. Counting starts at zero. 
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
		fsFrame = new Frame( dad.frame == null? "":dad.frame.getTitle() ); 
		fsFrame.setTitle( dad.frame.getTitle() ); 
		fsFrame.setUndecorated( true ); 
		fsFrame.setBackground( Color.black );
		fsFrame.setIgnoreRepaint( true ); 
		if( dad.width > 0 ){
			setResolution( dad.width, dad.height );
		}
	}
	
	/**
	 * Create a new fullscreen object
	 * 
	 * @param dad Your sketch
	 */
	public FullScreen( PApplet dad ){
		this( dad, 0 ); 
	}
	
	
	/**
	 * Are we in FullScreen mode? 
	 *
	 * @return true if so, yes if not
	 */
	public boolean isFullScreen(){
		return fsDevice.getFullScreenWindow() == fsFrame; 
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
		new DelayedAction( 2 ) {
			public void action() {
				setFullScreenImpl( fullScreen ); 
			}
		};
	}
	
	/** 
	 * Don't use this! 
	 */
	@SuppressWarnings("deprecation")
	private void setFullScreenImpl( boolean fullScreen ){
		if( fullScreen == isFullScreen() ){
			// no change required! 
			return; 
		}
		else if( fullScreen ){
			// go to fullScreen mode...
			
			if( available() ){
				dad.frame.setVisible( false );
				dad.frame.remove( dad ); 
				fsFrame.setVisible( true );
				fsFrame.setLayout( null ); 
				fsFrame.setSize( dad.width, dad.height );
				fsFrame.add( dad ); 
				fsDevice.setFullScreenWindow( fsFrame );
				setResolution( 0, 0 ); 
				fsFrame.setExtendedState( Frame.MAXIMIZED_BOTH );
				
				dad.setLocation( ( fsFrame.getWidth() - dad.width ) / 2, ( fsFrame.getHeight() - dad.height ) / 2 );
				
				GLDrawableHelper.reAllocate( this );
				GLTextureUpdateHelper.update( this );
				
				// Tell the sketch about the resolution change
				requestFocus();
				notifySketch( getSketch() ); 
			}
			else{
				System.err.println( "FullScreen API: Not available in applets. " ); 
			}
		}
		else{
			fsDevice.setFullScreenWindow( null );
			fsFrame.setVisible( false );
			fsFrame.remove( dad );
			
			dad.frame.add( dad ); 
			dad.frame.setVisible( true ); 
			dad.setLocation( dad.frame.insets().left, dad.frame.insets().top );
			/*dad.frame.setSize(
				dad.width + dad.frame.insets().left + dad.frame.insets().right, 
				dad.height + dad.frame.insets().top + dad.frame.insets().bottom 
			);*/
	
			GLDrawableHelper.reAllocate( this );
			GLTextureUpdateHelper.update( this );
			
			// Tell the sketch about the resolution change
			requestFocus(); 
			notifySketch( getSketch() );
		}
	}
	
	
	/**
	 * Change display resolution. Only sets the resolution, use 
	 * setFullScreen( true ) to go to fullscreen mode! 
	 *
	 * If you're not in fullscreen mode it memorizes the resolution and sets
	 * it the next time you go in fullscreen mode
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
			// dad.setLocation( ( fsDevice.getDisplayMode().getWidth() - dad.width ) / 2, ( fsDevice.getDisplayMode().getHeight() - dad.height ) / 2 ); 
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
			// dad.setLocation( ( fsDevice.getDisplayMode().getWidth() - dad.width ) / 2, ( fsDevice.getDisplayMode().getHeight() - dad.height ) / 2 ); 
			return; 
		}
	
	
		// Wait until we are in fullScreen exclusive mode..
		try{
			fsDevice.setDisplayMode( theMode ); 
			fsFrame.setSize( fsResolutionX, fsResolutionY ); 
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
