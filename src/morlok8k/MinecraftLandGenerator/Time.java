package morlok8k.MinecraftLandGenerator;

public class Time {

	/**
	 * Returns the time in a readable format given a time in Millis.
	 * 
	 * @param timeMillis
	 * @author Morlok8k
	 * @return String of Readable Time
	 */
	public static String displayTime(final long timeMillis) {

		final long seconds = timeMillis / 1000;
		final long minutes = seconds / 60;
		final long hours = minutes / 60;
		final long days = hours / 24;
		final long years = days / 365;

		String took =
				(years > 0 ? String.format("%d " + ((years) == 1 ? "Year, " : "Years, "), years)
						: "")
						+ (days > 0 ? String.format("%d "
								+ ((days % 365) == 1 ? "Day, " : "Days, "), days % 365) : "")
						+ (hours > 0 ? String.format("%d "
								+ ((hours % 24) == 1 ? "Hour, " : "Hours, "), hours % 24) : "")
						+ (minutes > 0 ? String.format("%d "
								+ ((minutes % 60) == 1 ? "Minute, " : "Minutes, "), minutes % 60)
								: "")
						+ String.format("%d " + ((seconds % 60) == 1 ? "Second" : "Seconds"),
								seconds % 60);

		if (!(var.verbose)) {
			final int commaFirst = took.indexOf(",");
			final int commaSecond = took.substring((commaFirst + 1), took.length()).indexOf(",");
			int end = (commaFirst + 1 + commaSecond);

			if (commaSecond == -1) {
				end = commaFirst;
			}

			if (commaFirst == -1) {
				end = took.length();
			}

			took = took.substring(0, end);
		}

		took = took.trim();
		return (took);
	}

	/**
	 * Returns the time in a readable format between two points of time given in Millis.
	 * 
	 * @param startTimeMillis
	 * @param endTimeMillis
	 * @author Morlok8k
	 * @return String of Readable Time
	 */
	public static String displayTime(final long startTimeMillis, final long endTimeMillis) {

		final long millis = (endTimeMillis - startTimeMillis);
		//I just duplicated displayTime to have a start & end times, because it just made things simpler to code.
		return (Time.displayTime(millis));
	}

	/**
	 * waits ten seconds. outputs 10%, 20%, etc after each second.
	 * 
	 * @author Morlok8k
	 */
	public static void waitTenSec(final boolean output) {

		if (var.dontWait) { return; }			//Don't wait!

		if (output) {
			Main.outP(var.MLG);						//here we wait 10 sec.
		}

		int count = 0;
		while (count <= 100) {
			if (output) {
				Main.outP(count + "% ");
			}

			try {
				Thread.sleep(1000);
			} catch (final InterruptedException e) {
				e.printStackTrace();
			}
			count += 10;
		}
		if (output) {
			Main.outP(var.newLine);
		}
		return;

	}

}
