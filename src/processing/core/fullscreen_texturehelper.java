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
package processing.core; 

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Opengl tends to erase textures when grabbing/releasing exclusive fullscreen
 * mode. So this class here helps to update the textures (if in opengl mode)
 */

public class fullscreen_texturehelper{
  
	public static void update( PApplet dad ){
		// Now, if in opengl mode all the textures will be erased, 
		// we'll have to call updatePixels() on all of them! 
		// Oh, and we have to use reflection, because we don't know if 
		// opengl is being used. So the class names might not even be there. 
		if( dad.g.getClass().getName().equals( "processing.opengl.PGraphicsOpenGL" ) ){
			try{
				int texts[] = new int[1];
				                      
				Field glField = dad.g.getClass().getField( "gl" );
				Object gl = glField.get( dad.g ); 
				Method m = gl.getClass().getMethod( "glGenTextures", new Class[]{ int.class, int[].class, int.class } );
				m.invoke( gl, new Object[]{ new Integer(1), texts, new Integer(0) } );
				//System.out.println( "FullScreen API: TexID=" + texts[0] );
				
				PImage textures[] = null; 
				textures = (PImage[]) PGraphics3D.class.getDeclaredField( "textures" ).get( dad.g ); 
				for( int i = 0; i < textures.length; i++ ){
					if( textures[ i ] != null ){
						textures[ i ].updatePixels(); 
					}
				}
			}
			catch( Exception e ){
				System.err.println( "FullScreen API: Failed to update textures!" ); 
				e.printStackTrace(); 
			}
		}
	}
}
