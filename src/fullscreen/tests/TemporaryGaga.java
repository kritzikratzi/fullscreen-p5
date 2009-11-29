package fullscreen.tests;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferStrategy;

import javax.swing.JComponent;
import javax.swing.JFrame;

import processing.core.PApplet;

import fullscreen.SoftFullScreen;

public class TemporaryGaga {

	public static class SplitApplet extends Demo.LaunchablePApplet{
		
		SoftFullScreen fs; 
		
		public void setup(){
			size( 800, 600 ); 
		}
		
		
		public void draw(){
			background( 127 );
			fill( 255 ); 
			rect( 10, 10, width-20, height-20 ); 
			ellipse( mouseX, mouseY, 20, 20 ); 
		}
		
	}
	
	public static void main( String args[]){
		JFrame frame; 
		frame = new JFrame(); 
		frame.setVisible( true ); 
		PApplet dad = new SplitApplet();
		frame.getContentPane().add( new PAppletPart( dad, 0, 0, 400, 400 ), BorderLayout.CENTER ); 
		frame.pack(); 
	
	}
	
	public static class PAppletPart extends JComponent{
		private int width, height, x, y; 
		private PApplet dad; 
		
		// where is the sketch position? 
		private int sketchX = 0; 
		private int sketchY = 0; 
		
		public PAppletPart( final PApplet dad, int x,int y, int width, int height ){
			dad.registerDraw( this );
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
		}
		
		
		/**
		 * Duplicates a mouse event, but only if necessary!  
		 * @return 
		 */
		public MouseEvent duplicate( MouseEvent e ){
			if( 
				e.getX() >= sketchX && e.getX() <= sketchX + width && 
				e.getY() >= sketchY && e.getY() <= sketchY + height 
			){
				
				return new MouseEvent( 
					(Component) e.getSource(), 
					e.getID(), 
					e.getWhen(), 
					e.getModifiers(), 
					e.getX() - sketchX, 
					e.getY() - sketchY, 
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
				
				// fast? 
				g.drawImage( dad.g.image, 
					sketchX, sketchY, // destination point 1
					sketchX + width, sketchY + height, // destination point 2
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
		
	}
}