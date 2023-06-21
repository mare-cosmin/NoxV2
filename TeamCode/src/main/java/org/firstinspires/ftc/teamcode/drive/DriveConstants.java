package org.firstinspires.ftc.teamcode.drive;

import com.acmerobotics.dashboard.config.Config;
import com.qualcomm.hardware.rev.RevHubOrientationOnRobot;
import com.qualcomm.robotcore.hardware.PIDFCoefficients;

@Config
public class DriveConstants {

    public static final double TICKS_PER_REV = 537.7;
    public static final double MAX_RPM = 312;


    public static final boolean RUN_USING_ENCODER = false;
    public static PIDFCoefficients MOTOR_VELO_PID = new PIDFCoefficients(0, 0, 0,
            getMotorVelocityF(MAX_RPM / 60 * TICKS_PER_REV));

    public static double WHEEL_RADIUS = 1.89; // in
    public static double GEAR_RATIO = 1;
    public static double TRACK_WIDTH = 11.7; // in

    public static double kV = 0.0176;
    public static double kA = 0.004;
    public static double kStatic = 0.0022;

    public static double MAX_VEL = 40;
    public static double MAX_ACCEL = 40;
    public static double MAX_ANG_VEL = Math.PI/2;
    public static double MAX_ANG_ACCEL = Math.PI/2;

    public static RevHubOrientationOnRobot.LogoFacingDirection LOGO_FACING_DIR =
            RevHubOrientationOnRobot.LogoFacingDirection.FORWARD;
    public static RevHubOrientationOnRobot.UsbFacingDirection USB_FACING_DIR =
            RevHubOrientationOnRobot.UsbFacingDirection.DOWN;


    public static double encoderTicksToInches(double ticks) {
        return WHEEL_RADIUS * 2 * Math.PI * GEAR_RATIO * ticks / TICKS_PER_REV;
    }

    public static double rpmToVelocity(double rpm) {
        return rpm * GEAR_RATIO * 2 * Math.PI * WHEEL_RADIUS / 60.0;
    }

    public static double getMotorVelocityF(double ticksPerSecond) {
        return 32767 / ticksPerSecond;
    }
}
