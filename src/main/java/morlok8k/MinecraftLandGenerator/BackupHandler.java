package morlok8k.MinecraftLandGenerator;

import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class BackupHandler {

	private static Log log = LogFactory.getLog(BackupHandler.class);

	public final Path file, backup;
	protected volatile boolean hasBackup;

	public BackupHandler(Path file) throws FileAlreadyExistsException {
		this.file = file;
		this.backup = file.resolveSibling(file.getFileName().toString() + ".bak");
		if (Files.exists(backup)) throw new FileAlreadyExistsException(backup.toString());
	}

	public synchronized void backup() throws IOException {
		if (!hasBackup && !Files.exists(backup)) {
			log.debug("Creating backup " + file + " --> " + backup);
			if (Files.exists(file)) Files.copy(file, backup);
			hasBackup = true;
		}
	}

	public synchronized void restore() throws IOException {
		log.debug("Restoring backup " + file + " <-- " + backup);
		if (Files.exists(backup))
			Files.move(backup, file, StandardCopyOption.REPLACE_EXISTING);
		else Files.deleteIfExists(file);
		hasBackup = false;
	}
}
