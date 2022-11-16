package org.firstinspires.ftc.teamcode.Autonomous;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

//@Disabled
@Autonomous(name="MecanumDriveForwardMohamed", group="Testing")
public class MecanumDriveForwardMohamed extends LinearOpMode {

    // Declare OpMode Members (Motors, Servos.. etc)

    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize Mode
        frontLeftDrive = hardwareMap.get(DcMotor.class,"frontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class,"frontRight");
        backLeftDrive = hardwareMap.get(DcMotor.class,"backLeft");
        backRightDrive = hardwareMap.get(DcMotor.class,"backRight");

        frontLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotorSimple.Direction.FORWARD);
        backRightDrive.setDirection(DcMotorSimple.Direction.REVERSE);

        // Wait until (play) is pressed
        waitForStart();

        // Insert Autonomous Code below:

        // Initialize variables to each motor

        frontLeftDrive.setPower(1);
        frontRightDrive.setPower(1);
        backLeftDrive.setPower(1);
        backRightDrive.setPower(1);

        sleep(2000);

        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);



    }
}
