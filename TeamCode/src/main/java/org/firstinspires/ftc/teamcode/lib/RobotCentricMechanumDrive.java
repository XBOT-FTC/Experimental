package org.firstinspires.ftc.teamcode.lib;

import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.BACKWARD;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.FORWARD;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.LEFT_STRAFE;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.LEFT_TURN;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.RIGHT_STRAFE;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.RIGHT_TURN;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
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
    private DcMotor speedModeLights = null;

    // Ticks measurement
    private double TICKS_PER_INCH;
    private double TICKS_PER_DEGREE;

    // Utility members.5
    private double speedModeLimiter = 0;
    public double defaultSpeed = 0;
    public double speedChange = 0;
    public double maxSpeed = 0;
    public double speedThreshold = 0;
    public double minSpeed = 0;
    public boolean speedMode = false;
    private boolean aIsPressed = false;


    public RobotCentricMechanumDrive(HardwareMap hardwareMap, Direction motorFrontLeftDirection) throws InterruptedException {
        // Declare our motors
        // Make sure your ID's match your configuration
        motorFrontLeft = hardwareMap.dcMotor.get(RobotConstants.FRONT_LEFT);
        motorBackLeft = hardwareMap.dcMotor.get(RobotConstants.BACK_LEFT);
        motorFrontRight = hardwareMap.dcMotor.get(RobotConstants.FRONT_RIGHT);
        motorBackRight = hardwareMap.dcMotor.get(RobotConstants.BACK_RIGHT);
        speedModeLights = hardwareMap.dcMotor.get("lights");
        // Reverse the right side motors
        // Reverse left motors if you are using NeveRests
        // This works for both bots (2939 motorFrontLeftDirection == FORWARD, 3231 motorFrontLeftDirection == REVERSE)
        motorFrontLeft.setDirection(motorFrontLeftDirection);
        motorFrontRight.setDirection(motorFrontLeftDirection.inverted());
        motorBackLeft.setDirection(motorFrontLeftDirection);
        motorBackRight.setDirection(motorFrontLeftDirection.inverted());

    }

    public void drive(Gamepad gamepad, Telemetry telemetry) {

        double y = -gamepad.left_stick_y; // Remember, this is reversed!
        double x = gamepad.left_stick_x * 1.1; // Counteract imperfect strafing
        double rx = gamepad.right_stick_x;

        if(gamepad.a){
            aIsPressed = true;
//            telemetry.addData("Path 1", gamepad.a);
        } else {
//            telemetry.addData("Path 2", gamepad.a);
            if(aIsPressed) {
//                telemetry.addData("Path 2.1", gamepad.a);
                aIsPressed = false;
                speedMode = !speedMode;    
            }
        }

        double frontLeftPower = (y + x + rx);
        double backLeftPower = (y - x + rx);
        double frontRightPower = (y - x - rx);
        double backRightPower = (y + x - rx);

        if(gamepad.dpad_down){
            if(speedMode){
                if(speedModeLimiter - speedChange >= minSpeed){
                    speedModeLimiter -= speedChange;
                }
            }else{
                if(defaultSpeed - speedChange >= speedThreshold) {
                    defaultSpeed -= speedChange;
                }
            }
        } else if(gamepad.dpad_up){
            if(speedMode){
                if(speedModeLimiter + speedChange <= speedThreshold){
                    speedModeLimiter += speedChange;
                }
            }else{
                if(defaultSpeed + speedChange <= maxSpeed){
                    defaultSpeed += speedChange;
                }
            }
        }

        //slow down mode
        if(speedMode){
            frontLeftPower *= speedModeLimiter;
            backLeftPower *= speedModeLimiter;
            frontRightPower *= speedModeLimiter;
            backRightPower *= speedModeLimiter;

            speedModeLights.setPower(1.0);

        }else{
            frontLeftPower *= defaultSpeed;
            backLeftPower *= defaultSpeed;
            frontRightPower *= defaultSpeed;
            backRightPower *= defaultSpeed;

            speedModeLights.setPower(0.0);
        }

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
        this.motorBackLeft.setPower(bL);
        this.motorBackRight.setPower(bR);
    }

    public void setSpeedModeLimiter(double speed) {
        this.speedModeLimiter = speed;
    }
    public void setSpeedChange(double limit){
        this.speedChange = limit;
    }
    public void setDefaultSpeed(double defaultSpeed){
        this.defaultSpeed = defaultSpeed;
    }
    public void setMaxSpeed(double maxSpeed){
        this.maxSpeed = maxSpeed;
    }
    public void setMinSpeed(double minSpeed){
        this.minSpeed = minSpeed;
    }
    public void setSpeedThreshold(double speedThreshold){
        this.speedThreshold = speedThreshold;
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
