/*
#######################################################################
#            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE              #
#                    Version 2, December 2004                         #
#                                                                     #
# Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>                    #
#                                                                     #
# Everyone is permitted to copy and distribute verbatim or modified   #
# copies of this license document, and changing it is allowed as long #
# as the name is changed.                                             #
#                                                                     #
#            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE              #
#   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION   #
#                                                                     #
#  0. You just DO WHAT THE FUCK YOU WANT TO.                          #
#                                                                     #
#######################################################################
*/

package morlok8k.MinecraftLandGenerator.GUI;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.text.NumberFormat;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JButton;
import javax.swing.JCheckBoxMenuItem;
import javax.swing.JFormattedTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JRadioButton;
import javax.swing.JSeparator;
import javax.swing.KeyStroke;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import javax.swing.plaf.metal.MetalLookAndFeel;
import javax.swing.plaf.metal.OceanTheme;

import morlok8k.MinecraftLandGenerator.Out;
import morlok8k.MinecraftLandGenerator.Startup;
import morlok8k.MinecraftLandGenerator.Time;
import morlok8k.MinecraftLandGenerator.Update;
import morlok8k.MinecraftLandGenerator.var;

/**
 * 
 * @author morlok8k
 */
public class MLG_GUI {

	final Font arial = new Font("Arial", Font.PLAIN, 12);
	final Font arialBold = new Font("Arial", Font.BOLD, 12);

	public static JFrame frmMLG_GUI;

	static JButton btnStart;
	static JButton btnStop;

	static JFormattedTextField txtSizeX;
	static JFormattedTextField txtSizeZ;

	static JFormattedTextField txtCPX;
	static JFormattedTextField txtCPZ;

	static JProgressBar pgbTotPer;
	static JProgressBar pgbCurPer;

	static JLabel lblTotPer;
	static JLabel lblCurPer;

	static JRadioButton rdbtnSizeSquarify;
	static JRadioButton rdbtnSizeCustomSize;

	static JRadioButton rdbtnAlignRegions;
	static JRadioButton rdbtnAlignChunks;

	static JRadioButton rdbtnCenterSpawnPoint;
	static JRadioButton rdbtnCenterOther;

	static JLabel lblTimeRem;
	static JLabel lblCurLoc;
	static JLabel lblCurStatus;

	static JLabel lblSizeZ;
	static JLabel lblSizeX;

	static JLabel lblCPX;
	static JLabel lblCPZ;
	static JMenuItem mntmStart;
	static JMenuItem mntmStop;
	static JCheckBoxMenuItem chckbxmntmImportCustomList;

	/**
	 * Create the application.
	 */
	public MLG_GUI() {

		var.UsingGUI = true;

		// Program-wide UI stuff here...
		MetalLookAndFeel.setCurrentTheme(new OceanTheme());
		try {
			UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
		} catch (final Exception e) {
			e.printStackTrace();
		}

		UIManager.put("Button.font", arial);
		UIManager.put("ToggleButton.font", arial);
		UIManager.put("RadioButton.font", arial);
		UIManager.put("CheckBox.font", arial);
		UIManager.put("ColorChooser.font", arial);
		UIManager.put("ComboBox.font", arial);
		UIManager.put("Label.font", arial);
		UIManager.put("List.font", arial);
		UIManager.put("MenuBar.font", arial);
		UIManager.put("MenuItem.font", arial);
		UIManager.put("RadioButtonMenuItem.font", arial);
		UIManager.put("CheckBoxMenuItem.font", arial);
		UIManager.put("Menu.font", arial);
		UIManager.put("PopupMenu.font", arial);
		UIManager.put("OptionPane.font", arial);
		UIManager.put("Panel.font", arial);
		UIManager.put("ProgressBar.font", arial);
		UIManager.put("ScrollPane.font", arial);
		UIManager.put("Viewport.font", arial);
		UIManager.put("TabbedPane.font", arial);
		UIManager.put("Table.font", arial);
		UIManager.put("TableHeader.font", arial);
		UIManager.put("TextField.font", arial);
		UIManager.put("PasswordField.font", arial);
		UIManager.put("TextArea.font", arial);
		UIManager.put("TextPane.font", arial);
		UIManager.put("EditorPane.font", arial);
		UIManager.put("TitledBorder.font", arial);
		UIManager.put("ToolBar.font", arial);
		UIManager.put("ToolTip.font", arial);
		UIManager.put("Tree.font", arial);

		// End Look and Feel code

		initialize();

	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {

		// Basic Program Initialization
		Startup.initialStart();
		boolean quit = false;
		quit = Startup.confFile();
		if (quit) { return; }

		//WorldVerify.verifyWorld();			//TODO: need to do this at a later point

		// Frame:
		frmMLG_GUI = new JFrame();
		frmMLG_GUI.setTitle("Minecraft Land Generator - Loading...");
		frmMLG_GUI.setResizable(false);
		frmMLG_GUI.setBounds(100, 100, 475, 400);
		frmMLG_GUI.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmMLG_GUI.setVisible(true);
		frmMLG_GUI.getContentPane().setLayout(new BorderLayout(0, 0));

		// Menu Bar:
		final JMenuBar menuBar = new JMenuBar();
		frmMLG_GUI.setJMenuBar(menuBar);

		// Top Level Menus:
		final JMenu mnFile = new JMenu("File");
		final JMenu mnInfo = new JMenu("Info");
		final JMenu mnHelp = new JMenu("Help");

		// File Menu Objects:
		mntmStart = new JMenuItem("Start");
		mntmStart.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {

				Start.start_GUI();
			}
		});

		mntmStop = new JMenuItem("Stop");
		mntmStop.setEnabled(false);
		mntmStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				stop_GUI();
			}
		});

		final JSeparator hzlFile1 = new JSeparator();

		chckbxmntmImportCustomList = new JCheckBoxMenuItem("Import Custom List");
		chckbxmntmImportCustomList.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				ImportCustomList();

			}
		});
		chckbxmntmImportCustomList.setEnabled(false);

		final JSeparator hzlFile2 = new JSeparator();

		final JMenuItem mntmExit = new JMenuItem("Exit");
		mntmExit.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent arg0) {

				Exit();

			}
		});
		mntmExit.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_F4, InputEvent.ALT_MASK));

		// Info Menu Objects:
		final JMenuItem mntmMapInfo = new JMenuItem("Map Info");
		mntmMapInfo.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				MapInfo();
			}
		});

		// Help Menu Objects:
		final JMenuItem mntmAboutMlg = new JMenuItem("About MLG");
		mntmAboutMlg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				AboutMLG();
			}
		});

		final JSeparator hzlHelp1 = new JSeparator();

		final JMenuItem mntmUpdateMlg = new JMenuItem("Update MLG");
		mntmUpdateMlg.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				UpdateMLG();
			}
		});

		// Create Menu Bar:
		menuBar.add(mnFile);
		menuBar.add(mnInfo);
		menuBar.add(mnHelp);

		// File:
		mnFile.add(mntmStart);
		mnFile.add(mntmStop);
		mnFile.add(hzlFile1);
		mnFile.add(chckbxmntmImportCustomList);
		mnFile.add(hzlFile2);
		mnFile.add(mntmExit);

		// Info:
		mnInfo.add(mntmMapInfo);

		// Help:
		mnHelp.add(mntmAboutMlg);
		mnHelp.add(hzlHelp1);
		mnHelp.add(mntmUpdateMlg);

		// NumberFormats
		final NumberFormat nfSizeInt = NumberFormat.getIntegerInstance();
		nfSizeInt.setParseIntegerOnly(true);
		final NumberFormat nfCPInt = NumberFormat.getIntegerInstance();
		nfCPInt.setParseIntegerOnly(true);

		// Create Main Panels, Interior Panels, and Objects...
		// TODO: Refactor this clusterf*ck!
		final JPanel mainWest = new JPanel(new BorderLayout());
		final JPanel mainEast = new JPanel(new BorderLayout());
		final JPanel mainSouth = new JPanel(new BorderLayout());
		final JPanel mainNorth = new JPanel(new BorderLayout());

		mainSouth.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Progress:"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		// Create Interior Panels
		final JPanel pnlStartStop = new JPanel(new BorderLayout());
		final JPanel pnlStatus = new JPanel();
		pnlStatus.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Status:"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));

		btnStart = new JButton("Start");
		btnStart.addActionListener(new ActionListener() {

			//@SuppressWarnings("unused")
			@Override
			public void actionPerformed(final ActionEvent e) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						SwingUtilities.invokeLater(new Runnable() {
							@Override
							public void run() {
								new Start().run();
							}
						});
					}
				}).start(); // start the thread
			}
		});
		btnStop = new JButton("Stop");
		btnStop.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				stop_GUI();

			}
		});
		btnStop.setEnabled(false);

		final JLabel lblMinecraftLandGenerator = new JLabel("Minecraft Land Generator");
		lblMinecraftLandGenerator.setFont(new Font("Arial", Font.BOLD, 14));
		lblMinecraftLandGenerator.setHorizontalAlignment(SwingConstants.CENTER);

		// Add Objects to interior panels

		mainNorth.add(lblMinecraftLandGenerator, BorderLayout.CENTER);

		pnlStartStop.add(btnStart, BorderLayout.LINE_START);
		pnlStartStop.add(btnStop, BorderLayout.LINE_END);

		// Add interior panels to Main panels

		mainWest.add(pnlStartStop, BorderLayout.NORTH);

		final Component horizontalStrutStartStop = Box.createHorizontalStrut(25);
		pnlStartStop.add(horizontalStrutStartStop, BorderLayout.CENTER);
		mainWest.add(pnlStatus, BorderLayout.CENTER);
		pnlStatus.setLayout(null);

		final JLabel lblCurrentStatus = new JLabel("Current Status:");
		lblCurrentStatus.setBounds(12, 12, 118, 15);
		pnlStatus.add(lblCurrentStatus);
		lblCurrentStatus.setFont(arialBold);

		lblCurStatus = new JLabel("Idle");
		lblCurStatus.setBounds(12, 24, 118, 15);
		pnlStatus.add(lblCurStatus);
		lblCurStatus.setHorizontalAlignment(SwingConstants.RIGHT);

		final JLabel lblCurrentLocation = new JLabel("Current Location:");
		lblCurrentLocation.setBounds(12, 48, 118, 15);
		pnlStatus.add(lblCurrentLocation);
		lblCurrentLocation.setFont(arialBold);

		lblCurLoc = new JLabel("[0,0,0]");
		lblCurLoc.setBounds(12, 60, 118, 15);
		pnlStatus.add(lblCurLoc);
		lblCurLoc.setHorizontalAlignment(SwingConstants.RIGHT);

		final JLabel lblTimeRemaining = new JLabel("Time Remaining:");
		lblTimeRemaining.setBounds(12, 84, 118, 15);
		pnlStatus.add(lblTimeRemaining);
		lblTimeRemaining.setFont(arialBold);

		lblTimeRem = new JLabel("0 Seconds");
		lblTimeRem.setBounds(12, 96, 118, 15);
		pnlStatus.add(lblTimeRem);
		lblTimeRem.setHorizontalAlignment(SwingConstants.RIGHT);

		// add Main panels to Top-Level Panel

		frmMLG_GUI.getContentPane().add(mainNorth, BorderLayout.NORTH);
		frmMLG_GUI.getContentPane().add(mainWest, BorderLayout.WEST);
		frmMLG_GUI.getContentPane().add(mainEast, BorderLayout.EAST);

		final JPanel pnlOptions = new JPanel();
		mainEast.add(pnlOptions, BorderLayout.CENTER);
		pnlOptions.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Options:"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pnlOptions.setLayout(new BorderLayout(0, 0));

		final JPanel pnlSize = new JPanel();
		pnlSize.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Size:"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pnlOptions.add(pnlSize, BorderLayout.NORTH);
		pnlSize.setLayout(new BorderLayout(0, 0));

		final JPanel pnlSizeX = new JPanel();
		pnlSize.add(pnlSizeX, BorderLayout.WEST);
		pnlSizeX.setLayout(new BorderLayout(0, 0));

		lblSizeX = new JLabel("X: ");
		pnlSizeX.add(lblSizeX, BorderLayout.WEST);

		txtSizeX = new JFormattedTextField(nfSizeInt);
		txtSizeX.setFont(arial);
		txtSizeX.setText("1000");
		pnlSizeX.add(txtSizeX, BorderLayout.EAST);
		txtSizeX.setColumns(7);

		final JPanel pnlSizeZ = new JPanel();
		pnlSize.add(pnlSizeZ, BorderLayout.EAST);
		pnlSizeZ.setLayout(new BorderLayout(0, 0));

		lblSizeZ = new JLabel(" Z: ");
		pnlSizeZ.add(lblSizeZ, BorderLayout.WEST);

		txtSizeZ = new JFormattedTextField(nfSizeInt);
		txtSizeZ.setFont(arial);
		txtSizeZ.setText("1000");
		txtSizeZ.setColumns(7);
		pnlSizeZ.add(txtSizeZ, BorderLayout.EAST);

		final JPanel pnlSizeXZ = new JPanel();
		pnlSize.add(pnlSizeXZ, BorderLayout.NORTH);
		pnlSizeXZ.setLayout(new BorderLayout(0, 0));

		rdbtnSizeCustomSize = new JRadioButton("Custom Size:");
		rdbtnSizeCustomSize.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				SizeSetEnable(true);
			}
		});

		rdbtnSizeCustomSize.setSelected(true);
		pnlSizeXZ.add(rdbtnSizeCustomSize, BorderLayout.CENTER);

		final JPanel pnlSizeSquarify = new JPanel();
		pnlSize.add(pnlSizeSquarify, BorderLayout.SOUTH);
		pnlSizeSquarify.setLayout(new BorderLayout(0, 0));

		rdbtnSizeSquarify = new JRadioButton("Squarify Existing Land");
		rdbtnSizeSquarify.setToolTipText("Not Functional Yet...");
		rdbtnSizeSquarify.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				SizeSetEnable(false);
				CenterPointSetEnable(false);

			}
		});
		pnlSizeSquarify.add(rdbtnSizeSquarify, BorderLayout.CENTER);

		final JPanel pnlCenterPoint = new JPanel();
		pnlCenterPoint.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Center Point:"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pnlOptions.add(pnlCenterPoint, BorderLayout.CENTER);
		pnlCenterPoint.setLayout(new BorderLayout(0, 0));

		final JPanel pnlCPrb = new JPanel();
		pnlCenterPoint.add(pnlCPrb, BorderLayout.NORTH);
		pnlCPrb.setLayout(new BorderLayout(0, 0));

		rdbtnCenterSpawnPoint = new JRadioButton("Spawn Point");
		rdbtnCenterSpawnPoint.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				CenterPointSetEnable(false);

			}
		});
		rdbtnCenterSpawnPoint.setSelected(true);
		pnlCPrb.add(rdbtnCenterSpawnPoint, BorderLayout.WEST);

		rdbtnCenterOther = new JRadioButton("Other:");
		rdbtnCenterOther.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(final ActionEvent e) {

				CenterPointSetEnable(true);

			}
		});
		pnlCPrb.add(rdbtnCenterOther, BorderLayout.EAST);

		final ButtonGroup bgCenterPoint = new ButtonGroup();
		bgCenterPoint.add(rdbtnCenterSpawnPoint);
		bgCenterPoint.add(rdbtnCenterOther);

		final JPanel pnlCPSelection = new JPanel();
		pnlCenterPoint.add(pnlCPSelection, BorderLayout.SOUTH);
		pnlCPSelection.setLayout(new BorderLayout(0, 0));

		final JPanel pnlCPx = new JPanel();
		pnlCPSelection.add(pnlCPx, BorderLayout.WEST);
		pnlCPx.setLayout(new BorderLayout(0, 0));

		lblCPX = new JLabel("X: ");
		lblCPX.setEnabled(false);
		pnlCPx.add(lblCPX, BorderLayout.WEST);

		txtCPX = new JFormattedTextField(nfCPInt);
		txtCPX.setFont(arial);
		txtCPX.setEnabled(false);
		txtCPX.setText("0");
		pnlCPx.add(txtCPX, BorderLayout.EAST);
		txtCPX.setColumns(7);

		final JPanel pnlCPz = new JPanel();
		pnlCPSelection.add(pnlCPz, BorderLayout.EAST);
		pnlCPz.setLayout(new BorderLayout(0, 0));

		lblCPZ = new JLabel("Z: ");
		lblCPZ.setEnabled(false);
		pnlCPz.add(lblCPZ, BorderLayout.WEST);

		txtCPZ = new JFormattedTextField(nfCPInt);
		txtCPZ.setEnabled(false);
		txtCPZ.setFont(arial);
		txtCPZ.setText("0");
		pnlCPz.add(txtCPZ, BorderLayout.EAST);
		txtCPZ.setColumns(7);

		final JPanel pnlAlignment = new JPanel();
		pnlAlignment.setBorder(BorderFactory.createCompoundBorder(
				BorderFactory.createTitledBorder("Alignment:"),
				BorderFactory.createEmptyBorder(5, 5, 5, 5)));
		pnlOptions.add(pnlAlignment, BorderLayout.SOUTH);
		pnlAlignment.setLayout(new BorderLayout(0, 0));

		rdbtnAlignChunks = new JRadioButton("Chunks");
		rdbtnAlignChunks.setSelected(true);
		pnlAlignment.add(rdbtnAlignChunks, BorderLayout.WEST);

		rdbtnAlignRegions = new JRadioButton("Regions");
		pnlAlignment.add(rdbtnAlignRegions, BorderLayout.EAST);

		final ButtonGroup bgAlignment = new ButtonGroup();

		bgAlignment.add(rdbtnAlignChunks);
		bgAlignment.add(rdbtnAlignRegions);

		final ButtonGroup bgSize = new ButtonGroup();
		bgSize.add(rdbtnSizeCustomSize);
		bgSize.add(rdbtnSizeSquarify);

		frmMLG_GUI.getContentPane().add(mainSouth, BorderLayout.SOUTH);

		final JPanel pnlCurPrg = new JPanel();
		mainSouth.add(pnlCurPrg, BorderLayout.NORTH);
		pnlCurPrg.setLayout(new BorderLayout(0, 0));

		final JLabel lblCurrentProgress = new JLabel("Current Progress: ");
		pnlCurPrg.add(lblCurrentProgress, BorderLayout.WEST);

		lblCurPer = new JLabel("100%");
		pnlCurPrg.add(lblCurPer, BorderLayout.EAST);

		pgbCurPer = new JProgressBar();
		pnlCurPrg.add(pgbCurPer, BorderLayout.CENTER);

		final JPanel pnlTotPrg = new JPanel();
		mainSouth.add(pnlTotPrg, BorderLayout.SOUTH);
		pnlTotPrg.setLayout(new BorderLayout(0, 0));

		final JLabel lblTotalProgress = new JLabel("Total Progress: ");
		pnlTotPrg.add(lblTotalProgress, BorderLayout.WEST);

		lblTotPer = new JLabel("100%");
		pnlTotPrg.add(lblTotPer, BorderLayout.EAST);

		pgbTotPer = new JProgressBar();
		pnlTotPrg.add(pgbTotPer, BorderLayout.CENTER);

		// Frame size and location

		frmMLG_GUI.validate();
		frmMLG_GUI.pack();
		frmMLG_GUI.setLocationRelativeTo(null);
		frmMLG_GUI.setTitle("Minecraft Land Generator");
		// Finished creation of frame
	}

	static void MapInfo() {

		// TODO: Display Map Info
		JOptionPane.showMessageDialog(frmMLG_GUI, "Seed:" + var.newLine + "SpawnPoint:");

	}

	static void AboutMLG() {

		final String n = var.newLine;
		final String N = n + n;
		final String message =
				"This program uses the Minecraft Server to expand your Minecraft world." + N
						+ var.WEBSITE + N + "Authors: " + var.AUTHORS + n
						+ "Special Thanks to: Graham Edgecombe (aka ancient) for JNBT" + N
						+ "BuildID: (" + var.MLG_Last_Modified_Date.getTime() + ")" + n
						+ "This version was last modified on "
						+ var.dateFormat.format(var.MLG_Last_Modified_Date);
		final String title = var.PROG_NAME + " v" + var.VERSION;

		//JOptionPane.showMessageDialog(frmMLG_GUI, message, title, JOptionPane.INFORMATION_MESSAGE);
		Out.msg(message, title, JOptionPane.INFORMATION_MESSAGE);

	}

	static void UpdateMLG() {

		final boolean update = Update.updateMLG();

		if (update) {
			//TODO: add a popup saying we got a new version
			Exit();
		} else {
			//TODO: popup: no new version / error
		}

	}

	void ImportCustomList() {

		// TODO: add Import Custom List

		// TODO: disable/enable size objects

	}

	static void Exit() {

		// TODO: Make sure everything has finished...

		// Lets Exit!
		System.exit(0);

	}

	static void SizeSetEnable(final boolean enabled) {

		txtSizeX.setEnabled(enabled);
		txtSizeZ.setEnabled(enabled);
		lblSizeX.setEnabled(enabled);
		lblSizeZ.setEnabled(enabled);

	}

	static void CenterPointSetEnable(final boolean enabled) {

		txtCPX.setEnabled(enabled);
		txtCPZ.setEnabled(enabled);
		lblCPX.setEnabled(enabled);
		lblCPZ.setEnabled(enabled);

	}

	static void stop_GUI() {

		btnStop.setEnabled(false);

		var.stoppingServerGUI = true;

		Time.waitTenSec(true);

		while (var.runningServerGUI) {
			//;
		}

		// TODO: add additional stop code

		if (rdbtnSizeCustomSize.isSelected()) {
			SizeSetEnable(true);
		} else {
			SizeSetEnable(false);
		}

		if (rdbtnCenterSpawnPoint.isSelected()) {
			CenterPointSetEnable(false);
		} else {
			CenterPointSetEnable(true);
		}

		//rdbtnSizeSquarify.setEnabled(true);
		rdbtnSizeCustomSize.setEnabled(true);

		rdbtnAlignRegions.setEnabled(true);
		rdbtnAlignChunks.setEnabled(true);

		rdbtnCenterSpawnPoint.setEnabled(true);
		rdbtnCenterOther.setEnabled(true);

		mntmStop.setEnabled(false);
		mntmStart.setEnabled(true);

		btnStart.setEnabled(true);
		btnStop.setEnabled(false);

		pgbCurPer.setIndeterminate(false);
		pgbTotPer.setIndeterminate(false);

	}

}
