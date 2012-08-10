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
				"Launch info: JVM: " + Return + " JAR: " + System.getProperty("sun.java.command")
						+ " ARGS: ";

		for (int i = 0; i < Main.originalArgs.length; i++) {
			Return = Return + (Main.originalArgs[i]) + " ";
		}

		Return = Return.trim();
		return Return;
	}

}
