package com.steelhearts.main.ops.old;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.steelhearts.main.monitoring.HealthPoints;
import com.steelhearts.main.monitoring.SpeedCap;

@TeleOp(name="Lift Timing", group="Linear Opmode")
@Disabled
public class Timeing extends LinearOpMode {

    private RevBlinkinLedDriver blinkin;
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DcMotor middleDrive;
    private DcMotor lift;
    private DcMotor arm1;
    private DcMotor arm2;
    private VoltageSensor battery;

    private boolean armMode = false;

    /**
     * This function is executed when this Op Mode is selected from the Driver Station.
     */
    @Override
    public void runOpMode() {
        blinkin = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");
        lift = hardwareMap.dcMotor.get("lift");

        battery = hardwareMap.voltageSensor.get("Expansion Hub 1");

        // Start HealthPoints
        HealthPoints hp = new HealthPoints(blinkin, telemetry);
        hp.start();
        hp.setupHPBatteryMonitor(battery);

        waitForStart();
        if (opModeIsActive()) {
            hp.updateBatteryMonitor();
            lift.setPower(-0.8);
            hp.updateBatteryMonitor();
            sleep(2500);
            hp.updateBatteryMonitor();
            lift.setPower(0);
        }

        hp.stopHPBatteryMonitor();
        try {
            hp.join();
        } catch (InterruptedException e){
            telemetry.addData("Stacktrace", e.getStackTrace().toString());telemetry.update();
        }
    }
}