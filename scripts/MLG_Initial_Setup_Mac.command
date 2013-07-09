#!/bin/bash

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

cd "$(dirname "$0")"
echo Minecraft Land Generator - Initial Setup - Mac OSX
echo $PWD

java -jar MinecraftLandGenerator.jar -nowait -update
java -jar MinecraftLandGenerator.jar -nowait -readme _MLG_Readme.txt
java -jar MinecraftLandGenerator.jar -nowait -downloadlist "MLG_Update_Files_Mac.txt"
java -jar MinecraftLandGenerator.jar -nowait -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
chmod +x *.command
chmod +x *.jar
java -jar MinecraftLandGenerator.jar -nowait -conf
java -jar MinecraftLandGenerator.jar -nowait 0 0 -w
rm -rf world

## Need to test the following on a mac:
##read -s -n 1 -p "Press any key to continue . . ."
##echo 
