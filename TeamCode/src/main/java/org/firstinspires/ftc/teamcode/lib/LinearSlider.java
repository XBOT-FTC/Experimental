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

    private int groundJunctionPosition = 0;
    private int smallPolePosition = 0;
    private int mediumPolePosition = 0;
    private int largePolePosition = 0;
    // You should set this to something realistic after manual calibration.
    private int maxManualPosition = 100000;
    // Idle power For 3231, .15 works well when the bot batter is 8V at rest
    private double holdPositionMotorPower = .02;

    private double maxIncrementSpeed = 0.0; // for moving via trigger
    private double maxDecrementSpeed = 0.0; // for moving via trigger
    private double autoSpeed = 0.0; // for moving w/ encoders

    private boolean encoderMode = false;

    public LinearSlider(DcMotor slideMotor, Direction direction) {
        this.slideMotor = slideMotor;
        this.slideMotor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        this.slideMotor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        this.slideMotor.setDirection(direction);
        this.encoderMode = false;
    }

    public void slide(Gamepad gamepad, Telemetry telemetry) {
        slideTrigger(gamepad, telemetry);
        slideEncoderTarget(gamepad, telemetry);
    }

    // Using left and right trigger to move the slider based on pressure:
    private void slideTrigger(Gamepad gamepad, Telemetry telemetry) {
        if (gamepad.left_trigger != 0.0 || gamepad.right_trigger != 0.0) {
            telemetry.addLine("The trigger has been pressed. Manual mode.");
            this.slideMotor.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            this.encoderMode = false;
            double forwardPower = gamepad.right_trigger;
            double reversePower = gamepad.left_trigger;
            double power = 0.0;
            if (forwardPower != ZERO_POWER) {
                power = forwardPower;
                power = Range.clip(power, 0, maxIncrementSpeed);
            } else if (reversePower != ZERO_POWER) {
                power = reversePower * -1;
                power = Range.clip(power, -maxDecrementSpeed, 0);
            }
            // Safety limits
            int position = slideMotor.getCurrentPosition();
            if (position < 0 && power < 0) {
                telemetry.addData(
                        "WARNING:  Ignoring negative power command to slider because position is out of bounds low",
                        position);
                slideMotor.setPower(0);
            } else if (position > this.maxManualPosition && power > 0) {
                telemetry.addData(
                        "WARNING:  Ignoring positive power command to slider because position is out of bounds high",
                        position);
                slideMotor.setPower(0);
            } else {
                // Hold the slider in place
                if (power >= 0 && power < .15 && position > 10) {
                    power = holdPositionMotorPower;
                }
                // Otherwise, set power based on trigger
                slideMotor.setPower(power);
            }
        } else if (encoderMode) {
            telemetry.addLine("Slide: In encoder mode, we are relying on encoders to stay afloat.");
            // do nothing since encoder control are upholding power already.
        } else {
            telemetry.addLine("Slide: Not in encoder mode and no power from the trigger, set zero.");
            slideMotor.setPower(0.0);
        }
        // The triggers are both at zero, so

        telemetry.addData("Slide Power is (w/ braking): ", slideMotor.getPower());
        telemetry.addData("Encoder Distance: ", slideMotor.getCurrentPosition());
    }

    public void setPosition(int ground, int small, int medium, int large) {
        this.groundJunctionPosition = ground;
        this.smallPolePosition = small;
        this.mediumPolePosition = medium;
        this.largePolePosition = large;
    }

    public void setManualSpeed(double increasing, double decreasing) {
        if (increasing > 1 || increasing < 0 || decreasing > 1 || decreasing < 0) {
            throw new RuntimeException("Slider max speed must be between 0 and 1");
        }
        this.maxIncrementSpeed = increasing;
        this.maxDecrementSpeed = decreasing;
    }

    public void setMaxManualPosition(int maxManualPosition) {
        this.maxManualPosition = maxManualPosition;
    }

    public void setAutoSpeed(double speed) {
        this.autoSpeed = speed;
    }

    private void slideEncoderTarget(Gamepad gamepad, Telemetry telemetry) {
        // If gamepad 2 buttons a,x,y are pressed, we will use encoders to reach target
        if (gamepad.a || gamepad.x || gamepad.y || gamepad.b) {
            int targetPosition = -1;
            if (gamepad.b) {
                targetPosition = groundJunctionPosition;
            } else if (gamepad.a) {
                targetPosition = smallPolePosition;
            } else if (gamepad.x) {
                targetPosition = mediumPolePosition;
            } else if (gamepad.y) {
                targetPosition = largePolePosition;
            }
            slideMotor.setTargetPosition(targetPosition);
            // If a target position is set, run to that position
            if (slideMotor.getTargetPosition() != -1) {
                slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // Motor will run at the designated power until it reaches the position
                slideMotor.setPower(autoSpeed);

                // Wait until the motor runs to that position
                while (slideMotor.isBusy()) {
                    telemetry.addData("Linear Slide Distance: ", slideMotor.getCurrentPosition());
                    telemetry.addData("Target: ", slideMotor.getTargetPosition());
                    // If the operator tries moving the slide manually, disable the run to position.
                    if (gamepad.left_trigger != 0.0 || gamepad.right_trigger != 0.0 || gamepad.start) {
                        break;
                    }
                }
                this.encoderMode = true;
            }
        }
    }

}