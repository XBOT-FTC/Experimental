package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Grabber {

    private Servo grabberMotor = null;

    public Grabber(Servo grabberMotor) {
        this.grabberMotor = grabberMotor;
    }


    public void grab(Gamepad gamepad, Telemetry telemetry) {
        if (gamepad.left_bumper) {
            grabberMotor.setPosition(0);
        } else if (gamepad.right_bumper) {
            grabberMotor.setPosition(0.3);
        }
        telemetry.addData("Grabber Position: ", grabberMotor.getPosition());
    }
}