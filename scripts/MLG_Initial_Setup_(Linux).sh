#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt
java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist MLG_Update_Files.txt
java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -conf
java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar 0 0 -w
