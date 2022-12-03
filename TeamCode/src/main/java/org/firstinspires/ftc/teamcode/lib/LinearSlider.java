package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LinearSlider {

    private final double ZERO_POWER = 0.0;

    private DcMotor slideMotor = null;

    private int smallPolePosition = 0;
    private int mediumPolePosition = 0;
    private int largePolePosition = 0;
    private int maxManualPosition = 100000;

    private double maxSpeed = 0.0; // for moving via trigger
    private double autoSpeed = 0.0;  // for moving w/ encoders

    public LinearSlider(DcMotor slideMotor, Direction direction) {
        this.slideMotor = slideMotor;
        this.slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.slideMotor.setDirection(direction);
    }

    public void slide(Gamepad gamepad, Telemetry telemetry) {
        slideTrigger(gamepad, telemetry);
//        slideEncoderTarget(gamepad, telemetry);
    }

    // Using left and right trigger to move the slider based on pressure:
    private void slideTrigger(Gamepad gamepad, Telemetry telemetry) {
        double forwardPower = gamepad.right_trigger;
        double reversePower = gamepad.left_trigger;
        double power = (forwardPower != ZERO_POWER) ? forwardPower * maxSpeed: -1 * reversePower * maxSpeed;
        power = Range.clip(power, -1 * maxSpeed, maxSpeed);
        // Safety limits
        int position = slideMotor.getCurrentPosition();
        if(position < 0 && power < 0) {
            telemetry.addData("WARNING:  Ignoring negative power command to slider because position is out of bounds low", position);
            slideMotor.setPower(0);
        } else if (position > this.maxManualPosition && power > 0) {
            telemetry.addData("WARNING:  Ignoring positive power command to slider because position is out of bounds high", position);
            slideMotor.setPower(0);
        } else {
            // Hold the slider in place
            if(power >= 0 && power < .15 && position > 10) {
                power = .15;
            }
            slideMotor.setPower(power);
        }
        telemetry.addData("Slide Power is (w/ braking): ", slideMotor.getPower());
        telemetry.addData("Encoder Distance: ", slideMotor.getCurrentPosition());
    }

    public void setPosition(int small, int medium, int large) {
        this.smallPolePosition = small;
        this.mediumPolePosition = medium;
        this.largePolePosition = large;
    }

    public void setManualSpeed(double speed) {
        if(speed > 1 || speed < 0) {
            throw new RuntimeException("Slider max speed must be between 0 and 1");
        }
        this.maxSpeed = speed;
    }

    public void setMaxManualPosition(int maxManualPosition) {
        this.maxManualPosition = maxManualPosition;
    }

    public void setAutoSpeed(double speed) {
        this.autoSpeed = speed;
    }

    private void slideEncoderTarget(Gamepad gamepad, Telemetry telemetry) {
        // If gamepad 2 buttons a,x,y are pressed, we will use encoders to reach target
        if (gamepad.a || gamepad.x || gamepad.y) {
            // Stop and reset the encoders
            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            // Encoder settings
            int currentPosition = slideMotor.getCurrentPosition();
            int targetPosition = currentPosition;
            if (gamepad.a) {
                targetPosition += smallPolePosition;
            } else if (gamepad.x) {
                targetPosition += mediumPolePosition;
            } else if (gamepad.y) {
                targetPosition += largePolePosition;
            }
            slideMotor.setTargetPosition(targetPosition);
            // If a target position is set, run to that position
            if (slideMotor.getTargetPosition() != 0) {
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // Motor will run at the designated power until it reaches the position
                slideMotor.setPower(autoSpeed);

                // Wait until the motor runs to that position
                while (slideMotor.isBusy()) {
                    telemetry.addData("Linear Slide Distance: ", slideMotor.getCurrentPosition());
                    telemetry.addData("Target: ", slideMotor.getTargetPosition());
                    telemetry.update();
                    // If the operator tries moving the slide manually, disable the run to position.
                    if (gamepad.left_trigger != 0.0 || gamepad.right_trigger != 0.0 || gamepad.start) {
                        // Reset encoders and set the mode back to run w/o encoders
                        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        break;
                    }
                }
            }
        }
    }

}