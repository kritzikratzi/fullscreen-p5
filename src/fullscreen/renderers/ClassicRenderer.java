package fullscreen.renderers;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;

import javax.swing.JComponent;

import processing.core.PApplet;
import fullscreen.FullScreenTools;

/**
 * The thing that takes care of painting the sketch when in fullscreen mode  
 * 
 * @author hansi
 */
public class ClassicRenderer extends JComponent implements Renderer{
	private static final long serialVersionUID = 1L;

	// The region this thing takes care for 
	private int width, height, x, y; 
	
	// The original sketch
	private PApplet dad; 
	
	// where is the sketch position? (like..scaled on the screen)
	public Rectangle boundaries = new Rectangle( 0, 0, 1, 1 );
	
	public ClassicRenderer( final PApplet dad, int x,int y, int width, int height ){
		dad.registerDraw( this );
		dad.registerPre( this ); 
		dad.registerPost( this ); 
		this.x = x; 
		this.y = y; 
		this.width = width; 
		this.height = height;
		this.dad = dad;
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
			boundaries = FullScreenTools.fit( width, height, getWidth(), getHeight() ); 
			//boundaries.x = ( getWidth() - width )/2; 
			//boundaries.y = ( getHeight() - height ) /2;
			
			// Draw the outer-black border like this 
			// makes video-sync less relevant, i hope. 
			// tearing will still be an issue, but another day! 
			g.setColor( Color.black ); 
			//g.fillRect( 0, 0, getWidth(), getHeight() );
			g.fillRect( 0, 0, getWidth(), boundaries.y );
			g.fillRect( 0, boundaries.y + height, getWidth(), boundaries.y ); 
			g.fillRect( 0, boundaries.y, boundaries.x, boundaries.y + height ); 
			g.fillRect( boundaries.x + width, boundaries.y, getWidth(), boundaries.y + height ); 
			
			
			// convenient! 
			// g.setClip( sketchX, sketchY, width, height ); 
			// g.drawImage( dad.g.image, sketchX - x, sketchY - y, null );
			
			// faster? 
			g.drawImage( dad.g.image, 
				boundaries.x, boundaries.y, // destination point 1
				boundaries.x + width, boundaries.y + height, // destination point 2
				x, y, // source point 1
				x + width, y + height, // source point 2
				null
			);
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


	@Override
	public Rectangle sketchBoundaries() {
		return boundaries; 
	}


	@Override
	public void die() {
		dad.unregisterDraw( this );
		dad.unregisterPre( this ); 
		dad.unregisterPost( this ); 
	}
	
}
