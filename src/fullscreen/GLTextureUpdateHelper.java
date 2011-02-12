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

import processing.core.PGraphics;
import processing.core.PGraphics3D;
import processing.core.PImage;

/**
 * Opengl tends to erase textures when grabbing/releasing exclusive fullscreen
 * mode. So this class here helps to update the textures (if in opengl mode)
 */

public class GLTextureUpdateHelper{
	
	/**
	 * Update the all the opengl textures belonging to a sketch imediately. 
	 * 
	 * @param fs The fullscreen object
	 * @throws NoSuchFieldException 
	 * @throws SecurityException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 */
	public static void update( FullScreenBase fs ){
		// Now, if in opengl mode all the textures will be erased, 
		// we'll have to call updatePixels() on all of them! 
		// Oh, and we have to use reflection, because we don't know if 
		// opengl is being used. So the class names might not even be there. 
		if( fs.isGL() ){
			try {
				PGraphics g = fs.getSketch().g;
				Field field = PGraphics3D.class.getDeclaredField( "textures" );
				field.setAccessible( true ); // make private variable public! 
				PImage textures[] = (PImage[]) field.get( g );
				
				for( int i = 0; i < textures.length; i++ ){
					if( textures[i] != null ){
						textures[i].updatePixels(); 
					}
				}
			}
			catch( SecurityException e ){
				System.err.println( "FullScreen-API: Unknown error: " + e.getMessage() );
				e.printStackTrace(); 
			}
			catch( NoSuchFieldException e ){
				System.err.println( "FullScreen-API: Couldn't find any field 'textures' in processing.core.PGraphics3D" );
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
		}
	}
}
