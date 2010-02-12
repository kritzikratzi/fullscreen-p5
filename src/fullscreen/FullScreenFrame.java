package fullscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;

import javax.media.opengl.GLContext;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;
import fullscreen.renderers.ClassicRenderer;
import fullscreen.renderers.GLRenderer;
import fullscreen.renderers.Renderer;

public class FullScreenFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	// The original sketch
	private PApplet dad; 
	
	// And the painter
	private Component renderer; 
	
	private int x, y, width, height; 
	/**
	 * Creates a fullscreen-frame thing. 
	 * 
	 * @param dad The original sketch
	 */
	public FullScreenFrame( PApplet dad, int screenNr, int x, int y, int width, int height ){
		this.dad = dad;
		this.x = x;
		this.y = y; 
		this.width = width; 
		this.height = height;
		
		if( FullScreenTools.isGL( dad ) ){
			javax.media.opengl.GLContext context = ((PGraphicsOpenGL) dad.g).getContext(); 
			try {
				Constructor c = Class.forName( "fullscreen.renderers.GLRenderer" ).getConstructors()[0];
				renderer = (Component) c.newInstance( dad, context, x, y, width, height );
				//renderer = new GLRenderer( dad, context, x, y, width, height ); 
			}
			catch (Exception e) {
				System.err.println( "FullScreen API: Sorry, GLRenderer not found. " ); 
				System.err.println( "                I don't know what to say... bye!" ); 
				e.printStackTrace(); 
				System.exit( 1 ); 
			}
		}
		else{
			renderer = new ClassicRenderer( dad, x, y, width, height );
		}
		
		getContentPane().add( renderer, BorderLayout.CENTER );
		// Fix for mac os: 
		// Add a 1-px border if using open gl
		// TODO: 
		// with this fix in place it is possible to click at the 
		// lowest pixel-line to lose focus. 
		// need to figure out where the focus goes ... 
		if( System.getProperty( "os.name" ).contains( "Mac OS X" ) && FullScreenTools.isGL( dad ) ){
			MacFixer fixer = new MacFixer(); 
			getContentPane().add( fixer, BorderLayout.SOUTH );
			setupKeyEvents( fixer ); 
		}
		
		setupKeyEvents( renderer );
		setupMouseEvents( renderer );
		
		GraphicsDevice device = FullScreenTools.getScreenDevice( screenNr ); 
		setBounds( device.getDefaultConfiguration().getBounds() );
		setExtendedState( MAXIMIZED_BOTH );
		setTitle( dad.frame == null?"":dad.frame.getTitle() ); 
		setUndecorated( true );
		setVisible( false );
	}
	
	
	private void setupMouseEvents( Component component ){
		// Forward all sorts of events... 
		component.addMouseMotionListener( new MouseMotionListener(){
			@Override
			public void mouseDragged( MouseEvent e ) {
				if( ( e = duplicate( e ) ) != null ) dad.mouseDragged( e ); 
			}

			@Override
			public void mouseMoved( MouseEvent e ){
				if( ( e = duplicate( e ) ) != null ) dad.mouseMoved( e ); 
			}
		} );
		
		component.addMouseListener( new MouseListener(){
			@Override
			public void mouseClicked(MouseEvent e) {
				if( ( e = duplicate( e ) ) != null ) dad.mouseClicked( e ); 
			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if( ( e = duplicate( e ) ) != null ) dad.mouseEntered( e ); 
			}

			@Override
			public void mouseExited(MouseEvent e) {
				if( ( e = duplicate( e ) ) != null ) dad.mouseExited( e ); 
			}

			@Override
			public void mousePressed(MouseEvent e) {
				if( ( e = duplicate( e ) ) != null ) dad.mousePressed( e ); 
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				if( ( e = duplicate( e ) ) != null ) dad.mouseReleased( e ); 
			}
		}); 
	}
	

	private void setupKeyEvents( Component component ){
		component.addKeyListener( new KeyListener(){
			@Override
			public void keyPressed(KeyEvent e) {
				dad.keyPressed( e ); 
			}

			@Override
			public void keyReleased(KeyEvent e) {
				dad.keyReleased( e ); 
			}

			@Override
			public void keyTyped(KeyEvent e) {
				dad.keyTyped( e ); 
			}
		}); 		
	}
	
	/**
	 * Duplicates a mouse event, but only if necessary!  
	 * @return 
	 */
	private MouseEvent duplicate( MouseEvent e ){
		Rectangle boundaries = ((Renderer) renderer).sketchBoundaries();
		
		if( boundaries.contains( e.getX(), e.getY() ) ){
			return new MouseEvent( 
				(Component) e.getSource(), 
				e.getID(), 
				e.getWhen(), 
				e.getModifiers(), 
				x + ( e.getX() - boundaries.x )*width/boundaries.width,
				y + ( e.getY() - boundaries.y )*height/boundaries.height,
				e.getXOnScreen(), 
				e.getYOnScreen(), 
				e.getClickCount(), 
				e.isPopupTrigger(), 
				e.getButton()
			); 
		}
		else{
			return null; 
		}
	}
	
	
	@Override
	public void requestFocus() {
		renderer.requestFocus(); 
	}
	
	
	@Override
	public void dispose() {
		((Renderer) renderer).die(); 
		
		super.dispose();
	}
	
	
	private class MacFixer extends JComponent{
		@Override
		public void paint(Graphics g) {
			g.setColor( Color.black ); 
			g.fillRect( 0, 0, getWidth(), getHeight() ); 
		}
		
		@Override
		public Dimension getPreferredSize(){
			return new Dimension( 200, 1 ); 
		}
		
		@Override
		public Dimension getMinimumSize() {
			return new Dimension( 200, 1 ); 
		}
		
		@Override
		public Dimension getMaximumSize() {
			return new Dimension( Integer.MAX_VALUE, 1 ); 
		}
	}
}

