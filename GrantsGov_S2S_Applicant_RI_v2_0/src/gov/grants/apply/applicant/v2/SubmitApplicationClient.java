package gov.grants.apply.applicant.v2;

import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.SubmitApplicationRequest;
import gov.grants.apply.services.applicantwebservices_v2.SubmitApplicationResponse;
import gov.grants.apply.system.grantscommonelements_v1.Attachment;
import gov.grants.commons.CommonsGlobals;
import gov.grants.commons.util.FileUtil;

import java.io.File;

import javax.activation.DataHandler;
import javax.activation.FileDataSource;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ximpleware.VTDNav;

public class SubmitApplicationClient extends BaseApplicantClient {

	private static final String CLASSNAME = SubmitApplicationClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			SubmitApplicationClient client = new SubmitApplicationClient();
			
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
			String configXml = getArgMap().get( Globals.CONFIG_XML_CMD_LINE_KEY );
			loadConfigXmlDoc( configXml );
			configVN.toElement( VTDNav.FIRST_CHILD, CLASSNAME );// navigate to client element
			
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.SUBMISSION_DIR_ELEMENT_NAME );
			String submissionDir = configVN.toNormalizedString( configVN.getText() );
			log.debug( "submissionDir: " + submissionDir );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.SUBMISSION_XML_ELEMENT_NAME );
			String xmlFileName = configVN.toNormalizedString( configVN.getText() );
			log.debug( "xmlFileName: " + xmlFileName );
			
			if ( StringUtils.isBlank( submissionDir ) || StringUtils.isBlank( xmlFileName ) ) {
				String s = "submission-dir and submission-xml elements in the config xml cannot be blank";
				log.error( s );
				throw new Exception( s );
			}// if
			
			byte[] fileBytes = null;
			String fileName = null;
			String cid = null;
			String contentType = null;
			log.debug( "load attachment(s)" );
			SubmitApplicationRequest request = new SubmitApplicationRequest();
			File att = null;
			
			while ( configVN.toElement( VTDNav.NEXT_SIBLING, Globals.ATTACHMENT_ELEMENT_NAME ) ) {
				
				configVN.toElement( VTDNav.FIRST_CHILD, Globals.FILE_NAME_ELEMENT_NAME );
				fileName = configVN.toNormalizedString2( configVN.getText() );
				log.debug( "attachment file name: " + fileName );
				//log.debug( configVN.toNormalizedString2( configVN.getCurrentIndex() ) + ": " + fileName );
				
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
			
			// Get the ApplicantPort (MTOM Enabled).
			ApplicantWebServicesPortType port = super.getApplicantPort();
			// Load the GrantsApplication XML (The usual way no streaming).
			log.debug( "load submission xml" );
			fileBytes = FileUtil.readFile( submissionDir + xmlFileName );
			log.debug( "xml bytes: " + fileBytes.length );
			log.debug( "xml size: " + new String( fileBytes ).length() );
			request.setGrantApplicationXML( new String( fileBytes,CommonsGlobals.PREFERRED_ENCODING ) );
			// Execute the Web Service call.
			SubmitApplicationResponse response = port.submitApplication( request );
			
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
	

}
