package com.steelhearts.main.utils;

import com.qualcomm.robotcore.hardware.Gamepad;

public class SpeedCap {

    public float capLeftDrive(Gamepad gamepad, float cap){
        if (gamepad.left_stick_y < -cap) {
            return -cap;
        } else if (gamepad.left_stick_y > cap) {
            return cap;
        } else {
            return gamepad.left_stick_y;
        }
    }

    public float capRightDrive(Gamepad gamepad, float cap){
        if (gamepad.right_stick_y < -cap) {
            return -cap;
        } else if (gamepad.right_stick_y > cap) {
            return cap;
        } else {
            return gamepad.right_stick_y;
        }
    }

    public float cap(float input, float cap){
        if (input < -cap) {
            return -cap;
        } else if (input > cap) {
            return cap;
        } else {
            return input;
        }
    }
}
