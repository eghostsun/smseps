#!/bin/bash
ant -buildfile build-tomcat.xml -Dbuildmode=release build

read -n1 -p "Press any key to continue..."
