

#include <Adafruit_NeoPixel.h>
#include <ESP8266WiFi.h> // WiFi Library


//const char* ssid = "adega"; // Name of WiFi Network
//const char* password = "adegaadega"; // Password of WiFi Network


const char* ssid = "adega"; // Name of WiFi Network
const char* password = "adegaadega"; // Password of WiFi Network

int firstrun = 0; // Check if system was just powered on
int buttonpressed = 5; // To hold which button was pressed on Web Page





#define PIN 14

// Parameter 1 = number of pixels in strip
// Parameter 2 = Arduino pin number (most are valid)
// Parameter 3 = pixel type flags, add together as needed:
//   NEO_KHZ800  800 KHz bitstream (most NeoPixel products w/WS2812 LEDs)
//   NEO_KHZ400  400 KHz (classic 'v1' (not v2) FLORA pixels, WS2811 drivers)
//   NEO_GRB     Pixels are wired for GRB bitstream (most NeoPixel products)
//   NEO_RGB     Pixels are wired for RGB bitstream (v1 FLORA pixels, not v2)
//   NEO_RGBW    Pixels are wired for RGBW bitstream (NeoPixel RGBW products)
Adafruit_NeoPixel strip = Adafruit_NeoPixel(20, PIN,  NEO_GRB + NEO_KHZ800);

// IMPORTANT: To reduce NeoPixel burnout risk, add 1000 uF capacitor across
// pixel power leads, add 300 - 500 Ohm resistor on first pixel's data input
// and minimize distance between Arduino and first pixel.  Avoid connecting
// on a live circuit...if you must, connect GND first.




WiFiServer server(80); // Define Web Server Port

void setup() {
Serial.begin(115200);
delay(10);

strip.begin();
strip.show(); // Initialize all pixels to 'off'

  colorWipe(strip.Color(255, 0, 0), 300); // RED
  blank();
  
Serial.println();
Serial.println();

// Connect to WiFi network
Serial.print("Connecting to ");
Serial.println(ssid);
WiFi.begin(ssid, password);

// Wait until connected to WiFi
while (WiFi.status() != WL_CONNECTED) {
delay(250);
Serial.print(".");
}

// Confirmation that WiFi is connected
Serial.println("");
Serial.println("WiFi connected");

// Start the Web Server
server.begin();
Serial.println("Web Server Started");

// Display IP address
Serial.print("You can connect to the Server here: ");
Serial.print("http://");
Serial.print(WiFi.localIP());
Serial.println();
Serial.println();


  colorWipe(strip.Color(0, 0, 0, 255), 10); // White RGBW

  blank();

}


void loop() {

// Check if someone is connected
WiFiClient client = server.available();
if (!client) {
return;
}

// Read which button was pressed on the Web Page
String request = client.readStringUntil('\r');

// Light Up leds based on the button pressed 
if (request.indexOf("/WIPE") != -1) {
colorWipe(strip.Color(255, 255, 255), 500);
buttonpressed = LOW;
firstrun=1;
}
if (request.indexOf("/RAINBOW") != -1) {
rainbow(1);
buttonpressed = HIGH;
firstrun=1;
}
if (request.indexOf("/BLANK") != -1) {
blank();
buttonpressed = HIGH;
firstrun=1;
}

if (request.indexOf("=") != -1) {
  String ledStr = request.substring(request.indexOf('(')+1,request.indexOf('='));
  Serial.println(ledStr);
  String colorStr = request.substring(request.indexOf('=')+1,request.indexOf(')'));
  Serial.println(colorStr);
  
  
  int led = ledStr.toInt();
  if(led==0){
    blank();
  }
  else{
    led=led-1;
    if(colorStr.indexOf("OFF")!= -1){
        color(led,strip.Color(0, 0, 0),50); 
    }
    else if(colorStr.indexOf("RED")!= -1){
        color(led,strip.Color(255, 0, 0),50); 
    }
    else if(colorStr.indexOf("GREEN")!= -1){
        color(led,strip.Color(0, 255, 0),50); 
    }
    else if(colorStr.indexOf("BLUE")!= -1){
        color(led,strip.Color(0, 0, 255),50); 
    }
    else if(colorStr.indexOf("WHITE")!= -1){
        color(led,strip.Color(255, 255, 255),50); 
    }
  }
}

// Create Web Page
client.println("HTTP/1.1 200 OK"); // HTML Header
client.println("Content-Type: text/html");
client.println("");
client.println("<!DOCTYPE HTML>");

client.println("<html>"); // Start of HTML

client.println("<style>");
client.println("body {background-color: #ACAEAD;}"); // Set Background Color
client.println("body {font-size: 50px;}"); // Set font size
client.println("</style>");

client.println("<html>");
client.println("<head>");
client.println("<title>MINHA ADEGA (20 garrafas)</title>");
client.println("</head>");

//


//client.println("<br>");
client.println("<a href=\"/BLANK\"\"><button>Apagar </button></a>");
client.println("<a href=\"/WIPE\"\"><button>Teste 1 </button></a>");
client.println("<a href=\"/RAINBOW\"\"><button>Teste 2 </button></a><br />"); 


client.println("<table>");
client.println("<caption>Garrafas</caption>");
client.println("<tr>");
client.println("<td><a href=\"/(1=WHITE)\"\"><button> 01 </button></a> </td> <td><a href=\"/(2=WHITE)\"\"><button> 02 </button></a> </td> <td><a href=\"/(3=WHITE)\"\"><button> 03 </button></a> </td> <td><a href=\"/(4=WHITE)\"\"><button> 04 </button></a> </td> <td> <a href=\"/(5=WHITE)\"\"><button> 05 </button></a> </td>");
client.println("</tr>");
client.println("<tr>"); 
client.println("<td><a href=\"/(6=WHITE)\"\"><button> 06 </button></a> </td> <td><a href=\"/(7=WHITE)\"\"><button> 07 </button></a> </td> <td><a href=\"/(8=WHITE)\"\"><button> 08 </button></a> </td> <td><a href=\"/(9=WHITE)\"\"><button> 09 </button></a> </td> <td> <a href=\"/(10=WHITE)\"\"><button> 10 </button></a> </td>");
client.println("</tr>");
client.println("<tr>"); 
client.println("<td><a href=\"/(11=WHITE)\"\"><button> 11 </button></a> </td> <td><a href=\"/(12=WHITE)\"\"><button> 12 </button></a> </td> <td><a href=\"/(13=WHITE)\"\"><button> 13 </button></a> </td> <td><a href=\"/(14=WHITE)\"\"><button> 14 </button></a> </td> <td> <a href=\"/1(15=WHITE)\"\"><button> 15 </button></a> </td>");
client.println("</tr>");
client.println("<tr>"); 
client.println("<td><a href=\"/(16=WHITE)\"\"><button> 16 </button></a> </td> <td><a href=\"/(17=WHITE)\"\"><button> 17 </button></a> </td> <td><a href=\"/(18=WHITE)\"\"><button> 18 </button></a> </td> <td><a href=\"/(19=WHITE)\"\"><button> 19 </button></a> </td> <td> <a href=\"/(20=WHITE)\"\"><button> 20 </button></a> </td>");
client.println("</tr>");
client.println("</table>");




client.println("</html>"); 
delay(10); 

}


// Fill the dots one after the other with a color
void blank() {
  for(uint16_t i=0; i<strip.numPixels(); i++) {
    strip.setPixelColor(i, strip.Color(0, 0, 0));
  }
  strip.show(); // Initialize all pixels to 'off'
}


// Fill the dots one after the other with a color
void color(uint32 idx, uint32_t c, uint8_t wait) {
    strip.setPixelColor(idx, c);
    strip.show();
    delay(wait);
}


// Fill the dots one after the other with a color
void colorWipe(uint32_t c, uint8_t wait) {
  for(uint16_t i=0; i<strip.numPixels(); i++) {
    strip.setPixelColor(i, c);
    strip.show();
    delay(wait);
    blank();
  }
}

void rainbow(uint8_t wait) {
  uint16_t i, j;

  for(j=0; j<256; j++) {
    for(i=0; i<strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel((i+j) & 255));
    }
    strip.show();
    delay(wait);
    delay(wait);
    delay(wait);
    delay(wait);
  }
  blank();
}

// Slightly different, this makes the rainbow equally distributed throughout
void rainbowCycle(uint8_t wait) {
  uint16_t i, j;

  for(j=0; j<256*5; j++) { // 5 cycles of all colors on wheel
    for(i=0; i< strip.numPixels(); i++) {
      strip.setPixelColor(i, Wheel(((i * 256 / strip.numPixels()) + j) & 255));
    }
    strip.show();
    delay(wait);
  }
}

//Theatre-style crawling lights.
void theaterChase(uint32_t c, uint8_t wait) {
  for (int j=0; j<10; j++) {  //do 10 cycles of chasing
    for (int q=0; q < 3; q++) {
      for (uint16_t i=0; i < strip.numPixels(); i=i+3) {
        strip.setPixelColor(i+q, c);    //turn every third pixel on
      }
      strip.show();

      delay(wait);

      for (uint16_t i=0; i < strip.numPixels(); i=i+3) {
        strip.setPixelColor(i+q, 0);        //turn every third pixel off
      }
    }
  }
}

//Theatre-style crawling lights with rainbow effect
void theaterChaseRainbow(uint8_t wait) {
  for (int j=0; j < 256; j++) {     // cycle all 256 colors in the wheel
    for (int q=0; q < 3; q++) {
      for (uint16_t i=0; i < strip.numPixels(); i=i+3) {
        strip.setPixelColor(i+q, Wheel( (i+j) % 255));    //turn every third pixel on
      }
      strip.show();

      delay(wait);

      for (uint16_t i=0; i < strip.numPixels(); i=i+3) {
        strip.setPixelColor(i+q, 0);        //turn every third pixel off
      }
    }
  }
}

// Input a value 0 to 255 to get a color value.
// The colours are a transition r - g - b - back to r.
uint32_t Wheel(byte WheelPos) {
  WheelPos = 255 - WheelPos;
  if(WheelPos < 85) {
    return strip.Color(255 - WheelPos * 3, 0, WheelPos * 3);
  }
  if(WheelPos < 170) {
    WheelPos -= 85;
    return strip.Color(0, WheelPos * 3, 255 - WheelPos * 3);
  }
  WheelPos -= 170;
  return strip.Color(WheelPos * 3, 255 - WheelPos * 3, 0);
}
