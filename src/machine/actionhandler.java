package machine;

import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.io.IOException;
import java.util.ArrayList;
import java.util.TimerTask;

public class actionhandler extends TimerTask {
    private final Pin sensor;
    private final Pin pump;
    private final ArrayList<Integer> data;
    private final SSD1306 oled;
    public actionhandler(SSD1306 oled, Pin sensor, Pin pump, ArrayList<Integer> data) {
        this.sensor = sensor;
        this.pump = pump;
        this.data = data;
        this.oled = oled;
    }
    public ArrayList<Integer> getVolt(){
        return data;
    }

    @Override
    public void run(){
        //Convert voltage to 0V-5V
        var sen = sensor.getValue();
        var dryVal = 740.0;
        var wet = 525.0;
        var m = 0.0-5.0/(dryVal-wet);
        var b = 17.21;
        var senVal = m*sen+b;
        double round = Math.round(senVal*100)/100.0;
        var SsenVal = String.valueOf(round);
        data.add((int)senVal);
        System.out.println(round);

        //decide whether soil is dry or wet
        if (senVal >= 4){
            try {
                pump.setValue(0);
                oled.clear();
                oled.getCanvas().setTextsize(2);
                oled.getCanvas().drawString(0,0,SsenVal);
                oled.getCanvas().drawString(0,20,"Wet");
                oled.display();
                System.out.println("Wet");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(senVal < 4 && senVal >= 1){
            try {
                pump.setValue(1);
                oled.clear();
                oled.getCanvas().setTextsize(2);
                oled.getCanvas().drawString(0,0,SsenVal);
                oled.getCanvas().drawString(0,20,"Little Wet");
                oled.display();
                System.out.println("Little Wet");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else if(senVal < 1){
            try {
                pump.setValue(1);
                oled.clear();
                oled.getCanvas().setTextsize(2);
                oled.getCanvas().drawString(0,0,SsenVal);
                oled.getCanvas().drawString(0,20,"Dry");
                oled.display();
                System.out.println("Dry");
            }
            catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
        else{
            System.out.println("error");
        }

    }
}