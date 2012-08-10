/**
 * 
 */
package morlok8k.MinecraftLandGenerator;

import java.util.ArrayList;


/**
 * @author morlok8k
 * 
 */
public class Arraylist {

	public static ArrayList<Coordinates> arrayListRemove(final ArrayList<Coordinates> list,
			final ArrayList<Coordinates> remove) {

		boolean changed = false;
		changed = list.removeAll(remove);

		if (Main.verbose) {
			System.out.println("ArrayList changed: " + changed);
		}

		return list;
	}

}
