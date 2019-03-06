package com.steelhearts.main;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;


import org.firstinspires.ftc.robotcore.external.Telemetry;

public class LEDManager extends Thread {

    public boolean doLEDs = true;

    private int patternIndex = 0;
    private RevBlinkinLedDriver blinkin;
    private Telemetry telemetry;

    public LEDManager(RevBlinkinLedDriver blinkin, Telemetry telemetry){
        this.blinkin = blinkin;
        this.telemetry = telemetry;
    }

    public void run() {
        load();
        updateLEDPattern();
        pause(500);
        off();
        updateLEDPattern();
    }

    public void updateLEDPattern() {
        if(doLEDs) {
            switch (patternIndex) {
                case 0:
                    blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.SHOT_RED);
                    break;
                case 1:
                    blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.CP1_2_SINELON);
                    break;
                case 2:
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
                    break;
                case 3:
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
                    break;
                case 4:
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
                    break;
                case 5:
                    blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.COLOR_WAVES_RAINBOW_PALETTE);
                    break;
                case 6:
                    blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.RED);
                    break;
                case 7:
                    blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLUE);
                    break;
                case 8:
                    blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
                    break;
            }
        } else {
            blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BLACK);
        }
    }

    public void pause(long milis){
        try {
            sleep(milis);
        } catch (InterruptedException e){
            telemetry.addData("Error", "Sleep Failed");
            telemetry.update();
            patternIndex = 0;
            try {
                run();
                join();
            } catch (InterruptedException ie){

            }
        }
    }

    public void error(){ patternIndex = 0; }
    public void goldLeft(){ patternIndex = 2; }
    public void goldMid(){ patternIndex = 3; }
    public void goldRight(){ patternIndex = 4; }
    public void unreadyRed(){ patternIndex = 6; }
    public void unreadyBlue(){ patternIndex = 7; }
    public void off(){ patternIndex = 8; }
    public void go(){ patternIndex = 5; }
    public void load() { patternIndex = 1;}

}
