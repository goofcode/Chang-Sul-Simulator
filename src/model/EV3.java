package model;
import java.awt.Point;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

public class EV3 {

	public static final double WIDTH = 42;
	public static final double HEIGHT = 50;

	private static final int LEFT_COLOR_SENSOR = 0;
	private static final int RIGHT_COLOR_SENSOR = 1;
	private static final double DIST_TO_COLOR_SENSOR_X = 24;
    private static final double DIST_TO_COLOR_SENSOR_Y = 29;
    private static final double DIST_TO_COLOR_SENSOR = Math.sqrt(DIST_TO_COLOR_SENSOR_X * DIST_TO_COLOR_SENSOR_X + DIST_TO_COLOR_SENSOR_Y * DIST_TO_COLOR_SENSOR_Y);
    private static final double ANGLE_TO_COLOR_SENSOR = Math.atan(DIST_TO_COLOR_SENSOR_X / DIST_TO_COLOR_SENSOR_Y);
    private static final double DIST_TO_FRONT = 10;

    private static final int NORTH_DIST_SENSOR = 0;
	private static final int EAST_DIST_SENSOR = 1;
    private static final int SOUTH_DIST_SENSOR = 2;
	private static final int WEST_DIST_SENSOR = 3;

	private static final int UNIT_DIST = 1;
	private static final int UNIT_VECTOR = 1;

	private ScriptEngine engine;
	private RunCodeThread runCodeThread;
	
	private double[] pos;
	private double direction;
	
	private boolean isRunning;
	
	
	public EV3() {
		pos = new double[2];
		direction = 0;
		
		engine = (new ScriptEngineManager()).getEngineByName("nashorn");
	}
	public void resetPosition(){
		setPos(Map.getStartPoint());
		setDirection(Map.getStartDirection());
	}
	
	public double getDirection(){return direction;}
	public double[] getPos(){return pos;}
	public boolean getIsRunning(){return isRunning;}
	public double[] getFrontCenterPos(){
		double x = pos[0] + DIST_TO_FRONT * Math.cos(direction);
		double y = pos[1] + DIST_TO_FRONT * Math.sin(direction);
		return new double[]{x,y};
	}
    private Point getSensorLocation(boolean isLeft){
        double angle = direction + (isLeft?-1:1)* ANGLE_TO_COLOR_SENSOR;
        double x = pos[0] + DIST_TO_COLOR_SENSOR * Math.cos(angle);
        double y = pos[1] + DIST_TO_COLOR_SENSOR * Math.sin(angle);
        return new Point((int)x, (int)y);
    }

	public void setPosCorrespondingToFrontPos(double[] frontPos){
		pos[0] = frontPos[0] - DIST_TO_FRONT * Math.cos(direction);
		pos[1] = frontPos[1] - DIST_TO_FRONT * Math.sin(direction);
	}


	private double getBrightnessSensor(int brightnessSensor){
	    return Map.getBrightness(getSensorLocation(brightnessSensor==0?true:false));
    }
    private int getDistSensor(int distSensor) {
	    double sensorDirection = direction + (distSensor*Math.PI/2);
	    System.out.println(Map.getDist(new Point((int)pos[0],(int)pos[1]), sensorDirection));
        return Map.getDist(new Point((int)pos[0],(int)pos[1]), sensorDirection);
    }

	public void setDirection(double direction){this.direction = direction;}
	public void setPos(Point pos){ this.pos[0] = pos.x; this.pos[1] = pos.y;}


	/* run code thread */
    public void startRunning(String code){
        runCodeThread = new RunCodeThread(code);
        runCodeThread.start();
        isRunning = true;
    }
    @SuppressWarnings("deprecation")
    public void stopRunning(){
        if( runCodeThread != null)
            runCodeThread.stop();

        runCodeThread = null;
        isRunning = false;
    }

    class RunCodeThread extends Thread{

        private String code;

        public RunCodeThread(String code) {
            this.code = code;
        }

        public void run(){
            try {
                engine.put("ev3", EV3.this);
                engine.eval(code);
            }catch (ScriptException e) {
                JOptionPane.showMessageDialog(null, e.getMessage());
            }finally {
                isRunning = false;
                JOptionPane.showMessageDialog(null, "finished");
            }

        }
    }
    /* run code thread */


	/* ev3 control methods */
	public void rotateMotor(int motor1, int motor2){
		
		final double motorDivisor = Math.max(motor1, motor2) / UNIT_VECTOR;

		double u1 = motor1/motorDivisor;
		double u2 = motor2/motorDivisor;

		
		double deltaAlpha = (motor1>motor2?-1:1)*Math.atan(Math.abs(u1-u2)/WIDTH);
		//System.out.printf((int)pos[0] +", "+(int)pos[1] +": ");
		//System.out.println( getSensorLocation(true).x + ", " + getSensorLocation(true).y+ "   "+getSensorLocation(false).x+ ", " + getSensorLocation(false).y);
		
		//accumulate vector movement
		for( int i =0 ; i<motorDivisor; i++){
			
			double[] frontPos = getFrontCenterPos();
				
			direction -= deltaAlpha;
			
			frontPos[0] += UNIT_DIST * Math.cos(direction);
			frontPos[1] += UNIT_DIST * Math.sin(direction);
			
			setPosCorrespondingToFrontPos(frontPos);
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	public double getBrightSensor1(){return getBrightnessSensor(LEFT_COLOR_SENSOR);}
	public double getBrightSensor2(){return getBrightnessSensor(RIGHT_COLOR_SENSOR);}
	public double getDistSensorNorth(){
	    return getDistSensor(NORTH_DIST_SENSOR);
    }
    public double getDistSensorSouth(){
        return getDistSensor(SOUTH_DIST_SENSOR);
    }
	public double getDistSensorEast(){
        return getDistSensor(EAST_DIST_SENSOR);
    }
	public double getDistSensorWest(){
        return getDistSensor(WEST_DIST_SENSOR);
    }
	/* ev3 control methods */

}
