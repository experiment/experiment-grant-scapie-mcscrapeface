@echo off

::
:: see commands.txt for command-line calls
::

:: Variables that you should change to suit your environment.
if not defined JAVA_HOME ( 
     echo JAVA_HOME Environment Variable is not set, using default...
     set JAVA_HOME=..\..\jdk1.7.0_60
) 
echo JAVA_HOME=%JAVA_HOME%
%JAVA_HOME%\bin\java -version
echo.

set REFIMPL=..\ApplicantClient_CL
echo REFIMPL=%REFIMPL%
echo.

set DIST_DIR=.
echo DIST_DIR=%DIST_DIR%
echo.

set LIB_DIR=.\lib
echo LIB_DIR=%LIB_DIR%
echo.

:: Toggle this variable to point to the package corresponds to the RI you would like to test.
set PACKAGE=gov.grants.apply.applicant.v2
echo PACKAGE=%PACKAGE%
echo.

:: Execute the Test script.
call run.bat %*
