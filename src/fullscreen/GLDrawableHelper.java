package fullscreen;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import processing.core.PGraphics;
import processing.opengl.PGraphicsOpenGL;

public class GLDrawableHelper {

	public static void reAllocate( FullScreenBase fs ){
		if( fs.isGL() ){
			// The next lines are a reflection based version of this: 
			// ( (PGraphicsOpenGL) fs.getSketch().g).context = null; 
			// ( (PGraphicsOpenGL) fs.getSketch().g).allocate();
			//
			// which isn't allowed because access to these fields is private :( 
			try{
				PGraphics g = fs.getSketch().g;
				Field context = PGraphicsOpenGL.class.getDeclaredField( "context" );
				context.setAccessible( true ); // make private public
				context.set( g, null );
				
				Method allocate = PGraphicsOpenGL.class.getDeclaredMethod( "allocate" );
				allocate.setAccessible( true );
				allocate.invoke( g );
			}
			catch( SecurityException e ){
				System.err.println( "FullScreen-API: Unknown error: " + e.getMessage() );
				e.printStackTrace(); 
			}
			catch( NoSuchFieldException e ){
				System.err.println( "FullScreen-API: Couldn't find any field 'context' in processing.opengl.PGraphicsOpenGL" );
				System.err.println( "Seems your processing version doesn't match this version of the fullscreen api" );
				e.printStackTrace();
			}
			catch( IllegalArgumentException e ){
				System.err.println( "FullScreen-API: Unknown error: " + e.getMessage() );
				e.printStackTrace();
			}
			catch( IllegalAccessException e ){
				System.err.println( "FullScreen-API: Unknown error: " + e.getMessage() );
				e.printStackTrace();
			}
			catch (NoSuchMethodException e) {
				System.err.println( "FullScreen-API: Couldn't find any method 'allocate' in processing.opengl.PGraphicsOpenGL" );
				System.err.println( "Seems your processing version doesn't match this version of the fullscreen api" );
				e.printStackTrace();
			}
			catch ( InvocationTargetException e ){
				System.err.println( "FullScreen-API: Unknown error: " + e.getMessage() );
				e.printStackTrace();
			}
		}
	}
}
