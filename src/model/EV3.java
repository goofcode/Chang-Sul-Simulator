package model;
import java.awt.Point;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.swing.JOptionPane;

public class EV3 {

	public static final double WIDTH = 42;
	public static final double HEIGHT = 50;
	
	public static final double DIST_SENSOR_X = 24;
	public static final double DIST_SENSOR_Y = 29;
	public static final double DIST_SENSOR = Math.sqrt(DIST_SENSOR_X*DIST_SENSOR_X + DIST_SENSOR_Y*DIST_SENSOR_Y);
	public static final double ANGLE_SENSOR = Math.atan(DIST_SENSOR_X/DIST_SENSOR_Y);

	public static final double DIST_FRONT = 10;
	
	
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
		double x = pos[0] + DIST_FRONT * Math.cos(direction);
		double y = pos[1] + DIST_FRONT * Math.sin(direction);
		return new double[]{x,y};
	}
	public void setPosCorresponingToFrontPos(double[] frontPos){
		pos[0] = frontPos[0] - DIST_FRONT * Math.cos(direction);
		pos[1] = frontPos[1] - DIST_FRONT * Math.sin(direction);
	}

	private Point getSensorLocation(boolean isLeft){
		double angle = direction + (isLeft?-1:1)*ANGLE_SENSOR; 
		double x = pos[0] + DIST_SENSOR * Math.cos(angle);
		double y = pos[1] + DIST_SENSOR * Math.sin(angle);
		return new Point((int)x, (int)y);
	}
	
	public void setDirection(double direction){this.direction = direction;}
	public void setPos(Point pos){ this.pos[0] = pos.x; this.pos[1] = pos.y;}
	
	
	public void rotateMotor(int motor1, int motor2){
		
		final double unitDist = 1;
		final double unitVector = 1;
		final double motorDivisor = Math.max(motor1, motor2) / unitVector;

		double u1 = motor1/motorDivisor;
		double u2 = motor2/motorDivisor;

		
		double deltaAlpha = (motor1>motor2?-1:1)*Math.atan(Math.abs(u1-u2)/WIDTH);
		;//System.out.printf((int)pos[0] +", "+(int)pos[1] +": ");
		//System.out.println( getSensorLocation(true).x + ", " + getSensorLocation(true).y+ "   "+getSensorLocation(false).x+ ", " + getSensorLocation(false).y);
		
		//accumulate vector movement
		for( int i =0 ; i<motorDivisor; i++){
			
			double[] frontPos = getFrontCenterPos();
				
			direction -= deltaAlpha;
			
			frontPos[0] += unitDist * Math.cos(direction);
			frontPos[1] += unitDist * Math.sin(direction);
			
			setPosCorresponingToFrontPos(frontPos);
			
			try {
				Thread.sleep(10);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	public double getBrightSensor1(){
		try{
			return Map.getBrightness(getSensorLocation(true));
		}catch(ArrayIndexOutOfBoundsException e){
			return 0;
		}
		
	}
	public double getBrightSensor2(){
		try{
			return Map.getBrightness(getSensorLocation(false));
		}catch(ArrayIndexOutOfBoundsException e){
			return 0;
		}
	}
	
	
	public void startRunning(String code){
		runCodeThread = new RunCodeThread(code);
		runCodeThread.start();	
		isRunning = true;
	}
	@SuppressWarnings("deprecation")
	public void stopRunning(){
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
}
