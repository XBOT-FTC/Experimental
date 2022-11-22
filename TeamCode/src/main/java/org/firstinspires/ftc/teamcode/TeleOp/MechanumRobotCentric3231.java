package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="MD: Robot Centric (3231)", group="Linear Opmode")
public class MechanumRobotCentric3231 extends LinearOpMode {

    private RobotCentricMechanumDrive drive = null;
    private DcMotor linearSlide = null;
    private Servo grabber = null;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = RobotCentricMechanumDrive(DcMotorSimple.Direction.FORWARD);
        // Additional functionality
        grabber = hardwareMap.servo.get("grabberServo");
        linearSlide = hardwareMap.dcMotor.get("linearSlide");

        waitForStart();
        telemetry.addData("Actual", "%7d : %7d   %7d : %7d",
                motorFrontLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition(),
                motorBackLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition());
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            drive();
            slideTrigger();
            slideEncoderTarget();
            grabber();
            telemetry.update();
        }
    }

    // Using left and right trigger to move the slider based on pressure:
    private void slideTrigger() {
        double constant = 1.0;
        double forwardPower = gamepad2.right_trigger * constant;
        double reversePower = gamepad2.left_trigger * constant;
        // Set the mode to run without encoders if manual control is detected
        if (forwardPower != 0.0 || reversePower != 0.0) {
            linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        }
        double powerLimiter = 0.5; // limits the power the motors can run at so from -0.5 to 0.5
        double power = (forwardPower != ZERO_POWER) ? forwardPower : -1 * reversePower;
//        if (forwardPower != ZERO_POWER) {
//            power = forwardPower;
//        } else {
//            power = -1 * reversePower;
//        }
        // TODO: set minimum and maximum power ranges if we don't want the motors to go too fast
        // default is -1.0 -> 1.0
        power = Range.clip(power, -1 * powerLimiter, powerLimiter);
        linearSlide.setPower(power);
        telemetry.addData("Slide Power: ", linearSlide.getPower());
        telemetry.addData("Encoder Distance: ", linearSlide.getCurrentPosition());
    }

    private void slideEncoderTarget() {
        // If gamepad 2 buttons a,x,y are pressed, we will use encoders to reach target
        if (gamepad2.a || gamepad2.x || gamepad2.y) {
            // Stop and reset the encoders
            linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            linearSlide.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            // Encoder settings
            int currentTicks = linearSlide.getCurrentPosition();
            int shortPole = 1900 + currentTicks, medPole = 4000 + currentTicks, highPole = 5000 + currentTicks;
            if (gamepad2.a) linearSlide.setTargetPosition(shortPole);
            else if (gamepad2.x) linearSlide.setTargetPosition(medPole);
            else if (gamepad2.y) linearSlide.setTargetPosition(highPole);

            // If a target position is set, run to that position
            if (linearSlide.getTargetPosition() != 0) {
                linearSlide.setMode(DcMotor.RunMode.RUN_TO_POSITION);

                // Motor will run at the designated power until it reaches the position
                double speed = 0.25;
                linearSlide.setPower(speed);

                // Wait until the motors stop.
                while (linearSlide.isBusy()) {
                    telemetry.addData("Linear Slide Distance: ", linearSlide.getCurrentPosition());
                    telemetry.addData("Target: ", linearSlide.getTargetPosition());
                    telemetry.update();
                    // STOP COMMAND
                    if (gamepad2.b) {
                        // Reset encoders and set the mode back to run w/o encoders
                        linearSlide.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
                        linearSlide.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
                        break;
                    }
                    idle();
                }

                // Set power back to zero since position is reached or broken out of.
                // TODO: need to find the power to counteract gravitational pull?
                linearSlide.setPower(ZERO_POWER);
            }
        }
    }

    private void grabber() {
        if (gamepad2.left_bumper) {
            grabber.setPosition(0);
        } else if (gamepad2.right_bumper) {
            grabber.setPosition(0.3);
        }
        telemetry.addData("Grabber Position: ", grabber.getPosition());
    }
}
