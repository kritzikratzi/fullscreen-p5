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
//
//  NativeOSX.java
//
//  Created by hansi on 29.10.07.
//  Copyright (c) 2007 __MyCompanyName__. All rights reserved.
//

package fullscreen; 

import javax.swing.*; 

public class NativeOSX {

    static {
    	System.out.println( "loading libhide..." );
    	System.out.println( System.getProperty( "java.library.path" ) ); 
        // Ensure native JNI library is loaded
        System.loadLibrary("hide_menubar");
    }

    public NativeOSX() {
        System.out.println("JNIWrapper instance created");
    }

	native void setVisible( boolean arg ); 

    public static void main (String args[]) {
        // insert code here...
        System.out.println("Started JNIWrapper");
        NativeOSX newjni = new NativeOSX();
        newjni.setVisible(false);
		
		JFrame frame = new JFrame( "hey" );
		frame.setDefaultCloseOperation( JFrame.EXIT_ON_CLOSE ); 
		frame.setSize( 320, 240 ); 
		frame.setLocation( 0, 0 ); 
		frame.setUndecorated( true );
		frame.setVisible( true );
    }
}