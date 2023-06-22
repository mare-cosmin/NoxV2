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
    DcMotorEx liftRight, liftLeft, brat;
    Servo claw;

    ColorRangeSensor color;

    boolean spate = false;

    int pickup_pos = 5;

    final int GROUND_POS = 0;
    final int LOW_POS = 0;
    final int MID_POS = 450;
    final int HIGH_POS = 950;

    final int BRAT_DOWN = 0;

    final int BRAT_SPATE = 200;

    final int BRAT_UP = 120;

    final int BRAT_IDLE = 20;
    boolean auto = false;
    boolean brat_spate = false;
    double pickup_time = 0;

    Encoder leftEncoder, rightEncoder, frontEncoder;

    public void init(HardwareMap hardwareMap){
        leftEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "leftRear")); //exp hub 0
        rightEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "rightFront")); //ctrl hub 2
        frontEncoder = new Encoder(hardwareMap.get(DcMotorEx.class, "rightRear")); //ctrl hub 3

        color = hardwareMap.get(ColorRangeSensor.class, "color");

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
        brat = hardwareMap.get(DcMotorEx.class, "brat");
        bratPickup();
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
        claw.setPosition(0.5);
        spate = false;
    }

    public void closeClaw(){
        claw.setPosition(0.7);
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

    public void bratFloat(){
        brat.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.FLOAT);
        brat.setPower(0);
    }

    public void bratBrake(){
        brat.setZeroPowerBehavior(DcMotor.ZeroPowerBehavior.BRAKE);
    }

    public void bratRunTo(int pos){
        brat.setTargetPosition(pos);
        brat.setMode(DcMotor.RunMode.RUN_TO_POSITION);
        brat.setPower(1);
    }

    public void bratSpate(){
        pickup_time = 99999999;
        brat_spate = true;
        bratBrake();
        bratRunTo(BRAT_SPATE);
    }

    public void bratHighSpate(){
        pickup_time = 99999999;
        brat_spate = true;
        bratBrake();
        bratRunTo(BRAT_SPATE);
    }
    public void bratFata(){
        pickup_time = 99999999;
        brat_spate = false;
        bratBrake();
        bratRunTo(BRAT_UP);
    }
    public void bratHighFata(){
        pickup_time = 99999999;
        brat_spate = false;
        bratBrake();
        bratRunTo(BRAT_UP);
    }

    public void bratIdle(){
        pickup_time = 99999999;
        brat_spate = false;
        bratBrake();
        bratRunTo(BRAT_IDLE);
    }
    public void bratPickup(){
        bratBrake();
        bratRunTo(BRAT_DOWN);
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
