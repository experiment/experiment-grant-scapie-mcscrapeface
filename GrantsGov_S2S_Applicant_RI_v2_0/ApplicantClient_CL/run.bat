:: Variables that you don't need to change unless you are debugging. 

set argC=0
for %%x in (%*) do Set /A argC+=1
echo argument count=%argC%
echo.

echo params: %*
echo.

set JAVAOPTS=-Xms128m -Xmx512m
echo JAVAOPTS=%JAVAOPTS%
echo.

IF NOT "%1" == "-legacy" (
 	set JAVA_EXT_OPTS=-Dhttps.protocols=TLSv1.2
 	echo Setting TLS Protocol [%JAVA_EXT_OPTS%] to run
	set CLASS=%PACKAGE%.%1 
 	echo Setting class [%CLASS%] to run
) ELSE (
 	set JAVA_EXT_OPTS=-Dhttps.protocols=TLSv1
 	echo Setting TLS Protocol [%JAVA_EXT_OPTS%] to run
	set CLASS=%PACKAGE%.%2
	echo Setting class [%CLASS%] to run
)
echo.


:: Dynamically set the classpath
echo Creating a classpath from the contents of your lib directory ...
set TEMP_CP=
FOR %%i IN ( %LIB_DIR%\*.*) DO call classpathAppend.bat %%i
set CLASSPATH=%TEMP_CP%
echo classpath: %CLASSPATH%
echo.


:: Run the test application with all arguments...
echo "========================================================"
echo %JAVA_HOME%\bin\java %JAVAOPTS% %JAVA_EXT_OPTS% %CLASS% %*
echo "========================================================"
:: Actual Execution of the Java Command.
%JAVA_HOME%\bin\java %JAVAOPTS% %JAVA_EXT_OPTS% %CLASS% %*

:: Uncomment this command to enable JAVA remote debugging and SSL debugging...
:: %JAVA_HOME%\bin\java -Djava.protocol.handler.pkgs=com.sun.net.ssl.internal.www.protocol -Djavax.net.debug=ssl,handshake -Xdebug -Xnoagent -Xrunjdwp:transport=dt_socket,address=5050,server=y,suspend=n %JAVAOPTS% %JAVA_EXT_OPTS% %CLASS% %*

:End

