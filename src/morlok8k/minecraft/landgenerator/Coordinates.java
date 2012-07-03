package morlok8k.minecraft.landgenerator;

/**
 * Coordinates are in the form of [X,Y,Z] or (X,Z)<br>
 * <br>
 * x-axis (longitude): the distance east (positive) or west (negative) of the origin point<br>
 * z-axis (latitude): the distance south (positive) or north (negative) of the origin point<br>
 * y-axis (elevation): how high or low (from 0 to 255 (previously 128), with 64 being sea level) <br>
 * The origin point: When both X and Z are both zero. (elevation is irrelevant)<br>
 */
public class Coordinates {
	//FYI: int's (Integer's) are good enough for Minecraft.  They have a range of -2,147,483,648 to 2,147,483,647
	//		Minecraft starts failing around ± 12,550,820 and ends at either ± 30,000,000 or ± 32,000,000 (depending on the version).
	// See: http://www.minecraftwiki.net/wiki/Far_Lands for more info.

	public int X = 0;
	public int Y = 0;
	public int Z = 0;

	/**
	 * @param x
	 * @param y
	 * @param z
	 */
	public Coordinates(int x, int y, int z) {
		super();
		X = x;
		Y = y;
		Z = z;
	}

	/**
	 * Someone created a new blank Coordinate! Lets set it to be [0,0,0].
	 */
	public Coordinates() {
		clear();
	}

	/**
	 * @return the x
	 */
	public int getX() {
		return X;
	}

	/**
	 * @return the y
	 */
	public int getY() {
		return Y;
	}

	/**
	 * @return the z
	 */
	public int getZ() {
		return Z;
	}

	/**
	 * @param x
	 *            the x to set
	 */
	public void setX(int x) {
		X = x;
	}

	/**
	 * @param y
	 *            the y to set
	 */
	public void setY(int y) {
		Y = y;
	}

	/**
	 * @param z
	 *            the z to set
	 */
	public void setZ(int z) {
		Z = z;
	}

	public void clear() {
		X = 0;
		Y = 0;
		Z = 0;
	}

	public static Coordinates parseString(String StringOfCoords) {
		//parse out string
		StringOfCoords.trim();

		int x = 0, y = 0, z = 0;

		//TODO: add validity checks:
		//TODO: add short version...  (Y = 0)

		int start = 0, end = 0, firstComma = 0, secComma = 0;

		String sX = "", sY = "", sZ = "";

		start = StringOfCoords.indexOf("[");
		end = StringOfCoords.indexOf("]");

		StringOfCoords = StringOfCoords.substring(start, end);

		firstComma = StringOfCoords.indexOf(",");
		secComma = StringOfCoords.lastIndexOf(",");

		System.out.println(start + " " + end + " " + firstComma + " " + secComma);

		sX = StringOfCoords.substring(start + 1, firstComma);
		sY = StringOfCoords.substring(firstComma + 1, secComma);
		sZ = StringOfCoords.substring(secComma + 1, end);

		System.out.println(sX + " " + sY + " " + sZ);

		x = Integer.parseInt(sX);
		y = Integer.parseInt(sY);
		z = Integer.parseInt(sZ);

		return new Coordinates(x, y, z);
	}

	///////////////////////////////////////////////////////////////////////////////////////
	// Java Language Specific Crap Below...  Stuff *gotta* be there so Java won't cry... //
	///////////////////////////////////////////////////////////////////////////////////////

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// I am overriding the inherited toString method.
		// Because it doesn't know how to deal with my custom data.
		// So instead of getting "blahblahblah.Coordinates@745f"
		//		(the location of the class and the hexstring of the hashcode)
		// I return "[X,Y,Z]" 

		return ("[" + X + "," + Y + "," + Z + "]");

	}

	public String toString(boolean Short) {
		if (Short) {								// We are overloading toString with an additional option:
			return ("(" + X + "," + Z + ")");		// Basically just an option to return just X and Z  (formatted differently as well: "(X,Z)")
		}
		return toString();							// Idiot catch.  default to: "[X,Y,Z]"

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		// I am overriding the inherited hashCode method.
		// Because it doesn't know how to deal with my custom data.
		// So instead of getting who knows what, we return valid data

		final int prime = 31;			// My hard coded prime number
		int result = 1;					// The hard coded number I start with
		result = prime * result + X;	// Add the X data
		result = prime * result + Y;	// Add the Y data
		result = prime * result + Z;	// Add the Z data
		return result;			//this result will consistently give the same result for the same data.
		// [0,0,0] will always give 29791.  [1,2,3] will always give 30817.
		//yes, If I was lazy, I could just do a "return 0;" and it would still be technically valid.
		//but if I'm going override the method, I might as well do it right...
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		// I am overriding the inherited equals method.
		// Because it doesn't know how to deal with my custom data.
		// So instead of always failing, it actually works!
		//		(by default it gets the memory addresses of each object.)

		// An object must equal itself
		if (this == obj) return true;

		// No object equals null
		if (obj == null) return false;
		if (this == null) return false;

		// Objects of different types are never equal
		if (getClass() != obj.getClass()) return false;

		// Cast to an Coordinates, then compare the data
		Coordinates c = (Coordinates) obj;
		if (X != c.X) return false;
		if (Y != c.Y) return false;
		if (Z != c.Z) return false;
		return true;			// If none of the above returned something, they must be equal!
	}
}
