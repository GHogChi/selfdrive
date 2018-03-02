#!/bin/bash
mvn verify
rm -f selfDrive
mv target/selfDrive.jar selfDrive
chmod a+rx selfDrive

