package gov.grants.apply.applicant.v2;


import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationInfoRequest;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationInfoResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class GetApplicationInfoClient extends BaseApplicantClient {
	
	private static final String CLASSNAME = GetApplicationInfoClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			GetApplicationInfoClient client = new GetApplicationInfoClient();
			
			log.debug( "args length: " + args.length );
			client.init( args );
			
			client.makeServiceCall();
			log.debug( "\n\nSUCCESS: " + CLASSNAME + " successfully completed" );

		} catch ( Exception e ) {
			log.error( "\n\nException: " + e.getMessage() );
		}// try-catch
		
	}// main
	
	public void makeServiceCall() throws Exception {
		try {
			String ggTrackingNum = getArgMap().get( Globals.GG_TRACKING_NUM_CMD_LINE_KEY );
			if ( StringUtils.isBlank( ggTrackingNum ) ) {
				throw  new Exception( "gg_tracking_num is missing from command line" );
			}// if
			
			log.debug( "gg_tracking_num: " + ggTrackingNum );
			ApplicantWebServicesPortType port = getApplicantPort();
			log.debug( "port: " + port );
			GetApplicationInfoRequest request = new GetApplicationInfoRequest();
			request.setGrantsGovTrackingNumber( ggTrackingNum );
			
			log.debug( "make service call" );
			GetApplicationInfoResponse response = port.getApplicationInfo( request );
			
			log.debug( "Get application info response recieved is: " + response.toString());
			log.debug( "**********************");
			log.debug( "Tracking Number: " + response.getGrantsGovTrackingNumber());
			log.debug( "Status detail: " + response.getStatusDetail());
			log.debug( "Agency Notes: " + response.getAgencyNotes());
			log.debug( "**********************");
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// makeServiceCall
	
}
