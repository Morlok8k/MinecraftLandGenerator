package morlok8k.MinecraftLandGenerator;

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
			Main.outP(var.MLG + "Invalid Input. " + msg);
		}

		Return = var.sc.nextInt();

		if (Return < 1000) {
			Main.out("Input must be 1000 or larger.");
			Main.outP(var.MLG + msg);
			Return = getInt(msg);
		}

		return Return;

	}

}
