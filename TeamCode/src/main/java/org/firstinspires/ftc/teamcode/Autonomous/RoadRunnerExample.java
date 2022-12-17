package org.firstinspires.ftc.teamcode.Autonomous;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.Grabber;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;
import org.firstinspires.ftc.teamcode.lib.Roadrunner.drive.SampleMecanumDrive;

public class RoadRunnerExample extends LinearOpMode {
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE), DcMotorSimple.Direction.REVERSE);
        Grabber grabber = new Grabber(hardwareMap.servo.get(RobotConstants.GRABBER), Servo.Direction.REVERSE);

        Trajectory myTrajectory = drive.trajectoryBuilder(new Pose2d())
                .forward(50)
                .turn(Math.toRadians(-135))
                // Slide Up
                .forward(5)
                // Grab: Open
                .back(5)
                // Slide Down
                .turn(Math.toRadians(-135))
                .forward(20)
                // Slide Up
                // Slide Down
                // Grab: Open
                // Slide Down
                // Slide Up
                .back(20)
                .turn(Math.toRadians(135))
                .forward(5)
                // Grab: Open
                .back(5)
                // Slide Down
                .turn(Math.toRadians(-135))
                .forward(20)
                // Slide Up
                // Slide Down
                // Grab: Open
                // Slide Down
                // Slide Up
                .back(20)
                .turn(Math.toRadians(135))
                .forward(5)
                // Grab: Open
                .back(5)
                // Slide Down
                .turn(Math.toRadians(-135))
                // Park at designated spot:
                .strafeLeft(20)
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectory(myTrajectory);
    }
}