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

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

import org.firstinspires.ftc.robotcore.external.hardware.camera.WebcamName;
import org.openftc.apriltag.AprilTagDetection;
import org.openftc.easyopencv.OpenCvCamera;
import org.openftc.easyopencv.OpenCvCameraFactory;
import org.openftc.easyopencv.OpenCvCameraRotation;

import java.util.ArrayList;

@Autonomous(name= "Custom Sleeve Parking (20 pts)", group="Linear Opmode")
public class AprilTagParking extends LinearOpMode {

    // Driver Members
    private DcMotor motorFrontLeft = null;
    private DcMotor motorBackLeft = null;
    private DcMotor motorFrontRight = null;
    private DcMotor motorBackRight = null;

    // TODO: EDIT after measuring
        static final double TICKS_PER_INCH = 50;

    OpenCvCamera camera;
    AprilTagDetectionPipeline aprilTagDetectionPipeline;

    static final double FEET_PER_METER = 3.28084;

    enum DIRECTION {
        FORWARD,
        BACKWARD,
        LEFT,
        RIGHT,
        LEFT_TURN,
        RIGHT_TURN
    }

    // Lens intrinsics
    // UNITS ARE PIXELS
    // NOTE: this calibration is for the C920 webcam at 800x448.
    // You will need to do your own calibration for other configurations!
    double fx = 578.272;
    double fy = 578.272;
    double cx = 402.145;
    double cy = 221.506;

    // UNITS ARE METERS
    double tagsize = 0.166;

    int LEFT = 11;
    int MIDDLE = 12;
    int RIGHT = 13;

    AprilTagDetection tagOfInterest = null;

    @Override
    public void runOpMode() {
        int cameraMonitorViewId = hardwareMap.appContext.getResources().getIdentifier("cameraMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        camera = OpenCvCameraFactory.getInstance().createWebcam(hardwareMap.get(WebcamName.class, "Webcam 1"), cameraMonitorViewId);
        aprilTagDetectionPipeline = new AprilTagDetectionPipeline(tagsize, fx, fy, cx, cy);

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

        // Initialize Drive Members and Motors
        motorFrontLeft = hardwareMap.dcMotor.get("motorFrontLeft");
        motorBackLeft = hardwareMap.dcMotor.get("motorBackLeft");
        motorFrontRight = hardwareMap.dcMotor.get("motorFrontRight");
        motorBackRight = hardwareMap.dcMotor.get("motorBackRight");

        // Reverse the right side motors
        // Reverse left motors if you are using NeveRests
        motorFrontLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        motorFrontRight.setDirection(DcMotorSimple.Direction.REVERSE);
        motorBackLeft.setDirection(DcMotorSimple.Direction.FORWARD);
        motorBackRight.setDirection(DcMotorSimple.Direction.REVERSE);

        telemetry.setMsTransmissionInterval(50);

        while (!isStarted() && !isStopRequested()) {
            ArrayList<AprilTagDetection> currentDetections = aprilTagDetectionPipeline.getLatestDetections();

            if (currentDetections.size() != 0) {
                boolean tagFound = false;
                for (AprilTagDetection tag : currentDetections) {
                    tagOfInterest = tag;
                    tagFound = true;
                    break;
                }

                if (tagFound) {
                    telemetry.addLine("Tag of interest is in sight!\n\nLocation data:");
                    tagToTelemetry(tagOfInterest);
                } else {
                    telemetry.addLine("Don't see tag of interest :(");

                    if (tagOfInterest == null) {
                        telemetry.addLine("(The tag has never been seen)");
                    } else {
                        telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                        tagToTelemetry(tagOfInterest);
                    }
                }

            } else {
                telemetry.addLine("Don't see tag of interest :(");

                if (tagOfInterest == null) {
                    telemetry.addLine("(The tag has never been seen)");
                } else {
                    telemetry.addLine("\nBut we HAVE seen the tag before; last seen at:");
                    tagToTelemetry(tagOfInterest);
                }

            }

            telemetry.update();
            sleep(20);
        }

        waitForStart();

        /*
         * The START command just came in: now work off the latest snapshot acquired
         * during the init loop.
         */

        /* Update the telemetry */
        if (tagOfInterest != null) {
            telemetry.addLine("Tag snapshot:\n");
            tagToTelemetry(tagOfInterest);
            telemetry.update();
        } else {
            telemetry.addLine("No tag snapshot available, it was never sighted during the init loop :(");
            telemetry.update();
        }

        /* Actually do something useful */
        if (tagOfInterest == null) {
            /*
             * Insert your autonomous code here, presumably running some default configuration
             * since the tag was never sighted during INIT
             */
            // Move forward to Position #2 for the 1/3 chance it works.
            telemetry.addLine("Running Autonomous without tags");
            telemetry.update();
//            moveLinear(DIRECTION.FORWARD, 9, 0.25);
//            sleep(3000);
            // TODO strafe goes wrong direction!
            strafe(DIRECTION.LEFT, 6, 0.25);
        } else {
            /*
             * Insert your autonomous code here, probably using the tag pose to decide your configuration.
             */

            // e.g.
            if (tagOfInterest.id == LEFT) {
                // Drive to Position #1 (Left) w/ Encoders
                moveLinear(DIRECTION.FORWARD, 16, 0.25);
                strafe(DIRECTION.LEFT, 26, 0.25);
            } else if (tagOfInterest.id == MIDDLE) {
                // Drive to Position #2 (Middle) w/ Encoders
                moveLinear(DIRECTION.FORWARD, 16, 0.25);
            } else if (tagOfInterest.id == RIGHT) {
                // Drive to Position #3 (Right) w/ Encoders
                moveLinear(DIRECTION.FORWARD, 16, 0.25);
                strafe(DIRECTION.RIGHT, 26, 0.25);
            } else {
                // Somehow we got another tag.. Drive to Position #2 (Middle) w/ one third chance.
                moveLinear(DIRECTION.FORWARD, 16, 0.25);
            }
        }
    }

    void tagToTelemetry(AprilTagDetection detection) {
        telemetry.addLine(String.format("\nDetected tag ID=%d", detection.id));
        telemetry.addLine(String.format("Translation X: %.2f feet", detection.pose.x * FEET_PER_METER));
        telemetry.addLine(String.format("Translation Y: %.2f feet", detection.pose.y * FEET_PER_METER));
        telemetry.addLine(String.format("Translation Z: %.2f feet", detection.pose.z * FEET_PER_METER));
        telemetry.addLine(String.format("Rotation Yaw: %.2f degrees", Math.toDegrees(detection.pose.yaw)));
        telemetry.addLine(String.format("Rotation Pitch: %.2f degrees", Math.toDegrees(detection.pose.pitch)));
        telemetry.addLine(String.format("Rotation Roll: %.2f degrees", Math.toDegrees(detection.pose.roll)));
    }

    // Negative speed = moveBackwards
    void moveLinear(DIRECTION direction, int inches, double speed) {
        // Set Drive to run with encoders.
        motorFrontLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        motorFrontLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        // Retrieve the current position for each motor
        int frontLeftPos, frontRightPos, backLeftPos, backRightPos;
        frontLeftPos = motorFrontLeft.getCurrentPosition();
        frontRightPos = motorFrontRight.getCurrentPosition();
        backLeftPos = motorBackLeft.getCurrentPosition();
        backRightPos = motorBackRight.getCurrentPosition();

        if (direction == DIRECTION.FORWARD) {
            // Do nothing
        } else if (direction == DIRECTION.BACKWARD) {
//            inches *= -1;
            speed *= -1;
        } else {
            throw new IllegalArgumentException();
        }

        // Calculate the ticks to be reached
        frontLeftPos += inches * TICKS_PER_INCH;
        frontRightPos += inches * TICKS_PER_INCH;
        backLeftPos += inches * TICKS_PER_INCH;
        backRightPos += inches * TICKS_PER_INCH;

        // Set the goal and power to the motors
        motorFrontLeft.setTargetPosition(frontLeftPos);
        motorFrontRight.setTargetPosition(frontRightPos);
        motorBackLeft.setTargetPosition(backLeftPos);
        motorBackRight.setTargetPosition(backRightPos);

        motorFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorFrontLeft.setPower(speed);
        motorFrontRight.setPower(speed);
        motorBackLeft.setPower(speed);
        motorBackRight.setPower(speed);

        // Wait for encoders to complete it's routine
        while (motorFrontLeft.isBusy() && motorFrontRight.isBusy() &&
                motorBackLeft.isBusy() && motorBackRight.isBusy()) {
            // Display Telemetry Data
            telemetry.addLine("Moving Forward");
            telemetry.addData("Target", "%7d : %7d", frontLeftPos, frontRightPos, backLeftPos, backRightPos);
            telemetry.addData("Actual", "%7d : %7d",
                    motorFrontLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition(),
                    motorBackLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition());
            telemetry.update();
        }

        // Once completed, stop all motors:
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
    }

    // Negative speed = strafeRight
    void strafe(DIRECTION direction, int inches, double speed) {
        // Retrieve the current position for each motor
        int frontLeftPos, frontRightPos, backLeftPos, backRightPos;
        frontLeftPos = motorFrontLeft.getCurrentPosition();
        frontRightPos = motorFrontRight.getCurrentPosition();
        backLeftPos = motorBackLeft.getCurrentPosition();
        backRightPos = motorBackRight.getCurrentPosition();

        if (direction == DIRECTION.LEFT) {
            inches *= -1;
        } else if (direction == DIRECTION.RIGHT) {
            //            speed *= -1;
        } else {
            throw new IllegalArgumentException();
        }

        // Calculate the ticks to be reached
        frontLeftPos -= inches * TICKS_PER_INCH;
        frontRightPos += inches * TICKS_PER_INCH;
        backLeftPos += inches * TICKS_PER_INCH;
        backRightPos -= inches * TICKS_PER_INCH;

        // Set the goal and power to the motors
        motorFrontLeft.setTargetPosition(frontLeftPos);
        motorFrontRight.setTargetPosition(frontRightPos);
        motorBackLeft.setTargetPosition(backLeftPos);
        motorBackRight.setTargetPosition(backRightPos);

        motorFrontLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        motorBackRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);

        motorFrontLeft.setPower(speed);
        motorFrontRight.setPower(speed);
        motorBackLeft.setPower(speed);
        motorBackRight.setPower(speed);

        // Wait for encoders to complete it's routine
        while (motorFrontLeft.isBusy() && motorFrontRight.isBusy() &&
                motorBackLeft.isBusy() && motorBackRight.isBusy()) {
            // Display Telemetry Data
            telemetry.addLine("Moving Forward");
            telemetry.addData("Target", "%7d : %7d", frontLeftPos, frontRightPos, backLeftPos, backRightPos);
            telemetry.addData("Actual", "%7d : %7d",
                    motorFrontLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition(),
                    motorBackLeft.getCurrentPosition(), motorFrontRight.getCurrentPosition());
            telemetry.update();
        }

        // Once completed, stop all motors:
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
    }

    // TODO: IMPLEMENT THIS LATER
    void turn(DIRECTION direction, int angle, double speed) {

    }

    void moveLinearNonEncoder(DIRECTION direction, double seconds, double speed){
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (direction == DIRECTION.FORWARD) {
            // Do nothing
        } else if (direction == DIRECTION.BACKWARD) {
            speed *= -1;
        } else {
            throw new IllegalArgumentException();
        }
        motorFrontLeft.setPower(speed);
        motorFrontRight.setPower(speed);
        motorBackLeft.setPower(speed);
        motorBackRight.setPower(speed);
        sleep((long) (seconds * 1000));
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
    }

    void strafeNonEncoder(DIRECTION direction, double seconds, double speed) {
        motorFrontLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorFrontRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackLeft.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        motorBackRight.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
        if (direction == DIRECTION.LEFT) {
            motorFrontLeft.setPower(speed);
            motorFrontRight.setPower(-1 * speed);
            motorBackLeft.setPower(speed);
            motorBackRight.setPower(-1 * speed);
        } else if (direction == DIRECTION.RIGHT) {
            motorFrontLeft.setPower(-1 * speed);
            motorFrontRight.setPower(speed);
            motorBackLeft.setPower(-1 * speed);
            motorBackRight.setPower(speed);
        } else {
            throw new IllegalArgumentException();
        }
        sleep((long) (seconds * 1000));
        motorFrontLeft.setPower(0);
        motorFrontRight.setPower(0);
        motorBackLeft.setPower(0);
        motorBackRight.setPower(0);
    }
}