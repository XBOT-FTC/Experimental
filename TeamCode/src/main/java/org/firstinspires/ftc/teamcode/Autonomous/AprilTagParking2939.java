/*
 * Copyright (c) 2021 OpenFTC Team
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.firstinspires.ftc.teamcode.Autonomous;

import org.firstinspires.ftc.teamcode.RobotConstants;

import static org.firstinspires.ftc.robotcore.external.BlocksOpModeCompanion.hardwareMap;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.FORWARD;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.BACKWARD;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.LEFT_STRAFE;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.RIGHT_STRAFE;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.LEFT_TURN;
import static org.firstinspires.ftc.teamcode.RobotConstants.Commands.DRIVE.RIGHT_TURN;


import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.robot.Robot;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.firstinspires.ftc.teamcode.lib.AprilTagsCamera;
import org.firstinspires.ftc.teamcode.lib.Grabber;
import org.firstinspires.ftc.teamcode.lib.LinearSlider;
import org.firstinspires.ftc.teamcode.lib.RobotCentricMechanumDrive;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;


import java.util.ArrayList;

@Autonomous(name= "Custom Sleeve Parking (2939)", group="Linear Opmode")
public class AprilTagParking2939 extends LinearOpMode {

    static final double TICKS_PER_INCH = 50;
    static final double TICKS_PER_DEGREE = 50;

    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    double tagsize = 0.166;

    @Override
    public void runOpMode() throws InterruptedException {
        // Initialize the robot
        RobotCentricMechanumDrive drive = new RobotCentricMechanumDrive(hardwareMap, DcMotorSimple.Direction.FORWARD);
        drive.setTicks(TICKS_PER_INCH, TICKS_PER_INCH);

        // Initialize Operator Members and Motors
        // Grabber grabber = new Grabber(hardwareMap.servo.get(RobotConstants.GRABBER), Servo.Direction.REVERSE);
        // LinearSlider slider = new LinearSlider(hardwareMap.dcMotor.get(RobotConstants.SLIDE), DcMotorSimple.Direction.REVERSE);

        // Initialize camera
        telemetry.setMsTransmissionInterval(50);
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
                drive.moveLinear(FORWARD, 16, 0.25, telemetry);
                drive.strafe(LEFT_STRAFE, 26, 0.25, telemetry);
                break;
            case 2:
                // Position #2 (Middle)
                drive.moveLinear(FORWARD, 16, 0.25, telemetry);
                break;
            case 3:
                // Position #3 (Right)
                drive.moveLinear(FORWARD, 16, 0.25, telemetry);
                drive.strafe(RIGHT_STRAFE, 26, 0.25, telemetry);
                break;
            default:
                // Tag is not found
                telemetry.addLine("Tag was not found.");
        }
        telemetry.update();
    }

}
