/*
 * ####################################################################### # DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE # # Version 2, December 2004 # # # # Copyright (C) 2004 Sam Hocevar
 * <sam@hocevar.net> # # # # Everyone is permitted to copy and distribute verbatim or modified # # copies of this license document, and changing it is allowed as long # # as the name is changed. # # #
 * # DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE # # TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION # # # # 0. You just DO WHAT THE FUCK YOU WANT TO. # # #
 * #######################################################################
 */

package morlok8k.MinecraftLandGenerator;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * 
 * @author morlok8k
 */
public class Server {

	private static Log log = LogFactory.getLog(Main.class);

	/**
	 * Starts the process in the given ProcessBuilder, monitors its output for a "Done" message, and sends it a "stop" message.
	 * If "verbose" is true, the process's output will also be printed to the console.
	 * 
	 * @throws IOException
	 * @throws InterruptedException
	 * @author Corrodias, Morlok8k, piegames
	 */
	protected static void runMinecraft() throws IOException, InterruptedException {
		log.info("Starting server.");
		final Process process = var.minecraft.start();

		final BufferedReader pOut =
				new BufferedReader(new InputStreamReader(process.getInputStream()));
		for (String line = pOut.readLine(); line != null; line = pOut.readLine()) {
			line = line.trim();
			if (log.isDebugEnabled()) log.debug(line);

			if (line.contains("Done")) {
				PrintStream out = new PrintStream(process.getOutputStream());

				log.info("Stopping server... ");
				out.println("save-all");
				out.flush();
				out.println("stop");
				out.flush();
			}
		}

		/* The process should have stopped by now. */
		int exit = process.waitFor();
		if (exit != 0)
			log.warn("Process stopped with non-zero exit code (" + exit + ")");
		else log.info("Stopped server");
	}
}
