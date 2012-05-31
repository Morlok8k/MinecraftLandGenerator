package morlok8k.minecraft.landgenerator;

import corrodias.minecraft.landgenerator.Main;

public class MLG_input_CLI {

	/**
	 * getInt(String msg) - outputs a message, will only accept a valid integer from keyboard
	 * 
	 * @param msg
	 *            String
	 * @return int
	 * @author Morlok8k
	 */
	public static int getInt(String msg) {

		while (!(Main.sc.hasNextInt())) {
			Main.sc.nextLine();
			Main.outP(Main.MLG + "Invalid Input. " + msg);
		}
		return Main.sc.nextInt();

	}

}
