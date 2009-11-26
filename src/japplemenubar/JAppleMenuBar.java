/*
 Part of jAppleMenuBar
 
 Copyright (c) 2006-09 Hansi Raber
 
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

package japplemenubar;

/**
 * Starting point for the application. General initialization should be done inside
 * the ApplicationController's init() method. If certain kinds of non-Swing initialization
 * takes too long, it should happen in a new Thread and off the Swing event dispatch thread (EDT).
 * 
 * @author hansi
 */
public class JAppleMenuBar {
	static{
		System.loadLibrary( "jAppleMenuBar" ); 
	}
	
	public native void setVisible( boolean visibility ); 
}
