package gov.grants.apply.applicant.v2;

import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationListRequest;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationListResponse;
import gov.grants.apply.services.applicantwebservices_v2.GetApplicationListResponse.ApplicationInfo;
import gov.grants.apply.system.grantscommonelements_v1.ApplicationFilter;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;


public class GetApplicationListClient extends BaseApplicantClient {
	
	private static final String CLASSNAME = GetApplicationListClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			GetApplicationListClient client = new GetApplicationListClient();
			
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
			ApplicantWebServicesPortType port = getApplicantPort();
			GetApplicationListRequest request = new GetApplicationListRequest();
			List<ApplicationFilter> filters = request.getApplicationFilter();
			ApplicationFilter filter = null;
			
			Set<String> argKeys = getArgMap().keySet();
			String filterName = null;
			String filterValue = null;
			
			for ( String key : argKeys ) {
				log.debug( "key: " + key );
				filterName = FILTER_NAMES.get( key.toLowerCase() );
				log.debug( "filter name: " + filterName );
				if ( StringUtils.isNotBlank( filterName ) ) {
					filterValue = getArgMap().get( key );
					
					if ( StringUtils.isNotBlank( filterValue ) ) {
						log.debug( "create filter[" + filterName + "]: " + filterValue );
						filter = new ApplicationFilter();
						filter.setFilter( filterName );
						filter.setFilterValue( filterValue );
						filters.add( filter );
					} else {
						log.error( "no value for filter name: " + filterName );
					}// if-else

				}// if
			}// for
			
			// verify filters before making service call
			log.debug( "filter count: " + request.getApplicationFilter().size() );
			for ( ApplicationFilter appFilter : request.getApplicationFilter() ) {
				log.debug( "\n-filter: " + ToStringBuilder.reflectionToString( appFilter ) );
			}// for
			
			GetApplicationListResponse response = port.getApplicationList(request);
			log.debug( "available appplication count: " + response.getAvailableApplicationNumber() );
			List <ApplicationInfo> list = response.getApplicationInfo();
			
			printList( list );
			printListConcise( list );
			
			log.debug( "Total Available Application Count: " + response.getAvailableApplicationNumber());
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// makeServiceCall
	
	public void printList( List<ApplicationInfo> list ) {
		if ( list == null || list.size() == 0 ) {
			log.debug( "Application list is empty." );
				return;
		}// if
	         
		log.debug( "Received " + list.size() + " applications in list" );
		for ( ApplicationInfo info : list ) {
			log.debug( "\n\n" );
			log.debug( ToStringBuilder.reflectionToString( info ) );
			log.debug( "\n\n" );
		}// for-each
	}// printList
	
	
	public void printListConcise( List<ApplicationInfo> list ) {
		if ( list == null || list.size() == 0 ) {
			log.debug( "Application list is empty." );
				return;
		}// if
	         
		log.debug( "Received " + list.size() + " applications in list" );
		StringBuilder sb = new StringBuilder().append( "\n" );
		int count = 0;
		for ( ApplicationInfo info : list ) {
			log.debug( "info: " + ToStringBuilder.reflectionToString( info ) );
			count++;
			sb.append( "Application list element " )
				.append( count )
				.append( ": " )
				.append( info.getGrantsGovTrackingNumber() )
				.append( "--- CFDA: " )
				.append( info.getCFDANumber() )
				.append( "--- Status: " )
				.append( info.getGrantsGovApplicationStatus() )
				.append( "\n" );
		}// for-each
			
		log.debug( sb.toString() );
	}// printListConcise
	
	
}
