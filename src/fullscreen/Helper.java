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
import java.lang.reflect.Method;

import processing.core.PApplet;

class Helper {
	
	/**
	 * Notifies the sketch about a display mode change. 
	 */
	public static void notifySketch( PApplet dad ){
		try{
			Method m = dad.getClass().getMethod( "displayChanged", new Class[]{ } );
			m.invoke( dad, new Object[]{ } );
		}
		catch( Exception e ){
			
		}
	}

}
