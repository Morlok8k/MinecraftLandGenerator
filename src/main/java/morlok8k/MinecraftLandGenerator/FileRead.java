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

import org.joml.Vector3i;

/**
 * @author morlok8k
 */
@Deprecated
public class FileRead {

	public static Vector3i parseString(String StringOfCoords) {
		StringOfCoords = StringOfCoords.trim();

		int start = StringOfCoords.indexOf("[");
		int end = StringOfCoords.indexOf("]");

		String[] coordlong = StringOfCoords.substring(start, end).split(",");
		if ((start == -1) || (end == -1)) {

			start = StringOfCoords.indexOf("(");
			end = StringOfCoords.indexOf(")");
			String[] coordshort = StringOfCoords.substring(start, end).split(",");
			if ((start != -1) && (end != -1)) {
				return new Vector3i(Integer.valueOf(coordshort[0]), 64,
						Integer.valueOf(coordshort[2]));
			} else {
				return new Vector3i(0, 0, 0);
			}
		} else {

			return new Vector3i(Integer.valueOf(coordlong[0]), Integer.valueOf(coordlong[1]),
					Integer.valueOf(coordlong[2]));
		}
	}
}
