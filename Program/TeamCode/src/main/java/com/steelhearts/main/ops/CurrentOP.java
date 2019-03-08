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
    private VoltageSensor battery;

    private CRServo collector;

    private CollectorState collectorState = CollectorState.NEUTRAL;

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

        collector = hardwareMap.crservo.get("rotatePickup");

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
                if (gamepad1.dpad_left ){
                    middleDrive.setPower(Configuration.SIDE_WHEEL_SPEED_LIMIT);
                    telemetry.addData("Middle Drive Power", middleDrive.getPower());
                } else if (gamepad1.dpad_right) {
                    middleDrive.setPower(-Configuration.SIDE_WHEEL_SPEED_LIMIT);
                    telemetry.addData("Middle Drive Power", middleDrive.getPower());
                } else {
                    middleDrive.setPower(0);
                    telemetry.addData("Middle Drive Power", middleDrive.getPower());
                }

                // Gamepad 2 Controls

                lift.setPower(sc.cap(gamepad2.right_stick_y, Configuration.LIFT_SPEED_LIMIT));

                telemetry.addData("Lift Power", lift.getPower());

                switch (collectorState){
                    case IN:
                        collector.setPower(1);
                    case OUT:
                        collector.setPower(-1);
                    case NEUTRAL:
                        collector.setPower(0);
                }

                if (!gamepad2.left_bumper && !gamepad2.right_bumper){
                    collectorState = CollectorState.NEUTRAL;
                } else if (gamepad2.right_bumper){
                    collectorState = CollectorState.OUT;
                } else if (gamepad2.left_bumper){
                    collectorState = CollectorState.IN;
                }
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
}
