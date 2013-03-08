#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Test Run - Linux
echo $BINDIR


java -jar MinecraftLandGenerator.jar -nowait 0 0 -w
