package org.firstinspires.ftc.teamcode.intro;

import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DistanceSensor;

public class challenge1 {

    public class MyFIRSTJavaOpMode extends LinearOpMode {
        DcMotor motorLeft;
        DcMotor motorRight;
        ColorSensor color1;
        DistanceSensor distance1;
        BNO055IMU imu;
        double power = 1;
        @Override
        public void runOpMode() {
            motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
            motorRight = hardwareMap.get(DcMotor.class, "motorRight");
            color1 = hardwareMap.get(ColorSensor.class, "color1");
            distance1 = hardwareMap.get(DistanceSensor.class, "distance1");
            imu = hardwareMap.get(BNO055IMU.class, "imu");
            // Put initialization blocks here
            motorLeft.setDirection(DcMotor.Direction.REVERSE);

            motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

            motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
            motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

            motorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
            motorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

            waitForStart();
            // Put run blocks here

            driveForward(1300,power);

            for (int i = 0; i <5; i++){
                while(motorLeft.isBusy() && motorRight.isBusy()){
                    sleep(10);
                }
            }

        }
        //create function
        private void driveForward(int pos, double speed){
            int leftPos = motorLeft.getCurrentPosition();
            int rightPos = motorRight.getCurrentPosition();
            leftPos += pos;
            rightPos += pos;
            motorLeft.setTargetPosition(leftPos);
            motorRight.setTargetPosition(rightPos);
            motorLeft.setPower(speed);
            motorRight.setPower(speed);

        }
    }

}
