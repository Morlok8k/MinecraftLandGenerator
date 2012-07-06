package morlok8k.minecraft.landgenerator;

import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.List;

import corrodias.minecraft.landgenerator.Main;

public class MLG_SelfAware {

	public static String JVMinfo() {

		String Return = "";

		RuntimeMXBean RuntimemxBean = ManagementFactory.getRuntimeMXBean();
		List<String> aList = RuntimemxBean.getInputArguments();

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
