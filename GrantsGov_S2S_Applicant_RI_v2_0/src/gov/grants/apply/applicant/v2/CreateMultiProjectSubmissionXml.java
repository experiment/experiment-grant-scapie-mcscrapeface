package gov.grants.apply.applicant.v2;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import gov.grants.business.exception.GrantsBusinessException;
import gov.grants.business.vo.AttachmentDetails;
import gov.grants.business.vo.HeaderInfoVO;
import gov.grants.commons.s2s.util.GrantApplicationHash;
import gov.grants.commons.util.FileUtil;
import gov.grants.commons.util.XMLHashUtil;
import gov.grants.webservice.bizobj.SubmissionXml;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ximpleware.AutoPilot;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;
import com.ximpleware.XMLModifier;

public class CreateMultiProjectSubmissionXml extends BaseApplicantClient {

	private static final String CLASSNAME = CreateMultiProjectSubmissionXml.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	private static final String MP_SCHEMA_NS_PREFIX = "mpgrant";
	private static final String MP_SCHEMA_NS_URI = "http://apply.grants.gov/system/MetaMultiGrantApplication";
	
	private static final String SCHEMA_VERSION_ATTR = " glob:schemaVersion=\"1.0\"";
//	private static final String HEADER_NS_PREFIX = "header";
//	private static final String HEADER_NS_URI = "http://apply.grants.gov/system/Header-V1.0";
	
	private static final String GRANT_SUBMISSION_HEADER_ELEMENT_LOCAL_PART = "GrantSubmissionHeader";
	
	private static final String APPLICATION_HEADER_ELEMENT_LOCAL_PART = "ApplicationHeader";
	private static final String APPLICATION_HEADER_ELEMENT_NAME = MP_SCHEMA_NS_PREFIX + ":" + APPLICATION_HEADER_ELEMENT_LOCAL_PART;
	private static final String APPLICATION_PKG_ELEMENT_LOCAL_PART = "ApplicationPackage";
	private static final String APPLICATION_PKG_ELEMENT_NAME = MP_SCHEMA_NS_PREFIX + ":" + APPLICATION_PKG_ELEMENT_LOCAL_PART;
	
//	private static final String OVERALL_APPLICATION_ELEMENT_NAME = "OverallApplication";
	private static final String OVERALL_APPLICATION_HEADER_ELEMENT_LOCAL_PART = "OverallApplicationHeader";
//	private static final String OVERALL_APPLICATION_ID_ELEMENT_NAME = "OverallApplicationID";
	
	public static final String SUB_APPLICATION_GROUP_ELEMENT_LOCAL_PART = "SubApplicationGroup";
	public static final String SUB_APPLICATION_GROUP_HEADER_ELEMENT_LOCAL_PART = "SubApplicationGroupHeader";
	public static final String SUB_APPLICATION_GROUP_ID_ELEMENT_LOCAL_PART = "SubApplicationGroupID";
	public static final String SUB_APPLICATION_ELEMENT_LOCAL_PART = "SubApplication";
	public static final String SUB_APPLICATION_HEADER_ELEMENT_LOCAL_PART = "SubApplicationHeader";
	public static final String SUB_APPLICATION_ID_ELEMENT_LOCAL_PART = "SubApplicationID";
	
	Map<String, String> defaultNsPrefixMap;
	
	/*
	 * namespace URL | schemaLocation
	 */
	private Map<String, String> schemaImportMap;
	
	/*
	 * namespace prefix | namespace URL
	 */
	private Map<String, String> schemaNsPrefixMap;
	
	
	private static boolean REMOVE_WHITESPACE_BETWEEN_TAGS = false;
	private static final String CID_PREFIX = "cid:";
	private int cidCount = 0;
	private Map<String, String> attCidFileNameMap = new HashMap<String, String>();
	
	/*
	 * VTDNav instances
	 */
	private VTDNav mpVN;// multi-project xml
	private VTDNav overallVN;// overall pkg
	private VTDNav sagVN;// sub-application group pkg
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			CreateMultiProjectSubmissionXml client = new CreateMultiProjectSubmissionXml();
			
			log.debug( "args length: " + args.length );
			client.init( args );
			
			client.createMPXml();
			log.debug( "\n\nSUCCESS: " + CLASSNAME + " successfully completed" );

		} catch ( Exception e ) {
			log.error( "\n\nException: " + e.getMessage() );
		}// try-catch
		
	}// main
	
	
	public void createMPXml() throws Exception {
		try {
			String configXml = getArgMap().get( Globals.CONFIG_XML_CMD_LINE_KEY );
			loadConfigXmlDoc( configXml );
			log.debug( "find element: " + CLASSNAME );
			configVN.toElement( VTDNav.FIRST_CHILD, CLASSNAME );// navigate to client element
			
			configVN.toElement( VTDNav.FIRST_CHILD, "compress-xml" );
			log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
			REMOVE_WHITESPACE_BETWEEN_TAGS = Boolean.parseBoolean( configVN.toNormalizedString( configVN.getText() ) );
			log.debug( "REMOVE_WHITESPACE_BETWEEN_TAGS: " + REMOVE_WHITESPACE_BETWEEN_TAGS );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, "mp-schema-url" );
			log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
			String mpSchemaUrl = configVN.toNormalizedString( configVN.getText() );
			log.debug( "mp schema url: " + mpSchemaUrl );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, "mp-template-dir" );
			log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
			String mpTemplateDir = configVN.toNormalizedString( configVN.getText() );
			log.debug( "mp template dir: " + mpTemplateDir );
			
			parseSchema( mpSchemaUrl, mpTemplateDir );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, "mp-template-xml" );
			log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
			String mpTemplateXml = configVN.toNormalizedString( configVN.getText() );
			log.debug( "mp template xml: " + mpTemplateXml );
			
			log.debug( "create mp VTDNav instance" );
			mpVN = loadXmlDoc( mpTemplateDir + mpTemplateXml );
			XMLModifier xmMP = new XMLModifier();
			log.debug( "bind to VTDNav" );
			xmMP.bind( mpVN );
			log.debug( "bound successfully" );
			log.debug( mpVN.toNormalizedString( mpVN.getCurrentIndex() ) + " @ index " + mpVN.getCurrentIndex() );
			
			// update root element
			xmMP.insertAttribute( addRootElementAttributes( mpSchemaUrl ) );
			
			log.debug( "load overall xml" );
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, "overall" );
			configVN.toElement( VTDNav.FIRST_CHILD, "overall-pkg-id" );
			log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
			String overallPkgId = configVN.toNormalizedString( configVN.getText() );
			log.debug( "overall pkg ID: " + overallPkgId );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, "submission-dir" );
			log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
			String overallSubDir = configVN.toNormalizedString( configVN.getText() );
			log.debug( "overall sub dir: " + overallSubDir );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, "submission-xml" );
			log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
			String overallSubXml = configVN.toNormalizedString( configVN.getText() );
			log.debug( "overall sub xml: " + overallSubXml );
			
			overallVN = loadXmlDoc( overallSubDir + overallSubXml );
			
			// add ApplicationHeader element
			xmMP.insertAfterHead( createApplicationHeaderElement( overallVN ) );
			commitMPXmlUpdates( xmMP );
			
			mpVN.toElement( VTDNav.FIRST_CHILD, APPLICATION_HEADER_ELEMENT_NAME );
			log.debug( "MP VTDNav pos:" + mpVN.toNormalizedString( mpVN.getCurrentIndex() ) + " @ index " + mpVN.getCurrentIndex() );
			
			// add ApplicationPackage element
			xmMP.insertAfterElement( createApplicationPackageElement() );
			commitMPXmlUpdates( xmMP );
			mpVN.toElement( VTDNav.FIRST_CHILD, APPLICATION_PKG_ELEMENT_NAME );
			log.debug( "MP VTDNav pos:" + mpVN.toNormalizedString( mpVN.getCurrentIndex() ) + " @ index " + mpVN.getCurrentIndex() );
			
			// add overall package element
			xmMP.insertAfterHead( createOverallApplicationElement( overallVN, overallPkgId, overallSubDir ) );
			commitMPXmlUpdates( xmMP );
//			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			xmMP.output( baos );
//			String mpXmlStr = baos.toByteArray();
			
			// add sub-project elements
			AutoPilot ap = new AutoPilot( configVN );
			String xpath = "//sub-application-group";
			ap.selectXPath( xpath );
			int i = -1;
			String groupName = null;
			String iterations = null;
			String submissionDir = null;
			String submissionXml = null;
			StringBuilder sapXml = new StringBuilder();
			int count = 0;
			while ( ( i = ap.evalXPath() ) > -1 ) {
				count++;
				log.debug( configVN.toNormalizedString( configVN.getCurrentIndex() ) + " @ index " + configVN.getCurrentIndex() );
				configVN.toElement( VTDNav.FIRST_CHILD, "group-name" );
				groupName = configVN.toNormalizedString( configVN.getText() );
				log.debug( "groupName: " + groupName );
				
				configVN.toElement( VTDNav.PARENT );
				configVN.toElement( VTDNav.FIRST_CHILD, "iterations" );
				iterations = configVN.toNormalizedString( configVN.getText() );
				log.debug( "iterations: " + iterations );
				
				configVN.toElement( VTDNav.PARENT );
				configVN.toElement( VTDNav.FIRST_CHILD, "submission-dir" );
				submissionDir = configVN.toNormalizedString( configVN.getText() );
				log.debug( "submissionDir: " + submissionDir );
				
				configVN.toElement( VTDNav.PARENT );
				configVN.toElement( VTDNav.FIRST_CHILD, "submission-xml" );
				submissionXml = configVN.toNormalizedString( configVN.getText() );
				log.debug( "submissionXml: " + submissionXml );
				
				sagVN = loadXmlDoc( submissionDir + submissionXml );
//				log.debug( "MP VTDNav pos:" + vnMP.toNormalizedString( vnMP.getCurrentIndex() ) + " @ index " + vnMP.getCurrentIndex() );
				sapXml.append( createSubApplicationElement( groupName, submissionDir, Integer.parseInt( iterations ) ) );
				
				configVN.toElement( VTDNav.PARENT );
				
//				if ( count == 1 ) {
//					break;
//				}// if
				
			}// while
			
			log.debug( "add sub-application elements to MP XML" );
			
			log.debug( "MP VTDNav pos:" + mpVN.toNormalizedString( mpVN.getCurrentIndex() ) + " @ index " + mpVN.getCurrentIndex() );
			mpVN.toElement( VTDNav.ROOT );
			mpVN.toElement( VTDNav.FIRST_CHILD, APPLICATION_PKG_ELEMENT_NAME );
			mpVN.toElement( VTDNav.FIRST_CHILD );// OverallApplication
			log.debug( "MP VTDNav pos:" + mpVN.toNormalizedString( mpVN.getCurrentIndex() ) + " @ index " + mpVN.getCurrentIndex() );
//			
//			FileUtil.bytesToDisk( sapXml.toString().getBytes(), mpTemplateDir + "sap.xml" );
//			long k = vnMP.getElementFragment();
//			xmMP.insertAfterElement( sapXml.toString().getBytes(), ( int ) k, ( int ) ( k >> 32 ) );
			xmMP.insertAfterElement( sapXml.toString() );
			
//			xmMP.insertAfterElement( "<testElement />" );
			
			log.debug( "commit changes to MP XML" );
			commitMPXmlUpdates( xmMP );
			mpVN = xmMP.outputAndReparse();
			xmMP.bind( mpVN );
			
			updateMPXmlHashValue( xmMP );
			
			log.debug( "===== write Multi-Project XML to disk =====" );
			xmMP.output( mpTemplateDir + "mpSubXml.xml" );
			
			generateConfig( mpTemplateDir, "mpSubXml.xml" );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// createMPXml
	
	
	/* ===== Private Method(s) ===== */
	
	private void generateConfig( String submissionDir, String xmlFileName ) throws Exception {
		try {
			StringBuilder attConfigXml = new StringBuilder();
			
			List<String> cidList = new ArrayList<String>( attCidFileNameMap.keySet() );
			Collections.sort( cidList );
			
			for ( String cid : cidList ) {
				attConfigXml.append( "\n\t\t")
					.append( "<attachment>" )
					.append( "\n\t\t\t" )
					.append( "<file-name>" ).append( attCidFileNameMap.get( cid ) ).append( "</file-name>" )
					.append( "\n\t\t\t" )
					.append( "<cid>" ).append( cid ).append( "</cid>" )
					.append( "\n\t\t" )
					.append( "</attachment>" );
			}// for
			
			StringBuilder submitAppXml = new StringBuilder();
			submitAppXml.append( "<generated-applicant-config>" )
				.append( "\n\t" )
				.append( "<SubmitApplicationClient>" )
				.append( "\n\t\t")
				.append( "<submission-dir>" ).append( submissionDir ).append( "</submission-dir>" )
				.append( "\n\t\t" )
				.append( "<submission-xml>" ).append( xmlFileName ).append( "</submission-xml>" )
				.append( attConfigXml )
				.append( "\n\t" )
				.append( "</SubmitApplicationClient>" );
			
			submitAppXml.append( "\n\n\t" )
				.append( "<SubmitApplicationAsThirdPartyClient>" )
				.append( "\n\t\t")
				.append( "<aor-user-id>" ).append( "aorUserId" ).append( "</aor-user-id>" )
				.append( "\n\t\t" )
				.append( "<aor-password>" ).append( "aorPassword" ).append( "</aor-password>" )
				.append( "\n\t\t" )
				.append( "<submission-dir>" ).append( submissionDir ).append( "</submission-dir>" )
				.append( "\n\t\t" )
				.append( "<submission-xml>" ).append( xmlFileName ).append( "</submission-xml>" )
				.append( attConfigXml )
				.append( "\n\t" )
				.append( "</SubmitApplicationAsThirdPartyClient>" )
				.append( "\n" )
				.append( "</generated-applicant-config>" );
			
			FileUtil.bytesToDisk( submitAppXml.toString().getBytes(), submissionDir + "generated-applicant-config.xml" );
			
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
			throw e;
		}// try-catch
		
	}// generateConfig
	
	
	
	private void updateMPXmlHashValue( XMLModifier xm ) throws Exception {
		try {
			SubmissionXml subXml = new SubmissionXml( mpVN );
			
			String updatedHash = subXml.createSubmissionXmlHashValue();
			log.debug( "updated xml hash value: " + updatedHash );
			
			String currentHash = subXml.getSubmittedXmlHashValue();
			log.debug( "submitted xml hash value: " + currentHash );
			
			// go to root
			mpVN.toElement( VTDNav.ROOT );
			if ( subXml.isMultiProject() ) {
				mpVN.toElement( VTDNav.FIRST_CHILD );// ApplicationHeader
			}// if
			
			int beginIndex = getElementIndex( mpVN, HeaderInfoVO.GRANT_SUBMISSION_HEADER_ELEMENT_LOCAL_PART, 0, 0 );
			log.debug( "begin index: " + beginIndex );
			
			int endIndex = getElementIndex( mpVN, SubmissionXml.APPLICATION_PACKAGE_ELEMENT_LOCAL_PART, beginIndex, 0 );
			log.debug( "end index: " + endIndex );
			
			String headerElement = mpVN.toNormalizedString( beginIndex );
			log.debug( "header element: " + headerElement );
			mpVN.toElement( VTDNav.FIRST_CHILD, headerElement );// GrantSubmissionHeader
			log.debug( mpVN.toNormalizedString( mpVN.getCurrentIndex() ) + " @ index: " + mpVN.getCurrentIndex()  );
			
			// HashValue
			int index = getElementIndex( mpVN, HeaderInfoVO.HASH_VALUE_ELEMENT_LOCAL_PART, beginIndex, endIndex );
			log.debug( "hash element index: " + index );
			if ( index > -1 ) {
				mpVN.toElement( VTDNav.FIRST_CHILD, mpVN.toNormalizedString( index ) );
				log.debug( "hash: " + mpVN.toNormalizedString( mpVN.getCurrentIndex() ) + " @ index: " + mpVN.getCurrentIndex()  );
				if ( mpVN.getText() > -1 ) { 
					log.debug( "update hash value: " + updatedHash );
					xm.updateToken( mpVN.getText(), updatedHash );
				}// if
				
			} else {
				String s = "HashValue element not found";
				log.error( s );
				throw new Exception( s );
			}// if-else
			
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
			throw e;
		}// try-catch
		
	}// updateMPXmlHashValue
	
	
	private String createSubApplicationElement( String pkgId, String submissionDir, int iterations ) {
		StringBuilder sagNsAttr = new StringBuilder();
		sagNsAttr.append( " xmlns:" )
			.append( pkgId )
			.append( "=\"" )
			.append( schemaNsPrefixMap.get( "xmlns:" + pkgId ) )
			.append( "\"" );
		
		StringBuilder sagElement = new StringBuilder();
		log.debug( "sub-application group ID: " + pkgId );
		
		try {
			// opening tag - SubApplicationGroup
			sagElement.append( "<" ).append( pkgId ).append( ":" )
				.append( SUB_APPLICATION_GROUP_ELEMENT_LOCAL_PART )
				.append( sagNsAttr )
				.append( ">" );
			
			// opening tag - SubApplicationGroupHeader
			sagElement.append( "<" ).append( pkgId ).append( ":" )
				.append( SUB_APPLICATION_GROUP_HEADER_ELEMENT_LOCAL_PART )
				.append( sagNsAttr )
				.append( ">" );
			
			// opening tag - SubApplicationGroupID
			sagElement.append( "<" ).append( pkgId ).append( ":" )
				.append( SUB_APPLICATION_GROUP_ID_ELEMENT_LOCAL_PART )
				.append( sagNsAttr )
				.append( ">" );
			// append pkg ID
			sagElement.append( pkgId );
			// closing tag - SubApplicationGroupID
			sagElement.append( "</" ).append( pkgId ).append( ":" )
				.append( SUB_APPLICATION_GROUP_ID_ELEMENT_LOCAL_PART ).append( ">" );
			
			// closing tag - SubApplicationGroupHeader
			sagElement.append( "</" ).append( pkgId ).append( ":" )
				.append( SUB_APPLICATION_GROUP_HEADER_ELEMENT_LOCAL_PART ).append( ">" );
			
			for ( int i = 1; i <= iterations; i++ ) {
				// opening tag - SubApplication
				sagElement.append( "<" ).append( pkgId ).append( ":" )
					.append( SUB_APPLICATION_ELEMENT_LOCAL_PART )
					.append( sagNsAttr )
					.append( ">" );
				
				// opening tag - SubApplicationHeader
				sagElement.append( "<" ).append( pkgId ).append( ":" )
					.append( SUB_APPLICATION_HEADER_ELEMENT_LOCAL_PART )
					.append( sagNsAttr )
					.append( ">" );
				
				// opening tag - SubApplicationID
				sagElement.append( "<" ).append( pkgId ).append( ":" )
					.append( SUB_APPLICATION_ID_ELEMENT_LOCAL_PART )
					.append( sagNsAttr )
					.append( ">" );
				
				// add sub application ID value
				sagElement.append( i );
				
				// closing tag - SubApplicationID
				sagElement.append( "</" ).append( pkgId ).append( ":" )
					.append( SUB_APPLICATION_ID_ELEMENT_LOCAL_PART ).append( ">" );
				
				// closing tag - SubApplicationHeader
				sagElement.append( "</" ).append( pkgId ).append( ":" )
					.append( SUB_APPLICATION_HEADER_ELEMENT_LOCAL_PART ).append( ">" );
				
				// add 'GrantApplication' element
				sagVN.toElement( VTDNav.ROOT );
				long k = sagVN.getElementFragment();
				String xml = sagVN.toRawString( ( int ) k, ( int )( k >> 32 ) );
				sagElement.append( modifySingleProjXml( xml, pkgId, submissionDir, REMOVE_WHITESPACE_BETWEEN_TAGS ) );
				
				// closing tag - SubApplication
				sagElement.append( "</" ).append( pkgId ).append( ":" )
					.append( SUB_APPLICATION_ELEMENT_LOCAL_PART ).append( ">" );
			
			}// for
			
			// closing tag - SubApplicationGroup
			sagElement.append( "</" ).append( pkgId ).append( ":" )
				.append( SUB_APPLICATION_GROUP_ELEMENT_LOCAL_PART ).append( ">" );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
		}// try-catch
		
//		log.debug( "returning element: " + sb );
		return sagElement.toString();
		
	}// createSubApplicationElement
	
	
	
	private String createOverallApplicationElement( VTDNav vnOverall, String overallPkgId, String submissionDir ) {
		StringBuilder overallNsAttr = new StringBuilder();
		overallNsAttr.append( " xmlns:" )
			.append( overallPkgId )
			.append( "=\"" )
			.append( schemaNsPrefixMap.get( "xmlns:" + overallPkgId ) )
			.append( "\"" );
		
		StringBuilder overallElement = new StringBuilder();
		
		try {
			// opening tag - OverallApplication
			overallElement.append( "<" ).append( overallPkgId ).append( ":" )
				.append( SubmissionXml.OVERALL_APPLICATION_ELEMENT_LOCAL_PART )
				.append( overallNsAttr )
				.append( ">" );
			
			// opening tag - OverallApplicationHeader
			overallElement.append( "<" ).append( overallPkgId ).append( ":" )
				.append( OVERALL_APPLICATION_HEADER_ELEMENT_LOCAL_PART )
				.append( overallNsAttr )
				.append( ">" );
			
			// opening tag - OverallApplicationID
			overallElement.append( "<" ).append( overallPkgId ).append( ":" )
				.append( SubmissionXml.OVERALL_APPLICATION_ID_ELEMENT_LOCAL_PART )
				.append( overallNsAttr )
				.append( ">" );
			
			// append overall pkg ID
			overallElement.append( overallPkgId );
			
			// closing tag - OverallApplicationID
			overallElement.append( "</" ).append( overallPkgId ).append( ":" )
				.append( SubmissionXml.OVERALL_APPLICATION_ID_ELEMENT_LOCAL_PART ).append( ">" );
			
			
			// closing tag - OverallApplicationHeader
			overallElement.append( "</" ).append( overallPkgId ).append( ":" )
				.append( OVERALL_APPLICATION_HEADER_ELEMENT_LOCAL_PART ).append( ">" );
			
			// add 'GrantApplication' element
			vnOverall.toElement( VTDNav.ROOT );
			long k = vnOverall.getElementFragment();
			String xml = vnOverall.toRawString( ( int ) k, ( int )( k >> 32 ) );
			overallElement.append( modifySingleProjXml( xml, overallPkgId, submissionDir, REMOVE_WHITESPACE_BETWEEN_TAGS ) );
			
			// closing tag - OverallApplication
			overallElement.append( "</" ).append( overallPkgId ).append( ":" )
				.append( SubmissionXml.OVERALL_APPLICATION_ELEMENT_LOCAL_PART ).append( ">" );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
		}// try-catch
		
		return overallElement.toString();
		
	}// createOverallApplicationElement
	
	
	
	private String modifySingleProjXml( 
			String spXml, 
			String updatedNsPrefix, 
			String submissionDir, 
			boolean removeWhitespaceBetweenTags ) 
	throws Exception {
		String updatedXml = null;
		try {
			
//			log.debug( "set new ns prefix: " + updatedNsPrefix + " to GrantApplication and Forms elements" );
//			int begin = spXml.indexOf( "<" );
//			int end = spXml.indexOf( ">" );
//			String rootElement = spXml.substring( begin + 1, end );
//			log.debug( "rootElement: " + rootElement );
//			String currentNsPrefix = rootElement.substring( 0, rootElement.indexOf( ":" ) );
//			log.debug( "current nsPrefix: " + currentNsPrefix );
			
			VTDGen vg = new VTDGen();
			vg.setDoc( spXml.getBytes() );
			/*
			 * set namespace aware to 'true' for XPath to work when searching for attachment elements.
			 * If set to false, XPath will not return any elements.
			 */
			vg.parse( true );
			VTDNav vn = vg.getNav();
			XMLModifier xm = new XMLModifier();
			xm.bind( vn );
			log.debug( "updated XML successfully parsed and bound" );
			log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
			
			// remove GrantSubmissionHeader element
			vn.toElement( VTDNav.FIRST_CHILD );// GrantSubmissionHeader
			if ( !vn.endsWith( vn.getCurrentIndex(), GRANT_SUBMISSION_HEADER_ELEMENT_LOCAL_PART ) ) {
				throw new Exception( "GrantSubmissionHeader element is not found" );
			}// if
			
			log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
			xm.remove();
			vn = xm.outputAndReparse();// commit updates
			xm.bind( vn );// re-bind
			
			/*
			 * Find all FileLocation elements and update CIDs
			 */
			modifyAttachmentInfo( vn, xm, submissionDir );
//			vn = xm.outputAndReparse();// commit updates
//			xm.bind( vn );// re-bind
			
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
//			xm.output( submissionDir + "modifiedSpXml.xml" );
			xm.output( baos );
			updatedXml = new String( baos.toByteArray() );
			
			vg.setDoc( updatedXml.getBytes() );
			/*
			 * set namespace aware to 'false' to allow element ns prefix changes.
			 * If set to true, will throw exception if ns prefix is not defined.
			 */
			vg.parse( false );
			vn = vg.getNav();
			xm.bind( vn );
			
			vn.toElement( VTDNav.ROOT );
			log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
			
			String rootElementName = vn.toNormalizedString( vn.getCurrentIndex() );
			log.debug( "rootElementName: " + rootElementName );
			String rootNsPrefix = rootElementName.substring( 0, rootElementName.indexOf( ":" ) );
			log.debug( "rootNsPrefix: " + rootNsPrefix );
			
			int beginIndex = vn.getCurrentIndex() + 1;// skip element name
			vn.toElement( VTDNav.FIRST_CHILD );
			int endIndex = vn.getCurrentIndex();
			vn.toElement( VTDNav.PARENT );
			Map<String, String> attrMap = getAttributeMap( vn, beginIndex, endIndex );

			// update 'Forms' ns prefix
			vn.toElement( VTDNav.ROOT );
			vn.toElement( VTDNav.FIRST_CHILD );// 'Forms' element
			log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
			String formsElementName = vn.toNormalizedString( vn.getCurrentIndex() );
			log.debug( "formsElementName: " + formsElementName );
			String updatedFormsElementName = updatedNsPrefix + formsElementName.substring( formsElementName.indexOf( ":" ) );
			log.debug( "updatedFormsElementName: " + updatedFormsElementName );
			xm.updateElementName( updatedFormsElementName );
			vn = xm.outputAndReparse();// commit updates
			xm.bind( vn );// re-bind
			
			/*
			 * Basically, replace 'GrantApplication' element to remove 'schemaLocation', 
			 * current ns prefix, 'xsi', etc... attributes from GrantApplication element.
			 */
			vn.toElement( VTDNav.ROOT );
			vn.toElement( VTDNav.FIRST_CHILD );// 'Forms' element
			log.debug( "extract 'Forms' element: " + vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
			long k = vn.getElementFragment();
			String formsElement = vn.toRawString( ( int ) k, ( int ) ( k >> 32 ) );
//			log.debug( "forms element: " + formsElement );
			if ( StringUtils.isBlank( formsElement ) ) {
				String s = "Forms element is blank";
				log.error( s );
				throw new Exception( s );
			}// if
			
			StringBuilder sb = new StringBuilder();
			sb.append( "<" )
				.append( updatedNsPrefix )
				.append( ":" )
				.append( SubmissionXml.GRANT_APPLICATION_ELEMENT_LOCAL_PART );
			
			Set<String> attrNames = attrMap.keySet();
			for ( String attrName : attrNames ) {
				log.debug( "attr name: " + attrName );
				if ( !attrName.endsWith( "schemaLocation" ) && !attrName.endsWith( "xsi" ) && !attrName.endsWith( rootNsPrefix ) ) {
					sb.append( " " )
						.append( attrName )
						.append( "=\"" )
						.append( attrMap.get( attrName ) )
						.append( "\"" );
				}// if
			}// for
			
			// add updated ns prefix namespace declaration
			sb.append( " xmlns:" )
				.append( updatedNsPrefix )
				.append( "=\"" )
				.append( schemaNsPrefixMap.get( "xmlns:" + updatedNsPrefix ) )
				.append( "\"" );
			
			// close opening GrantApplication tag
			sb.append( ">");
			
			// add 'Forms' element
			sb.append( formsElement );
			
			// add closing GrantApplication tag
			sb.append( "</" )
				.append( updatedNsPrefix )
				.append( ":" )
				.append( SubmissionXml.GRANT_APPLICATION_ELEMENT_LOCAL_PART )
				.append( ">" );
			
			
//			log.debug( "updated xml: " + sb );
			updatedXml = sb.toString();
			
//			spXml = spXml.replaceAll( nsPrefix + ":", SubmissionXml.META_GRANT_APPLICATION_NS_PREFIX + ":" );
//			updatedXml = spXml.replaceAll( currentNsPrefix + ":", updatedNsPrefix + ":" );
//			log.debug( "updated xml: " + updatedXml );
			
			// remove XML formatting
			if ( removeWhitespaceBetweenTags ) {
				String regex = ">[\\s]+<";
				updatedXml = updatedXml.replaceAll( regex, "><" );
			}// if
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
			throw e;
		}// try-catch
		
		return updatedXml;
		
	}// modifySingleProjXml
	
	
	private void modifyAttachmentInfo( VTDNav vn, XMLModifier xm, String attachmentDir ) throws Exception {
		log.debug( "modify attachment info" );
		try {
			AutoPilot ap = new AutoPilot( vn );
			addAllNamespaces( ap, vn );
			String xpath = "//" + AttachmentDetails.FILE_LOCATION_ELEMENT;
			ap.selectXPath( xpath );
			
			int count = 0;
			int i = -1;
//			int elementIndex = -1;
			String currentCid = null;
			String updatedCid = null;
			String fileName = null;
			String currentAttHash = null;
			String updatedAttHash = null;
			while ( ( i = ap.evalXPath() ) > -1 ) {
				cidCount++;
				log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
				currentCid = vn.toNormalizedString( vn.getAttrVal( AttachmentDetails.CID_ATTRIBUTE ) );
				log.debug( "current cid: " + currentCid );
				// set updated CID
				updatedCid = CID_PREFIX + StringUtils.leftPad( String.valueOf( cidCount ), 4, "0" );
				log.debug( "updated cid: " + updatedCid );
				xm.updateToken( vn.getAttrVal( AttachmentDetails.CID_ATTRIBUTE ), updatedCid );
				
				vn.toElement( VTDNav.PARENT );
				vn.toElement( VTDNav.FIRST_CHILD, AttachmentDetails.FILENAME_ELEMENT );
				fileName = vn.toNormalizedString( vn.getText() );
				log.debug( "file name: " + fileName );
				
				attCidFileNameMap.put( updatedCid, attachmentDir + fileName );
				
				vn.toElement( VTDNav.PARENT );
				vn.toElement( VTDNav.FIRST_CHILD, AttachmentDetails.HASH_VALUE_ELEMENT );
				currentAttHash = vn.toNormalizedString( vn.getText() );
				log.debug( "current att hash: " + currentAttHash );
				// set updated att hash
				updatedAttHash = createAttHashValue( attachmentDir + fileName );
//				log.debug( "updated att hash: " + updatedAttHash );
				xm.updateToken( vn.getText(), updatedAttHash );
				
			}// while
			
			log.debug( "attCidFileNameMap: " + attCidFileNameMap );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
			throw e;
		}// try-catch
	}
	
	
	private void parseSchema( String schemaUrl, String outputDir ) throws Exception {
		try {
			URL url = new URL( schemaUrl );
			log.debug( "schema URL: " + url );
			InputStream in = url.openStream();
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			final int BUF_SIZE = 1024;
			byte[] buffer = new byte[BUF_SIZE];
			int bytesRead = -1;
			while( ( bytesRead = in.read( buffer ) ) > -1 ) {
				out.write( buffer, 0, bytesRead );
			}// while
			in.close();
			byte[] xsdBytes = out.toByteArray();
			FileUtil.bytesToDisk( xsdBytes, outputDir + "mpSchema.xsd" );
			
			VTDGen vg = new VTDGen();
			vg.setDoc_BR( xsdBytes );
			vg.parse( true );
			VTDNav vn = vg.getNav();
//			log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
			
			vn.toElement( VTDNav.ROOT );
			String nsUrl = null;
			String schemaLocation = null;
			
			// parse imports
			schemaImportMap = new HashMap<String, String>();
			
			String importElement = vn.toNormalizedString( getElementIndex( vn, "import", vn.getCurrentIndex(), -1 ) );
			log.debug( "importElement: " + importElement );
			AutoPilot ap = new AutoPilot( vn );
			addAllNamespaces( ap, vn );
			String xpath = "//" + importElement;
			ap.selectXPath( xpath );
			int i = -1;
			while ( ( i = ap.evalXPath() ) > -1 ) {
				log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
				nsUrl = vn.toNormalizedString( vn.getAttrVal( "namespace" ) );
				schemaLocation = vn.toNormalizedString( vn.getAttrVal( "schemaLocation" ) );
				log.debug( nsUrl + " :: " + schemaLocation );
				schemaImportMap.put( nsUrl, schemaLocation );
			}// while
			
//			vn.toElement( VTDNav.ROOT );
//			vn.toElement( VTDNav.FIRST_CHILD );
//			log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
//			nsUrl = vn.toNormalizedString( vn.getAttrVal( "namespace" ) );
//			schemaLocation = vn.toNormalizedString( vn.getAttrVal( "schemaLocation" ) );
//			log.debug( nsUrl + " :: " + schemaLocation );
//			schemaImportMap.put( nsUrl, schemaLocation );
//			
//			while ( vn.toElement( VTDNav.NEXT_SIBLING ) && vn.endsWith( vn.getCurrentIndex(), "import" ) ) {
//				log.debug( vn.toNormalizedString( vn.getCurrentIndex() ) + " @ index " + vn.getCurrentIndex() );
//				nsUrl = vn.toNormalizedString( vn.getAttrVal( "namespace" ) );
//				schemaLocation = vn.toNormalizedString( vn.getAttrVal( "schemaLocation" ) );
//				log.debug( nsUrl + " :: " + schemaLocation );
//				schemaImportMap.put( nsUrl, schemaLocation );
//			}// while
			
			log.debug( "schemaImportMap: " + schemaImportMap );
			
			// initialize schemaNsPrefixMap
			schemaNsPrefixMap = new HashMap<String, String>();
			
			vn.toElement( VTDNav.ROOT );
			int beginIndex = vn.getCurrentIndex() + 1;// skip element name
			vn.toElement( VTDNav.FIRST_CHILD );
			int endIndex = vn.getCurrentIndex();
			
			int attrCount = vn.getAttrCount();
			log.debug( "schema attribute count: " + attrCount );
			Map<String, String> m = getAttributeMap( vn, beginIndex, endIndex );
			Set<String> nsPrefixes = m.keySet();
			String nsPrefixUrl = null;
			for ( String nsPrefix : nsPrefixes ) {
				nsUrl = m.get( nsPrefix );
//				log.debug( "ns url: " + nsUrl );
				nsPrefixUrl = schemaImportMap.get( nsUrl );
//				log.debug( "ns prefix url: " + nsPrefixUrl );
				if ( StringUtils.isNotBlank( nsPrefixUrl ) ) {
					log.debug( "adding: " + nsPrefix + " :: " + nsPrefixUrl );
					schemaNsPrefixMap.put( nsPrefix, nsUrl );
				}// if
			}// for-each
			log.debug( "nsPrefixMap: " + schemaNsPrefixMap );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
			throw e;
		}// try-catch
		
	}// parseSchema
	
	
	public Map<String, String> getAttributeMap( VTDNav vn, int beginIndex, int endIndex ) {
		log.debug( "beginIndex: " + beginIndex );
		log.debug( "endIndex: " + endIndex );
		Map<String, String> m = new HashMap<String, String>();
		try {
			String nsPrefix = null;
			String nsUrl = null;
			
			for ( int i = beginIndex; i <  endIndex; i++ ) {
				String token = vn.toNormalizedString( i );
				log.debug( "token: " + token );
//				if ( token.startsWith( "xmlns:" ) ) {
//				if ( vn.startsWith( i, SubmissionXml.XMLNS_PREFIX ) ) {
//				if ( token.indexOf( "=" ) > -1 ) {
//					nsPrefix = token.substring( token.indexOf( ":" ) + 1 );
					nsPrefix = token;
					i++;
					nsUrl = vn.toNormalizedString( i );
//					log.debug( "add ns: " + nsPrefix + "=" + nsUrl );
					m.put( nsPrefix, nsUrl );
//				}// if
				
			}// for
			
		} catch ( Exception e ) {
			log.error( "Exception getting attributes: " + e.getMessage() );
		}// try-catch
		log.debug( "returning map: " + m );
		return m;
	}// getAttributeMap
	
	
	public void addAllNamespaces( AutoPilot ap, VTDNav vn ) {
		try {
			int size = vn.getTokenCount();
			
			String nsPrefix = null;
			String nsUrl = null;
			
			for ( int i = 0; i < size; i++ ) {
				String token = vn.toNormalizedString( i );
				
//				if ( token.startsWith( "xmlns:" ) ) {
				if ( vn.startsWith( i, SubmissionXml.XMLNS_PREFIX ) ) {
					nsPrefix = token.substring( token.indexOf( ":" ) + 1 );
					nsUrl = vn.toNormalizedString( i + 1 );
//					log.debug( "add ns: " + nsPrefix + "=" + nsUrl );
					ap.declareXPathNameSpace( nsPrefix, nsUrl );
					
				}// if
				
//			    log.debug("token count => "+i);
//			    log.debug("token type =>" + vNav.getTokenType(i));
//			    log.debug("token offset => "+ vNav.getTokenOffset(i));
//			    log.debug("token length => "+ vNav.getTokenLength(i));
			}// for
			
		} catch ( Exception e ) {
			log.error( "Cannot add all namespaces: " + e.getMessage() );
		}// try-catch
		
	}// getAllNamespaces
	
	
	private void commitMPXmlUpdates( XMLModifier xm ) throws Exception {
		try {
			mpVN = xm.outputAndReparse();// commit updates
			xm.bind( mpVN );// re-bind XMLModifier
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
			throw e;
		}// try-catch
		
	}// commitMPXmlUpdates
	
	
	private void commitSAPXmlUpdates( XMLModifier xm ) throws Exception {
		try {
			sagVN = xm.outputAndReparse();// commit updates
			xm.bind( sagVN );// re-bind XMLModifier
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
			throw e;
		}// try-catch
		
	}// commitSAPXmlUpdates
	
	
	
	private String createApplicationPackageElement() {
		StringBuilder sb = new StringBuilder();
		
		try {
			// opening tag
			sb.append( "<" )
			.append( MP_SCHEMA_NS_PREFIX )
			.append( ":" )
			.append( APPLICATION_PKG_ELEMENT_LOCAL_PART )
			.append( ">" );
			
			// closing tag
			sb.append( "</" )
			.append( MP_SCHEMA_NS_PREFIX )
			.append( ":" )
			.append( APPLICATION_PKG_ELEMENT_LOCAL_PART )
			.append( ">" );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
		}// try-catch
		
		return sb.toString();
		
	}// createApplicationPackageElement
	
	
	private String createApplicationHeaderElement( VTDNav vnOverall ) {
		StringBuilder sb = new StringBuilder();
		
		try {
			// opening tag
			sb.append( "<" )
				.append( MP_SCHEMA_NS_PREFIX )
				.append( ":" )
				.append( APPLICATION_HEADER_ELEMENT_LOCAL_PART )
				.append( SCHEMA_VERSION_ATTR )
				.append( ">" );
			
			SubmissionXml subXml = new SubmissionXml( vnOverall );
			String headerXml = subXml.getHeaderXml( REMOVE_WHITESPACE_BETWEEN_TAGS );
			log.debug( "header xml: " + headerXml );
			sb.append( headerXml );
			
			// closing tag
			sb.append( "</" )
			.append( MP_SCHEMA_NS_PREFIX )
			.append( ":" )
			.append( APPLICATION_HEADER_ELEMENT_LOCAL_PART )
			.append( ">" );
			
		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
		}// try-catch
		
		return sb.toString();
		
	}// createApplicationHeaderElement
	
	
	private String addRootElementAttributes( String mpSchemaUrl ) {
		
		if ( defaultNsPrefixMap == null ) {
			log.debug( "initialize defaultNsPrefixMap" );
			
			defaultNsPrefixMap = new HashMap<String, String>();
			defaultNsPrefixMap.put( "xmlns:" + MP_SCHEMA_NS_PREFIX, MP_SCHEMA_NS_URI );
//			defaultNsPrefixMap.put( "xmlns:glob", "http://apply.grants.gov/system/Global-V1.0" );
//			defaultNsPrefixMap.put( "xmlns:globLib", "http://apply.grants.gov/system/GlobalLibrary-V1.0" );
//			defaultNsPrefixMap.put( "xmlns:att", "http://apply.grants.gov/system/Attachments-V1.0" );
//			defaultNsPrefixMap.put( "xmlns:header", "http://apply.grants.gov/system/Header-V1.0" );
//			defaultNsPrefixMap.put( "xmlns:footer", "http://apply.grants.gov/system/Footer-V1.0" );
			
			// for testing purposes
//			defaultNsPrefixMap.put( "xmlns:core", "http://apply.grants.gov/system/MetaMultiGrantApplication/core" );
//			defaultNsPrefixMap.put( "xmlns:project", "http://apply.grants.gov/system/MetaMultiGrantApplication/project" );
//			defaultNsPrefixMap.put( "xmlns:Admin", "http://apply.grants.gov/system/MetaMultiGrantApplication/Admin" );
			
//			nsPrefixMap.putAll( defaultNsPrefixMap );
		}// if
		
		
		/*
		 * Need to create StringBuilder to avoid the error:
		 * 		There can be only one insert per offset
		 */
		StringBuilder sb = new StringBuilder();
		try {
			// set default namespace prefixes
			Set<String> nsPrefixes = defaultNsPrefixMap.keySet();
			for ( String nsPrefix : nsPrefixes ) {
				sb.append( " " )
					.append( nsPrefix )
					.append( "=\"" )
					.append( defaultNsPrefixMap.get( nsPrefix ) )
					.append( "\"" );
			}// for-each
			
			// schema namespace prefixes
			nsPrefixes = schemaNsPrefixMap.keySet();
			for ( String nsPrefix : nsPrefixes ) {
				if ( !defaultNsPrefixMap.containsKey( nsPrefix ) ) {
					sb.append( " " )
						.append( nsPrefix )
						.append( "=\"" )
						.append( schemaNsPrefixMap.get( nsPrefix ) )
						.append( "\"" );
				} else {
					log.debug( "skipping: " + nsPrefix + " :: " + schemaNsPrefixMap.get( nsPrefix ) );
				}
			}// for-each
			
			sb.append( " xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"" );
			sb.append( createSchemaLocationAttr( MP_SCHEMA_NS_URI, mpSchemaUrl ) );
			
		} catch ( Exception e ) {
			log.error( "Exception caught creating root element attributes: " + e.getMessage() );
			log.error( "Exception cause: " + e.getCause() );
		}// try-catch
		
		log.debug( "root element attributes: " + sb );
		return sb.toString();
		
	}// createRootElementAttributes
	
	
	
	private String createSchemaLocationAttr( String schemaNs, String schemaUrl ) {
		StringBuilder sb = new StringBuilder();
		sb.append( " xsi:schemaLocation=\"" )
			.append( schemaNs )
			.append( " " )
			.append( schemaUrl )
			.append( "\"" );
		
		log.debug( "returning schema location attr: " + sb );
		return sb.toString();
	}// createSchemaLocationAttr
	
	
	
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
	
	
	public String createAttHashValue( String attFilePath ) 
	throws GrantsBusinessException {
		String attHashValue = null;
    	try {
			File f = new File( attFilePath );
			if ( !f.exists() ) {
	    		String s = "Attachment was not written to disk: " + attFilePath;
	    		log.error( s );
	    		throw new Exception( s );
			}// if
			
			log.debug( "Create FileInputStream for attachment" );
			FileInputStream fis = new FileInputStream( f );
			GrantApplicationHash gAppHash = new GrantApplicationHash();
			attHashValue = gAppHash.computeAttachmentHash( fis );
			log.debug( "attachment hash: " + attHashValue );
			
    	} catch ( Exception e ) {
    		String s = "Exception caught creating attachment hash: " + e.getMessage();
    		log.error( s );
    		throw new GrantsBusinessException( s );
    	}// try-catch
		
		log.debug( "created attachment hash value: " + attHashValue );
		return attHashValue;
	}// createAttHashValue
	
	

}
