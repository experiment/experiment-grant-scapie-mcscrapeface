package gov.grants.apply.applicant.v2;

import java.io.ByteArrayOutputStream;

import gov.grants.business.exception.GrantsBusinessException;
import gov.grants.business.util.CommonConstants;
import gov.grants.business.vo.AttachmentDetails;
import gov.grants.business.vo.HeaderInfoVO;
import gov.grants.commons.util.FileUtil;
import gov.grants.commons.util.XMLHashUtil;
import gov.grants.webservice.bizobj.SubmissionXml;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;

public class PrepareSubmissionXml extends BaseApplicantClient {

	private static final String CLASSNAME = PrepareSubmissionXml.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			PrepareSubmissionXml client = new PrepareSubmissionXml();
			
//			args = new String[2];
//			args[0] = "client=SubmitApplicationClient";
//			args[1] = "config_xml=../Config/grantsws-applicant-config.xml";
			
			log.debug( "args length: " + args.length );
			client.init( args );
			
			client.prepareXml();
			log.debug( "\n\nSUCCESS: " + CLASSNAME + " successfully completed" );

		} catch ( Exception e ) {
			log.error( "\n\nException: " + e.getMessage() );
		}// try-catch
		
	}// main
	
	
	public void prepareXml() throws Exception {
		try {
			String clientName = getArgMap().get( Globals.CLIENT_CMD_LINE_KEY );
			log.debug( "client element name: " + clientName );
			
			String configXml = getArgMap().get( Globals.CONFIG_XML_CMD_LINE_KEY );
			loadConfigXmlDoc( configXml );
			log.debug( "find element: " + clientName );
			configVN.toElement( VTDNav.FIRST_CHILD, clientName );// navigate to client element
			
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.SUBMISSION_DIR_ELEMENT_NAME );
			String submissionDir = configVN.toNormalizedString( configVN.getText() );
			log.debug( "submissionDir: " + submissionDir );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.SUBMISSION_XML_ELEMENT_NAME );
			String xmlFileName = configVN.toNormalizedString( configVN.getText() );
			log.debug( "xmlFileName: " + xmlFileName );
			
			byte[] fileBytes = null;
			
			log.debug( "load submission xml" );
			fileBytes = FileUtil.readFile( submissionDir + xmlFileName );
			log.debug( "xml bytes: " + fileBytes.length );
			log.debug( "xml size: " + new String( fileBytes ).length() );
			backupXml( fileBytes, submissionDir + xmlFileName );
			SubmissionXml subXml = new SubmissionXml( fileBytes );
			
			VTDNav vn = subXml.getVTDNav();
			XMLModifier xm = new XMLModifier();
			log.debug( "bind to VTDNav" );
			xm.bind( vn );
			log.debug( "bound successfully" );

			String fileName = null;
			String attFilePath = null;
			String attHash = null;
			String cid = null;
			int index = 0;
			
			while ( configVN.toElement( VTDNav.NS, Globals.ATTACHMENT_ELEMENT_NAME ) ) {
				
				configVN.toElement( VTDNav.FIRST_CHILD, Globals.FILE_NAME_ELEMENT_NAME );
				fileName = configVN.toNormalizedString2( configVN.getText() );
				log.debug( "attachment file name: " + fileName );
//				log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + ": " + fileName );
				attFilePath = submissionDir + fileName;
				log.debug( "attFilePath: " + attFilePath );
				attHash = subXml.createAttHashValue( attFilePath );
				
				configVN.toElement( VTDNav.PARENT );
				configVN.toElement( VTDNav.FIRST_CHILD, Globals.CID_ELEMENT_NAME );
				cid = configVN.toRawString( configVN.getText() );
				log.debug( "cid: " + cid );
//				log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + ": " + cid );
//				index = getElementIndex( vn, cid );
				
				// move VTDNav cursor to FileLocation element for cid
				subXml.getFileLocationElementIndexByCid( cid );
				log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index: " + vn.getCurrentIndex()  );
				
				// navigate to HashValue element
				vn.toElement( VTDNav.PARENT );
				vn.toElementNS( VTDNav.FIRST_CHILD, CommonConstants.GLOBAL_NS_URI, AttachmentDetails.HASH_VALUE_ELEMENT_LOCAL_PART );
				log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index: " + vn.getCurrentIndex()  );
				
				/*
				 * - navigate to HashValue element value: vn.getText()
				 * - update token (current index value)
				 */
				xm.updateToken( vn.getText(), attHash );
				
				configVN.toElement( VTDNav.PARENT );
			}// while
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			xm.output( baos );
			byte[] xmlBytes = baos.toByteArray();
			
			String xmlHash = createSubmissionXmlHashValue( xmlBytes, subXml.isMultiProject() );
			
			// go to root
			vn.toElement( VTDNav.ROOT );
			if ( subXml.isMultiProject() ) {
				vn.toElement( VTDNav.FIRST_CHILD );// ApplicationHeader
			}// if
			
			int beginIndex = getElementIndex( vn, HeaderInfoVO.GRANT_SUBMISSION_HEADER_ELEMENT_LOCAL_PART, 0, 0 );
			log.debug( "begin index: " + beginIndex );
			
			int endIndex = getElementIndex( vn, SubmissionXml.APPLICATION_PACKAGE_ELEMENT_LOCAL_PART, beginIndex, 0 );
			log.debug( "end index: " + endIndex );
			
			String headerElement = vn.toNormalizedString( beginIndex );
			log.debug( "header element: " + headerElement );
			vn.toElement( VTDNav.FIRST_CHILD, headerElement );// GrantSubmissionHeader
			log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index: " + vn.getCurrentIndex()  );
			
			// HashValue
			index = getElementIndex( vn, HeaderInfoVO.HASH_VALUE_ELEMENT_LOCAL_PART, beginIndex, endIndex );
			log.debug( "hash element index: " + index );
			if ( index > -1 ) {
				vn.toElement( VTDNav.FIRST_CHILD, vn.toNormalizedString( index ) );
				log.debug( "hash: " + vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index: " + vn.getCurrentIndex()  );
				if ( vn.getText() > -1 ) { 
					log.debug( "update hash value: " + xmlHash );
					xm.updateToken( vn.getText(), xmlHash );
				}// if
				
			} else {
				String s = "HashValue element not found";
				log.error( s );
				throw new Exception( s );
			}// if-else
			
			log.debug( "output file: " + xmlFileName );
			xm.output( submissionDir + xmlFileName );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// prepareXml
	
	
	private String createSubmissionXmlHashValue( byte[] xmlBytes, boolean isMultiProject ) 
	throws GrantsBusinessException {
		try {
			String elementNsUri = null;
			String elementLocalPart = null;
			
			if ( isMultiProject ) {
				elementNsUri = SubmissionXml.META_MULTI_GRANT_APPLICATION_NS_URI;
				elementLocalPart = SubmissionXml.APPLICATION_PACKAGE_ELEMENT_LOCAL_PART; // ApplicationPackage
			} else {
				elementNsUri = SubmissionXml.META_GRANT_APPLICATION_NS_URI;
				elementLocalPart = SubmissionXml.FORMS_ELEMENT_LOCAL_PART; // Forms
			}// if-else
			
			String calculatedHash = XMLHashUtil.createXmlHash( xmlBytes, elementNsUri, elementLocalPart );
			
			log.debug( "returning calculated hash value: " + calculatedHash );
			
			return calculatedHash;
			
    	} catch ( Exception e ) {
    		String s = "Exception caught creating submission xml hash: " + e.getMessage();
    		log.error( s );
    		throw new GrantsBusinessException( s );
    	}// try-catch
		
	}// createSubmissionXmlHashValue
	
	
	private int getElementIndex( VTDNav vNav, String localPart, int beginIndex, int endIndex ) 
	throws Exception {
		try {
			if ( endIndex < 1 ) {
				endIndex = vNav.getTokenCount();
			}// if
			
			for ( int i = beginIndex; i < endIndex; i++ ) {
				String token = vNav.toNormalizedString( i );
				
//				if ( token.endsWith( localPart ) ) {
//					log.debug( "token: " + token + " @ index: " + i );
//					return i;
//				}// if
				
				if ( vNav.endsWith( i, localPart ) ) {
					log.debug( "token: " + token + " @ index: " + i );
					return i;
				}// if
				
			}// for
			
			return -1;
			
		} catch ( Exception e ) {
			String s = "Exception getting element index: " + e.getMessage();
			log.error( s );
			throw new GrantsBusinessException( s );
		}// try-catch
		
	}// getElementIndex
	
	
	private void backupXml( byte[] xmlBytes, String filePath ) throws Exception {
		try {
			if ( StringUtils.isBlank( filePath ) ) {
				String s = "File path cannot be blank";
				log.error( s );
				throw new Exception( s );
			}// if
			
			if ( xmlBytes == null || xmlBytes.length == 0 ) {
				String s = "XML is blank";
				log.error( s );
				throw new Exception( s );
			}// if
			
			int index = filePath.lastIndexOf( "." );
			String fileExt = filePath.substring( index );
			String filename = filePath.substring( 0, index );
			
			String backupFileName = filename + "_orig" + fileExt;
			log.debug( "backup file name: " + backupFileName );
			
			FileUtil.bytesToDisk( xmlBytes, backupFileName );
			
		} catch ( Exception e ) {
			String s = "Exception caught backing up XML to disk: " + e.getMessage();
			log.error( s );
			throw new Exception( s );
		}// try-catch
	}// backupXml
	

}
