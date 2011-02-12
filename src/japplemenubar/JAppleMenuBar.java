/*
  Part of the jAppleMenuBar

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
	
	public native void setVisible( boolean visibility, boolean kioskMode ); 

	public void setVisible( boolean visibility ){
		// Keep original API in-tact.  Default kiosk-mode to off.
		setVisible( visibility, false );
	}
}
