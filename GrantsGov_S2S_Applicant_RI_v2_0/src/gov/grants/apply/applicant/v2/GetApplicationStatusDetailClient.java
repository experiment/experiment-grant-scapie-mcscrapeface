package gov.grants.apply.applicant.v2;

import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationStatusDetailRequest;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationStatusDetailResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;


public class GetApplicationStatusDetailClient extends BaseApplicantClient {

	private static final String CLASSNAME = GetApplicationStatusDetailClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			GetApplicationStatusDetailClient client = new GetApplicationStatusDetailClient();
			
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
			log.debug( "gg_tracking_num passed is: " + ggTrackingNum );
			
			ApplicantWebServicesPortType port = super.getApplicantPort();
			
			GetApplicationStatusDetailRequest request = new GetApplicationStatusDetailRequest();
			request.setGrantsGovTrackingNumber( ggTrackingNum );
			
			GetApplicationStatusDetailResponse response = port.getApplicationStatusDetail( request );
			
			String detail = response.getDetailedStatus();
			log.debug( "detail: " + detail );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// makeServiceCall
	

}
