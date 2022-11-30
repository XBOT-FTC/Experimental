import com.qualcomm.hardware.bosch.BNO055IMU;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DistanceSensor;

/***********************************************************************
 *                                                                      *
 * OnbotJava Editor is still : beta! Please inform us of any bugs      *
 * on our discord channel! https://discord.gg/e7nVjMM                   *
 * Only BLOCKS code is submitted when in Arena                          *
 *                                                                      *
 ***********************************************************************/


public class MyFIRSTJavaOpMode extends LinearOpMode {
    DcMotor motorLeft;
    DcMotor motorRight;
    ColorSensor color1;
    DistanceSensor distance1;
    BNO055IMU imu;

    @Override
    public void runOpMode() {
        motorLeft = hardwareMap.get(DcMotor.class, "motorLeft");
        motorRight = hardwareMap.get(DcMotor.class, "motorRight");
        color1 = hardwareMap.get(ColorSensor.class, "color1");
        distance1 = hardwareMap.get(DistanceSensor.class, "distance1");
        imu = hardwareMap.get(BNO055IMU.class, "imu");

        motorLeft.setDirection(DcMotorSimple.Direction.REVERSE);
        motorRight.setDirection(DcMotorSimple.Direction.FORWARD);

        // Put initialization blocks here
        waitForStart();

        driveForward(800, 0.5);
        sleep(1500);
        driveForward(550, 0.5);
    }

    void driveForward(int ticks, double speed) {
        // Set Drive to run with encoders.
        motorLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Retrieve the current position for each motor
        int leftPos = motorLeft.getCurrentPosition();
        int rightPos = motorRight.getCurrentPosition();

        // Calculate the ticks to be reached
        leftPos += ticks;
        rightPos += ticks;

        motorLeft.setTargetPosition(leftPos);
        motorRight.setTargetPosition(rightPos);

        motorLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorLeft.setPower(speed);
        motorRight.setPower(speed);

        // Wait for encoders to complete it's routine
        while (motorLeft.isBusy() && motorRight.isBusy()) {
            // Display Telemetry Data
        }

    }

}
