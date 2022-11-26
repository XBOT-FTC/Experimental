package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Grabber {

    private DcMotor grabberMotor = null;

    public Grabber(Servo slideMotor) {
        this.slideMotor = slideMotor;
    }


    public void grab(Gamepad gamepad, Telemetry telemetry) {
        if (gamepad2.left_bumper) {
            grabber.setPosition(0);
        } else if (gamepad2.right_bumper) {
            grabber.setPosition(0.3);
        }
        telemetry.addData("Grabber Position: ", grabber.getPosition());
    }

}