package fullscreen.tests;

import java.awt.GraphicsEnvironment;
import java.awt.Point;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.lang.reflect.InvocationTargetException;

import japplemenubar.JAppleMenuBar;

import org.junit.Test;
import static org.junit.Assert.*; 

import processing.core.PApplet;

import fullscreen.FullScreen;
import fullscreen.FullScreenBase;
import fullscreen.SoftFullScreen;

/**
 * Tests a few things semi-automatically... 
 * @author hansi
 */
public class Tests {

	final int SCREEN_W = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getWidth(); 
	final int SCREEN_H = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode().getHeight(); 

	/**
	 * Tests if hiding the menu-bar works on mac-os
	 * @throws InterruptedException 
	 */
	@Test
	public void testMenuBar() throws InterruptedException{
		if( PApplet.platform == PApplet.MACOSX ){
			JAppleMenuBar menuBar = new JAppleMenuBar(); 
			menuBar.setVisible( false ); 
			Thread.sleep( 500 ); 
			menuBar.setVisible( true ); 
		}
	}
	
	/**
	 * Creates a SoftFullScreen object on screen 0 and 1.
	 */
	@Test
	public void softFS() throws Exception{
		for( int screenNr = 0; screenNr < 2; screenNr ++ ){
			Demo.Simple sketch = new Demo.Simple( "SoftFullScreen: screen " + screenNr );
			SoftFullScreen fs = new SoftFullScreen( sketch, screenNr );
			fs.setFullScreen( true );
			
			Thread.sleep( 2000 ); 
			fs.setFullScreen( false ); 
			
			Thread.sleep( 2000 );
			
			killSketch( sketch );  
		}
		// if we haven't crashed until now we're good! 
	}
	
	/**
	 * Creates the "classic" FS object on screen 0 and 1. 
	 */
	@Test
	public void classicFS() throws Exception{ 
		for( int screenNr = 0; screenNr < 2; screenNr ++ ){
			Demo.Simple sketch = new Demo.Simple( "FSEM: screen " + screenNr );
			FullScreen fs = new FullScreen( sketch, screenNr );
			fs.setFullScreen( true );
			
			Thread.sleep( 2000 ); 
			fs.setFullScreen( false ); 
			
			Thread.sleep( 2000 );
			
			killSketch( sketch );  
		}
	}
	
	/**
	 * Make's sure the framerate is >30fps. 
	 * Bug: http://github.com/kritzikratzi/fullscreen-p5/issues#issue/10
	 */
	@Test
	public void issue10_1() throws Exception{
		assertFrameRate( SCREEN_W, SCREEN_H, FullScreen.class ); 
	}
	
	@Test
	public void issue10_2() throws Exception{
		assertFrameRate( 640, 480, FullScreen.class ); 
	}
	
	@Test
	public void issue10_3() throws Exception{
		assertFrameRate( SCREEN_W, SCREEN_H, SoftFullScreen.class ); 
	}
	
	@Test
	public void issue10_4() throws Exception{
		assertFrameRate( 640, 480, SoftFullScreen.class ); 
	}
	
	/**
	 * Tests if the framerate is larger than a certain value. 
	 * @throws NoSuchMethodException 
	 * @throws InvocationTargetException 
	 * @throws IllegalAccessException 
	 * @throws InstantiationException 
	 * @throws SecurityException 
	 * @throws IllegalArgumentException 
	 * @throws InterruptedException 
	 */
	private void assertFrameRate( int width, int height, Class<? extends FullScreenBase> fsClass ) throws IllegalArgumentException, SecurityException, InstantiationException, IllegalAccessException, InvocationTargetException, NoSuchMethodException, InterruptedException{
		String title = "Framerate using " + fsClass.getName() + " [" + width + "x" + height + "]"; 
		Demo.Simple sketch = new Demo.Simple( title, width, height, PApplet.OPENGL );
		sketch.frameRate( 100 );
		
		FullScreenBase fs = fsClass.getConstructor( PApplet.class ).newInstance( sketch ); 
		fs.setFullScreen( true );  
		
		Thread.sleep( 2000 );
		float fps = sketch.frameRate; 
		fs.leave(); 
		killSketch( sketch ); 

		if( fps < 90 ){
			fail( 
				"Framerate too low on " + fsClass.getName() + ": " + 
				"[" + width + "x" + height + "@" + sketch.frameRate + "fps]" 
			); 
		}
		
	}
	
	/**
	 * Tests if the frame really pauses when iconified, and also that it automatically continues when deiconified
	 */
	@Test
	public void iconify() throws Exception{
		Demo.Simple sketch = new Demo.Simple( "iconify", 640, 480, PApplet.JAVA2D );
		sketch.frameRate( 60 );
		
		SoftFullScreen fs = new SoftFullScreen( sketch );
		Thread.sleep( 2000 ); 
		
		fs.minimize(); 
		int startFrameNum = sketch.frameCount; 
		Thread.sleep( 2000 ); 
		int finalFrameNum = sketch.frameCount; 
		fs.restore(); 
		
		
		if( finalFrameNum - startFrameNum > 10 ){
			fail( "Sketch didn't pause when iconified! [" + (finalFrameNum - startFrameNum) + " frames were drawn]" );
		}
		
		startFrameNum = sketch.frameCount; 
		Thread.sleep( 2000 ); 
		finalFrameNum = sketch.frameCount; 
		
		if( finalFrameNum - startFrameNum < 10 ){
			fail( "Sketch didn't resume when restored! [" + (finalFrameNum - startFrameNum ) + " frames were drawn]" );  
		}

		killSketch( sketch ); 
	}
	
	
	/**
	 * Tests if kiosk mode works. 
	 */
	@Test
	public void kioskMode() throws Exception {
		Demo.Simple sketch = new Demo.Simple( "kiosk mode", 640, 480, PApplet.JAVA2D );
		if( PApplet.platform == PApplet.MACOSX ){
			sketch.frameRate( 60 );
			
			SoftFullScreen fs = new SoftFullScreen( sketch );
			fs.setKioskMode(  true  ); 
			fs.enter();
			
			Thread.sleep( 500 );
			Robot r = new Robot(); 
			r.setAutoDelay( 100 ); 
			
			// let's get the keyboard focus by clicking our sketch
			Point where = sketch.getLocationOnScreen(); 
			r.mouseMove( where.x + 100, where.y + 100 );  
			r.mousePress(  MouseEvent.BUTTON1_MASK );
			r.mouseRelease( MouseEvent.BUTTON1_MASK );
			Thread.sleep( 5000 ); 

			// press apple+tab
			/*r.keyPress( KeyEvent.VK_META );
			r.keyPress( KeyEvent.VK_Q );
			r.keyRelease( KeyEvent.VK_META ); 
			r.keyRelease(  KeyEvent.VK_Q );*/
			
			//r.mousePress(  MouseEvent.BUTTON1_MASK );
			//r.mouseRelease( MouseEvent.BUTTON1_MASK );
			
			// yea, this doesn't really check anything at this point
			if( !sketch.hasFocus() ){
				fail( "Kiosk mode not working [frame should have focus after pressing apple+tab, but it doesn't]" ); 
			}
			
			fs.leave(); 
		}
		
		killSketch( sketch ); 
	}
	
	/**
	 * Kill a sketch
	 */
	private void killSketch( PApplet sketch ){
		sketch.frame.setVisible( false ); 
		sketch.stop(); 
		sketch.destroy(); 
	}
}