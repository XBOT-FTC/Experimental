package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.Grabber;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;
import org.firstinspires.ftc.teamcode.lib.RobotCentricMechanumDrive;


@TeleOp(name="MD: Robot Centric (2939)", group="Linear Opmode")
public class MechanumRobotCentric2939 extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RobotCentricMechanumDrive drive = new RobotCentricMechanumDrive(hardwareMap, Direction.FORWARD);
        LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE), Direction.REVERSE);
        Grabber grabber = new Grabber(hardwareMap.servo.get(RobotConstants.GRABBER), Servo.Direction.REVERSE);


        // Initializations:

        // Drive
        drive.setSpeedModeLimiter(0.5); // set a the power limit to 1.0 (driver preference)
        drive.setDefaultSpeed(0.7);
        drive.setSpeedChange(0.25);

        // Slider
        slider.setManualSpeed(0.4, 0.25);
        slider.setAutoSpeed(0.5);
        slider.setPosition(600,2200, 3400, 4600);
        slider.setMaxManualPosition(4600);

        // Grabber
        grabber.setMaxPosition(0.6);

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
