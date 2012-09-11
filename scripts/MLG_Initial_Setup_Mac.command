#!/bin/bash
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
