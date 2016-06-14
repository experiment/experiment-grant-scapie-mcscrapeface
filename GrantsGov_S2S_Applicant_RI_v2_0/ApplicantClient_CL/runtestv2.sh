#!/bin/bash

##
# see commands.txt for command-line calls
##


# Variables that you should change to suit your environment.
if [ -z "${JAVA_HOME}" ]; then
     echo "JAVA_HOME Environment Variable is not set, using default..."
     export JAVA_HOME=/opt/app/jdk/jdklatest
fi

echo "JAVA_HOME: " ${JAVA_HOME}
$JAVA_HOME/bin/java -version
echo "."

export REFIMPL=../ApplicantClient_CL
echo "REFIMPL="${REFIMPL}
echo "."

export DIST_DIR=.
echo "DIST_DIR="${DIST_DIR}
echo "."

export LIB_DIR=./lib
echo "LIB_DIR="${LIB_DIR}
echo "."

# Toggle this variable to point to the package corresponds to the RI you would like to test.
export PACKAGE=gov.grants.apply.applicant.v2
echo "PACKAGE="${PACKAGE}
echo "."

# Execute the Test script, use the "$@" to preserve arguments with spaces.
./run.sh "$@"
