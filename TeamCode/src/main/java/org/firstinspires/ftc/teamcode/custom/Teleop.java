package org.firstinspires.ftc.teamcode.custom;

import android.content.pm.PackageInfo;
import android.graphics.Color;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.util.ElapsedTime;
import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.util.AsciiArt;

@TeleOp(name = "Teleop", group = "Teleop")
public class Teleop extends LinearOpMode {
    @Override
    public void runOpMode() throws InterruptedException {
        TeleOpHardware robot = new TeleOpHardware();
        ElapsedTime runtime = new ElapsedTime();
        boolean manualOpen = false;
        double touch_time = 0;

        robot.init(hardwareMap);

        double joyScale;
        int joySpeed = 3;
        double up_time = 0;
        double down_time = 0;
        double open_time = 0;

        waitForStart();
        while(opModeIsActive()) {
            if(robot.pickup_time + 1.5 < runtime.seconds() && robot.pickup_time + 1.6 > runtime.seconds()){
                robot.bratOff();
                robot.openClaw();
                robot.auto = true;
            }
            if (gamepad2.dpad_up) {
                robot.spate = true;
            } else {
                if (gamepad2.dpad_down) {
                    robot.spate = false;
                }
            }

            if (robot.auto) {
                if (robot.color.getDistance(DistanceUnit.MM) < 15 && (!manualOpen || runtime.milliseconds() - open_time > 1500)) {
                    robot.closeClaw();
                }
            }

            if (gamepad2.left_trigger > 0.5) {
                robot.auto = false;
                robot.closeClaw();
            } else {
                if (gamepad2.right_trigger > 0.5) {
                    robot.openClaw();
                    manualOpen = true;
                    robot.auto = true;
                    open_time = runtime.milliseconds();
                }
            }

            if (gamepad2.dpad_right) {
                if (up_time + 300 < runtime.milliseconds()) {
                    robot.pickup_pos += (robot.pickup_pos < 5 ? 1 : 0);
                    up_time = runtime.milliseconds();
                }
                telemetry.clear();
            }
            if (gamepad2.dpad_left) {
                if (down_time + 300 < runtime.milliseconds()) {
                    robot.pickup_pos -= (robot.pickup_pos > 1 ? 1 : 0);
                    down_time = runtime.milliseconds();
                }
                telemetry.clear();
            }

            if (!robot.touch.isPressed() && touch_time + 3 < runtime.seconds()){
                telemetry.clear();
                switch (robot.pickup_pos) {
                    case 1:
                        telemetry.addData("1", AsciiArt.ONE);
                    case 2:
                        telemetry.addData("1", AsciiArt.TWO);
                    case 3:
                        telemetry.addData("1", AsciiArt.THREE);
                    case 4:
                        telemetry.addData("1", AsciiArt.FOUR);
                    case 5:
                        telemetry.addData("1", AsciiArt.FIVE);
                }
        }
            telemetry.addData("leftEnc", robot.leftEncoder.getCurrentPosition());
            telemetry.addData("rightEnc", robot.rightEncoder.getCurrentPosition());
            telemetry.addData("frontEnc", robot.frontEncoder.getCurrentPosition());

            if(robot.touch.isPressed()){
                telemetry.clear();
                telemetry.addData("1", AsciiArt.WHITE);
                touch_time = runtime.seconds();
            }

            telemetry.update();

            if(gamepad2.a){
                robot.pickupGround();
                robot.pickup_time = runtime.seconds();
                if(robot.brat_spate)
                    robot.pickup_time = robot.pickup_time-0.5;
            }
            if(gamepad2.b){
                if(robot.spate)
                    robot.coneUpLowSpate();
                else
                    robot.coneUpLowFata();
            }
            if(gamepad2.x){
                if(robot.spate)
                    robot.coneUpMidSpate();
                else
                    robot.coneUpMidFata();
            }
            if(gamepad2.y){
                if(robot.spate)
                    robot.coneUpHighSpate();
                else
                    robot.coneUpHighFata();
            }

            if(gamepad2.start){
                robot.bratIdle();
            }
            if(gamepad2.left_bumper || gamepad2.right_bumper){
                robot.pickupStack();
                robot.pickup_time = runtime.seconds();
            }

            if(gamepad1.right_bumper){
                if(up_time+500 < runtime.milliseconds()) {
                    joySpeed += (joySpeed < 3 ? 1 : 0);
                    up_time = runtime.milliseconds();
                }
            }
            if(gamepad1.left_bumper){
                if(down_time+500 < runtime.milliseconds()){
                    joySpeed -= (joySpeed > 1 ? 1 : 0);
                    down_time = runtime.milliseconds();
                }
            }

            switch(joySpeed){
                case 1:
                    joyScale = 0.4;
                    break;
                case 2:
                    joyScale = 0.6;
                    break;
                default:
                    joyScale = 1;
            }

            double y = -gamepad1.left_stick_y; // Remember, this is reversed!
            double x = gamepad1.left_stick_x; // Counteract imperfect strafing
            double rx = gamepad1.right_stick_x;

            // Denominator is the largest motor power (absolute value) or 1
            // This ensures all the powers maintain the same ratio, but only when
            // at least one is out of the range [-1, 1]
            double denominator = Math.max(Math.abs(y) + Math.abs(x) + Math.abs(rx), 1);
            double frontLeftPower = (joyScale) * (y + x + rx) / denominator;
            double backLeftPower = (joyScale) * (y - x + rx) / denominator;
            double frontRightPower = (joyScale) * (y - x - rx) / denominator;
            double backRightPower = (joyScale) * (y + x - rx) / denominator;

            robot.leftFront.setPower(frontLeftPower);
            robot.leftRear.setPower(backLeftPower);
            robot.rightFront.setPower(frontRightPower);
            robot.rightRear.setPower(backRightPower);
        }
    }
}
