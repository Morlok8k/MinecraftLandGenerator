#!/bin/sh

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

BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Initial Setup - Linux
echo $BINDIR

java -jar MinecraftLandGenerator.jar -nowait -update 
java -jar MinecraftLandGenerator.jar -nowait -readme _MLG_Readme.txt
java -jar MinecraftLandGenerator.jar -nowait -downloadlist "MLG_Update_Files_Linux.txt"
java -jar MinecraftLandGenerator.jar -nowait -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
chmod +x *.sh
chmod +x *.jar
java -jar MinecraftLandGenerator.jar -nowait -conf
java -jar MinecraftLandGenerator.jar -nowait 0 0 -w
rm -rf world

read -s -n 1 -p "Press any key to continue . . ."
echo 
