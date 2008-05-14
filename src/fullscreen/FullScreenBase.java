package fullscreen;

import java.awt.Frame;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.net.URL;

import processing.core.GLFullScreenHelper;
import processing.core.PApplet;

public abstract class FullScreenBase {

	// Our daddie
	private final PApplet dad;
	
	// Cmd (apple) or ctrl (windows/linux) 
	final static int fsControlKey = Toolkit.getDefaultToolkit().getMenuShortcutKeyMask();
	
	// Enable key events?
	private boolean enableKeyEvents = true;
	
	// The previous key event
	static KeyEvent lastEvent = null;
	
	/**
	 * Create a fullscreen thingie
	 */
	public FullScreenBase( PApplet dad ){
		this.dad = dad;
		
		// Listen to processings key events
		dad.registerKeyEvent( this );
		
		// Make this sketch gl aware
		GLFullScreenHelper.addOpenGLSupport( dad ); 
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
	 * 
	 * @returns true on success
	 */
	public void enter(){
		setFullScreen( true ); 
	}
	
	
	/**
	 * Leaves fullscreen mode
	 * 
	 * @returns true on success
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
			if( ( e.getKeyCode() == e.VK_F && e.getModifiers() == fsControlKey ) ||
					( dad.platform == dad.WINDOWS && e.getKeyCode() == e.VK_ENTER && e.getModifiers() == e.VK_ALT ) ){
				// toggle fullscreen! 
				setFullScreen( !isFullScreen() ); 
			}
		}
	}
}
