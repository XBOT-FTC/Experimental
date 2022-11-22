package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

@TeleOp(name="MD: Robot Centric (2939)", group="Linear Opmode")
public class MechanumRobotCentric2939 extends LinearOpMode {

    private RobotCentricMechanumDrive drive = null;

    @Override
    public void runOpMode() throws InterruptedException {
        drive = RobotCentricMechanumDrive(DcMotorSimple.Direction.FORWARD);
        // Additional functionality

        waitForStart();
        telemetry.addData("Actual", "%7d : %7d   %7d : %7d",
                motorFrontLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition(),
                motorBackLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition());
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            drive();
            telemetry.update();
        }
    }

}
