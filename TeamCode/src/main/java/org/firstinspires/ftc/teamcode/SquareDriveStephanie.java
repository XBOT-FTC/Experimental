package org.firstinspires.ftc.teamcode;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;

@Autonomous(name="SquareDrive", group="Testing")
public class SquareDriveStephanie extends LinearOpMode {

    // Declare OpMode Members (Motors, Servos.. etc)
    private DcMotor frontLeftDrive = null;
    private DcMotor frontRightDrive = null;
    private DcMotor backLeftDrive = null;
    private DcMotor backRightDrive = null;

    static private double motorPower = .75;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize Mode
        frontLeftDrive = hardwareMap.get(DcMotor.class, "frontLeft");
        frontRightDrive = hardwareMap.get(DcMotor.class, "frontRight");
        backLeftDrive = hardwareMap.get(DcMotor.class, "backLeft");
        backRightDrive = hardwareMap.get(DcMotor.class, "backRight");

        frontLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        frontRightDrive.setDirection(DcMotor.Direction.REVERSE);
        backLeftDrive.setDirection(DcMotor.Direction.FORWARD);
        backRightDrive.setDirection(DcMotor.Direction.REVERSE);

        // Wait until (play) is pressed
        waitForStart();



        // Insert Autonomous Code below:
        // drive forward
        frontLeftDrive.setPower(motorPower);
        frontRightDrive.setPower(motorPower);
        backLeftDrive.setPower(motorPower);
        backRightDrive.setPower(motorPower);

        sleep(2000);
        // turn right and move forward 3 times
        for(int i = 0; i < 3; i++){
            //turn right
            frontLeftDrive.setPower(motorPower);
            frontRightDrive.setPower(-motorPower);
            backLeftDrive.setPower(motorPower);
            backRightDrive.setPower(-motorPower);
            sleep(2000);
            //move forward
            frontLeftDrive.setPower(motorPower);
            frontRightDrive.setPower(motorPower);
            backLeftDrive.setPower(motorPower);
            backRightDrive.setPower(motorPower);
            sleep(1000);

        }
        //stop after finished
        frontLeftDrive.setPower(0);
        frontRightDrive.setPower(0);
        backLeftDrive.setPower(0);
        backRightDrive.setPower(0);


    }
}
