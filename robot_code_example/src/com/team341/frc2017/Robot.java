package com.team341.frc2017;

import com.team341.frc2017.vision.ADB;
import com.team341.frc2017.vision.TestReceiver;
import com.team341.frc2017.vision.VisionServer;
import edu.wpi.first.wpilibj.IterativeRobot;


/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {

  @Override
  public void robotInit() {
    ADB adb = ADB.getInstance();
    adb.start();
    adb.reversePortFoward(8341, 8341);
    VisionServer server = new VisionServer(8341);
    server.registerReceiver(new TestReceiver());
  }


  @Override
  public void disabledInit() {

  }

  @Override
  public void disabledPeriodic() {

  }


  @Override
  public void autonomousInit() {

  }

  @Override
  public void autonomousPeriodic() {

  }

  @Override
  public void teleopInit() {

  }

  @Override
  public void teleopPeriodic() {

  }


  @Override
  public void testInit() {

  }

  @Override
  public void testPeriodic() {

  }
}

