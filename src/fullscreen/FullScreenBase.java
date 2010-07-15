package fullscreen;

import japplemenubar.JAppleMenuBar;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.Method;

import processing.core.PApplet;
import processing.core.PConstants;


/**
 * The base class {@link FullScreen} and {@link SoftFullScreen} inherit from. <br /> 
 * 
 * It defines some common methods that you might wanna dig through (like {@link FullScreenBase#setShortcutsEnabled(boolean)})
 * @author hansi
 */
public abstract class FullScreenBase {

	// Our daddie
	private final PApplet dad;
	
	// Cmd (apple) or ctrl (windows/linux) 
	final static int fsControlKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	// Enable key events?
	private boolean enableKeyEvents = true;
	
	// The previous key event
	static KeyEvent lastEvent = null;
	
	// Is opengl being used in this sketch? 
	private boolean isGL; 
	
	/**
	 * Create a fullscreen thingie
	 */
	public FullScreenBase( PApplet dad ){
		this.dad = dad;
		
		// Listen to processings key events
		dad.registerKeyEvent( this );
		if( dad.frame != null ) registerFrame( dad.frame ); 
		Class<?> clazz = dad.g.getClass(); 
		while( clazz != null  ){
			if( clazz.getName().equals( "processing.opengl.PGraphicsOpenGL" ) )
				isGL = true;
			
			clazz = clazz.getSuperclass(); 
		}
		
		if( isGL ){
			// Make ppl aware that gl doesn't always work!
			System.err.println( "FullScreen API: Warning, OPENGL Support is experimental! " ); 
			System.err.println( "Keep checking http://www.superduper.org/processing/fullscreen_api/ for updates!" );
		}
	}
	
	
	/** 
	 * Enters/Leaves fullscreen mode
	 */
	public abstract void setFullScreen( boolean state );
	
	
	/**
	 * Are we currently in fullscreen mode? 
	 */
	public abstract boolean isFullScreen(); 
	
	
	/**
	 * Set resolution
	 * 
	 * @param xRes x resolution
	 * @param yRes y resolution
	 */
	public abstract void setResolution( int xRes, int yRes );  


	/**
	 * Enters fullscreen mode
	 */
	public void enter(){
		setFullScreen( true ); 
	}
	
	
	/**
	 * Leaves fullscreen mode
	 */
	public void leave(){
		setFullScreen( false ); 
	}
		
	/**
	 * Allow shortcuts?
	 * 
	 * @param state yes if true, no if false. 
	 */
	public void setShortcutsEnabled( boolean state ){
		enableKeyEvents = state; 
	}
	
	
	/**
	 * Whatever frame this specific implementation uses, it has to be registered here so that key events can be caught
	 */
	protected void registerFrame( Frame f ){
		// Key Listener
		f.addKeyListener( new KeyAdapter(){
			public void keyPressed( KeyEvent e ){
				keyEvent( e );
			}
		}); 
		
		// Window listener
		f.addWindowListener( new WindowAdapter(){
			public void windowDeiconified( WindowEvent w ){
				dad.loop(); 
			}
			
			@Override
			public void windowIconified( WindowEvent e ){
				dad.noLoop();
			}
			
			public void windowClosing( WindowEvent e ){
				dad.exit();
			}
		} ); 
	}



	/**
	 * Implement keyEvent to listen to processing's key events
	 * @param e
	 */
	public void keyEvent( KeyEvent e ){
		if( e.equals( lastEvent ) || !enableKeyEvents ){
			return; 
		}
		
		lastEvent = e; 
		
		// Catch the ESC key if in fullscreen mode
		if( e.getKeyCode() == KeyEvent.VK_ESCAPE ){
			if( isFullScreen() ){
				if( e.getID() == KeyEvent.KEY_RELEASED ){
					setFullScreen( false ); 
				}
			}
		}
		
		// catch the CMD+F combination (ALT+ENTER or CTRL+F for windows)
		else if( e.getID() == KeyEvent.KEY_PRESSED ){
			if( ( e.getKeyCode() == KeyEvent.VK_F && e.getModifiers() == fsControlKey ) ||
					( PApplet.platform == PConstants.WINDOWS && e.getKeyCode() == KeyEvent.VK_ENTER && e.getModifiers() == KeyEvent.VK_ALT ) ){
				// toggle fullscreen! 
				setFullScreen( !isFullScreen() ); 
			}
		}
	}
	
	/**
	 * Notifies the sketch about a display mode change. 
	 */
	protected static void notifySketch( PApplet dad ){
		try{
			Method m = dad.getClass().getMethod( "displayChanged", new Class[]{ } );
			m.invoke( dad, new Object[]{ } );
		}
		catch( Exception e ){
			
		}
	}

	
	/**
	 * Requests focus for the sketch.. 
	 */
	public void requestFocus(){
		getSketch().requestFocus(); 
		new DelayedAction( 2 ){
			public void action(){
				getSketch().requestFocus();
			}
		}; 
	}
	
	
	/**
	 * Is opengl somehow being used in this sketch? 
	 */
	public boolean isGL(){
		return isGL;
	}
	
	/**
	 * Returns the sketch
	 * 
	 * @return The Sketch associated with this object
	 */
	public PApplet getSketch() {
		return dad;
	}
	
	
	/**
	 * A sweet little helper. 
	 */
	public abstract class DelayedAction{
		private int skipFrames = 0; 
		
		public DelayedAction( int skipFrames ){
			dad.registerPost( this );
			this.skipFrames = skipFrames; 
		}
		
		public void post(){
			skipFrames --; 
			
			if( skipFrames <= 0 ){
				action(); 
				dad.unregisterPost( this );
			}
		}
		
		public abstract void action(); 
	}	
}
