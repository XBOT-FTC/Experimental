package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;

import org.firstinspires.ftc.teamcode.lib.RobotCentricMechanumDrive;


@TeleOp(name="MD: Robot Centric (2939)", group="Linear Opmode")
public class MechanumRobotCentric2939 extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RobotCentricMechanumDrive drive = new RobotCentricMechanumDrive(hardwareMap, Direction.FORWARD);

        // Utility initializations:
        drive.setSpeedLimiter(1.0); // set a the power limit to 1.0 (driver preference)

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            drive.drive(gamepad1, telemetry);
            telemetry.update();
        }
    }

}
