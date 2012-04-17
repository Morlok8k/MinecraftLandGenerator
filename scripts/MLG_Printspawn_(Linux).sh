#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Print Spawn - Linux
echo $BINDIR

java -Djava.awt.headless=true -jar MinecraftLandGenerator.jar -printspawn

