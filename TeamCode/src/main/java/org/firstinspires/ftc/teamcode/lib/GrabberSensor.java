package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class GrabberSensor {


    private TouchSensor touch;
    private DcMotor led;

    public GrabberSensor(TouchSensor touch, DcMotor led) {
        this.touch = touch;
        this.led = led;
    }

    public void sense(Telemetry telemetry) {
        if (this.touch.isPressed()) {
            this.led.setPower(.15);
            telemetry.addLine("Grabber Sensor sensing press.");
        } else {
            this.led.setPower(0);
            telemetry.addLine("Grabber Sensor sensing nothing");
        }
    }
}