package org.firstinspires.ftc.teamcode.custom;


import com.qualcomm.hardware.rev.RevColorSensorV3;
import com.qualcomm.robotcore.hardware.ColorRangeSensor;
import com.qualcomm.robotcore.hardware.ColorSensor;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.hardware.TouchSensor;

import org.firstinspires.ftc.teamcode.util.Encoder;

import java.util.Arrays;
import java.util.List;

public class TeleOpHardware {
    //800 ticks max
    DcMotor leftFront, leftRear, rightFront, rightRear;
    List<DcMotor> motors;
    DcMotorEx liftRight, liftLeft;
    Servo claw, rightBrat, leftBrat;

    RevColorSensorV3 colorUp;
    ColorRangeSensor color;
    TouchSensor touch;

    boolean spate = false;

    int pickup_pos = 5;

    final int GROUND_POS = 0;
    final int LOW_POS = 0;
    final int MID_POS = 450;
    final int HIGH_POS = 950;
    boolean auto = false;
    boolean brat_spate = false;
    double pickup_time = 0;

    Encoder leftEncoder, rightEncoder, frontEncoder;

    public void init(HardwareMap hardwareMap){
        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "leftRear")); //exp hub 0
        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "rightFront")); //ctrl hub 2
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "rightRear")); //ctrl hub 3

        color = hardwareMap.get(ColorRangeSensor.class, "color");
        colorUp = hardwareMap.get(RevColorSensorV3.class, "colorUp");
        touch = hardwareMap.get(TouchSensor.class, "touch");

        leftRear = hardwareMap.dcMotor.get("leftRear");
        leftFront = hardwareMap.dcMotor.get("leftFront");
        rightRear = hardwareMap.dcMotor.get("rightRear");
        rightFront = hardwareMap.dcMotor.get("rightFront");
        motors = Arrays.asList(leftFront, leftRear, rightFront, rightRear);
        for(DcMotor motor : motors){
            motor.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
            motor.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor.setPower(0);
            motor.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        }

        leftFront.setDirection(DcMotor.Direction.REVERSE);
        leftRear.setDirection(DcMotorSimple.Direction.REVERSE);

        liftLeft = hardwareMap.get(DcMotorEx.class, "liftLeft");
        liftRight = hardwareMap.get(DcMotorEx.class, "liftRight");

        liftLeft.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftRight.setMode(DcMotor.RunMode.RUN_USING_ENCODER);

        liftLeft.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);
        liftRight.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);

        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftLeft.setPower(0);
        liftRight.setPower(0);

        claw = hardwareMap.servo.get("claw");
        closeClaw();
        rightBrat = hardwareMap.servo.get("rightBrat");
        leftBrat = hardwareMap.servo.get("leftBrat");
        bratPickup();
    }

    public void bratOn(){
        leftBrat.getController().pwmEnable();
        rightBrat.getController().pwmEnable();
    }

    public void bratOff(){
        leftBrat.getController().pwmDisable();
        rightBrat.getController().pwmDisable();
    }

    public void gotoPosLift(int pos){
        liftLeft.setTargetPosition(pos);
        liftRight.setTargetPosition(-pos);
        liftLeft.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftRight.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        liftLeft.setPower(1);
        liftRight.setPower(1);
    }

    public void openClaw(){
        claw.setPosition(0.15);
        spate = false;
    }

    public void closeClaw(){
        claw.setPosition(0.4);
    }

    public void liftBreak(){
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
        setPowerZeroLift();
    }

    public void liftFloat(){
        liftLeft.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        liftRight.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        setPowerZeroLift();
    }

    public void bratSpate(){
        pickup_time = 99999999;
        brat_spate = true;
        bratOn();
        rightBrat.setPosition(0);
        leftBrat.setPosition(1);
    }

    public void bratHighSpate(){
        pickup_time = 99999999;
        brat_spate = true;
        bratOn();
        rightBrat.setPosition(0);
        leftBrat.setPosition(1);
    }

    public void bratFata(){
        pickup_time = 99999999;
        brat_spate = false;
        bratOn();
        rightBrat.setPosition(0.51);
        leftBrat.setPosition(0.49);
    }

    public void bratHighFata(){
        pickup_time = 99999999;
        brat_spate = false;
        bratOn();
        rightBrat.setPosition(0.44);
        leftBrat.setPosition(0.56);
    }

    public void bratIdle(){
        pickup_time = 99999999;
        brat_spate = false;
        bratOn();
        rightBrat.setPosition(0.96);
        leftBrat.setPosition(0.04);
    }

    public void bratPickup(){
        bratOn();
        rightBrat.setPosition(1);
        leftBrat.setPosition(0);
    }

    public void setPowerZeroLift(){
        liftLeft.setPower(0);
        liftRight.setPower(0);
    }

    public void idlePos(){
        closeClaw();
        auto = false;
        bratIdle();
        gotoPosLift(GROUND_POS);
        if(!liftRight.isBusy())
            liftFloat();
    }

    public void pickupGround(){
        closeClaw();
        auto = false;
        bratPickup();
        gotoPosLift(GROUND_POS);
        if(!liftRight.isBusy())
            liftFloat();
    }

    public void pickupStack(){
        closeClaw();
        switch (pickup_pos){
            case 1:
                pickupGround();
                break;
            case 2:
                bratPickup();
                gotoPosLift(80);
                break;
            case 3:
                bratPickup();
                gotoPosLift(120);
                break;
            case 4:
                bratPickup();
                gotoPosLift(160);
                break;
            default:
                bratPickup();
                gotoPosLift(200);
        }
    }

    public void coneUpLowFata() {
        gotoPosLift(LOW_POS);
        closeClaw();
        auto = false;
        bratFata();
        if(!liftRight.isBusy())
            liftFloat();
    }

    public void coneUpMidFata(){
        closeClaw();
        auto = false;
        bratFata();
        gotoPosLift(MID_POS);
    }
    public void coneUpHighFata(){
        closeClaw();
        auto = false;
        bratHighFata();
        gotoPosLift(HIGH_POS);
        if(!liftRight.isBusy())
            liftBreak();
    }

    public void coneUpLowSpate(){
        closeClaw();
        auto = false;
        bratSpate();
        gotoPosLift(LOW_POS);
        if(!liftRight.isBusy())
            liftFloat();
    }

    public void coneUpMidSpate(){
        closeClaw();
        auto = false;
        gotoPosLift(MID_POS);
        bratSpate();
    }

    public void coneUpHighSpate(){
        closeClaw();
        auto = false;
        gotoPosLift(HIGH_POS);
        bratHighSpate();
        if(!liftRight.isBusy())
            liftBreak();
    }
}
