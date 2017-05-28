package view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.border.LineBorder;

@SuppressWarnings("serial")
public class CodePanel extends JPanel{


    private String template =
            "/*\n * Chang-Sul Simulator\n *\n" +
            " * ev3.rotateMotor(10,10);\n\n" +
            " * ev3.getBrightSensor1();\n" +
            " * ev3.getBrightSensor2();\n\n" +
            " * ev3.getDistSensorNorth();\n" +
            " * ev3.getDistSensorSouth();\n" +
            " * ev3.getDistSensorEast();\n" +
            " * ev3.getDistSensorWest();\n */\n\n" +
            "//line tracing\n" +
            "while(true) {\n" +
            "  if (ev3.getBrightSensor1() > 0.8 && ev3.getBrightSensor2() > 0.8)\n" +
            "    ev3.rotateMotor(1, 1);\n" +
            "  else if (ev3.getBrightSensor1() <= 0.8 && ev3.getBrightSensor2() > 0.8)\n" +
            "    ev3.rotateMotor(-20, 10);\n" +
            "  else if (ev3.getBrightSensor1() > 0.8 && ev3.getBrightSensor2() <= 0.8)\n" +
            "    ev3.rotateMotor(10, -20);\n" +
            "  else\n" +
            "    break;\n" +
            "}\n";

    private JPanel buttonPanel;
    private JButton resetButton;

    private JScrollPane codePane;
    private JTextArea code;

    public CodePanel() {
        setLayout(new BorderLayout());
        setBorder(new LineBorder(Color.BLACK));;

        buttonPanel = new JPanel();
        resetButton = new JButton("Get source template!");
        resetButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                code.setText(template);
            }
        });
        resetButton.setBackground(Color.CYAN);
        buttonPanel.add(resetButton);

        code = new JTextArea();

        code.setFont(new Font("times",Font.PLAIN, 20));

        codePane = new JScrollPane(code);

        add(buttonPanel, BorderLayout.NORTH);
        add(codePane,BorderLayout.CENTER);

    }

    public String getCode(){ return code.getText();}
}
