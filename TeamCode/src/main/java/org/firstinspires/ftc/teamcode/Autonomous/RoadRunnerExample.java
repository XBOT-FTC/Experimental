package org.firstinspires.ftc.teamcode.Autonomous;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.Grabber;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;
import org.firstinspires.ftc.teamcode.lib.Roadrunner.drive.SampleMecanumDrive;

@Disabled
public class RoadRunnerExample extends LinearOpMode {
    @Override
    public void runOpMode() {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE), DcMotorSimple.Direction.REVERSE);
        Grabber grabber = new Grabber(hardwareMap.servo.get(RobotConstants.GRABBER), Servo.Direction.REVERSE);

        Trajectory myTrajectory = drive.trajectoryBuilder(new Pose2d())
                .splineTo(new Vector2d(36, 12), Math.toRadians(270))
                // Slide Up
                .splineTo(new Vector2d(31, 12), Math.toRadians(135))
                // Grab: Close
                .lineToLinearHeading(new Pose2d(36, 12, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(56, 12, Math.toRadians(0)))
                // Slide Down
                // Grab: Open
                .lineToLinearHeading(new Pose2d(36, 12, Math.toRadians(0)))
//              .lineToLinearHeading(new Pose2d(31, 12, Math.toRadians(135)))
                .build();

        waitForStart();

        if(isStopRequested()) return;

        drive.followTrajectory(myTrajectory);
    }
}