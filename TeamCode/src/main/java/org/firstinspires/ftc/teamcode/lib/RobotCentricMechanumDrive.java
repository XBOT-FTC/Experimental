package org.firstinspires.ftc.teamcode.lib;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.Servo;
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

        double limiter = 0.35;
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

}
