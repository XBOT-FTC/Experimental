package org.firstinspires.ftc.teamcode.lib;

import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.BACKWARD;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.FORWARD;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.LEFT_STRAFE;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.LEFT_TURN;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.RIGHT_STRAFE;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.RIGHT_TURN;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.util.Range;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.RobotConstants;

// NOT a general-purpose MechanumDrive class.
// Just an extraction of the stuff that's common to our robots, and a means of providing
// the stuff that differs, to the constructop
public class RobotCentricMechanumDrive {

    private final double ZERO_POWER = 0.0;

    // Declare OpMode members
    private DcMotor motorFrontLeft = null;
    private DcMotor motorBackLeft = null;
    private DcMotor motorFrontRight = null;
    private DcMotor motorBackRight = null;

    // Ticks measurement
    private double TICKS_PER_INCH;
    private double TICKS_PER_DEGREE;

    // Utility members
    private double speedLimiter = 1.0;

    public RobotCentricMechanumDrive(HardwareMap hardwareMap, Direction motorFrontLeftDirection) throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        motorFrontLeft = hardwareMap.dcMotor.get(RobotConstants.FRONT_LEFT);
        motorBackLeft = hardwareMap.dcMotor.get(RobotConstants.BACK_LEFT);
        motorFrontRight = hardwareMap.dcMotor.get(RobotConstants.FRONT_RIGHT);
        motorBackRight = hardwareMap.dcMotor.get(RobotConstants.BACK_RIGHT);

        // Reverse the right side motors
        // Reverse left motors if you are using NeveRests
        // This works for both bots (2939 motorFrontLeftDirection == FORWARD, 3231 motorFrontLeftDirection == REVERSE)
        motorFrontLeft.setDirection(motorFrontLeftDirection);
        motorFrontRight.setDirection(motorFrontLeftDirection.inverted());
        motorBackLeft.setDirection(motorFrontLeftDirection);
        motorBackRight.setDirection(motorFrontLeftDirection.inverted());

    }

    public void drive(Gamepad gamepad, Telemetry telemetry) {
        double speedFactor = gamepad.left_trigger * 1;
        telemetry.addData("left_trigger (speedFactor): ", gamepad.left_trigger);

        double y = -gamepad.left_stick_y; // Remember, this is reversed!
        double x = gamepad.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad.right_stick_x;

        // Denominator is the largest motor power (absolute value) or 1
        // This ensures all the powers maintain the same ratio, but only when
        // at least one is out of the range [-1, 1]
        double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
        denominator = denominator * (1 + speedFactor);
        double frontLeftPower = (y + x + rx) / denominator;
        double backLeftPower = (y - x + rx) / denominator;
        double frontRightPower = (y - x - rx) / denominator;
        double backRightPower = (y + x - rx) / denominator;

        double limiter = this.speedLimiter;
        frontLeftPower = Range.clip(frontLeftPower, -1 * limiter, limiter);
        frontRightPower = Range.clip(frontRightPower, -1 * limiter, limiter);
        backLeftPower = Range.clip(backLeftPower, -1 * limiter, limiter);
        backRightPower = Range.clip(backRightPower, -1 * limiter, limiter);

        motorFrontLeft.setPower(frontLeftPower);
        motorBackLeft.setPower(backLeftPower);
        motorFrontRight.setPower(frontRightPower);
        motorBackRight.setPower(backRightPower);

        telemetry.addData("Actual", "%7d : %7d   %7d : %7d",
                motorFrontLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition(),
                motorBackLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition());

        telemetry.addData("Calculated Motor Power", "fL: %.3f - fR: %.3f - bL: %.3f - bR: %.3f",
                frontLeftPower, frontRightPower,
                backLeftPower, backRightPower);

        telemetry.addData("Actual Motor Power", "fL: %.3f - fR: %.3f - bL: %.3f - bR: %.3f",
                motorFrontLeft.getPower(), motorFrontRight.getPower(),
                motorBackLeft.getPower(), motorFrontRight.getPower());

        telemetry.addData("Encoder Values", "fL: %7d - fR: %7d - bL: %7d - bR: %7d",
                motorFrontLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition(),
                motorBackLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition());
    }

    public void setTicks(double perInch, double perDegree) {
        this.TICKS_PER_INCH = perInch;
        this.TICKS_PER_DEGREE = perDegree;
    }

    public void setSpeed(double fL, double fR, double bL, double bR) {
        this.motorFrontLeft.setPower(fL);
        this.motorFrontRight.setPower(fR);
        this.motorFrontLeft.setPower(bL);
        this.motorFrontLeft.setPower(bR);
    }

    public void setSpeedLimiter(double speed) {
        this.speedLimiter = speed;
    }

    public void setModeWithEncoders() {
        // Set Drive to run with encoders.
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
    }

    public void setModeWithoutEncoders() {
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
    }

    public void updateEncoderTarget(RobotConstants.Commands.DRIVE command, int inches, int angle) {
        // Retrieve the current position for each motor
        setModeWithEncoders();
        int frontLeftPos, frontRightPos, backLeftPos, backRightPos;
        frontLeftPos = motorFrontLeft.getCurrentPosition();
        frontRightPos = motorFrontRight.getCurrentPosition();
        backLeftPos = motorBackLeft.getCurrentPosition();
        backRightPos = motorBackRight.getCurrentPosition();

        if (command == FORWARD || command == BACKWARD) {
            // Calculate the ticks to be reached
            frontLeftPos += inches * TICKS_PER_INCH;
            frontRightPos += inches * TICKS_PER_INCH;
            backLeftPos += inches * TICKS_PER_INCH;
            backRightPos += inches * TICKS_PER_INCH;
        } else if (command == LEFT_STRAFE || command == RIGHT_STRAFE) {
            // Calculate the ticks to be reached
            frontLeftPos -= inches * TICKS_PER_INCH;
            frontRightPos += inches * TICKS_PER_INCH;
            backLeftPos += inches * TICKS_PER_INCH;
            backRightPos -= inches * TICKS_PER_INCH;
        } else if (command == LEFT_TURN || command == RIGHT_TURN) {
            frontLeftPos += angle * TICKS_PER_DEGREE;
            frontRightPos -= angle * TICKS_PER_DEGREE;
            backLeftPos += angle * TICKS_PER_DEGREE;
            backRightPos -= angle * TICKS_PER_DEGREE;
        }

        // Set the goal and power to the motors
        motorFrontLeft.setTargetPosition(frontLeftPos);
        motorFrontRight.setTargetPosition(frontRightPos);
        motorBackLeft.setTargetPosition(backLeftPos);
        motorBackRight.setTargetPosition(backRightPos);

        motorFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
    }

    public void moveLinear(RobotConstants.Commands.DRIVE driveCommand, int inches, double speed, Telemetry telemetry) {
        if (driveCommand == FORWARD) {
            inches *= 1;
        } else if (driveCommand == BACKWARD) {
            inches *= -1;
        } else {
            throw new IllegalArgumentException();
        }
        // Set the target ticks and run the motors
        updateEncoderTarget(driveCommand, inches, 0);
        setSpeed(speed, speed, speed, speed);

        // Wait for encoders to complete it's routine
        while (motorFrontLeft.isBusy() && motorFrontRight.isBusy() &&
                motorBackLeft.isBusy() && motorBackRight.isBusy()) {
            // Display Telemetry Data
            telemetry.addLine("Moving Linearly");
            updateEncoderTelemetry(telemetry);
        }

        // Once completed, stop all motors:
        setSpeed(ZERO_POWER, ZERO_POWER, ZERO_POWER, ZERO_POWER);
    }

    public void strafe(RobotConstants.Commands.DRIVE driveCommand, int inches, double speed, Telemetry telemetry) {
        if (driveCommand == LEFT_STRAFE) {
            inches *= 1;
        } else if (driveCommand == RIGHT_STRAFE) {
            inches *= -1;
        } else {
            throw new IllegalArgumentException();
        }
        // Set the target ticks and run the motors
        updateEncoderTarget(driveCommand, inches, 0);
        setSpeed(speed, speed, speed, speed);

        // Wait for encoders to complete it's routine
        while (motorFrontLeft.isBusy() && motorFrontRight.isBusy() &&
                motorBackLeft.isBusy() && motorBackRight.isBusy()) {
            // Display Telemetry Data
            telemetry.addLine("Strafing");
            updateEncoderTelemetry(telemetry);
        }

        // Once completed, stop all motors:
        setSpeed(ZERO_POWER, ZERO_POWER, ZERO_POWER, ZERO_POWER);
    }

    public void turn(RobotConstants.Commands.DRIVE driveCommand, int angle, double speed, Telemetry telemetry) {
        if (driveCommand == RIGHT_TURN) {
            angle *= 1;
        } else if (driveCommand == LEFT_TURN) {
            angle *= -1;
        } else {
            throw new IllegalArgumentException();
        }
        // Set the target ticks and run the motors
        updateEncoderTarget(driveCommand, 0, angle);
        setSpeed(speed, speed, speed, speed);

        // Wait for encoders to complete it's routine
        while (motorFrontLeft.isBusy() && motorFrontRight.isBusy() &&
                motorBackLeft.isBusy() && motorBackRight.isBusy()) {
            // Display Telemetry Data
            telemetry.addLine("Turning");
            updateEncoderTelemetry(telemetry);
        }

        // Once completed, stop all motors:
        setSpeed(ZERO_POWER, ZERO_POWER, ZERO_POWER, ZERO_POWER);
    }

    void updateEncoderTelemetry(Telemetry telemetry) {
        telemetry.addData("Front Target", "%7d : %7d",
                motorFrontLeft.getTargetPosition(), motorFrontRight.getTargetPosition());
        telemetry.addData("Rear Target", "%7d : %7d",
                motorFrontLeft.getTargetPosition(), motorBackRight.getTargetPosition());
        telemetry.addData("Front Actual", "%7d : %7d",
                motorFrontLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition());
        telemetry.addData("Rear Actual", "%7d : %7d",
                motorBackLeft.getCurrentPosition(), motorBackRight.getCurrentPosition());
        telemetry.update();
    }

    void moveLinearNonEncoder(RobotConstants.Commands.DRIVE driveCommand, int seconds, double speed) throws InterruptedException {
        if (driveCommand == FORWARD) {
            speed *= 1;
        } else if (driveCommand == BACKWARD) {
            speed *= -1;
        } else {
            throw new IllegalArgumentException();
        }
        setModeWithoutEncoders();
        setSpeed(speed, speed, speed, speed);
        Thread.sleep((int) (seconds * 1000));
        // Once completed, stop all motors:
        setSpeed(ZERO_POWER, ZERO_POWER, ZERO_POWER, ZERO_POWER);
    }

    void strafeNonEncoder(RobotConstants.Commands.DRIVE driveCommand, double seconds, double speed) throws InterruptedException {
        setModeWithoutEncoders();
        if (driveCommand == LEFT_STRAFE) {
            setSpeed(speed, -speed, speed, -speed);
        } else if (driveCommand == RIGHT_STRAFE) {
            setSpeed(-speed, speed, -speed, speed);
        } else {
            throw new IllegalArgumentException();
        }
        Thread.sleep((int) (seconds * 1000));
        // Once completed, stop all motors:
        setSpeed(ZERO_POWER, ZERO_POWER, ZERO_POWER, ZERO_POWER);
    }
}
