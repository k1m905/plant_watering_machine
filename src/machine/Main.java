package machine;

import edu.princeton.cs.introcs.StdDraw;
import org.firmata4j.I2CDevice;
import org.firmata4j.Pin;
import org.firmata4j.firmata.FirmataDevice;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Timer;

public class Main {
    static final int A0 = 14;

    public static void main(String[] args) throws InterruptedException, IOException {
        var port = "/dev/cu.usbserial-0001";
        var device = new FirmataDevice(port);

        try {
            //Initialize the board
            device.start();
            System.out.println("Board started.");
            device.ensureInitializationIsDone();
        }

        catch (Exception ex){
            System.out.println("couldn't connect to board.");
        }

        finally {
            //Initialize every pins.
            ArrayList<Integer> data = new ArrayList<>();
            I2CDevice i2cObject = device.getI2CDevice((byte) 0x3C);
            SSD1306 OledDisplay = new SSD1306(i2cObject, SSD1306.Size.SSD1306_128_64);
            OledDisplay.init();

            var sensor = device.getPin(A0);
            sensor.setMode(Pin.Mode.ANALOG);
            var pump = device.getPin(2);
            pump.setMode(Pin.Mode.OUTPUT);
            var button = device.getPin(6);
            button.setMode(Pin.Mode.INPUT);

            //Timer task to schedule extracting soil condition data. (Total of 15 datas)
            Timer timer = new Timer();
            var task = new actionhandler(OledDisplay, sensor, pump, data);
            new Timer().schedule(task, 0, 1000);

            //State Machine turns off the whole system.
            stopButton stateMachine = new stopButton(OledDisplay,button,pump,task,timer);
            device.addEventListener(stateMachine);

            //extract data about 15 seconds
            Thread.sleep(15000);

            //Make a graph frame
            StdDraw.setXscale(-1,15);
            StdDraw.setYscale(-1,6);

            StdDraw.setPenRadius(0.005);
            StdDraw.setPenColor(StdDraw.BLUE);

            StdDraw.line(0,0,0,5);
            StdDraw.line(0,0,15,0);

            StdDraw.text(7.5,-0.5,"Time [S]");

            //Use for loop to put numbers from 0 to 15
            for(int i = 0; i<=15; i++){
                StdDraw.text(i,-0.2,Integer.toString(i));
            }

            StdDraw.text(-1,2.5,"[V]");

            //use for loop sto put numbers from 0 to 5
            for(int i = 0; i<=5; i++){
                StdDraw.text(-0.5,i,Integer.toString(i));
            }

            StdDraw.text(7.5,5.5,"Sensor Voltage VS Time");

            //call getvolt() method from task object to access to the data collection
            data = task.getVolt();

            //plot all the data from arrayList collection
            int sample = 1;

            for(int i = 0; i < data.size(); i++){
                double value = data.get(i);
                StdDraw.text(sample,value,"*");
                sample ++;
                Thread.sleep(100);
            }
        }
    }
}