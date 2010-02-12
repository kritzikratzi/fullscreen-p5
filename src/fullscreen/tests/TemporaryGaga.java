package fullscreen.tests;

import processing.core.PApplet;
import fullscreen.FullScreen;

public class TemporaryGaga {
	static int texID; 
	
	public static void main( String args[]) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		final Demo.Simple demo = new Demo.Simple( 800, 800, PApplet.OPENGL );
		FullScreen fs = new FullScreen( demo );
	}
}