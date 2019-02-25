// NeoPixel Ring simple sketch (c) 2013 Shae Erisson
// released under the GPLv3 license to match the rest of the AdaFruit NeoPixel library

//#include <SoftwareSerial.h>
#include <Adafruit_NeoPixel.h>
#include <avr/power.h>

#define RX_PIN    0 // Connect this Trinket pin to BLE 'TXO' pin
#define CTS_PIN   1 // Connect this Trinket pin to BLE 'CTS' pin

// Which pin on the Arduino is connected to the NeoPixels?
// On a Trinket or Gemma we suggest changing this to 1
#define NEO_PIN 6

// How many NeoPixels are attached to the Arduino?
#define NUM_LEDS 16

#define PREVIOUS_LED(p) (p < NUM_LEDS-1?p+1:0)
#define NEXT_LED(p)     (p > 0?p-1:NUM_LEDS-1)

// Uno: SoftwareSerial bt(RX_PIN, CTS_PIN);
#define bt Serial // Leonardo #define bt Serial1

// When we setup the NeoPixel library, we tell it how many pixels, and which pin to use to send signals.
// Note that for older NeoPixel strips you might need to change the third parameter--see the strandtest
// example for more information on possible values.
Adafruit_NeoPixel pixels = Adafruit_NeoPixel(NUM_LEDS, NEO_PIN, NEO_GRB + NEO_KHZ800);

int iR = 0x6e;
int iG = 0x00;
int iB = 0x7e;
int animation = 2;
int maxbright = 0x1f;
int fadein = 0;
int interval = 1000/NUM_LEDS; // One second for all pixels
unsigned long previousMillis = 0;        // will store last time LED was updated

// pixels.Color takes RGB values, from 0,0,0 up to 255,255,255
uint32_t black = pixels.Color(0, 0, 0);
uint32_t magenta = pixels.Color(iR, iG, iB);
uint32_t color = magenta;

int l = 0, pl = l;
int s = NUM_LEDS-1, ps = s;

int m = 0, pm = m;
int t = NUM_LEDS/2, pt = t;

int d = 1;

void setup() 
{
    // This is for Trinket 5V 16MHz, you can remove these three lines if you are not using a Trinket
#if defined (__AVR_ATtiny85__)
    if (F_CPU == 16000000) clock_prescale_set(clock_div_1);
#endif
    // End of trinket special code

    // initialize digital pin 13 as an output.
    pinMode(13, OUTPUT);
    
    // Stop incoming data & init software serial
//    pinMode(CTS_PIN, OUTPUT);
//    digitalWrite(CTS_PIN, HIGH);
    bt.begin(9600);
    delay(200);
    
    // Send test message to other device
    bt.println("Hello from Arduino");
    
    pixels.begin(); // This initializes the NeoPixel library.
}

int readNumber()
{
    int result = 0;

    while (bt.available())
    {
        int a = bt.read();
        delay(10); // Important
        
        if (a >= '0' && a <= '9')
        {
            result *= 10;
            result += a-'0';
        }
    }
    return result;
}

void getInput()
{
    int a = bt.read();
    delay(10); // Important
    
    if (a == 'a')
    {
        ++animation;
        if (animation == 5)
            animation = 0;
            
//      if (animation == 0)
//          bt.println("Animation off");
//      else
//          bt.println("Animation on");

        for (int l = 0; l < NUM_LEDS; ++l)
            pixels.setPixelColor(l, black);

        digitalWrite(13, LOW);
    }
    if (a == 'h')
    {
        maxbright = 0xff;
    }
    if (a == 'l')
    {
        maxbright = 0x1f;
    }

    if (a == 'r')
    {
        iR = readNumber();
        color = pixels.Color(iR, iG, iB);
    }
    else
    if (a == 'g')
    {
        iG = readNumber();
        color = pixels.Color(iR, iG, iB);
    }
    else
    if (a == 'b')
    {
        iB = readNumber();
        color = pixels.Color(iR, iG, iB);
    }
    else
    if (a == 'i')
    {
        maxbright = readNumber();
    }
            
    bt.flush();   
}

void loop() 
{
    unsigned long currentMillis = millis();
 
    if (bt.available()) // if text arrived in from BT serial...
    {
        getInput();
    }

    if (currentMillis - previousMillis >= interval)
    {
        // save the last time you blinked the LED 
        previousMillis = currentMillis; 
        
        if (fadein != maxbright)
        {
            if (fadein > maxbright)
                fadein -= 2;
            if (fadein < maxbright)
                fadein += 2;
                
            if (fadein >= 0 && fadein <= 0xff)
                pixels.setBrightness(fadein);
        }   

        if (animation == 0)
        {
            for (int l = 0; l < NUM_LEDS; ++l)
                pixels.setPixelColor(l, color);
                        
            pixels.show(); // This sends the updated pixel color to the hardware.
        }
        else
        if (animation == 1)
        {
            if (pl != l)
                pixels.setPixelColor(pl, black);
            pixels.setPixelColor(l, color);
    
            if (ps != s)
                pixels.setPixelColor(ps, black);
            pixels.setPixelColor(s, color);

            pl = l;
            ps = s;
            
            if (d)
            {
                digitalWrite(13, HIGH);
                ++l;
                --s;
        
                if (l == NUM_LEDS/2-1)
                    d = 0;
            }
            else
            {
                digitalWrite(13, LOW);
                --l;
                ++s;

                if (l == 0)
                    d = 1;
            }
                
            pixels.show(); // This sends the updated pixel color to the hardware.
        }
        else
        if (animation == 2)
        {
            pixels.setPixelColor(pm, black);
            pixels.setPixelColor(m, color);

            pixels.setPixelColor(pt, black);
            pixels.setPixelColor(t, color);

            pm = m;
            pt = t;
            
            ++m;
            ++t;
            if (m == NUM_LEDS/2)
                m = 0;
            if (t == NUM_LEDS)
                t = NUM_LEDS/2;
         
            pixels.show(); // This sends the updated pixel color to the hardware.
        }
        else
        if (animation == 3)
        {
            pixels.setPixelColor(pm, black);
            pixels.setPixelColor(m, color);

            pixels.setPixelColor(pt, black);
            pixels.setPixelColor(t, color);

            pm = m;
            pt = t;
            
            --m;
            --t;
            if (m == -1)
                m = NUM_LEDS/2-1;
            if (t == NUM_LEDS/2-1)
                t = NUM_LEDS-1;
         
            pixels.show(); // This sends the updated pixel color to the hardware.
        }
        else
        if (animation == 4)
        {
            pixels.setPixelColor(2, pixels.Color(192, 0, 0));
            pixels.setPixelColor(NUM_LEDS-1-2, pixels.Color(192, 0, 0));
            pixels.setPixelColor(NUM_LEDS/2-1, pixels.Color(128, 128, 128));
            pixels.setPixelColor(NUM_LEDS/2, pixels.Color(128, 128, 128));
                
            pixels.show(); // This sends the updated pixel color to the hardware.
        }
    }
}
