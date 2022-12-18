package org.firstinspires.ftc.teamcode.Autonomous;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.FORWARD;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.LEFT_STRAFE;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.RIGHT_STRAFE;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.trajectory.Trajectory;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.RobotConstants;
import org.firstinspires.ftc.teamcode.lib.AprilTagsCamera;
import org.firstinspires.ftc.teamcode.lib.Grabber;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;
import org.firstinspires.ftc.teamcode.lib.Roadrunner.drive.SampleMecanumDrive;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

@Disabled
public class RoadRunnerExample extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE), DcMotorSimple.Direction.REVERSE);
        Grabber grabber = new Grabber(hardwareMap.servo.get(RobotConstants.GRABBER), Servo.Direction.REVERSE);
        grabber.setMaxPosition(0.6);

        Pose2d startPose = new Pose2d(36, 62, Math.toRadians(180));

        Trajectory myTrajectory = drive.trajectoryBuilder(startPose)
                .addTemporalMarker(1, () -> {
                    // Grab onto cone
                    grabber.grabberMotor.setPosition(grabber.maxPosition);
                })
                .splineTo(new Vector2d(36, 12), Math.toRadians(270))
                .addTemporalMarker(3, () -> {
                    // Move slide up
                    slider.slideMotor.setTargetPosition(3400);
                    slider.slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    slider.slideMotor.setPower(0.5);

                })
                .splineTo(new Vector2d(31, 12), Math.toRadians(135))
                .addTemporalMarker(1, () -> {
                    // Release the cone
                    grabber.grabberMotor.setPosition(0);
                })
                .lineToLinearHeading(new Pose2d(36, 12, Math.toRadians(0)))
                .addTemporalMarker(3, () -> {
                    // Move slide down
                    slider.slideMotor.setTargetPosition(600);
                    slider.slideMotor.setMode(DcMotor.RunMode.RUN_TO_POSITION);
                    slider.slideMotor.setPower(0.5);
                })
                .build();

        // Parking trajectories:
        Trajectory parkingTwo = drive.trajectoryBuilder(new Pose2d(36, 12, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(36, 32, Math.toRadians(0)))
                .build();

        Trajectory parkingThree = drive.trajectoryBuilder(new Pose2d(36, 12, Math.toRadians(0)))
                .lineToLinearHeading(new Pose2d(36, 62, Math.toRadians(0)))
                .build();

        telemetry.setMsTransmissionInterval(50);
        double fx = 578.272;
        double fy = 578.272;
        double cx = 402.145;
        double cy = 221.506;

        double tagsize = 0.166;
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        OpenCvCamera camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        AprilTagDetectionPipeline aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

        camera.setPipeline(aprilTagDetectionPipeline);
        camera.openCameraDeviceAsync(new OpenCvCamera.AsyncCameraOpenListener() {
            @Override
            public void onOpened() {
                camera.startStreaming(800, 448, OpenCvCameraRotation.UPRIGHT);
            }

            @Override
            public void onError(int errorCode) {

            }
        });

        AprilTagsCamera aprilTagsCamera = new AprilTagsCamera(aprilTagDetectionPipeline);

        AprilTagDetection tagOfInterest = null;
        while (!isStarted() && !isStopRequested()) {
            tagOfInterest = aprilTagsCamera.detectTag(telemetry);
        }

        waitForStart();
        aprilTagsCamera.updateTelemetry(telemetry);

        // Retrieve the position
        int position = aprilTagsCamera.getPosition(tagOfInterest);
        telemetry.addLine("We are heading towards position #" + position + "!");
        switch (position) {
            case 1:
                // Position #1 (Left)
                drive.followTrajectory(myTrajectory);
                break;
            case 2:
                // Position #2 (Middle)
                drive.followTrajectory(myTrajectory);
                drive.followTrajectory(parkingTwo);
                break;
            case 3:
                // Position #3 (Right)
                drive.followTrajectory(myTrajectory);
                drive.followTrajectory(parkingThree);
                break;
            default:
                // Tag is not found
                drive.followTrajectory(myTrajectory);
                telemetry.addLine("Tag was not found.");
        }
        telemetry.update();
    }
}