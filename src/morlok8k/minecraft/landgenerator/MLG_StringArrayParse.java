package morlok8k.minecraft.landgenerator;

public class MLG_StringArrayParse {

	public static String[] Parse(String[] array, String ParseOut) {

		//There is probably a better way to do this.
		//We input a String[] array, and a String.
		//if the String matches one inside the array, it gets "deleted"
		//(actually a new String[] without it is returned)

		String[] workingArray = new String[array.length];		//workingArray is our working array.  we don't modify the original.

		boolean removed = false;

		try {

			int ii = 0;
			for (int i = 0; i < array.length; i++) {
				workingArray[ii] = array[i];								// copy
				if ((array[i].equals(ParseOut)) && (removed == false)) {	// we only remove the first match!
					workingArray[ii] = null;								// we make sure this is set to null (if the last arg is the match it would otherwise be copied... granted it would later be removed... but whatever.)
					ii = ii - 1;											// we just simply move back one
					removed = true;											// set our flag
				}
				ii++;
			}

		} catch (Exception ex) {
			System.err.println("Something went wrong! (Parsing Error?)");
			ex.fillInStackTrace();
			return array;			//we got some error... return the original array, just in case.
		}

		if (removed) {
			// at this point, workingArray has null for its last string.  we need to remove it.
			String[] returnArray = new String[workingArray.length - 1];
			for (int i = 0; i < returnArray.length; i++) {
				returnArray[i] = workingArray[i];
			}

			return returnArray;
		}
		return array;			//no changes have been done, return the original array

	}

}
