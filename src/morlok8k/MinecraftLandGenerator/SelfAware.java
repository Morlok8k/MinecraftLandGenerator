package morlok8k.MinecraftLandGenerator;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

public class SelfAware {

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
