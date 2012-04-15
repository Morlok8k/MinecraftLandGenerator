#!/bin/bash
cd "$(dirname "$0")"

exec java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
exec java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt
exec java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist MLG_Update_Files.txt
exec java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar
exec java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -conf
exec java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar 0 0 -w
