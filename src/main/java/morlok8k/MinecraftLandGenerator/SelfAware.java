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

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

/**
 * 
 * @author morlok8k
 */
public class SelfAware {

	/**
	 * 
	 * @return
	 */
	public static String JVMinfo() {

		String Return = "";

		final RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		final List<String> aList = RuntimemxBean.getInputArguments();

		for (int i = 0; i < aList.size(); i++) {
			Return = Return + (aList.get(i)) + " ";
		}

		Return = Return.trim();

		Return =
				"Launch info: Java: " + System.getProperty("java.vm.name") + " "
						+ System.getProperty("java.version") + " OS: "
						+ System.getProperty("os.name") + " " + System.getProperty("os.version")
						+ " " + System.getProperty("os.arch") + " "
						+ System.getProperty("sun.desktop") + var.newLine + "# JVM: " + " JAR: "
						+ System.getProperty("sun.java.command") + " ARGS: ";

		for (int i = 0; i < var.originalArgs.length; i++) {
			Return = Return + (var.originalArgs[i]) + " ";
		}

		Return = Return.trim();
		return Return;
	}
}
