package fullscreen.tests;

import processing.core.PApplet;
import fullscreen.SoftFullScreen;

public class TemporaryGaga {
	static int texID; 
	
	public static void main( String args[]) throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException{
		final Demo.Simple demo = new Demo.Simple( 800, 800, PApplet.OPENGL );
		SoftFullScreen fs = new SoftFullScreen( demo );
	}
}