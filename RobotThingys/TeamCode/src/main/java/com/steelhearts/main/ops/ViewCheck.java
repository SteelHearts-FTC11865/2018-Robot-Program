package com.steelhearts.main.ops;

import com.acmerobotics.dashboard.FtcDashboard;
import com.acmerobotics.dashboard.telemetry.TelemetryPacket;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

@TeleOp(name="View Checker", group="Linear Opmode")

public class ViewCheck extends LinearOpMode {

    @Override
    public void runOpMode() {

        FtcDashboard dashboard = FtcDashboard.getInstance();

        waitForStart();

        while (opModeIsActive()) {
            TelemetryPacket packet = new TelemetryPacket();
            packet.fieldOverlay()
                    .setStrokeWidth(4)
                    .setStroke("goldenrod")
                    .strokeCircle(0, 0, 10);
            dashboard.sendTelemetryPacket(packet);
        }
    }
}
