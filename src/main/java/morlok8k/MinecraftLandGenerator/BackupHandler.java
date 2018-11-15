package morlok8k.MinecraftLandGenerator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * Helper class to back up files that shouldn't be modified and to restore them to their previous state. Backup files
 * have the same name as the original file, but extended by a ".bak". Existing backup files are never ever overwritten due
 * to potential data loss.
 * 
 * @author piegames
 */
public class BackupHandler {

	private static Log log = LogFactory.getLog(BackupHandler.class);

	public final Path file, backup;
	protected volatile boolean hasBackup;

	/**
	 * @param file
	 *            The file to create a backup from
	 * @throws FileAlreadyExistsException
	 *             if the backup file already exists
	 * @author piegames
	 */
	public BackupHandler(Path file) throws FileAlreadyExistsException {
		this.file = file;
		this.backup = file.resolveSibling(file.getFileName().toString() + ".bak");
		if (Files.exists(backup)) throw new FileAlreadyExistsException(backup.toString());
	}

	/**
	 * Creates a backup of the file if it hasn't already been done yet and the original file actually exists.
	 * 
	 * @author piegames
	 */
	public synchronized void backup() throws IOException {
		if (!hasBackup && !Files.exists(backup)) {
			log.debug("Creating backup " + file + " --> " + backup);
			if (Files.exists(file)) Files.copy(file, backup);
			hasBackup = true;
		}
	}

	/**
	 * Restore the backup file to the original and delete it.
	 * 
	 * @author piegames
	 */
	public synchronized void restore() throws IOException {
		log.debug("Restoring backup " + file + " <-- " + backup);
		if (Files.exists(backup))
			Files.move(backup, file, StandardCopyOption.REPLACE_EXISTING);
		else Files.deleteIfExists(file);
		hasBackup = false;
	}
}
