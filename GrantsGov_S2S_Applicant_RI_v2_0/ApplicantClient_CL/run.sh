#!/bin/bash

# Variables that you don't need to change unless you are debugging.

echo "argument count="$#
echo "."

echo "params:" $*
echo "."

JAVAOPTS="-Xms128m -Xmx512m"
echo "JAVAOPTS="${JAVAOPTS} 
echo "."

if [ "$1" != "-legacy"  ]
then
        JAVA_EXT_OPTS=-Dhttps.protocols=TLSv1.2
        echo Setting TLS Protocol [${JAVA_EXT_OPTS}] to run 
        CLASS=${PACKAGE}.${1}
        echo Setting class [${CLASS}] to run
         
else
        JAVA_EXT_OPTS=-Dhttps.protocols=TLSv1
        echo Setting TLS Protocol [${JAVA_EXT_OPTS}] to run
        CLASS=${PACKAGE}.${2}
        echo Setting class [${CLASS}] to run
fi
echo "."

# Dynamically set the classpath
echo "Creating a classpath from the contents of your lib directory ..."
# UNIX classpath separator
export CP_SEP=':'
TEMP_CP=`find ${LIB_DIR} -name '*.jar' -type f | sort | paste -d${CP_SEP} -s -`
TEMP_CP=${TEMP_CP}${CP_SEP}${DIST_DIR}
CLASSPATH=${TEMP_CP}
export CLASSPATH
echo "classpath=" ${CLASSPATH}
echo "."

# Run the test application with all arguments...
# "$@" allows for passing args with spaces as one argument
#  when quotes are used ... i.e. status="Received by Agency"
echo "========================================================"
echo $JAVA_HOME/bin/java ${JAVAOPTS} ${JAVA_EXT_OPTS} ${CLASS} "$@"
echo "========================================================"
# Actual Execution of the Java Command.
$JAVA_HOME/bin/java ${JAVAOPTS} ${JAVA_EXT_OPTS} ${CLASS} "$@" 

# Uncomment this command to enable JAVA remote debugging and SSL debugging...
# $JAVA_HOME/bin/java -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.debug=ssl,handshake -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=5050,server=y,suspend=y ${JAVAOPTS} ${JAVA_EXT_OPTS} ${CLASS} "$@"
