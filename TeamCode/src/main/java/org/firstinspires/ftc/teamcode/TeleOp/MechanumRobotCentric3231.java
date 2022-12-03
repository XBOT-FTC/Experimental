package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;


import org.firstinspires.ftc.teamcode.lib.Grabber;
import org.firstinspires.ftc.teamcode.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.RobotCentricMechanumDrive;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;

@TeleOp(name="MD: Robot Centric (3231)", group="Linear Opmode")
public class MechanumRobotCentric3231 extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RobotCentricMechanumDrive drive = new RobotCentricMechanumDrive(hardwareMap, Direction.REVERSE);
        LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE), Direction.FORWARD);
        Grabber grabber = new Grabber(hardwareMap.servo.get(RobotConstants.GRABBER), Servo.Direction.FORWARD);

        // Utility initializations:
        drive.setSpeedLimiter(0.5); // set a the power limit to 0.5 (driver preference)

        slider.setManualSpeed(0.25);
        slider.setAutoSpeed(0.5);
        slider.setPosition(500, 1000, 1500);

        grabber.setMaxPosition(0.3);

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            drive.drive(gamepad1, telemetry);
            slider.slide(gamepad2, telemetry);
            grabber.grab(gamepad2, telemetry);
            telemetry.update();
        }
    }

}
