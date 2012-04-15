#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

java -client -showversion -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -update
java -jar MinecraftLandGenerator.jar -readme _MLG_Readme.txt

#rm server.log
