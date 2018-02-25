#!/bin/bash
mvn verify
rm -f autonomousDriving
mv target/autonomousDriving.jar autonomousDriving
chmod a+rx autonomousDriving

