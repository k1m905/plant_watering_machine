package machine;

// Interface defined: (https://bit.ly/3IhVg1J)
import org.firmata4j.IODeviceEventListener;
import org.firmata4j.IOEvent;
import org.firmata4j.Pin;
import org.firmata4j.ssd1306.SSD1306;
import java.util.Timer;

public class stopButton implements IODeviceEventListener {
    private final SSD1306 oled;
    private final Pin buttonPin;
    private final Pin pump;
    private final actionhandler task;
    private final Timer timer;

    stopButton(SSD1306 oled, Pin buttonPin, Pin pump, actionhandler task, Timer timer) {
        this.buttonPin = buttonPin;
        this.oled = oled;
        this.pump = pump;
        this.task = task;
        this.timer = timer;
    }

    @Override
    public void onPinChange(IOEvent event) {
        if (event.getPin().getIndex() != buttonPin.getIndex()) {
            return;
        }

        //if buttonPin is pressed shutdown everything from the program.
        //if it's not on then do nothing.
        try {
            if (buttonPin.getValue() == 1) {
                task.cancel();
                timer.cancel();
                pump.setValue(0);
                oled.clear();
                Thread.sleep(1000);
                System.exit(0);

            } else if(buttonPin.getValue() == 0){
                return;
            }

        }catch(Exception ex){
        }

    }
    @Override
    public void onStart(IOEvent event) {}
    @Override
    public void onStop(IOEvent event) {}
    @Override
    public void onMessageReceive(IOEvent event, String message) {}
}