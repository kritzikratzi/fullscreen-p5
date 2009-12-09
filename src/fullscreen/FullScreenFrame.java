package fullscreen;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import javax.swing.JComponent;
import javax.swing.JFrame;

import processing.core.PApplet;
import processing.opengl.PGraphicsOpenGL;

public class FullScreenFrame extends JFrame{
	private static final long serialVersionUID = 1L;
	
	// The original sketch
	private PApplet dad; 
	
	// And the painter
	private FullScreenPainter painter; 
	
	// Active? 
	private boolean active = false; 
		
	/**
	 * Creates a fullscreen-frame thing. 
	 * 
	 * @param dad The original sketch
	 */
	public FullScreenFrame( PApplet dad, int screenNr, int x, int y, int width, int height ){
		this.dad = dad;
		painter = new FullScreenPainter( dad, x, y, width, height );
		getContentPane().add( painter, BorderLayout.CENTER );
		
		GraphicsDevice device = FullScreenTools.getScreenDevice( screenNr ); 
		//setBounds( device.getDefaultConfiguration().getBounds() );
		setLocation( 0, 0 );
		pack(); 
		setTitle( "K" ); 
		//setUndecorated( true );
		setVisible( false ); 
	}
	
	@Override
	public void setVisible( boolean visible ){
		super.setVisible( visible );
		active = visible; 
	}

	@Override
	public void requestFocus() {
		painter.requestFocus(); 
	}
	
	/**
	 * The thing that takes care of painting the sketch when in fullscreen mode  
	 * 
	 * @author hansi
	 */
	public class FullScreenPainter extends JComponent{
		private static final long serialVersionUID = 1L;

		// The region this thing takes care for 
		private int width, height, x, y; 
		
		// The original sketch
		private PApplet dad; 
		
		// where is the sketch position? 
		private int sketchX = 0; 
		private int sketchY = 0;
		
		public FullScreenPainter( final PApplet dad, int x,int y, int width, int height ){
			dad.registerDraw( this );
			dad.registerPre( this ); 
			dad.registerPost( this ); 
			this.x = x; 
			this.y = y; 
			this.width = width; 
			this.height = height;
			this.dad = dad;
			
			// Forward all sorts of events... 
			addMouseMotionListener( new MouseMotionListener(){
				@Override
				public void mouseDragged( MouseEvent e ) {
					if( ( e = duplicate( e ) ) != null ) dad.mouseDragged( e ); 
				}

				@Override
				public void mouseMoved( MouseEvent e ){
					if( ( e = duplicate( e ) ) != null ) dad.mouseMoved( e ); 
				}
			} );
			addMouseListener( new MouseListener(){
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
			addKeyListener( new KeyListener(){
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
			if( 
				e.getX() >= sketchX && e.getX() <= sketchX + width && 
				e.getY() >= sketchY && e.getY() <= sketchY + height 
			){
				
				return new MouseEvent( 
					(Component) e.getSource(), 
					e.getID(), 
					e.getWhen(), 
					e.getModifiers(), 
					e.getX() - sketchX + x, 
					e.getY() - sketchY + y, 
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
		
		
		/**
		 * Before draw
		 */
		public void pre(){
			/*if( active ){
				sketchImage = dad.g.image == null? sketchImage:dad.g.image; 
				dad.g.image = null;
			}*/
		}
		
		/**
		 * After draw... 
		 */
		public void post(){
			/*dad.g.image = sketchImage == null? dad.g.image : sketchImage;*/ 
		}
		
		
		/**
		 * Draw some part to the frame...
		 */
		public void draw(){
			Graphics g = getGraphics(); 
			if( g != null ){
				sketchX = ( getWidth() - width )/2; 
				sketchY = ( getHeight() - height ) /2;
				
				// Draw the outer-black border like this 
				// makes video-sync less relevant, i hope. 
				// tearing will still be an issue, but another day! 
				g.setColor( Color.black ); 
				//g.fillRect( 0, 0, getWidth(), getHeight() );
				g.fillRect( 0, 0, getWidth(), sketchY ); 
				g.fillRect( 0, sketchY + height, getWidth(), sketchY ); 
				g.fillRect( 0, sketchY, sketchX, sketchY + height ); 
				g.fillRect( sketchX + width, sketchY, getWidth(), sketchY + height ); 
				
				
				// convenient! 
				// g.setClip( sketchX, sketchY, width, height ); 
				// g.drawImage( dad.g.image, sketchX - x, sketchY - y, null );
				
				// faster? 
				g.drawImage( dad.g.image, 
					sketchX, sketchY, // destination point 1
					sketchX + width, sketchY + height, // destination point 2
					x, y, // source point 1
					x + width, y + height, // source point 2
					null
				);
				
				// TODO: 
				// In case of opengl we need to somehow duplicate the context, 
				// or something weird like that. 
				// No idea exactly how that might work but hey... 
				// Shouldn't be too hard to figure out...
			}
		}

		@Override
		public void update(Graphics g) {
		}
		
		@Override
		public void paint( Graphics g ){
		}
		
		@Override
		public Dimension getMinimumSize() {
			return new Dimension( width, height ); 
		}
		
		@Override
		public Dimension getPreferredSize() {
			return getMinimumSize(); 
		}
		
	}
}

