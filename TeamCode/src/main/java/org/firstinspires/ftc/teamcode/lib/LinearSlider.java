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

    private double manualSpeed = 0.0; // for moving via trigger
    private double autoSpeed = 0.0;  // for moving w/ encoders

    public LinearSlider(DcMotor slideMotor, Direction direction) {
        this.slideMotor = slideMotor;
        this.slideMotor.setDirection(direction);
    }

    public void slide(Gamepad gamepad, Telemetry telemetry) {
        slideTrigger(gamepad, telemetry);
        slideEncoderTarget(gamepad, telemetry);
    }

    // Using left and right trigger to move the slider based on pressure:
    private void slideTrigger(Gamepad gamepad, Telemetry telemetry) {
        double constant = 1.0;
        double forwardPower = gamepad.right_trigger * constant;
        double reversePower = gamepad.left_trigger * constant;
        // Set the mode to run without encoders if manual control is detected
        if (forwardPower != 0.0 || reversePower != 0.0) {
            slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        double powerLimiter = manualSpeed; // limits the power the motors can run at so from -0.5 to 0.5
        double power = (forwardPower != ZERO_POWER) ? forwardPower : -1 * reversePower;
        // if (forwardPower != ZERO_POWER) {
        // power = forwardPower;
        // } else {
        // power = -1 * reversePower;
        // }
        // TODO: set minimum and maximum power ranges if we don't want the motors to go
        // too fast
        // default is -1.0 -> 1.0
        power = Range.clip(power, -1 * powerLimiter, powerLimiter);
        slideMotor.setPower(power);
        telemetry.addData("Slide Power: ", slideMotor.getPower());
        telemetry.addData("Encoder Distance: ", slideMotor.getCurrentPosition());
    }

    public void setPosition(int small, int medium, int large) {
        this.smallPolePosition = small;
        this.mediumPolePosition = medium;
        this.largePolePosition = large;
    }

    public void setManualSpeed(double speed) {
        this.manualSpeed = speed;
    }

    public void setAutoSpeed(double speed) {
        this.autoSpeed = speed;
    }

    private void slideEncoderTarget(Gamepad gamepad, Telemetry telemetry) {
        // If gamepad 2 buttons a,x,y are pressed, we will use encoders to reach target
        if (gamepad.a || gamepad.x || gamepad.y) {
            // Stop and reset the encoders
            slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

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

                // Wait until the motors stop.
                while (slideMotor.isBusy()) {
                    telemetry.addData("Linear Slide Distance: ", slideMotor.getCurrentPosition());
                    telemetry.addData("Target: ", slideMotor.getTargetPosition());
                    telemetry.update();
                    // STOP COMMAND
                    if (gamepad.b) {
                        // Reset encoders and set the mode back to run w/o encoders
                        slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        slideMotor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        break;
                    }
                    // TODO: Before this was moved into its own class, we had an idle() call
                    // here. I think maybe this is a WIP (do we really want to idle the bot?
                    // idle();
                }

                // Set power back to zero since position is reached or broken out of.
                // TODO: need to find the power to counteract gravitational pull?
                slideMotor.setPower(ZERO_POWER);
            }
        }
    }

}