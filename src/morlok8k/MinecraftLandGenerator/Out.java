package morlok8k.MinecraftLandGenerator;

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

}
