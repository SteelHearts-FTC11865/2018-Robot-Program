package com.steelhearts.main.ops;

import com.acmerobotics.dashboard.FtcDashboard;
import com.qualcomm.hardware.rev.RevBlinkinLedDriver;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.steelhearts.main.Configuration;
import com.steelhearts.main.utils.HealthPoints;
import com.vuforia.CameraDevice;

import org.firstinspires.ftc.robotcore.external.ClassFactory;
import org.firstinspires.ftc.robotcore.external.navigation.VuforiaLocalizer;
import org.firstinspires.ftc.robotcore.external.tfod.Recognition;
import org.firstinspires.ftc.robotcore.external.tfod.TFObjectDetector;

import java.util.List;

@com.qualcomm.robotcore.eventloop.opmode.Autonomous(name = "Autonomous Depot", group = "Autonomous")
public class AutonomousDepot extends LinearOpMode {
    private static final String TFOD_MODEL_ASSET = "RoverRuckus.tflite";
    private static final String LABEL_GOLD_MINERAL = "Gold Mineral";
    private static final String LABEL_SILVER_MINERAL = "Silver Mineral";

    /*
     * IMPORTANT: You need to obtain your own license key to use Vuforia. The string below with which
     * 'parameters.vuforiaLicenseKey' is initialized is for illustration only, and will not function.
     * A Vuforia 'Development' license key, can be obtained free of charge from the Vuforia developer
     * web site at https://developer.vuforia.com/license-mmanager.
     *
     * Vuforia license keys are always 380 characters long, and look as if they contain mostly
     * random data. As an example, here is a example of a fragment of a valid key:
     *      ... yIgIzTqZ4mWjk9wd3cZO9T1axEqzuhxoGlfOOI2dRzKS4T0hQ8kT ...
     * Once you've obtained a license key, copy the string from the Vuforia web site
     * and paste it in to your code on the next line, between the double quotes.
     */

    private static final String VUFORIA_KEY = "AUA5lZz/////AAABmW17wZx5jEH3tPmrYICcogETmmmtyBthV9L8I3SmlGE6g8zSTaCeCJUQYQD/SjwLsCCTgka4So61A0XpURTnEqLXmjn2K7Vn9kRRu8rzJBXn9SED9QDVpreODLmw5v/ThX03iUmIz4PrcBEwtRK479pcZZp2QdKqRjImkcBjxjyNz8OxfIyMyiGZTwJKqxswwr9Kx3Wr4WTNqjgaYhCv6A22fFl4/lE1IOGWzLd5zK5F4knqwwp+SOfL3BCBthp9WoWF5RjWpr8q2Smno7EJsd8xwmjo4FKrUKuz8Iz8HsUFfI7VOFc+6sm/Tbrf1xsSLbZh1bA4p/9R75Wnub53HPzrLIzlVWjofcTxcGLSqy+t";

    /**
     * {@link #vuforia} is the variable we will use to store our instance of the Vuforia
     * localization engine.
     */
    private VuforiaLocalizer vuforia;

    /**
     * {@link #tfod} is the variable we will use to store our instance of the Tensor Flow Object
     * Detection engine.
     */
    private TFObjectDetector tfod;

    /**
     * Position of the gold mineral
     * 0 = right, 1 = center, 2 = left
     */

    private int mineralPos = 1;

    /**
     * Robot stuffs
     */
    private RevBlinkinLedDriver blinkin;
    private DcMotor leftDrive;
    private DcMotor rightDrive;
    private DcMotor middleDrive;
    private DcMotor lift;
    private DcMotor lift2;
    private CRServo claim;
    private HealthPoints hp;

    @Override
    public void runOpMode() {
        FtcDashboard dashboard = FtcDashboard.getInstance();

        blinkin = hardwareMap.get(RevBlinkinLedDriver.class, "blinkin");

        hp = new HealthPoints(blinkin, telemetry);

        hp.start();
        hp.load();

        leftDrive = hardwareMap.dcMotor.get("leftDrive");
        rightDrive = hardwareMap.dcMotor.get("rightDrive");
        middleDrive = hardwareMap.dcMotor.get("middleDrive");
        lift = hardwareMap.dcMotor.get("lift");
        lift2 = hardwareMap.dcMotor.get("lift2");

        claim = hardwareMap.crservo.get("claim");
        claim.setDirection(CRServo.Direction.REVERSE);

        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.SINELON_LAVA_PALETTE);

        // The TFObjectDetector uses the camera frames from the VuforiaLocalizer, so we create that
        // first.
        initVuforia();

        if (ClassFactory.getInstance().canCreateTFObjectDetector()) {
            initTfod();
        } else {
            telemetry.addData("Sorry!", "This device is not compatible with TFOD");
            telemetry.update();
            hp.error();
            sleep(500);
            stop();
        }

        /** Wait for the game to begin */
        telemetry.addData(">", "Press Play to start tracking");
        telemetry.update();

        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BREATH_BLUE);

        boolean loopDone = false;
        waitForStart();

        if (opModeIsActive()) {
            /** Activate Tensor Flow Object Detection. */
            if (tfod != null) {
                tfod.activate();
            }

            if (tfod != null) {

                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
                // DO NOT TOUCH THE CODE FROM HERE
                //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

                long startTime = System.currentTimeMillis();

                while (!loopDone && !isStopRequested()) {
                    try {
                        dashboard.sendImage(vuforia.convertFrameToBitmap(vuforia.getFrameQueue().take()));
                        telemetry.addData("Image", vuforia.convertFrameToBitmap(vuforia.getFrameQueue().take()));
                    } catch (Exception e) {

                    }
                    // getUpdatedRecognitions() will return null if no new information is available since
                    // the last time that call was made.
                    List<Recognition> updatedRecognitions = tfod.getUpdatedRecognitions();
                    if (updatedRecognitions != null) {
                        telemetry.addData("# Object Detected", updatedRecognitions.size());
                        if (updatedRecognitions.size() == 2) {
                            int goldMineralX = -1;
                            int silverMineral1X = -1;
                            boolean isGoldLeft = false;
                            for (Recognition recognition : updatedRecognitions) {
                                if (recognition.getLabel().equals(LABEL_GOLD_MINERAL)) {
                                    goldMineralX = (int) recognition.getLeft();
                                } else if (silverMineral1X == -1) {
                                    silverMineral1X = (int) recognition.getLeft();
                                } else {
                                    isGoldLeft = true;
                                }
                            }
                            if (isGoldLeft) {
                                // Gold is on the Left

                                mineralPos = 0;

                                loopDone = true;
                                telemetry.addData("Gold Mineral Position", "Left");
                                hp.goldLeft();
                            } else if (goldMineralX > silverMineral1X) {
                                // Gold is on the right

                                mineralPos = 2;

                                loopDone = true;
                                telemetry.addData("Gold Mineral Position", "Right");
                                hp.goldRight();
                            } else if (goldMineralX < silverMineral1X){
                                // Gold is in the center

                                mineralPos = 1;

                                loopDone = true;
                                telemetry.addData("Gold Mineral Position", "Center");
                                hp.goldMid();
                            } else {
                                loopDone = true;
                                //Somehow, we don't know where it is
                            }
                        }
                        telemetry.update();
                    }
                    if (System.currentTimeMillis() >= startTime + 20000){
                        loopDone = true;
                        blinkin.setPattern(RevBlinkinLedDriver.BlinkinPattern.BEATS_PER_MINUTE_LAVA_PALETTE);
                    }
                }
            }

            CameraDevice.getInstance().setFlashTorchMode(false);

            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!
            // TO HERE
            //!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!

            lift.setPower(-0.8);
            lift2.setPower(-0.8);
            sleep(Configuration.ARM_LOWER_TIME - 200);
            lift.setPower(0);
            lift2.setPower(0);

            sleep(400);

            leftDrive.setPower(-1);
            rightDrive.setPower(1);
            sleep(100);
            leftDrive.setPower(0);
            rightDrive.setPower(0);

            sleep(400);

            lift.setPower(-0.8);
            lift2.setPower(-0.8);
            sleep(100);
            lift.setPower(0);
            lift2.setPower(0);

            sleep(400);

            middleDrive.setPower(1);
            rightDrive.setPower(-0.8);
            sleep(Configuration.DEHOOK_LEFT_TIME);
            middleDrive.setPower(0);
            rightDrive.setPower(0);

            sleep(400);

            leftDrive.setPower(-1);
            rightDrive.setPower(1);
            sleep(Configuration.FORWARD_HOOK_TIME);
            leftDrive.setPower(0);
            rightDrive.setPower(0);

            if (mineralPos == 2) {
                /*
                sleep(400);

                leftDrive.setPower(-1);
                rightDrive.setPower(1);
                sleep(300);
                leftDrive.setPower(0);
                rightDrive.setPower(0);

                sleep(400);
*/
                middleDrive.setPower(1);
                rightDrive.setPower(-0.5);
                sleep(Configuration.RIGHT_ALIGNING_TIME + 100);
                middleDrive.setPower(0);
                rightDrive.setPower(0);

                sleep(400);

                leftDrive.setPower(-1);
                rightDrive.setPower(1);
                sleep(400);
                leftDrive.setPower(0);
                rightDrive.setPower(0);

                sleep(400);

                leftDrive.setPower(-1);
                rightDrive.setPower(-1);
                sleep(400);
                leftDrive.setPower(0);
                rightDrive.setPower(0);

                sleep(400);

                leftDrive.setPower(-1);
                rightDrive.setPower(1);
                sleep(Configuration.FORWARD_TO_HIT_TIME - 100);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
                /*
                sleep(400);

                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(200);
                leftDrive.setPower(0);
                rightDrive.setPower(0);


                sleep(400);

                arm1.setPower(0.6);
                arm2.setPower(-0.6);
                sleep(700);
                arm1.setPower(0.1);
                arm2.setPower(-0.1);
                sleep(800);
                arm1.setPower(0);
                arm2.setPower(0);
                */
            } else if (mineralPos == 1) {
                middleDrive.setPower(-1);
                rightDrive.setPower(0.5);
                sleep(500);
                middleDrive.setPower(0);
                rightDrive.setPower(0);

                sleep(400);

                leftDrive.setPower(-1);
                rightDrive.setPower(1);
                sleep(Configuration.FORWARD_TO_HIT_TIME);
                leftDrive.setPower(0);
                rightDrive.setPower(0);

                sleep(400);

                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(200);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
            } else if (mineralPos == 0) {
                middleDrive.setPower(-1);
                rightDrive.setPower(0.5);
                sleep(Configuration.LEFT_ALIGNING_TIME);
                middleDrive.setPower(0);
                rightDrive.setPower(0);

                /*
                leftDrive.setPower(-1);
                rightDrive.setPower(-1);
                sleep(400);
                leftDrive.setPower(0);
                rightDrive.setPower(0);
                */
                leftDrive.setPower(-1);
                rightDrive.setPower(1);
                sleep(Configuration.FORWARD_TO_HIT_TIME);
                leftDrive.setPower(0);
                rightDrive.setPower(0);

                sleep(400);

                leftDrive.setPower(1);
                rightDrive.setPower(1);
                sleep(700);
                leftDrive.setPower(0);
                rightDrive.setPower(0);

                /*
                sleep(400);

                middleDrive.setPower(1);
                sleep(100);
                middleDrive.setPower(0);

                sleep(400);


                arm1.setPower(0.6);
                arm2.setPower(-0.6);
                sleep(700);
                arm1.setPower(0.1);
                arm2.setPower(-0.1);
                sleep(800);
                arm1.setPower(0);
                arm2.setPower(0);
                */
            }
/*
            claim.setPower(1);
            sleep(1500);
            claim.setPower(-1);
            sleep(1500);
            claim.setPower(0);
  */
        }

        if (tfod != null) {
            tfod.shutdown();
        }
    }

    /**
     * Initialize the Vuforia localization engine.
     */
    private void initVuforia() {
        /*
         * Configure Vuforia by creating a Parameter object, and passing it to the Vuforia engine.
         */
        VuforiaLocalizer.Parameters parameters = new VuforiaLocalizer.Parameters();

        parameters.vuforiaLicenseKey = VUFORIA_KEY;
        parameters.cameraDirection = VuforiaLocalizer.CameraDirection.BACK;

        //  Instantiate the Vuforia engine
        vuforia = ClassFactory.getInstance().createVuforia(parameters);

        vuforia.enableConvertFrameToBitmap();

        CameraDevice.getInstance().setFlashTorchMode(true);
        // Loading trackables is not necessary for the Tensor Flow Object Detection engine.
    }

    /**
     * Initialize the Tensor Flow Object Detection engine.
     */
    private void initTfod() {
        int tfodMonitorViewId = hardwareMap.appContext.getResources().getIdentifier(
                "tfodMonitorViewId", "id", hardwareMap.appContext.getPackageName());
        TFObjectDetector.Parameters tfodParameters = new TFObjectDetector.Parameters(tfodMonitorViewId);
        tfod = ClassFactory.getInstance().createTFObjectDetector(tfodParameters, vuforia);
        tfod.loadModelFromAsset(TFOD_MODEL_ASSET, LABEL_GOLD_MINERAL, LABEL_SILVER_MINERAL);
    }
}
