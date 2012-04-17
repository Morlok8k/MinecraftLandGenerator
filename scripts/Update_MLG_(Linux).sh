#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Update MLG - Linux
echo $BINDIR

java -showversion -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
chmod +x *.jar
java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt

