package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.LineBorder;

import model.EV3;
import model.Map;

@SuppressWarnings("serial")
public class SimulatorPanel extends JPanel{

	private static final int REDRAW_CYCLE = 10; //ms
	
	private EV3 ev3;
	
	private MainFrame parentFrame;
	
	private JPanel buttonPanel;
	private JButton startButton;
	private JButton resetButton;
	private JButton prevButton;
	private JButton nextButton;
	
	
	private SimulatingPanel simulatingPanel;
	private RedrawThread redrawThread;
	
	
	public SimulatorPanel(MainFrame parentFrame, EV3 ev3) {
		
		this.parentFrame = parentFrame;
		this.ev3 = ev3;
		
		setLayout(new BorderLayout());
		setBorder(new LineBorder(Color.BLACK));;

		buttonPanel =new JPanel();
		
		startButton = new JButton("start");
		startButton.addActionListener(new StartButtonActionListener());
		startButton.setBackground(Color.WHITE);
		
		resetButton = new JButton("reset");
		resetButton.addActionListener(new ResetButtonActionListener());
		resetButton.setBackground(Color.WHITE);
		
		prevButton = new JButton("<");
		prevButton.addActionListener(new PrevButtonActionListener());
		prevButton.setBackground(Color.WHITE);
		
		nextButton = new JButton(">");
		nextButton.addActionListener(new NextButtonActionListener());
		nextButton.setBackground(Color.WHITE);
		
		buttonPanel.add(startButton);
		buttonPanel.add(resetButton);
		buttonPanel.add(prevButton);
		buttonPanel.add(nextButton);
		
		simulatingPanel = new SimulatingPanel();

		add(buttonPanel,BorderLayout.NORTH);
		add(simulatingPanel, BorderLayout.CENTER);   
	}

	
	public void redrawSimulatingPanel(){
		
		simulatingPanel.removeAll();
		//simulatingPanel.revalidate();
		simulatingPanel.repaint();
	}
	
	private void startRedrawThread(){
		redrawThread = new RedrawThread();
		redrawThread.start();
	}
	private void stopRedrawThread(){
		redrawThread.interrupt();
		redrawThread = null;
	}
	
	
	class SimulatingPanel extends JPanel{
		
		private BufferedImage ev3Image;
		
		public SimulatingPanel() {
			try {
				ev3Image = ImageIO.read(new File(".\\img\\ev3.png"));
			} catch (IOException e1) {
				e1.printStackTrace();
			}
		}
		
		@Override
		protected void paintComponent(Graphics g) {
			super.paintComponent(g);
			
			g.drawImage(Map.getBufferedImage(), 0, 0, null);
			
			AffineTransform at = new AffineTransform();
			
			at.translate(ev3.getPos()[0], ev3.getPos()[1]);
			at.rotate(ev3.getDirection() + Math.PI/2);
			at.translate(-ev3Image.getWidth()/2, -ev3Image.getHeight()/2);
	        
			Graphics2D g2d = (Graphics2D) g;
	        g2d.drawImage(ev3Image, at, null);
		}
	}
	
	class StartButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			if(!ev3.getIsRunning()){
				ev3.startRunning(parentFrame.getCode());
				startRedrawThread();
			}
		}
		
	}
	class ResetButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			ev3.stopRunning();
			ev3.resetPosition();
			stopRedrawThread();
			redrawSimulatingPanel();
		}
	}
	class PrevButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			Map.changeToPrevMap();
			ev3.resetPosition();
			redrawSimulatingPanel();
		}
	}
	class NextButtonActionListener implements ActionListener{
		@Override
		public void actionPerformed(ActionEvent e) {
			Map.changeToNextMap();
			ev3.resetPosition();
			redrawSimulatingPanel();
		}
	}
	
	class RedrawThread extends Thread{
		
		public RedrawThread() {
			super();			
		}
		
		public void run(){
			try {
				while ( !isInterrupted()) {
					redrawSimulatingPanel();
					sleep(REDRAW_CYCLE);
				}
			} catch (InterruptedException e) {
			}
		}
	}
}


