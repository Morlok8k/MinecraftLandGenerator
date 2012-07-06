package corrodias.minecraft.landgenerator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import morlok8k.minecraft.landgenerator.Coordinates;

import org.jnbt.CompoundTag;
import org.jnbt.IntTag;
import org.jnbt.LongTag;
import org.jnbt.NBTInputStream;
import org.jnbt.NBTOutputStream;
import org.jnbt.Tag;

public class MLG_SpawnPoint {

	/**
	 * Changes the spawn point in the given Alpha/Beta level to the given coordinates.<br>
	 * Note that, in Minecraft levels, the Y coordinate is height.<br>
	 * (We picture maps from above, but the game was made from a different perspective)
	 * 
	 * @param level
	 *            the level file to change the spawn point in
	 * @param xyz
	 *            the Coordinates of the spawn point
	 * @throws IOException
	 *             if there are any problems reading/writing the file
	 * @author Corrodias
	 */
	protected static void setSpawn(File level, Coordinates xyz) throws IOException {

		try {
			NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			//@formatter:off
			
			//Note: The Following Information is Old (from 2010), compared to the Data inside a current "level.dat".
			//However, What we look at (SpawnX,Y,Z and RandomSeed) have not changed.
			
			/* <editor-fold defaultstate="collapsed" desc="structure">
			* Structure:
			*
			*TAG_Compound("Data"): World data.
			*	* TAG_Long("Time"): Stores the current "time of day" in ticks. There are 20 ticks per real-life second, and 24000 ticks per Minecraft day, making the day length 20 minutes. 0 appears to be sunrise, 12000 sunset and 24000 sunrise again.
			*	* TAG_Long("LastPlayed"): Stores the Unix time stamp (in milliseconds) when the player saved the game.
			*	* TAG_Compound("Player"): Player entity information. See Entity Format and Mob Entity Format for details. Has additional elements:
			*		o TAG_List("Inventory"): Each TAG_Compound in this list defines an item the player is carrying, holding, or wearing as armor.
			*			+ TAG_Compound: Inventory item data
			*				# TAG_Short("id"): Item or Block ID.
			* 				# TAG_Short("Damage"): The amount of wear each item has suffered. 0 means undamaged. When the Damage exceeds the item's durability, it breaks and disappears. Only tools and armor accumulate damage normally.
			*				# TAG_Byte("Count"): Number of items stacked in this inventory slot. Any item can be stacked, including tools, armor, and vehicles. Range is 1-255. Values above 127 are not displayed in-game.
			*				# TAG_Byte("Slot"): Indicates which inventory slot this item is in.
			*		o TAG_Int("Score"): Current score, doesn't appear to be implemented yet. Always 0.
			*	* TAG_Int("SpawnX"): X coordinate of the player's spawn position. Default is 0.
			*	* TAG_Int("SpawnY"): Y coordinate of the player's spawn position. Default is 64.			
			*	* TAG_Int("SpawnZ"): Z coordinate of the player's spawn position. Default is 0.
			*	* TAG_Byte("SnowCovered"): 1 enables, 0 disables, see Winter Mode
			*	* TAG_Long("SizeOnDisk"): Estimated size of the entire world in bytes.
			*	* TAG_Long("RandomSeed"): Random number providing the Random Seed for the terrain.
			* </editor-fold>
			*/
			
			//@formatter:on

			Map<String, Tag> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some reason, so we have to make a copy.
			Map<String, Tag> newData = new LinkedHashMap<String, Tag>(originalData);

			// .get() a couple of values, just to make sure we're dealing with a valid level file, here. Good for debugging, too.
			@SuppressWarnings("unused")
			IntTag spawnX = (IntTag) newData.get("SpawnX"); // we never use these... Its only here for potential debugging.
			@SuppressWarnings("unused")
			IntTag spawnY = (IntTag) newData.get("SpawnY"); // but whatever... so I (Morlok8k) suppressed these warnings.
			@SuppressWarnings("unused")
			IntTag spawnZ = (IntTag) newData.get("SpawnZ"); // I don't want to remove existing code, either by myself (Morlok8k) or Corrodias

			newData.put("SpawnX", new IntTag("SpawnX", xyz.getX()));		// pulling the data out of the Coordinates,
			newData.put("SpawnY", new IntTag("SpawnY", xyz.getY()));		// and putting it into our IntTag's
			newData.put("SpawnZ", new IntTag("SpawnZ", xyz.getZ()));

			// Again, we can't modify the data map in the old Tag, so we have to make a new one.
			CompoundTag newDataTag = new CompoundTag("Data", newData);
			Map<String, Tag> newTopLevelMap = new HashMap<String, Tag>(1);
			newTopLevelMap.put("Data", newDataTag);
			CompoundTag newTopLevelTag = new CompoundTag("", newTopLevelMap);

			NBTOutputStream output = new NBTOutputStream(new FileOutputStream(level));
			output.writeTag(newTopLevelTag);
			output.close();
		} catch (ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

	//TODO: update this
	/**
	 * @param level
	 * @return
	 * @throws IOException
	 * @author Corrodias
	 */
	protected static Coordinates getSpawn(File level) throws IOException {
		try {
			NBTInputStream input = new NBTInputStream(new FileInputStream(level));
			CompoundTag originalTopLevelTag = (CompoundTag) input.readTag();
			input.close();

			Map<String, Tag> originalData =
					((CompoundTag) originalTopLevelTag.getValue().get("Data")).getValue();
			// This is our map of data. It is an unmodifiable map, for some
			// reason, so we have to make a copy.
			Map<String, Tag> newData = new LinkedHashMap<String, Tag>(originalData);
			// .get() a couple of values, just to make sure we're dealing with a
			// valid level file, here. Good for debugging, too.
			IntTag spawnX = (IntTag) newData.get("SpawnX");
			IntTag spawnY = (IntTag) newData.get("SpawnY");
			IntTag spawnZ = (IntTag) newData.get("SpawnZ");

			LongTag Seed = (LongTag) newData.get("RandomSeed");
			Main.randomSeed = Seed.getValue();
			Main.out("Seed: " + Main.randomSeed); // lets output the seed, cause why not?

			Coordinates ret =
					new Coordinates(spawnX.getValue(), spawnY.getValue(), spawnZ.getValue());
			return ret;
		} catch (ClassCastException ex) {
			throw new IOException("Invalid level format.");
		} catch (NullPointerException ex) {
			throw new IOException("Invalid level format.");
		}
	}

}
