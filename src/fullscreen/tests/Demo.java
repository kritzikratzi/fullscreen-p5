package fullscreen.tests;

import java.awt.Color;
import java.awt.Frame;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.Insets;
import java.awt.SystemColor;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import processing.core.PApplet;

/**
 * This class contains PApplets that can be used to test 
 * the fullscreen-api. 
 *  
 * @author hansi
 */
public class Demo{
	private static final long serialVersionUID = 1L;
	
	/**
	 * This is a simple Sketch using the JAVA-2D renderer
	 * @author hansi
	 *
	 */
	public static class Simple extends LaunchablePApplet{
		private static final long serialVersionUID = 1L;

		@Override
		public void setup(){
			size( 400, 400 ); 
		}
		
		@Override
		public void draw(){
			noStroke(); 
			
			for( int i = 0; i < min( width/2, height/2 ); i+= 20 ){
				fill( ( i/5 + frameCount )%255 ); 
				rect( i, i, width - 2*i, height - 2*i ); 
			}
		}
		
	}

	/**
	 * This is a PApplet that actually launches itself when instantized.
	 * (That means no trouble with the main-method...)  
	 */
	private static class LaunchablePApplet extends PApplet{
		private static final long serialVersionUID = 1L;

		public LaunchablePApplet(){
			// Disable abyssmally slow Sun renderer on OS X 10.5.
			if ( PApplet.platform == PApplet.MACOSX ){
				// Only run this on OS X otherwise it can cause a permissions error.
				// http://dev.processing.org/bugs/show_bug.cgi?id=976
				System.setProperty("apple.awt.graphics.UseQuartz", "true");
			}

			Color backgroundColor = Color.BLACK;
			GraphicsDevice displayDevice = null;

			// try to get the user folder. if running under java web start,
			// this may cause a security exception if the code is not signed.
			// http://processing.org/discourse/yabb_beta/YaBB.cgi?board=Integrate;action=display;num=1159386274
			String folder = null;
			try {
				folder = System.getProperty("user.dir");
			} catch (Exception e) { }

			if (displayDevice == null) {
				GraphicsEnvironment environment =
					GraphicsEnvironment.getLocalGraphicsEnvironment();
				displayDevice = environment.getDefaultScreenDevice();
			}

			Frame frame = new Frame(displayDevice.getDefaultConfiguration());
			frame.setResizable(false);
			frame.setTitle( "Applet" );

			// these are needed before init/start
			this.frame = frame;
			this.sketchPath = folder;
			this.args = new String[]{};  

			frame.setLayout(null);
			frame.add(this);
			frame.pack();
			init();

			while (this.defaultSize && !this.finished) {
				//System.out.println("default size");
				try {
					Thread.sleep(5);

				} catch (InterruptedException e) {
					//System.out.println("interrupt");
				}
			}
			//println("not default size " + this.width + " " + this.height);
			//println("  (g width/height is " + this.g.width + "x" + this.g.height + ")");

			// if not presenting
			Insets insets = frame.getInsets();

			int windowW = Math.max(this.width, PApplet.MIN_WINDOW_WIDTH) +
			insets.left + insets.right;
			int windowH = Math.max(this.height, PApplet.MIN_WINDOW_HEIGHT) +
			insets.top + insets.bottom;

			frame.setSize(windowW, windowH);

			// just center on screen
			frame.setLocation((this.screen.width - this.width) / 2,
					(this.screen.height - this.height) / 2);

			if (backgroundColor == Color.black) {  //BLACK) {
				// this means no bg color unless specified
				backgroundColor = SystemColor.control;
			}
			frame.setBackground(backgroundColor);

			int usableWindowH = windowH - insets.top - insets.bottom;
			this.setBounds((windowW - this.width)/2,
					insets.top + (usableWindowH - this.height)/2,
					this.width, this.height);

			// !external
			frame.addWindowListener(new WindowAdapter() {
				public void windowClosing(WindowEvent e) {
					System.exit(0);
				}
			});
			// handle frame resizing events
			this.setupFrameResizeListener();

			// all set for rockin
			if (this.displayable()) {
				frame.setVisible(true);
			}
		this.requestFocus(); // ask for keydowns
		//System.out.println("exiting main()");
	}
}
}
