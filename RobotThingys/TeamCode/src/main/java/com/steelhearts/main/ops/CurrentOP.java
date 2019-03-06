package com.steelhearts.main.ops;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.steelhearts.main.Configuration;
import com.steelhearts.main.monitoring.HealthPoints;
import com.steelhearts.main.monitoring.SpeedCap;

import org.firstinspires.ftc.robotcore.external.Telemetry;

@TeleOp(name="State OP Mode", group="Linear Opmode")
public class CurrentOP extends LinearOpMode {

    private RevBlinkinLedDriver blinkin;
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DcMotor middleDrive;
    private DcMotor lift;
    private DcMotor arm1;
    private DcMotor arm2;
    private DcMotor armString;
    private CRServo armServo;
    private VoltageSensor battery;

    private boolean armMode = false;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {

        FtcDashboard dashboard = FtcDashboard.getInstance();
        Telemetry dashboardTelemetry = dashboard.getTelemetry();

        blinkin = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        leftDrive = hardwareMap.dcMotor.get("leftDrive");
        rightDrive = hardwareMap.dcMotor.get("rightDrive");
        middleDrive = hardwareMap.dcMotor.get("middleDrive");
        lift = hardwareMap.dcMotor.get("lift");
        arm1 = hardwareMap.dcMotor.get("arm1");
        arm2 = hardwareMap.dcMotor.get("arm2");
        armString = hardwareMap.dcMotor.get("armString");
        armServo = hardwareMap.crservo.get("armServo");

        battery = hardwareMap.voltageSensor.get("Expansion Hub 1");

        //  SpeedCap
        SpeedCap sc = new SpeedCap();

        // Start HealthPoints
        HealthPoints hp = new HealthPoints(blinkin, dashboardTelemetry);
        hp.start();
        hp.setupHPBatteryMonitor(battery);

        waitForStart();
        if (opModeIsActive()) {
            while (opModeIsActive()) {
                // Update LEDS
                hp.updateBatteryMonitor();

                dashboardTelemetry.addData("Voltage", Math.floor(battery.getVoltage()));
                dashboardTelemetry.update();

                // Gamepad 1 Controls

                // Set Wheel Speed
                leftDrive.setPower(-sc.capLeftDrive(gamepad1, Configuration.LEFT_WHEEL_SPEED_LIMIT));
                rightDrive.setPower(sc.capRightDrive(gamepad1, Configuration.RIGHT_WHEEL_SPEED_LIMIT));

                dashboardTelemetry.addData("Left Wheel Power", leftDrive.getPower());
                dashboardTelemetry.addData("Right Wheel Power", rightDrive.getPower());

                // Set Middle Wheel Speed
                if (gamepad1.dpad_left ){
                    middleDrive.setPower(Configuration.SIDE_WHEEL_SPEED_LIMIT);
                    dashboardTelemetry.addData("Middle Drive Power", middleDrive.getPower());
                } else if (gamepad1.dpad_right) {
                    middleDrive.setPower(-Configuration.SIDE_WHEEL_SPEED_LIMIT);
                    dashboardTelemetry.addData("Middle Drive Power", middleDrive.getPower());
                } else {
                    middleDrive.setPower(0);
                    dashboardTelemetry.addData("Middle Drive Power", middleDrive.getPower());
                }

                // Gamepad 2 Controls

                lift.setPower(sc.cap(gamepad2.right_stick_y, Configuration.LIFT_SPEED_LIMIT));

                dashboardTelemetry.addData("Lift Power", lift.getPower());

                // Invert Arm Mode
                if (gamepad2.a){ armMode = !armMode;}

                while (gamepad2.a) { }

                // Cap arm speed a t0.5
                if (gamepad2.dpad_up) {
                    arm1.setPower(-Configuration.UP_ARM_SPEED_LIMIT);
                    arm2.setPower(Configuration.UP_ARM_SPEED_LIMIT);
                } else if (gamepad2.dpad_down) {
                    arm1.setPower(Configuration.DOWN_ARM_SPEED_LIMIT);
                    arm2.setPower(-Configuration.DOWN_ARM_SPEED_LIMIT);
                } else {
                    if (armMode) {
                        arm1.setPower(-Configuration.ARM_HOVER_SPEED);
                        arm2.setPower(Configuration.ARM_HOVER_SPEED);
                    } else {
                        arm1.setPower(0);
                        arm2.setPower(0);
                    }
                }
                if (gamepad2.dpad_left){
                    armServo.setPower(-0.5);
                } else if (gamepad2.dpad_right) {
                    armServo.setPower(0.5);
                } else {
                    armServo.setPower(0);
                }
            }
            hp.stopHPBatteryMonitor();
            try {
                hp.join();
            } catch (InterruptedException e){
                dashboardTelemetry.addData("Stacktrace", e.getStackTrace().toString());
                telemetry.update();
            }
        }
    }
}
