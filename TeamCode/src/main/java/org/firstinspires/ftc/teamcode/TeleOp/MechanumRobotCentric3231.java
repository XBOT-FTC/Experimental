package org.firstinspires.ftc.teamcode.TeleOp;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple.Direction;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.Range;

import org.firstinspires.ftc.teamcode.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.RobotCentricMechanumDrive;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;

@TeleOp(name="MD: Robot Centric (3231)", group="Linear Opmode")
public class MechanumRobotCentric3231 extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        RobotCentricMechanumDrive drive = new RobotCentricMechanumDrive(hardwareMap, Direction.REVERSE);
        LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE));
        // Additional functionality
        Servo grabber = hardwareMap.servo.get(RobotConstants.GRABBER);

        waitForStart();
        if (isStopRequested()) return;

        while (opModeIsActive()) {
            drive.drive(gamepad1, telemetry);
            slider.slide(gamepad2);
            grabber();
            telemetry.update();
        }
    }

    private void grabber() {
        if (gamepad2.left_bumper) {
            grabber.setPosition(0);
        } else if (gamepad2.right_bumper) {
            grabber.setPosition(0.3);
        }
        telemetry.addData("Grabber Position: ", grabber.getPosition());
    }
}
