package morlok8k.MinecraftLandGenerator;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Locale;

public class MD5 {

	/**
	 * This gets the MD5 of a file <br>
	 * <br>
	 * Thanks to R.J. Lorimer at <br>
	 * <a href="http://www.javalobby.org/java/forums/t84420.html">http://www. javalobby.org/java/forums/t84420.html</a>
	 * 
	 * @author Morlok8k
	 */
	public static String fileMD5(final String fileName) throws NoSuchAlgorithmException,
			FileNotFoundException {
		// out("");
		// out("");
		final MessageDigest digest = MessageDigest.getInstance("MD5");
		final InputStream is = new FileInputStream(fileName);
		final byte[] buffer = new byte[8192];
		int read = 0;
		try {
			while ((read = is.read(buffer)) > 0) {
				digest.update(buffer, 0, read);
			}
			final byte[] md5sum = digest.digest();
			final BigInteger bigInt = new BigInteger(1, md5sum);
			final String output = String.format("%1$032X", bigInt);    //pad on left to 32 chars with 0's, also capitalize.
			// out("MD5: " + output);
			return output.toUpperCase(Locale.ENGLISH);
		} catch (final IOException e) {
			throw new RuntimeException("Unable to process file for MD5", e);
		} finally {
			try {
				is.close();
			} catch (final IOException e) {
				throw new RuntimeException("Unable to close input stream for MD5 calculation", e);
			}
		}
	}
}
