package frame;

import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.JFrame;

import core.EV3;

@SuppressWarnings("serial")
public class MainFrame extends JFrame{
	
	private static final int WIDTH = 1475;
	private static final int HEIGHT = 600;
	
	private SimulatorPanel simulatorPanel;
	private CodePanel codePanel;
	
	public MainFrame(String title, EV3 ev3) {
		super(title);
		
		codePanel = new CodePanel();
		simulatorPanel = new SimulatorPanel(this, ev3);
		
		setLayout(new GridBagLayout());  
		GridBagConstraints c = new GridBagConstraints();
		
		c.gridx = c.gridy = 0;
		c.gridwidth = 3;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 3;
		c.weighty = 1;
		add(simulatorPanel,c);
		
		c.gridx = 3;
		c.gridy = 0;
		c.fill = GridBagConstraints.BOTH;
		c.weightx = 1;
		c.weighty = 1;
		add(codePanel,c);
		
		ev3.resetPosition();
		simulatorPanel.redrawSimulatingPanel();
		
		setSize(WIDTH, HEIGHT);
		setLocation(0, 0);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		setVisible(true);
	}

	public String getCode(){return codePanel.getCode();}
	public void resetSimulator(){simulatorPanel.redrawSimulatingPanel();}
}
