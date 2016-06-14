
NOTE: All directories (directory paths) are relative to grantsws_applicant_ri_V2/

JDK Setup:
	- JDK version 1.7 is required to be able to run this reference implementation (RI). Any minor version of JDK 1.7 should work
	  with this RI, however the RI was developed and tested against JDK 1.7.0.60
	- After downloading and installing the JDK, make sure to specify the JDK path in the runtestv2.bat.
	- The runtestv2.bat is implemented to use the most recent SSL Protocol supported by the new Grants.gov S2S System,
	  If no port is being specified in the URL, it will default to Port 443.	

Security Setup:
	- Place your JKS truststore file in Security/Truststores
	- Place your JKS keystore file(s) in Security/Keystores
		- NOTE: the keystore JKS file MUST contain the KEY PAIR
	- Edit the ApplicantClient_CL/resources/applicant-s2s-config.properties file:
		- replace <your-keystore-filename>.jks with your keystore JKS file name
		- replace <your-keystore-password> with the password of your keystore.jks file
		- replace <your-third-party-keystore-filename>.jks with your keystore JKS file name (for third-party submitter ONLY)
		- replace <your-third-party-keystore-password> with the password of your keystore.jks file (for third-party submitter ONLY)

Configuration:
	- applicant_commands_bat.txt contain all the commands to run the S2S clients in DOS
	- Applicant client configurations is in Config/grantsws-applicant-config.xml
	
Organization:
	- Single-project submissions should be placed in SampleXML/SubmissionXML
	- Multi-project submissions should be placed in SampleXML/SubmissionXML

Running the clients:
	- Run the applicant clients from the ApplicantClient_CL directory
	
Troubleshooting:
	- Be sure the paths in the Config/grantsws-applicant-config.xml are valid when pointing
	  to test submissions, manage package xml, and opportunity xml submission files.
	- Be sure to specify the JDK path in the runtestv2.bat.
	- RI is configured by default to log output to both STDOUT & Local file under TEMP/grantsws-applicant-ri-v2.log,
	  In some cases where the output characters of the innovation results are UTF-8 encoded, you will see a 
	  series of <?????> on the STDOUT, to view the actual characters, please view the log file. 