package morlok8k.minecraft.landgenerator;

import corrodias.minecraft.landgenerator.Main;

public class MLG_RCON {

	/**
	 * connects to server using RCON, sends a message, and disconnects. Not Functional yet.
	 * 
	 * @param message
	 */
	@SuppressWarnings("unused")
	private static void rconConnectAndSendMsg(String message) {
		//This is a placeholder for future code.
		/*
		step 1: connect to rcon_IPaddress : rcon_Port, with rcon_Password.
		step 2: send message (probably "stop" or "save-all")
		step 3: disconnect.
		*/
		Main.out("Connect to Server: " + Main.rcon_IPaddress + ":" + Main.rcon_Port + " Password: "
				+ Main.rcon_Password);

		return;
	}

	/**
	 * Queries the server using RCON to see if we can connect. Not functional yet.
	 * 
	 * @return QuerySucess
	 */
	@SuppressWarnings("unused")
	private static boolean rconQueryServer() {
		boolean QuerySucess = false;

		//This is a placeholder for future code.
		/*
		step 1: query rcon_IPaddress : rcon_Port
		step 2: return true or false if successful or not.
		*/
		Main.out("Query Server: " + Main.rcon_IPaddress + ":" + Main.rcon_Port);

		return QuerySucess;
	}
}
