/**
 * 
 */
package morlok8k.minecraft.landgenerator;

import java.util.ArrayList;

import corrodias.minecraft.landgenerator.Main;

/**
 * @author morlok8k
 * 
 */
public class MLG_ArrayList {

	public static ArrayList<Coordinates> arrayListRemove(ArrayList<Coordinates> list,
			ArrayList<Coordinates> remove) {

		boolean changed = false;
		changed = list.removeAll(remove);

		if (Main.verbose) {
			System.out.println("ArrayList changed: " + changed);
		}

		return list;
	}

	//TODO: add read arraylist file

	//TODO: add save arraylist file  (save this file only after generation is complete)

}
