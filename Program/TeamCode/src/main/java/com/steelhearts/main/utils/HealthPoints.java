package com.steelhearts.main.utils;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.hardware.VoltageSensor;

import org.firstinspires.ftc.robotcore.external.Telemetry;

public class HealthPoints extends Thread {

    private RevBlinkinLedDriver blinkin;
    private Telemetry telemetry;
    private VoltageSensor batt = null;

    private static final double MIN_VOLTAGE = 11.0;
    private static final double MAX_VOLTAGE = 13.5;

    private static boolean HPRun = false;

    private final RevBlinkinLedDriver.BlinkinPattern[] COLORS = {RevBlinkinLedDriver.BlinkinPattern.RED, RevBlinkinLedDriver.BlinkinPattern.RED_ORANGE, RevBlinkinLedDriver.BlinkinPattern.ORANGE, RevBlinkinLedDriver.BlinkinPattern.GOLD, RevBlinkinLedDriver.BlinkinPattern.YELLOW, RevBlinkinLedDriver.BlinkinPattern.LAWN_GREEN, RevBlinkinLedDriver.BlinkinPattern.LIME, RevBlinkinLedDriver.BlinkinPattern.DARK_GREEN};

    public HealthPoints(RevBlinkinLedDriver blinkin, Telemetry telemetry){
        this.blinkin = blinkin;
        this.telemetry = telemetry;
    }

    public void run() {
        load();
        pause(500);
        off();
    }

    public void setupHPBatteryMonitor(VoltageSensor battery){
        batt = battery;
    }

    public void stopHPBatteryMonitor(){
        HPRun = false;
    }

    public void pause(long milis){
        try {
            sleep(milis);
        } catch (InterruptedException e){
            telemetry.addData("Error", "Sleep Failed");
            telemetry.update();
            error();
        }
    }

    private RevBlinkinLedDriver.BlinkinPattern getBatteryVoltageColor(double voltage) {
        double difference = MAX_VOLTAGE - MIN_VOLTAGE;
        double bucketSize = difference / COLORS.length;

        // If voltage is below the minimum, use the first color.
        if (voltage - MIN_VOLTAGE <= 0) {
            return COLORS[0];
        }

        // If voltage is above the maximum, use the last color.
        else if (MAX_VOLTAGE - voltage <= 0) {
            return COLORS[COLORS.length - 1];
        }

        // If voltage is in between min/max, select the color closest to its position.
        else {
            int bucket = (int) Math.floor((voltage - MIN_VOLTAGE) / bucketSize);
            return COLORS[bucket];
        }
    }

    public void error() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.SHOT_RED);
    }

    public void load() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_2_SINELON);
    }

    public void goldLeft() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.GOLD);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

    public void goldMid() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.GOLD);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

    public void goldRight() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.WHITE);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.GOLD);
        pause(500);
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

    public void goColor1() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_HEARTBEAT_MEDIUM);
    }

    public void goColor2() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP2_HEARTBEAT_MEDIUM);
    }

    public void off() {
        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
    }

    public void updateBatteryMonitor(){
        blinkin.setPattern(getBatteryVoltageColor(batt.getVoltage()));
    }


}
