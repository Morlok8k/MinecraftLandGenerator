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

package morlok8k.MinecraftLandGenerator;

import javax.swing.JOptionPane;

import morlok8k.MinecraftLandGenerator.GUI.MLG_GUI;

/**
 * 
 * @author morlok8k
 */
public class Out {

	/**
	 * Outputs a formatted string to System.err as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void err(final String str) {
		System.err.println(var.MLGe + str);
	}

	/**
	 * Outputs a formatted string to System.out as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void out(final String str) {
		System.out.println(var.MLG + str);		// is there a better/easier way to do this?  I just wanted a lazier way to write "System.out.println(MLG + blah..."
	}

	/**
	 * Outputs a formatted string to System.out as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void outD(final String str) {
		System.out.println(var.MLG + "[DEBUG] " + str);
	}

	/**
	 * Outputs a string to System.out without a newline.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	public static void outP(final String str) {
		System.out.print(str);
	}

	/**
	 * Outputs a formatted string to System.out as a line.
	 * 
	 * @param str
	 *            String to display and format
	 * @author Morlok8k
	 */
	static void outS(final String str) {
		System.out.println("[Server] " + str);
	}

	/**
	 * Makes a dialog box, and outputs a formatted string to System.out as a line.
	 * 
	 * @param msg
	 *            Message
	 * @param title
	 *            title
	 * @param messageType
	 *            JOptionPane messageType
	 * 
	 * @author Morlok8k
	 */
	public static void msg(final String msg, final String title, final int messageType) {
		String msgType = "";

		switch (messageType) {
			case JOptionPane.ERROR_MESSAGE:
				msgType = "Error Message";
				break;
			case JOptionPane.INFORMATION_MESSAGE:
				msgType = "Information Message";
				break;
			case JOptionPane.WARNING_MESSAGE:
				msgType = "Warning Message";
				break;
			case JOptionPane.QUESTION_MESSAGE:
				msgType = "Question Message";
				break;
			case JOptionPane.PLAIN_MESSAGE:
				msgType = "Message";
				break;
			default:
				msgType = "Message";
				break;
		}

		System.out.println("[" + msgType + "] Title: " + title + var.newLine + msg);
		JOptionPane.showMessageDialog(MLG_GUI.frmMLG_GUI, msg, title, messageType);

	}
}
