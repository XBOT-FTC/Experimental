package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Servo;


import org.firstinspires.ftc.teamcode.lib.Grabber;
import org.firstinspires.ftc.teamcode.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.RobotCentricMechanumDrive;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;

@TeleOp(name="MD: Robot Centric (3231)", group="Linear Opmode")
public class MechanumRobotCentric3231 extends LinearOpMode {


    @Override
    public void runOpMode() throws InterruptedException {
        RobotCentricMechanumDrive drive = new RobotCentricMechanumDrive(hardwareMap, Direction.REVERSE);
        LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE), Direction.REVERSE);
        Grabber grabber = new Grabber(hardwareMap.servo.get(RobotConstants.GRABBER), Servo.Direction.FORWARD);

        // Initializations

        // Drive
        drive.setSpeedModeLimiter(0.5); // set a the power limit to 0.5 (driver preference)
        drive.setDefaultSpeed(0.7);
        drive.setSpeedChange(0.25);
        drive.setMaxSpeed(1);
        drive.setMinSpeed(0.25);
        drive.setMidSpeed(0.5);


        // Slider
        slider.setManualSpeed(0.5, 0.5);
        slider.setAutoSpeed(0.5);
        slider.setPosition(0,3000, 4750, 6500);
        slider.setMaxManualPosition(6400);

        // Grabber
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
