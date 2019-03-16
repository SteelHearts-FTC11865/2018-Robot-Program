package com.steelhearts.main.ops;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.steelhearts.main.Configuration;
import com.steelhearts.main.utils.HealthPoints;
import com.steelhearts.main.utils.SpeedCap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

import java.util.HashMap;
import java.util.Map;

@TeleOp(name="State OP Mode", group="Linear Opmode")
public class CurrentOP extends LinearOpMode {

    enum CollectorState {
        IN,
        OUT,
        NEUTRAL
    }


    private RevBlinkinLedDriver blinkin;
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DcMotor middleDrive;
    private DcMotor lift;
    private DcMotor lift2;
    private VoltageSensor battery;

    private DcMotor liftArm;
    private DcMotor extend;

    private CRServo rotate;
    private CRServo collector;

    private CollectorState collectorState;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        blinkin = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        leftDrive = hardwareMap.dcMotor.get("leftDrive");
        rightDrive = hardwareMap.dcMotor.get("rightDrive");
        middleDrive = hardwareMap.dcMotor.get("middleDrive");
        lift = hardwareMap.dcMotor.get("lift");
        lift2 = hardwareMap.dcMotor.get("lift2");

        collector = hardwareMap.crservo.get("pickup");
        rotate = hardwareMap.crservo.get("rotatePickup");
        extend = hardwareMap.dcMotor.get("extendArm");
        liftArm = hardwareMap.dcMotor.get("liftArm");

        battery = hardwareMap.voltageSensor.get("Expansion Hub 1");

        //  SpeedCap
        SpeedCap sc = new SpeedCap();

        // Start HealthPoints
        HealthPoints hp = new HealthPoints(blinkin, telemetry);
        hp.start();
        hp.setupHPBatteryMonitor(battery);

        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                // Update LEDS
                hp.updateBatteryMonitor();

                telemetry.addData("Voltage", Math.floor(battery.getVoltage()));
                telemetry.update();

                // Gamepad 1 Controls

                // Set Wheel Speed
                leftDrive.setPower(-sc.capLeftDrive(gamepad1, Configuration.LEFT_WHEEL_SPEED_LIMIT));
                rightDrive.setPower(sc.capRightDrive(gamepad1, Configuration.RIGHT_WHEEL_SPEED_LIMIT));

                telemetry.addData("Left Wheel Power", leftDrive.getPower());
                telemetry.addData("Right Wheel Power", rightDrive.getPower());

                // Set Middle Wheel Speed
                if (rightDrive.getPower() <= 0.1 && gamepad1.dpad_left) {
                    middleDrive.setPower(Configuration.SIDE_WHEEL_SPEED_LIMIT);
                    rightDrive.setPower(-0.6);
                    telemetry.addData("Middle Drive Power", middleDrive.getPower());
                } else if (rightDrive.getPower() <= 0.1 && gamepad1.dpad_right) {
                    middleDrive.setPower(-Configuration.SIDE_WHEEL_SPEED_LIMIT);
                    rightDrive.setPower(0.6);
                    telemetry.addData("Middle Drive Power", middleDrive.getPower());
                } else {
                    middleDrive.setPower(0);
                    leftDrive.setPower(-sc.capLeftDrive(gamepad1, Configuration.LEFT_WHEEL_SPEED_LIMIT));
                    rightDrive.setPower(sc.capRightDrive(gamepad1, Configuration.RIGHT_WHEEL_SPEED_LIMIT));

                    telemetry.addData("Middle Drive Power", middleDrive.getPower());
                }

                // Gamepad 2 Controls

                if (gamepad2.left_trigger != 0) {
                    lift2.setPower(gamepad2.left_trigger);
                    lift.setPower(gamepad2.left_trigger);
                } else if (gamepad2.right_trigger != 0) {
                    lift.setPower(-gamepad2.right_trigger);
                    lift2.setPower(-gamepad2.right_trigger);
                } else {
                    lift.setPower(0);
                    lift2.setPower(0);
                }

                telemetry.addData("Lift Power", lift.getPower());

                if (gamepad2.right_bumper) {
                    collectorState = CollectorState.OUT;
                    collector.setPower(-1);
                } else if (gamepad2.left_bumper) {
                    collectorState = CollectorState.IN;
                    collector.setPower(1);
                } else {
                    collectorState = CollectorState.NEUTRAL;
                    collector.setPower(0);
                }
/*
                switch (collectorState){
                    case IN:
                        collector.setPower(1);
                    case OUT:
                        collector.setPower(-1);
                    case NEUTRAL:
                        collector.setPower(0);
                    default:
                        collector.setPower(0);
                }
*/
                telemetry.addData("Collector State", collectorState);
                if (gamepad2.dpad_up)
                    extend.setPower(0.6);
                else if (gamepad2.dpad_down)
                    extend.setPower(-0.6);
                else
                    extend.setPower(0);

                liftArm.setPower(gamepad2.left_stick_y);
                rotate.setPower(gamepad2.right_stick_y);
            }
            hp.stopHPBatteryMonitor();
            try {
                hp.join();
            } catch (InterruptedException e){
                telemetry.addData("Stacktrace", e.getStackTrace().toString());
                telemetry.update();
            }
        }
    }

    private void sidePower(float power){

        if (power > 1)
            power = 1;
        else if (power < -1)
            power = -1;

        if (power < -0.01){
            rightDrive.setPower(0.5);
            middleDrive.setPower(-power);
        } else if (power > 0.01){
            leftDrive.setPower(0.5);
            middleDrive.setPower(power);
        } else {
            middleDrive.setPower(0);
            rightDrive.setPower(0);
        }

    }
}
