package com.steelhearts.main;

@com.acmerobotics.dashboard.config.Config
public class Configuration {

    /*
     * TO UPLOAD TO THE PHONE
     *
     * Once you are done editing this file, plug the phone that plugs into the robot into the computer.
     * Then click the GREEN TRIANGLE not the triangle on a gear and click on the phone in the connected devices menu
     * Finally, click OK, and wait for the app to close itself and then launch itself again, then unplug the phone
     */

    /*
     * MOTOR SPEEDS / SPEED LIMIT
     *
     * Go ahead and mess with them, just be careful with ARM_HOVER_SPEED,
     * If it's too high it could damage the arm
     *
     * I don't think I need to say this but don't delete the variables.
     */

    public static float UP_ARM_SPEED_LIMIT = 0F;
    public static float DOWN_ARM_SPEED_LIMIT = 0F;
    public static float LEFT_WHEEL_SPEED_LIMIT = 1F;
    public static float RIGHT_WHEEL_SPEED_LIMIT = 1F;
    public static float SIDE_WHEEL_SPEED_LIMIT = 1F;
    public static float LIFT_SPEED_LIMIT = 1F;
    public static float ARM_HOVER_SPEED = 0.15F;

    /*
     * AUTONOMOUS TIMINGS
     *
     * Backup to Autonomous Old.txt on desktop before editing
     * Also, BE VERY CAREFUL
    */

    public static long ARM_LOWER_TIME = 2100;
    public static long DEHOOK_LEFT_TIME = 300;
    public static long FORWARD_HOOK_TIME = 600;
    public static long RIGHT_ALIGNING_TIME = 850;
    public static long LEFT_ALIGNING_TIME = 1350;
    public static long FORWARD_TO_HIT_TIME = 1500;

}
