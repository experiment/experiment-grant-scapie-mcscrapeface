package gov.grants.apply.applicant.v2;

import java.util.List;
import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.GetOpportunitiesRequest;
import gov.grants.apply.services.applicantwebservices_v2.GetOpportunitiesResponse;
import gov.grants.apply.services.applicantwebservices_v2.GetOpportunitiesResponse.OpportunityInfo;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;



public class GetOpportunitiesClient extends BaseApplicantClient {

	private static final String CLASSNAME = GetOpportunitiesClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			GetOpportunitiesClient client = new GetOpportunitiesClient();
			
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
			String cfda = getArgMap().get( Globals.CFDA_CMD_LINE_KEY );
			log.debug( "cfda: " + cfda );
			
			String oppId = getArgMap().get( Globals.OPP_ID_CMD_LINE_KEY );
			log.debug( "opp ID: " + oppId );
			
			String compId = getArgMap().get( Globals.COMP_ID_CMD_LINE_KEY );
			log.debug( "comp ID: " + compId );
			
			ApplicantWebServicesPortType port = getApplicantPort();
			GetOpportunitiesRequest request = new GetOpportunitiesRequest();
			
			request.setFundingOpportunityNumber( oppId );
			request.setCFDANumber( cfda );
			request.setCompetitionID( compId );
			
			GetOpportunitiesResponse response = port.getOpportunities(request);
			List <OpportunityInfo> oppInfoList = response.getOpportunityInfo();
			if( oppInfoList != null && oppInfoList.size() > 0) {	
				StringBuilder sb = new StringBuilder();
				for ( OpportunityInfo info : oppInfoList ) {
					sb.append( "\n" );
					log.debug( ToStringBuilder.reflectionToString( info ) );
					/*
					 * "opportunityID", "opportunityTitle", "openingDate", "closingDate", "cfdaNumber", 
					 * "competitionID", "schemaURL", "instructionURL" (GetOpportunityList)
					 * "cfdaDescription", "offeringAgency", "agencyContact" (GetOpportunityListWithInfo)
					 * "isMultiProject" (GetOpportunities)
					 */
					sb.append( "\nOpportunity ID: " ).append( info.getFundingOpportunityNumber());
					byte[] bytes = StringUtils.defaultString(info.getFundingOpportunityTitle()).getBytes("UTF-8"); 
					sb.append( "\nOpportunity Title: " ).append( new String(bytes,"UTF-8") );
					sb.append( "\nOpening Date: " );
					log.debug("OpeningDate: "+info.getOpeningDate());
					if ( info.getOpeningDate() != null ) {
						sb.append( formatDate( info.getOpeningDate().toGregorianCalendar().getTime(), MMddyyyy ) );
					}// if
					
					sb.append( "\nClosing Date: " );
					log.debug("OpeningDate: "+info.getOpeningDate());
					if ( info.getClosingDate() != null ) {
						sb.append( formatDate( info.getClosingDate().toGregorianCalendar().getTime(), MMddyyyy ) );
					}// if
					
					sb.append( "\nCFDA Number: " ).append( info.getCFDANumber() );
					sb.append( "\nCompetition ID: " ).append( info.getCompetitionID() );
					sb.append( "\nSchema URL: " ).append( info.getSchemaURL() );
					sb.append( "\nInstruction URL: " ).append( info.getInstructionsURL() );
					bytes = StringUtils.defaultString(info.getCFDADescription()).getBytes("UTF-8"); 
					sb.append( "\nCFDA Description: " ).append( new String(bytes,"UTF-8") );
					sb.append( "\nOffering Agency: " ).append( info.getOfferingAgency() );
					bytes = StringUtils.defaultString(info.getAgencyContactInfo()).getBytes("UTF-8"); 
					sb.append( "\nAgency Contact: " ).append( new String(bytes,"UTF-8") );
					sb.append( "\nIs Multi-Project: " ).append( info.isIsMultiProject() );
					sb.append( "\n\n" );
					
					log.debug( sb );
					sb.setLength( 0 );
				}// for
			}
			log.debug( "opportunity count: " + oppInfoList.size() );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// makeServiceCall
	
}
