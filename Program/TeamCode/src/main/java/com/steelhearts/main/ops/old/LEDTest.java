package com.steelhearts.main.ops.old;

import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.steelhearts.main.LEDManager;

@TeleOp(name="LED Test", group="Linear Opmode")
@Disabled
public class LEDTest extends LinearOpMode {
    RevBlinkinLedDriver blinkin;

    @Override
    public void runOpMode() {
        blinkin = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");

        LEDManager lm = new LEDManager(blinkin, telemetry);
        lm.start();

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.y){
                lm.error();
                lm.updateLEDPattern();
            }
            if (gamepad1.b){
                lm.goldLeft();
                lm.updateLEDPattern();
            }
            if (gamepad1.a){
                lm.goldMid();
                lm.updateLEDPattern();
            }
            if (gamepad1.x){
                lm.goldRight();
                lm.updateLEDPattern();
            }
            if (gamepad1.right_bumper){
                lm.unreadyRed();
                lm.updateLEDPattern();
            }
            if (gamepad1.left_bumper){
                lm.unreadyBlue();
                lm.updateLEDPattern();
            }
            if (gamepad1.dpad_up){
                lm.go();
                lm.updateLEDPattern();
            }
            if (gamepad1.dpad_down){
                lm.load();
                lm.updateLEDPattern();
            }
            if (gamepad1.dpad_left){
                lm.off();
                lm.updateLEDPattern();
            }
        }
    }
}
