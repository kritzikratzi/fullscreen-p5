// You can use the fullscreen api to find out which 
// resolutions are available on your computer
import fullscreen.*; 

FullScreen fs; 

void setup(){
  // set size to 640x480
  size(640, 480);

  // Create the fullscreen object
  fs = new FullScreen(this); 
  
  // list available resolutions
  println( "Resolutions: " ); 
  println( fs.getResolutions() ); 
  
  exit(); 
}
