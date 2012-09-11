#!/bin/sh
BINDIR=$(dirname "$(readlink -fn "$0")")
cd "$BINDIR"
echo Minecraft Land Generator - Run Minecraft Server - Linux
echo $BINDIR

java -Djava.net.preferIPv4Stack=true -Djava.awt.headless=true -Xms1024m -Xmx1024m -Xincgc -jar minecraft_server.jar

