@echo off
::

::            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE

::                    Version 2, December 2004

::

:: Copyright (C) 2004 Sam Hocevar <sam@hocevar.net>

::

:: Everyone is permitted to copy and distribute verbatim or modified

:: copies of this license document, and changing it is allowed as long

:: as the name is changed.

::

::            DO WHAT THE FUCK YOU WANT TO PUBLIC LICENSE

::   TERMS AND CONDITIONS FOR COPYING, DISTRIBUTION AND MODIFICATION

::

::  0. You just DO WHAT THE FUCK YOU WANT TO.

::



SET BINDIR=%~dp0
CD /D "%BINDIR%"
SET ScriptTitle=Minecraft Land Generator - Update Minecraft Server - Windows
title %ScriptTitle%
cls
echo %ScriptTitle%
echo %BINDIR%

java -jar MinecraftLandGenerator.jar -downloadfile https://s3.amazonaws.com/MinecraftDownload/launcher/minecraft_server.jar

