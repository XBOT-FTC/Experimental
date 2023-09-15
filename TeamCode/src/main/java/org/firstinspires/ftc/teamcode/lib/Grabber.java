package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo.Direction;


import org.firstinspires.ftc.robotcore.external.Telemetry;

public class Grabber {

    private Servo grabberMotor = null;
    private double maxPosition;

    public Grabber(Servo grabberMotor, Direction direction) {
        this.grabberMotor = grabberMotor;
        this.grabberMotor.setDirection(direction);
        this.maxPosition = 0.0;
    }

    public void grab(Gamepad gamepad, Telemetry telemetry) {
        if (gamepad.left_bumper) {
            grabberMotor.setPosition(0);
        } else if (gamepad.right_bumper) {
            grabberMotor.setPosition(this.maxPosition);
        }
        telemetry.addData("Grabber Position: ", grabberMotor.getPosition());
    }

    public void setMaxPosition(double position) {
        this.maxPosition = position;
    }
}