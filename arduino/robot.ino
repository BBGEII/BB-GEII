#define PORT_SPEED_M1 5 
#define PORT_SPEED_M2 6 
#define PORT_DIRECTION_M1 4 
#define PORT_DIRECTION_M2 7
#define FORWARD 1 
#define BACKWARD 0

// define which serial is used by the bluetooth adapter
#define BLUETOOTH Serial1
// size of the messages
#define SIZE_BUF 10

void setup()
{
  pinMode(PORT_SPEED_M1, OUTPUT); 
  pinMode(PORT_SPEED_M2, OUTPUT); 
  pinMode(PORT_DIRECTION_M1, OUTPUT); 
  pinMode(PORT_DIRECTION_M2, OUTPUT);
  //Initialisation of serial for debug
  Serial.begin(115200);
  //Initialisation of bluetooth adapter
  BLUETOOTH.begin(9600); 
}

void loop()
{
  static int iLpReception = 0;
  static int iLp;
  int iR;
  int iL;
  char cVal;
  static char strFrame[SIZE_BUF]="_________";

  if(BLUETOOTH.available() > 0){

    // The frame is read character per character
    strFrame[iLpReception] = BLUETOOTH.read();
    
    if(strFrame[iLpReception] == '\0') {	// search end of frame
        // iRentification of the first character of the frame
        cVal = strFrame[0];  
        
        // Shifting the frame to the left
        for(iLp=0;iLp<iLpReception;iLp++){
          strFrame[iLp]=strFrame[iLp+1];
        }
        
        // Controlling the motors depending on the first character received
        if(cVal=='l'){
          // Converting to int
          iL=atoi(strFrame);
          Serial.print("iL : ");
          Serial.println(iL);
          if(iL<0){
            // Changing direction when the frame received is negative
            digitalWrite(PORT_DIRECTION_M1, BACKWARD);
            iL=iL*(-1);
          }
          else{
            digitalWrite(PORT_DIRECTION_M1, FORWARD);
          }
          analogWrite(PORT_SPEED_M1, iL); 
        }
        
        if(cVal=='r'){
          // Converting to int
          iR=atoi(strFrame);
          Serial.print("iR : ");
          Serial.println(iR);
          if(iR<0){
            // Changing direction when the frame received is negative
            digitalWrite(PORT_DIRECTION_M2, BACKWARD);
            iR=iR*(-1);
          }
          else{
            digitalWrite(PORT_DIRECTION_M2, FORWARD);
          }
          analogWrite(PORT_SPEED_M2, iR);
        }
        iLpReception=0;
    }
    else {    // Keep reading the frame if not finished
      iLpReception++;
    }
    
  }
   
}
