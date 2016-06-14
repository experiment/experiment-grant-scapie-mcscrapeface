package gov.grants.apply.applicant.v2;


import java.io.File;

import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationZipRequest;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationZipResponse;
import gov.grants.commons.s2s.util.SoapUtil;
import com.sun.xml.ws.developer.StreamingDataHandler;
import org.apache.log4j.Logger;


/* 
 * GetApplicationZipClient 
 * url=https://devweb.grants.gov:8002/grantsws-applicant/services/ApplicantIntegrationSoapPort 
 * processid=GRANTxxxxxxxx
 */

public class GetApplicationZipClient extends BaseApplicantClient {
	
	private static final String CLASSNAME = GetApplicationZipClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	private static final String FILE_EXT = ".zip";
	
	
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			GetApplicationZipClient client = new GetApplicationZipClient();
			
			log.debug( "args length: " + args.length );
			client.init( args );
			
			client.makeServiceCall();
			log.debug( "\n\nSUCCESS: " + CLASSNAME + " successfully completed" );

		} catch ( Exception e ) {
			log.error( "\n\nTEST FAILED - Exception: " + e.getMessage() );
		}// try-catch
		
	}// main
	
	
	public void makeServiceCall() throws Exception {
		try {
			String trackingNum = getArgMap().get( Globals.GG_TRACKING_NUM_CMD_LINE_KEY );
			log.debug( "trackingNum: " + trackingNum );
			String downloadDir = SoapUtil.getProperty( Globals.DOWNLOAD_DIR__KEY );
			log.debug( "downloadDir: " + downloadDir );
			String appDownloadDir = downloadDir + "/" + trackingNum;
			File dir = new File( appDownloadDir );
			if ( !dir.exists() && !dir.mkdirs() ) {
				log.error( "Could not create dir: " + appDownloadDir );
				throw new Exception( "Could not create dir: " + appDownloadDir );
			}// if
			log.debug( "created dir: " + appDownloadDir );
			ApplicantWebServicesPortType port = getApplicantPort();
			GetApplicationZipRequest request = new GetApplicationZipRequest();
			request.setGrantsGovTrackingNumber( trackingNum );
			log.debug( "Make service call" );
			GetApplicationZipResponse response = port.getApplicationZip( request );
			log.debug( "Now Streaming Binary Data of the ZIP file to the Client from the Server");
			File f = new File( appDownloadDir + "/" + trackingNum + FILE_EXT);
		    StreamingDataHandler dh = (StreamingDataHandler) ( response.getFileDataHandler() );
		    log.debug( "dh.moveTo()" );
		    dh.moveTo( f );
		    log.debug( "dh.close()" );
		    dh.close();
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// makeServiceCall
	
	
}
