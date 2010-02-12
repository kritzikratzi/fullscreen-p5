// assumes that screen 0 runs at 800x600
// and screen 1 runs at 1024x768

import fullscreen.*; 

FullScreen fs; 

void setup(){
  // set size to 640x480
  size(800+1024, 768);

  // 5 fps
  frameRate(5);

  // Create the fullscreen object
  fs = new FullScreen(this); 
  fs.setScreens( 
    0, 0, 0, 800, 600, // map the rectangle(0,0,800,600) to screen 0
    1, 1024, 0, 1024, 768 // map the rectangle (800,0,1024,768) to screen 1
  ); 
  
  // enter fullscreen mode
  fs.enter(); 
}


void draw(){
  background(0);
  fill(255, 0, 0);
  // draw graphics for screen 0
  stroke( 255 ); 
  line( 0, 0, 800, 600 ); 
  line( 800, 0, 0, 600 ); 
  
  // draw graphics for screen 1
  translate( 800, 0 ); 
  for(int i = 0; i < 10; i++){
    fill(
      random(255),
      random(255),
      random(255)
    );
    rect(
      i*10, i*10,
      1024 - i*20, height - i*20
    );
  }
}
