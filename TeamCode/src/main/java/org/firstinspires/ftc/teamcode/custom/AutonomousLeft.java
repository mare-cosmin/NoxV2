package org.firstinspires.ftc.teamcode.custom;

import com.acmerobotics.roadrunner.geometry.Pose2d;
import com.acmerobotics.roadrunner.geometry.Vector2d;
import com.acmerobotics.roadrunner.profile.VelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.AngularVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.MinVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TrajectoryVelocityConstraint;
import com.acmerobotics.roadrunner.trajectory.constraints.TranslationalVelocityConstraint;
import com.qualcomm.robotcore.eventloop.opmode.Autonomous;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;

import org.firstinspires.ftc.teamcode.drive.SampleMecanumDrive;
import org.firstinspires.ftc.teamcode.trajectorysequence.TrajectorySequence;

import java.util.Arrays;

@Autonomous(name = "Autonomous Left", group = "Autonomous")
public class AutonomousLeft extends LinearOpMode {

    @Override
    public void runOpMode() throws InterruptedException {
        SampleMecanumDrive drive = new SampleMecanumDrive(hardwareMap);
        int tag = 0;
        Detection detectare = new Detection();
        detectare.init(hardwareMap);
        while (opModeInInit()) {
            tag = detectare.detect();
            telemetry.addData("tag", tag);
            telemetry.update();
        }
        Pose2d startPose = new Pose2d(35.1, 62.8, Math.toRadians(90));
        drive.setPoseEstimate(startPose);
        TrajectoryVelocityConstraint slowConstraint = new MinVelocityConstraint(Arrays.asList(

                new TranslationalVelocityConstraint(10),

                new AngularVelocityConstraint(3)

        ));
        TrajectorySequence trajSeq = drive.trajectorySequenceBuilder(startPose)
                .lineToLinearHeading(new Pose2d(35.1, 11, Math.toRadians(180)))
                .build();
        waitForStart();
        if (!isStopRequested()) {
            drive.followTrajectorySequence(trajSeq);
            switch (tag){
                case 5:
                    drive.followTrajectorySequence(drive.trajectorySequenceBuilder(trajSeq.end())
                            .setVelConstraint(slowConstraint)
                            .lineTo(new Vector2d(10, 11))
                            .build());
                    break;
                case 3:
                            drive.followTrajectorySequence(drive.trajectorySequenceBuilder(trajSeq.end())
                            .setVelConstraint(slowConstraint)
                            .lineTo(new Vector2d(59, 11))
                            .build());
                    break;

            }
        }
    }
}
