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


/**
 * This class hides/shows the menubar on osx system. 
 * 
 * @author hansi
 */
public class NativeOSX {

    static {
        // Ensure native JNI library is loaded
        System.loadLibrary("hide_menubar");
    }

    public NativeOSX() {
    }

	native void setVisible( boolean arg ); 
}