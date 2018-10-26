/*
#######################################################################
#            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE              #
#                    Version 2, December 2004                         #
#                                                                     #
# Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>                    #
#                                                                     #
# Everyone is permitted to copy and distribute verbatim or modified   #
# copies of this license document, and changing it is allowed as long #
# as the name is changed.                                             #
#                                                                     #
#            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE              #
#   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION   #
#                                                                     #
#  0. You just DO WHAT THE FUCK YOU WANT TO.                          #
#                                                                     #
#######################################################################
*/

package morlok8k.MinecraftLandGenerator;

/**
 * 
 * @author morlok8k
 */
public class StringArrayParse {

	/**
	 * 
	 * @param array
	 * @param ParseOut
	 * @return
	 */
	public static String[] Parse(final String[] array, final String ParseOut) {

		//There is probably a better way to do this.
		//We input a String[] array, and a String.
		//if the String matches one inside the array, it gets "deleted"
		//(actually a new String[] without it is returned)

		final String[] workingArray = new String[array.length];		//workingArray is our working array.  we don't modify the original.

		boolean removed = false;

		try {

			int ii = 0;
			for (int i = 0; i < array.length; i++) {
				workingArray[ii] = array[i];								// copy
				if ((array[i].contains(ParseOut)) && (removed == false)) {	// we only remove the first match!
					workingArray[ii] = null;								// we make sure this is set to null (if the last arg is the match it would otherwise be copied... granted it would later be removed... but whatever.)
					ii = ii - 1;											// we just simply move back one
					removed = true;											// set our flag
				}
				ii++;
			}

		} catch (final Exception ex) {
			System.err.println("Something went wrong! (Parsing Error?)");
			ex.fillInStackTrace();
			return array;			//we got some error... return the original array, just in case.
		}

		if (removed) {
			// at this point, workingArray has null for its last string.  we need to remove it.
			final String[] returnArray = new String[workingArray.length - 1];
			for (int i = 0; i < returnArray.length; i++) {
				returnArray[i] = workingArray[i];
			}

			return returnArray;
		}
		return array;			//no changes have been done, return the original array

	}
}
