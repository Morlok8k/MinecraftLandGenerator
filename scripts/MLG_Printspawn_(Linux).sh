#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"

java -jar MinecraftLandGenerator.jar -printspawn
