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

import processing.opengl.PGraphicsOpenGL;

/**
 * Opengl tends to erase textures when grabbing/releasing exclusive fullscreen
 * mode. So this class here helps to update the textures (if in opengl mode)
 */

public class GLTextureUpdateHelper{
	
	/**
	 * Update the all the opengl textures belonging to a sketch imediately. 
	 * 
	 * @param dad The sketch
	 */
	public static void update( PApplet dad ){
		// Now, if in opengl mode all the textures will be erased, 
		// we'll have to call updatePixels() on all of them! 
		// Oh, and we have to use reflection, because we don't know if 
		// opengl is being used. So the class names might not even be there. 
		if( dad.g.getClass().getName().equals( "processing.opengl.PGraphicsOpenGL" ) ){
			PGraphicsOpenGL g = (PGraphicsOpenGL) dad.g;
			
			PImage[] textures = g.textures; 
			for( int i = 0; i < textures.length; i++ ){
				if( textures[i] != null ){
					textures[i].updatePixels(); 
				}
			}
		}
	}
}
