package gov.grants.apply.applicant.v2;
import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.AuthenticateAORRequest;
import gov.grants.apply.services.applicantwebservices_v2.AuthenticateAORResponse;
import gov.grants.apply.services.applicantwebservices_v2.SubmitApplicationAsThirdPartyRequest;
import gov.grants.apply.services.applicantwebservices_v2.SubmitApplicationAsThirdPartyResponse;
import gov.grants.apply.system.grantscommonelements_v1.Attachment;
import gov.grants.apply.system.grantscommonelements_v1.Token;
import gov.grants.business.util.CommonConstants;
import gov.grants.commons.CommonsGlobals;
import gov.grants.commons.util.FileUtil;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.builder.ToStringBuilder;
import org.apache.log4j.Logger;

import com.ximpleware.VTDNav;

public class SubmitApplicationAsThirdPartyClient extends BaseApplicantClient {

	private static final String CLASSNAME = SubmitApplicationAsThirdPartyClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			SubmitApplicationAsThirdPartyClient client = new SubmitApplicationAsThirdPartyClient();
			
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
			
			// Get the ApplicantPort (MTOM Enabled).
			ApplicantWebServicesPortType port = super.getApplicantPort();
			
			String configXml = getArgMap().get( Globals.CONFIG_XML_CMD_LINE_KEY );
			loadConfigXmlDoc( configXml );
			configVN.toElement( VTDNav.FIRST_CHILD, CLASSNAME );// navigate to client element
			
			log.debug( "prepare authenticateAOR service call" );
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.AOR_USER_ID_ELEMENT_NAME );
			String aorUserId = configVN.toNormalizedString2( configVN.getText() );
			log.debug( "AOR user ID: " + aorUserId );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.AOR_PASSWORD_ELEMENT_NAME );
			String aorPassword = configVN.toNormalizedString2( configVN.getText() );
			log.debug( "AOR password: " + aorPassword );
			
			if ( StringUtils.isBlank( aorUserId ) || StringUtils.isBlank( aorPassword ) ) {
				String s = "aor-user-id and aor-password elements in the config xml cannot be blank";
				log.error( s );
				throw new Exception( s );
			}// if
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.SUBMISSION_DIR_ELEMENT_NAME );
			String submissionDir = configVN.toNormalizedString2( configVN.getText() );
			log.debug( "submission dir: " + submissionDir );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.SUBMISSION_XML_ELEMENT_NAME );
			String xmlFileName = configVN.toNormalizedString2( configVN.getText() );
			log.debug( "xmlFileName: " + xmlFileName );
			
			if ( StringUtils.isBlank( submissionDir ) || StringUtils.isBlank( xmlFileName ) ) {
				String s = "submission-dir and submission-xml elements in the config xml cannot be blank";
				log.error( s );
				throw new Exception( s );
			}// if
			
//			log.debug( "make AuthenticateAOR service call" );
//			AuthenticateAORResponseXml tokenXml = authenticateAOR( port, aorUserId, aorPassword );
//			String aorStatus = tokenXml.getToken().getAORStatus().toString();
//			log.debug( "AOR status: " + aorStatus );
//			if ( CommonConstants.AOR_STATUS_UNAUTHORIZED.equals( aorStatus ) ) {
//				log.error( "### aorUserId [" + aorUserId + "] is " + CommonConstants.AOR_STATUS_UNAUTHORIZED );
//			}// if
			
			log.debug( "make AuthenticateAOR service call" );
			Token token = authenticateAOR( port, aorUserId, aorPassword );
			String aorStatus = token.getAORStatus().toString();
			log.debug( "AOR status: " + aorStatus );
			if ( CommonConstants.AOR_STATUS_UNAUTHORIZED.equals( aorStatus ) ) {
				log.error( "### aorUserId [" + aorUserId + "] is " + CommonConstants.AOR_STATUS_UNAUTHORIZED);
			}// if
			
			log.debug( "prepare to submit application as third party service call" );
			SubmitApplicationAsThirdPartyRequest request = new SubmitApplicationAsThirdPartyRequest();
//			request.setGrantsGovTokenXML( tokenXml.xmlText( XMLBeansUtil.getXMLBeansXmlOptions( true ) ) );
			request.setToken( token);
			
			byte[] fileBytes = null;
			String fileName = null;
			String cid = null;
			String contentType = null;
			log.debug( "load attachment(s)" );
			File att = null;
			
			while ( configVN.toElement( VTDNav.NS, Globals.ATTACHMENT_ELEMENT_NAME ) ) {
				
				configVN.toElement( VTDNav.FIRST_CHILD, Globals.FILE_NAME_ELEMENT_NAME );
				fileName = configVN.toNormalizedString2( configVN.getText() );
				log.debug( "attachment file name: " + fileName );
				//log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + ": " + fileName );
				
				
				att = new File( submissionDir + fileName );
				log.debug( "try to load: " + att.getCanonicalPath() );
				if ( !att.exists() ) {
					log.warn( "File does not exist: " + att.getCanonicalPath() );
					att = new File( fileName );
					log.debug( "try to load: " + att.getCanonicalPath() );
					if ( !att.exists() ) {
						log.error( "File does not exist: " + att.getCanonicalPath() );
						throw new Exception( "File does not exist: " + att.getCanonicalPath() );
					}// if
				}// if

				contentType = new javax.activation.MimetypesFileTypeMap().getContentType( att );
				log.debug( "content type: " + contentType );
				
				configVN.toElement( VTDNav.PARENT );
				configVN.toElement( VTDNav.FIRST_CHILD, Globals.CID_ELEMENT_NAME );
				cid = configVN.toRawString( configVN.getText() );
				log.debug( "cid: " + cid );
				//log.debug( configVN.toNormalizedString2( configVN.getCurrentIndex() ) + ": " + cid );

				// Create Attachment Object for each Large File.
				Attachment attachment = new Attachment();
				// Create Data Handler for each file.
				attachment.setFileDataHandler(new DataHandler(new FileDataSource(submissionDir+fileName)));
				// Assign the CID
				attachment.setFileContentId(cid);
				// Add the Attachment to the List of Attachments to be streamed.
				request.getAttachment().add( attachment);
				configVN.toElement( VTDNav.PARENT );
				
			}// while
						
			// Load the GrantsApplication XML (The usual way no streaming).
			log.debug( "load submission xml" );
			fileBytes = FileUtil.readFile( submissionDir + xmlFileName );
			log.debug( "xml bytes: " + fileBytes.length );
			log.debug( "xml size: " + new String( fileBytes ).length() );
			request.setApplicationXML( new String( fileBytes,CommonsGlobals.PREFERRED_ENCODING ) );
			SubmitApplicationAsThirdPartyResponse response = port.submitApplicationAsThirdParty( request );
			String ggTrackingNum = response.getGrantsGovTrackingNumber();
			String receivedDateTime = formatDate( response.getReceivedDateTime().toGregorianCalendar().getTime(), yyyyMMddHHmmss );
			StringBuilder sb = new StringBuilder();
			sb.append( "\n\nGG Tracking Number: " ).append( ggTrackingNum );
			sb.append( "\nReceived Date/Time: " ).append( receivedDateTime );
			sb.append( "\n\n" );
			log.debug( sb );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// makeServiceCall
	
	
	private Token authenticateAOR( 
			ApplicantWebServicesPortType port, 
			String aorUserId, 
			String aorPassword ) 
	throws Exception {
		
		Token token = null;
		try {
			
			AuthenticateAORRequest request = new AuthenticateAORRequest();
			request.setAORUserID( aorUserId );
			request.setAORPassword( aorPassword );
			
			AuthenticateAORResponse response = port.authenticateAOR( request );
			token = response.getToken();
		} catch ( Exception e ) {
			String s = "Exception caught authenticating AOR [" + aorUserId + "]: " + e.getMessage();
			log.error( s );
			throw new Exception( s );
		}// try-catch
		
		log.debug( "returning token: " + ToStringBuilder.reflectionToString( token ) );
		return token;
		
	}// authenticateAOR
	

}
