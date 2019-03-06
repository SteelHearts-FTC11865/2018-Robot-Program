package com.steelhearts.main.ops.old;
import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;

@TeleOp(name="Cal-Arm-Ity", group="Linear Opmode")
@Disabled
public class Calarmity extends LinearOpMode {

    private DcMotor armOne;
    private DcMotor armTwo;
    private DcMotor armAngle;

    private DcMotor leftDrive;
    private DcMotor rightDrive;

    private final float armSpeed = 0.7F;
    private final float armAdjustSpeed = 0.8F;

    @Override
    public void runOpMode() {
        armOne  = hardwareMap.get(DcMotor.class, "arm1");
        armTwo = hardwareMap.get(DcMotor.class, "arm2");
        armAngle = hardwareMap.get(DcMotor.class, "armAngle");
        leftDrive = hardwareMap.get(DcMotor.class, "leftDrive");
        rightDrive = hardwareMap.get(DcMotor.class, "rightDrive");

        telemetry.addData("Data Readout", "Program Ready");

        telemetry.update();

        waitForStart();
        while (opModeIsActive()) {
            if (gamepad1.dpad_up){
                armAngle.setPower(armAdjustSpeed);
            } else {
                armAngle.setPower(0);
            }

            if (gamepad1.dpad_down){
                armAngle.setPower(not(armAdjustSpeed));
            } else {
                armAngle.setPower(0);
            }

            if (gamepad1.dpad_left){
                armOne.setPower(armSpeed);
                armTwo.setPower(armSpeed);
            } else {
                armOne.setPower(0);
                armTwo.setPower(0);
            }

            if (gamepad1.dpad_right){
                armOne.setPower(not(armSpeed));
                armTwo.setPower(not(armSpeed));
            } else {
                armOne.setPower(0);
                armTwo.setPower(0);
            }

            leftDrive.setPower(gamepad1.left_stick_y);
            rightDrive.setPower(not(gamepad1.right_stick_x));
        }
    }

    public float not(float invert){
        return invert * -1;
    }
}
