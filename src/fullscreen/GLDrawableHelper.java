/*
  Part of the Processing Fullscreen API

  The MIT License

  Copyright (c) 2011, hansi raber.

  Permission is hereby granted, free of charge, to any person obtaining a copy
  of this software and associated documentation files (the "Software"), to deal
  in the Software without restriction, including without limitation the rights
  to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
  copies of the Software, and to permit persons to whom the Software is
  furnished to do so, subject to the following conditions:

  The above copyright notice and this permission notice shall be included in
  all copies or substantial portions of the Software.

  THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
  IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
  FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
  AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
  LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
  OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
  THE SOFTWARE.
*/
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
