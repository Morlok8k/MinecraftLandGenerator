#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

java -client -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -downloadlist MLG_Update_Files.txt
chmod +x *.sh
