#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Update Extra MLG Files All OS - Linux
echo $BINDIR

java -jar MinecraftLandGenerator.jar -downloadlist "MLG_Update_Files.txt"
chmod +x *.sh
chmod +x *.jar

