/*
  Part of the Processing Fullscreen API

  Copyright (c) 2006-08 Hansi Raber

  This library is free software; you can redistribute it and/or
  modify it under the terms of the GNU General Public
  License as published by the Free Software Foundation; either
  version 3 of the License, or (at your option) any later version.

  This library is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
  Lesser General Public License for more details.

  You should have received a copy of the GNU General
  Public License along with this library; if not, write to the
  Free Software Foundation, Inc., 59 Temple Place, Suite 330,
  Boston, MA  02111-1307  USA
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
