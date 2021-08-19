# Minecraft Land Generator version 1.7.6

Updated January 19, 2015
(BuildID: 1421666774000)

| Author  | Edited |
| ------------- | -------------      |
| Original Code by Corrodias  | November 2010  |
| Enhanced Code by Morlok8k  | Feb. 2011 to Now (or at least to January 19, 2015!)  |
| Additional Code by pr0f1x  | October 2011  |
| Additional Code/Idea by jaseg  | August 2012  |
| Additional Code by Gallion  | January 2015  |



Website: https://sites.google.com/site/minecraftlandgenerator/<br>
Forum: http://www.minecraftforum.net/topic/187737-<br>
Source: https://github.com/Morlok8k/MinecraftLandGenerator<br>

---

This program lets you generate an area of land with your Minecraft SMP server (and is prossibly future-proof for newer versions). You set up your java command line and minecraft server paths in the MinecraftLandGenerator.conf file, set up the server's server.properties file with the name of the world you wish to use, and then run this program.
When a Minecraft server is launched, it automatically generates chunks within a square area of 25x25 chunks (400x400 blocks), centered on the current spawn point (formally 20x20 chunks, 320x320 blocks). When provided X and Z ranges as arguments, this program will launch the server repeatedly, editing the level.dat file between sessions, to generate large amounts of land without players having to explore them. The generated land will have about the X and Z ranges as requested by the arguments, though it will not be exact due to the spawn point typically not on the border of a chunk. (Because of this, MLG by default adds a slight overlap with each pass - 380x380 blocks) You can use the -x and -z switches to override the spawn offset and center the land generation on a different point.
The program makes a backup of level.dat as level_backup.dat before editing, and restores the backup at the end. In the event that a level_backup.dat file already exists, the program will refuse to proceed, leaving the user to determine why the level_backup.dat file exists and whether they would rather restore it or delete it, which must be done manually.<br>

This program is free, and the source code is included in the .jar file.  (If accidently missing, like in 1.3.0 and 1.4.0, it is always available at Github.)<br>

Copyright © 2010
This work is free. You can redistribute it and/or modify it under the terms of the Do What The Fuck You Want To Public License, Version 2, as published by Sam Hocevar. See http://www.wtfpl.net/ for more details.<br>

The JNBT library is included (inside the .jar). It is not public domain. Its license is included, as LICENSE.TXT.
It is also available at: http://jnbt.sourceforge.net/ (Original) and at: https://github.com/Morlok8k/JNBT (Current)<br>

The "unescape" method/function is also not Public Domain.  Its License is the W3C© Software License, and located here: http://www.w3.org/Consortium/Legal/2002/copyright-software-20021231
Other Public Domain code has been used in this program, and references to sources are included in the comments of Minecraft Land Generator's source code.


Usage: 
```
java -jar MinecraftLandGenerator.jar x z [serverpath] [switches]

Arguments:
              x : X range to generate
              z : Z range to generate
     serverpath : the path to the directory in which the server runs (takes precedence over the config file setting)

Switches:
       -verbose : causes the application to output the server's messages to the console
             -v : same as -verbose
             -w : Ignore [WARNING] and [SEVERE] messages.
           -alt : alternate server launch sequence
             -a : same as -alt
        -nowait : don't pause for anything
             -n : same as -nowait
            -i# : override the iteration spawn offset increment (default 380) (example: -i100)
            -x# : set the X offset to generate land around (example: -x0 or -x1000 or -x-500)
            -z# : set the Z offset to generate land around (example: -z0 or -z1000 or -z-500)
```
Other options:
```
  java -jar MinecraftLandGenerator.jar -update
        Checks for and downloads new versions of MLG online.

  java -jar MinecraftLandGenerator.jar -printspawn
  java -jar MinecraftLandGenerator.jar -ps
        Outputs the current world's spawn point coordinates.

  java -jar MinecraftLandGenerator.jar -conf
  java -jar MinecraftLandGenerator.jar -conf download
        Generates or downloads a MinecraftLandGenerator.conf file.

  java -jar MinecraftLandGenerator.jar -readme readme.txt
  java -jar MinecraftLandGenerator.jar -readme
        Generates a readme file using supplied name or the default _MLG_Readme.txt

  java -jar MinecraftLandGenerator.jar -downloadfile http://example.com/file.txt
        Downloads whatever file from the internet you give it.
  java -jar MinecraftLandGenerator.jar -downloadlist list.txt
        list.txt (or any other file) contains a URL on each line which will be downloaded.

  java -jar MinecraftLandGenerator.jar -version
  java -jar MinecraftLandGenerator.jar -help
  java -jar MinecraftLandGenerator.jar /?
        Prints this message.
```
When launched with the -conf switch, this application creates a MinecraftLandGenerator.conf file that contains configuration options.
If this file does not exist or does not contain all required properties, the application will not run.

MinecraftLandGenerator.conf properties:
```
           Java : The command line to use to launch the server
     ServerPath : The path to the directory in which the server runs (can be overridden by the serverpath argument)
      Done_Text : The output from the server that tells us that we are done
 Preparing_Text : The output from the server that tells us the percentage
Preparing_Level : The output from the server that tells us the level it is working on
        Level-0 : Name of Level 0: The Overworld
        Level-1 : Name of Level 1: The Nether
        Level-2 : Name of Level 2: The End
        Level-3 : Name of Level 3: (Future Level)
        Level-4 : Name of Level 4: (Future Level)
        Level-5 : Name of Level 5: (Future Level)
        Level-6 : Name of Level 6: (Future Level)
        Level-7 : Name of Level 7: (Future Level)
        Level-8 : Name of Level 8: (Future Level)
        Level-9 : Name of Level 9: (Future Level)
       WaitSave : Optional: Wait before saving.
```
---

Version History:
Morlok8k:
1.7.6
- Gallion: fixed null world name (minor bug) 
- Morlok8k: fixed elua bug
1.7.5
- Added "save-all" to alternate mode 
- Added fix for new style of java error messages
1.7.4
- Released Minecraft land Generator under the WTFPL.  (With the permission of Corrodias)
1.7.3
- Fixed a minor display bug (specifically when using Server Generation Fix Mod)
- Updated Readme text a bit.
1.7.2
- Fixed "1152 bug"
- Updated to JNBT 1.3
- adjusted archive integrity check to account for timezone-related bugs...
1.7.1
- Major Code Refactoring
- Updated to JNBT 1.2
- making code ready for a GUI
1.7.0
- Major Code Optimization
- Drastically reduced the amount of time it takes for MLG to expand a world after it has already done so before!
  (To do this, I rewrote the Main loop of the program, and add my own Coordinate object)
- Added Resume Functionality
- Updated Time Output yet again.
- Made xx% output nicer by rewriting previous existing line.
- Misc. Tweaks
- Misc. Additions
1.6.3
- Minor Code Optimization
- Finely got on the ball and added the JNBT source and everything (as an internal .zip) to be completely faithful to his license
- Also adding script files internally in the .jar for archive (or offline) purposes. (Manual Extract needed for use)
- Modified output of MLG slightly to show whats the server and whats MLG. (I may do more with this later.)
1.6.2
- Major Code Optimization
- Updated Time Output again.  Now says "1 Minute" instead of "1 Minutes".
- Updated Location Code - the center of the square is now truely centered, and it trys to get as close to the given size as possible.
- Added "-nowait" and its shorter version "-n"
- Added currently non-functional RCON code.  Will try to make functional in the future.

1.6.11
- Removed End-of-Generation ASCII-Graphic - It didn't really fit with MLG.
- Updated Time Output.
- Changed estimated time remaining to count all runs, not just the last four.
- Added the time it took to complete at the end of generation.

1.6.1
- Added some modifications for scripting  (Mainly for a new Initial setup script)
- Changed MLG's Y to Z.  Now it matches Minecraft.  Y in the game is Height.
- Renamed -y switch to -z.  MLG will remain backwards compatible if you use the old -y switch.
- Updated -printspawn to show X,Y,Z
- Added End-of-Generation ASCII-Graphic
- Slightly altered some text output

1.6.05
- MLG displays if the server is converting the Map format, when not in verbose mode. (McRegion -> Anvil, or Chunk-File -> McRegion)
- Minor fixes/edits/typos
- Added link to new MLG website to readme file
1.6.03
- added decoding of escape characters of URL's (so a space is a " " and not "%20")
- added "-downloadlist [list]" where [list] is a text file with URL's on each line

1.6.02
- small fix on caculating md5sum where old version didnt pad out to 32chars with zeros on the left side- quick Archive intergity fix after injecting source code into .jar after it compiled.- no new functionality, md5 issue doesnt affect -update on old versions.
1.6.0
- NOW DOES NOT NEED ANY SCRIPT FILES!
- Added the ability to download files from the internet
- Added a switch to download any file off the internet, if needed (useless for most people, but included it in case I wanted it in the future.)
- Added the ability to check what version the .jar is. (Using MD5 hashes, timestamps, and the BuildID file)
- Added "-update" to download new versions of MLG directly from github.
- Updated estimated time.  Now shows up on loop 2+ instead of loop 4+.
- Standard % output of the Server should look nicer now.
- Code Refactoring
- Code Formatting
- Code Optimization
- Duplicate sections of code have been turned into Methods/"Functions"

1.5.1
- pr0f1x: Added the "save-all" command to be sent to the server before shutting it down.
- pr0f1x: Added a 40 second wait before shutting down.
- Morlok8k: Made 40 second wait optional.
- Morlok8k: Changed the Dimensions code.  (I had assumed it would be DIM-1, DIM-2, etc.  but it turned out to be DIM-1 and DIM1. Change reflects Server output of "Level n")
- Morlok8k: Config file is automatically updated to reflect these changes.
- Morlok8k: Cleaned up code.

1.5.0
- Supports Server Beta 1.6.4 (& hopefully future versions as well, while remaining backward compatible.)
- Added "-a","-alt" to use alternate method (a slightly simplier version of 1.3.0's code - pure verbose only)
- Added world specific output for 9 dimensions (DIM-1 is the Nether, DIM-2 through DIM-9 dont exist yet, but if and when they do, you can configure it's text).  ("Level 0", the default world, is displayed as the worlds name)
- Updated Config File for these Dimensions.
- Reads and outputs the Seed to the output. (If you had used text for the Seed, Minecraft converts it into a number. This outputs the number.)
- Changed the default 300 blocks to 380.  The server now makes a 400x400 square block terrain instead of 320x320.  Thus it is faster because there are less loops.  To use the old way, use "-i300"
- Added total Percentage done (technically, it displays the % done once the server finishes...)
- Added debugging output vars of conf file (disabled - need to re-compile source to activate)

		+ (the goal is to have MLG be configureable, so it can work on any version of the server, past or present.)

*** 1.4.5 (pre 1.5.0) ***
- sorry!  I shouldn't release untested code...
*************************

1.4.4
- Added ablilty to ignore [WARNING] and [SEVERE] errors with "-w"

1.4.3
- Fixed "-ps","-printspawn" as I had forgot I had broken it in 1.4.0 - due to config file change.

1.4.2
- No New Features
- Changed non-verbose mode to display server progress on the same line, saving a lot of space.
	- This couldn't wait for 1.5.0 ...  I (Morlok8k) liked it too much.

1.4.0
- Future Proofing
- Configurble Server Message reading. (If server updates and breaks MLG, you can add the new text!)
- Updated config file, and auto updating from old format.
- Added % of spawn area to non-verbose output.
- Removed datetime stamps from server output in verbose mode
- Other Misc fixes.

1.3.0
- Fixed Problems with Minecraft Beta 1.3 -- Morlok8k

-----------------------------------------------

Corrodias:
1.2.0
- land generation now centers on the spawn point instead of [0, 0]
- the server is launched once before the spawn point is changed, to verify that it can run and to create a world if one doesn't exist
- added -printspawn [-ps] switch to print the current spawn coordinates to the console
- added -x and -y switches to override the X and Y offsets
- added -v switch, does the same as -verbose
- improved status message spacing to make things easier to read
- improved time estimation algorithm: it now averages the last 3 launches

1.1.0
- added MinecraftLandGenerator.conf file to hold the java command line and the server path
- added -conf solo switch to generate a .conf file
- added -verbose switch to output server output to the console (default is to ignore it)
- added -i switch to allow customizing the block increment size (default is 300)
- added instructions output in this version, i think
- improved status message output to include current iteration and total iterations

1.0.0
- initial release

-----------------------------------------------

Notes:
Due to changes in server beta 1.6, it now generates the nether as well as the world at the same time.
However, Since beta 1.9 and Minecraft 1.0, the nether or the end is no longer generated.
The "Server Generation Fix Mod" by Morlok8k can generate The Nether and The End.  Link: http://www.minecraftforum.net/topic/1378775-

I recommend using MCEDIT to relight the map after you generate it. This will take a long time, but should fix all those incorrectly dark spots in your level.

