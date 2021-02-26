#!/bin/bash
cd de.variantsync.core
mvn clsean verify
eLevel=$?

if [ $eLevel -eq 0 ] 
then
   #Building library was successful
   cp target/de.variantsync.core-0.0.3-SNAPSHOT.jar ../de.tubs.variantsync.core/lib/
   cd ..
   mvn clean verify
   exit $?
else
   #Building library was not successful
   exit $eLevel
fi   
