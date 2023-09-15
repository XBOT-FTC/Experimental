package org.firstinspires.ftc.teamcode;


import com.qualcomm.robotcore.hardware.DcMotorSimple;

public class RobotConstants {

    // Mechanum Drive Hardware
    public static final String FRONT_LEFT = "frontLeft";
    public static final String FRONT_RIGHT = "frontRight";
    public static final String BACK_LEFT = "backLeft";
    public static final String BACK_RIGHT = "backRight";

    // Operating Hardware
    public static final String SLIDE = "linearSlide";
    public static final String GRABBER = "grabber";
    public static final String LIGHT_1 = "light_1";
    public static final String TOUCH_SENSOR = "touch_sensor";


    // Autonomous Drive Constants (AprilTags)

    public static class Commands {
        public enum DRIVE {
            FORWARD,
            BACKWARD,
            LEFT_STRAFE,
            RIGHT_STRAFE,
            LEFT_TURN,
            RIGHT_TURN
        }

        public enum SLIDE {
            SHORT,
            MEDIUM,
            LONG,
            GROUND
        }

        public enum GRABBER {
            GRAB,
            RETRACT
        }
    }
}
