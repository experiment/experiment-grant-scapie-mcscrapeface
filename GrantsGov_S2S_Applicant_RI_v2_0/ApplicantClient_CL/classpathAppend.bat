rem ---------------------------------------------------------------------------
rem Append to CLASSPATH
rem ---------------------------------------------------------------------------

:: WINDOWS classpath separator
set CP_SEP=;

echo Adding[ %1 ]
if ""%1"" == """" goto end
set TEMP_CP=%TEMP_CP%%CP_SEP%%1
shift

rem Process the remaining arguments
:setArgs
if ""%1"" == """" goto doneSetArgs
echo Process the remaining arguments: %1
set TEMP_CP=%TEMP_CP% %1
shift
goto setArgs

:doneSetArgs
:end
