#!/bin/bash
cd "$(dirname "$0")"
exec java -Djava.awt.headless=true -Xms1024m -Xmx1024m -Xincgc -jar MinecraftLandGenerator.jar -update





