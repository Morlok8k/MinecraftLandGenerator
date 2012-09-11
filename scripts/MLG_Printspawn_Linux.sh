#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Print Spawn - Linux
echo $BINDIR

java -jar MinecraftLandGenerator.jar -printspawn

