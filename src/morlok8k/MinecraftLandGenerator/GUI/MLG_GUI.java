package morlok8k.MinecraftLandGenerator.GUI;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;

public class MLG_GUI extends JFrame {

	private static final long serialVersionUID = -8791419906463664152L;		//auto generated
	private JPanel contentPane;

	/**
	 * Create the frame.
	 */
	public MLG_GUI() {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 450, 300);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		JLabel lblHelloWorld = new JLabel("Hello World");
		lblHelloWorld.setHorizontalAlignment(SwingConstants.CENTER);
		contentPane.add(lblHelloWorld, BorderLayout.CENTER);
	}

}
