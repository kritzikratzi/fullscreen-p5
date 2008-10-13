package processing.opengl;

import fullscreen.FullScreenBase;

public class GLDrawableHelper {

	public static void reAllocate( FullScreenBase fs ){
		if( fs.isGL() ){
			( (PGraphicsOpenGL) fs.getSketch().g).context = null; 
			( (PGraphicsOpenGL) fs.getSketch().g).allocate();
		}
	}
}
