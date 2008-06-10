// processing will show/hide the cursor whenever you 
// go to fullscreen mode / leave fullscreen mode. 
// here's an example how you can supress this behaviour
import fullscreen.*; 

FullScreen fs; 

void setup(){
  // set size to 640x480
  size(640, 480);

  // 5 fps
  frameRate(5);

  // Create the fullscreen object
  fs = new FullScreen(this); 
  
  noCursor(); 
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

// every time the display mode changes hide the cursor! 
void displayChanged(){
  noCursor(); 
}
