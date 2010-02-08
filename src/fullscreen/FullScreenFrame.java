package fullscreen;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.GraphicsDevice;
import java.awt.Rectangle;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

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
			PGraphicsOpenGL g = (PGraphicsOpenGL) dad.g; 
			renderer = new GLRenderer( dad, g.getContext(), x, y, width, height );
		}
		else{
			renderer = new ClassicRenderer( dad, x, y, width, height );
		}
		getContentPane().add( renderer, BorderLayout.CENTER );
		setupEvents(); 
		
		GraphicsDevice device = FullScreenTools.getScreenDevice( screenNr ); 
		setBounds( device.getDefaultConfiguration().getBounds() );
		setExtendedState( MAXIMIZED_BOTH );
		setTitle( dad.frame == null?"":dad.frame.getTitle() ); 
		setUndecorated( true );
		setVisible( false ); 
	}
	
	
	private void setupEvents(){
		// Forward all sorts of events... 
		renderer.addMouseMotionListener( new MouseMotionListener(){
			@Override
			public void mouseDragged( MouseEvent e ) {
				if( ( e = duplicate( e ) ) != null ) dad.mouseDragged( e ); 
			}

			@Override
			public void mouseMoved( MouseEvent e ){
				if( ( e = duplicate( e ) ) != null ) dad.mouseMoved( e ); 
			}
		} );
		
		renderer.addMouseListener( new MouseListener(){
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
		
		renderer.addKeyListener( new KeyListener(){
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
}

