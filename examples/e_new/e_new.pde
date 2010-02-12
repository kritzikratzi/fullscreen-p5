// Font's don't screw up anymore... yey! 
import fullscreen.*; 

FullScreen fs; 
PFont font; 

void setup(){
  // set size to 640x480
  size(640, 480);

  // 5 fps
  frameRate(30);

  // Create the fullscreen object
  fs = new FullScreen(this); 

  // enter fullscreen mode
  fs.enter(); 
  
  // Create a new font
  font = loadFont( "Serif-24.vlw" ); 
  textFont( font ); 
}


void draw(){
  background(0);
  fill(255, 0, 0);

  for(int i = 0; i < width; i += 20 ){
    for( int j = 0; j < height; j+= 20 ){
      char text = (char)((i*width/20+j+frameCount)%255); 
      text( text, i, j ); 
    }
  }
}
