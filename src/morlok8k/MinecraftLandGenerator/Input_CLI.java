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

/**
 * 
 * @author morlok8k
 */
public class Input_CLI {

	/**
	 * getInt(String msg) - outputs a message, will only accept a valid integer from keyboard
	 * 
	 * @param msg
	 *            String
	 * @return int
	 * @author Morlok8k
	 */
	public static int getInt(final String msg) {

		int Return = 0;

		while (!(var.sc.hasNextInt())) {
			var.sc.nextLine();
			Out.outP(var.MLG + "Invalid Input. " + msg);
		}

		Return = var.sc.nextInt();

		if (Return < 1000) {
			Out.out("Input must be 1000 or larger.");
			Out.outP(var.MLG + msg);
			Return = getInt(msg);
		}

		return Return;

	}
}
