package grpio;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import RPIgetItem.HttpUrlCon;
import RPIgetItem.JsonAPI;

import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.GpioPinDigitalOutput;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;

import database.DBClass;

public class PinController {
	

	long photoTimer = 0;
	private static GpioPinDigitalOutput pinMeatAndFish;
	private static GpioPinDigitalOutput pinOthers;
	private static GpioPinDigitalOutput pinMilkAndEggs;
	private static GpioPinDigitalOutput pinFruitAndVegtables;
	//private static GpioPinDigitalOutput pinBarcodeScanner;
	private static final GpioController gpioC = GpioFactory.getInstance();;
	GpioPinDigitalInput photoSwitch;
	private DBClass dbC;
	final JsonAPI jAPI = JsonAPI.getInstance( );
	HttpUrlCon http = new HttpUrlCon();
	
	public PinController(){
		
		dbC = DBClass.getInstance( );
		
		//System.out.println("initiiert");
		
		//Position Indicatior Pins
		pinMeatAndFish = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_00,"MeatAndFish",PinState.LOW);
		pinMilkAndEggs = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_01,"pinMilkAndEggs",PinState.LOW);
		pinFruitAndVegtables = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_02,"pinFruitAndVegtables",PinState.LOW);
		pinOthers = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_03,"pinOthers",PinState.LOW);
		
		//BarcodeScanner Pin
		//pinBarcodeScanner = gpioC.provisionDigitalOutputPin(RaspiPin.GPIO_05,"BarcodeScanner",PinState.LOW);
		//photoswitch
		photoSwitch = gpioC.provisionDigitalInputPin(RaspiPin.GPIO_07, PinPullResistance.PULL_DOWN);
		photoSwitch.setShutdownOptions(true);
		photoSwitch.addListener(new GpioPinListenerDigital() {
            @Override
            public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
                // display pin state on console
            	
                System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
                if(event.getState()==PinState.HIGH){
                	
                	try {
                		if(System.currentTimeMillis() >= photoTimer + 10000){
                			photoTimer = System.currentTimeMillis();
                			TakeAndSavePicture();
                		}
					} catch (IOException | SQLException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                }
            }

        });
		
	}
	
	//Position Indicatior Functions
	
	public void positionIndicatorLed(int categorie){
		
		setAllPinsLow();
		//System.out.println("storeInPinC "+ categorie);
		
		switch(categorie){
			case 1:
				pinMeatAndFish.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinMeatAndFish);
				break;
			case 2:
				pinMilkAndEggs.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinMilkAndEggs);
				break;
			case 3:
				pinFruitAndVegtables.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinFruitAndVegtables);
				break;
			case 4:
				pinOthers.setState((PinState.HIGH));
				setPinStateLowAfterDelay(pinOthers);
				break;
		}
	}
	
	private void setPinStateLowAfterDelay(GpioPinDigitalOutput pin){
		new java.util.Timer().schedule( 
		        new java.util.TimerTask() {
		            @Override
		            public void run() {
		            	setAllPinsLow();
		            }
		        }, 
		        5000 
		);

	}
	private void setAllPinsLow(){
		
		pinMeatAndFish.setState(PinState.LOW);
		pinMilkAndEggs.setState(PinState.LOW);
		pinFruitAndVegtables.setState(PinState.LOW);
		pinOthers.setState(PinState.LOW);
	}
	
	//BarcodeScanner
	private void toggleBarcodeScanner(){
		//pinBarcodeScanner.toggle();
	}
	
	int i= 0;
	//TakeAndSavePicture
	private void TakeAndSavePicture() throws IOException, SQLException{
		System.out.println("bild aufgenommen!");
		i++;
		System.out.println("i = "+i);
		photoTimer = System.currentTimeMillis();
		String currentDir = System.getProperty("user.dir");
        Runtime.getRuntime().exec("sudo fswebcam -r 1024x768 -d /dev/video0 /home/pi/Desktop/fridge.jpg"); 
        
       // Runtime.getRuntime().exec("sudo fswebcam -d /dev/video0  -S 1 -s brightness=60% -s Contrast=15%  -s Gamma=50%  -p YUYV -r 1280x720 --jpeg 80 -s Sharpness=40% -s Saturation=15% "+currentDir+"/fridge.jpg");  
	   //sudo fswebcam -d /dev/video0  -S 1 -s brightness=60% -s Contrast=15%  -s Gamma=50%  -p YUYV -r 1280x720 --jpeg 80 -s Sharpness=40% -s Saturation=15% /Desktop/fridge.jpg
        File file = new File("/home/pi/Desktop/fridge.jpg");
	    System.out.println("ok3");
        dbC.storePic(0, file);
        System.out.println("ok4");
       // dbC.getPircturesFromDB();
        try {
        	System.out.println("ok5");
			http.sendImage("031"+jAPI.getAllPhotos());
			
			//System.out.println("031"+jAPI.getAllPhotos());
			System.out.println("ok6");
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        
        
        
	}
	

}
