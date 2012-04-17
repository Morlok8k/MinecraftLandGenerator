#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Initial Setup - Linux
echo $BINDIR

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files_(Linux).txt"
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
chmod +x *.sh
chmod +x *.jar
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -conf
java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar 0 0 -w

