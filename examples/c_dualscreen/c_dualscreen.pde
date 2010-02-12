// demonstrates the use of a sketch that spans over two screens. 
// you need to manually set the resolution of each screen to 
// 1024x768 (both screens need to be the same!)
// then run this sketch. 

import fullscreen.*; 

FullScreen fs; 

void setup(){
  // set size to 640x480
  size(2048, 768);

  // 5 fps
  frameRate(5);

  // Create the fullscreen object
  fs = new FullScreen(this); 
  fs.setScreens( 
    0, 0, 0, 1024, 768, // map the rectangle(0,0,1024,768) to screen 0
    1, 1024, 0, 1024, 768 // map the rectangle (1024,0,1024,768) to screen 1
  ); 
  
  // enter fullscreen mode
  fs.enter(); 
}


void draw(){
  background(0);
  fill(255, 0, 0);

  for(int i = 0; i < 10; i++){
    fill(
      random(255),
      random(255),
      random(255)
    );
    rect(
      i*10, i*10,
      width - i*20, height - i*20
    );
  }
}
