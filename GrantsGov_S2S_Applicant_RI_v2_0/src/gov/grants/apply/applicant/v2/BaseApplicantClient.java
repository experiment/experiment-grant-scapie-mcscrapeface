package gov.grants.apply.applicant.v2;

import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesV20;
import gov.grants.commons.s2s.util.SoapUtil;
import gov.grants.commons.util.FileUtil;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import javax.net.ssl.HttpsURLConnection;
import javax.xml.ws.BindingProvider;
import javax.xml.ws.soap.MTOMFeature;
import com.sun.xml.ws.developer.JAXWSProperties;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import com.ximpleware.VTDGen;
import com.ximpleware.VTDNav;


public class BaseApplicantClient {
	
	public static final String MMddyyyy = "MM/dd/yyyy";
	public static final String yyyyMMddHHmmss = "yyyy-MM-dd HH:mm:ss";
	public static java.text.SimpleDateFormat SDF;
	
	protected VTDNav configVN;
	
	private static final Logger log = Logger.getLogger( BaseApplicantClient.class.getName() );
	private Map<String, String> argMap = new HashMap<String, String>();
	private static final String TRUSTSTORE_PASSWORD = "javax.net.ssl.trustStorePassword";
	private static final String TRUSTSTORE = "javax.net.ssl.trustStore";
	private static final String KEYSTORE_PASSWORD = "javax.net.ssl.keyStorePassword";
	private static final String KEYSTORE = "javax.net.ssl.keyStore";
	
	private static final String CONFIG_PROPERTIES_FILE = "resources/applicant-s2s-config.properties";
	
	private static String SOAP_URL_PREFIX = null;
	private static final String SOAP_URL_SUFFIX = "/grantsws-applicant/services/v2/ApplicantWebServicesSoapPort";

	/*
	 * Filter names
	 */
	protected static Map<String, String> FILTER_NAMES;
	
	
	static {
		// Use log4j.xml configuration in test directory
		DOMConfigurator config = new DOMConfigurator();
		config.doConfigure( 
				BaseApplicantClient.class.getResourceAsStream( "/resources/log4j.xml" ), 
				log.getLoggerRepository() );
		
		initSoapUtils();
		setSSLProps();
		
	}// static
	
	
	protected ApplicantWebServicesPortType getApplicantPort() throws Exception {
		log.debug( "create Streaming applicant port" );
		ApplicantWebServicesV20 service = new ApplicantWebServicesV20();
		log.debug( "service URL: " + service.getWSDLDocumentLocation() );
		ApplicantWebServicesPortType port = service.getApplicantWebServicesSoapPort( new MTOMFeature( true ) ); // Enable MTOM;
		BindingProvider bp = ( BindingProvider ) port;
		bp.getRequestContext().put( BindingProvider.ENDPOINT_ADDRESS_PROPERTY, SOAP_URL_PREFIX + SOAP_URL_SUFFIX );
		bp.getRequestContext().put( JAXWSProperties.HTTP_CLIENT_STREAMING_CHUNK_SIZE, 8192 );// enables streaming
		log.debug( "applicant integration port type: " + port );
		
		HttpsURLConnection.setDefaultHostnameVerifier( new JAXWSTestHostnameVerifier() );
		log.debug( "initialized: JAXWSTestHostnameVerifier" );
		return port;
	}// getApplicantPort
	
	
	protected void init( String[] args ) {
		try {
			int count = 0;
			String[] argList = null;
			
			for ( String arg : args ) {
				// skip client class name arg and -legacy arg
				if ( arg.indexOf( "=" ) > -1 ) {
					log.debug( "arg [" + count + "]: " + arg );
					argList = StringUtils.split( arg, "=" );
					argMap.put( argList[0], argList[1] );
				}// if
				count++;
			}// for-each
			log.debug( "arg map keys: " + argMap.keySet() );
			log.debug( "arg map: " + argMap );
			
			SOAP_URL_PREFIX = argMap.get( Globals.URL_PREFIX_CMD_LINE_KEY );
			log.debug( "Setting SOAP URL prefix: " + SOAP_URL_PREFIX );
			if ( StringUtils.isBlank( SOAP_URL_PREFIX ) ) {
				log.error( "url_prefix command-line parameter is blank" );
			}// if-else
			
			initFilterNames();
			
		} catch ( Exception e ) {
			log.error( "Exception caught during initialization: " + e.getMessage() );
		}// try-catch
		
	}// init
	
	
	
	private static void initSoapUtils() {
		try {
			log.info( "init SoapUtil with properties from: " + CONFIG_PROPERTIES_FILE );
			SoapUtil.setConfigPropertiesFile( CONFIG_PROPERTIES_FILE );
//			log.info( "soap URL: " + SoapUtil.getSoapURL() );
		} catch ( Exception e ) {
			log.error( "Exception caught initializing SoapUtils: " + e.getMessage() );
			e.printStackTrace();
		}// try-catch
		
	}// initSoapUtils

	
	
	private static void setSSLProps() {
		try {
			File f = null;
			
			System.setProperty( KEYSTORE, SoapUtil.getProperty( KEYSTORE ) );
			log.debug( "keystore: " + System.getProperty( KEYSTORE ) );
			// check if keystore exists
			f = new File( System.getProperty( KEYSTORE ) );
			if ( !f.exists() ) {
				log.error( "Cannot find file: " + System.getProperty( KEYSTORE ) );
				throw new Exception( "Cannot find keystore: " + System.getProperty( KEYSTORE ) );
			} else {
				log.info( "Keystore found: " + System.getProperty( KEYSTORE ) );
			}// if-else
			
			System.setProperty( KEYSTORE_PASSWORD, SoapUtil.getProperty( KEYSTORE_PASSWORD ) );
			
			System.setProperty( TRUSTSTORE, SoapUtil.getProperty( TRUSTSTORE ) );
			log.debug( "truststore: " + System.getProperty( TRUSTSTORE ) );
			// check if truststore exists
			f = new File( System.getProperty( TRUSTSTORE ) );
			if ( !f.exists() ) {
				log.error( "Cannot find file: " + System.getProperty( TRUSTSTORE ) );
				throw new Exception( "Cannot find truststore: " + System.getProperty( TRUSTSTORE ) );
			} else {
				log.info( "Truststore found: " + System.getProperty( TRUSTSTORE ) );
			}// if-else
			
			System.setProperty( TRUSTSTORE_PASSWORD, SoapUtil.getProperty( TRUSTSTORE_PASSWORD ) );
		} catch ( Exception e ) {
			log.error( "Exception caught setting keystore/truststore properties: " + e.getMessage() );
			e.printStackTrace();
			System.exit( 1 );
		}// try-catch
	}// setSSLProps
	
	
	private static void initFilterNames() {
		FILTER_NAMES = new HashMap<String, String>();
		FILTER_NAMES.put( "status", "Status" );
		FILTER_NAMES.put( "oppid", "OpportunityID" );
		FILTER_NAMES.put( "cfda", "CFDANumber" );
		FILTER_NAMES.put( "submissiontitle", "SubmissionTitle" );
		FILTER_NAMES.put( "grantsgovtrackingnumber", "GrantsGovTrackingNumber" );
		log.debug( "Initialized FILTER_NAMES map: " + FILTER_NAMES );
	}// init
	
	
	public static String formatDate( Date d, String format ) {
		try {
			if ( SDF == null ) {
				log.debug( "initialize SimpleDateFormat" );
				SDF = new java.text.SimpleDateFormat( format );
			}// if
			
			return SDF.format( d );
		} catch ( Exception e ) {
			log.error( "Exceptioin formatting date [" + d + "]: " + e.getMessage() );
			return "";
		}// try-catch
	}// formatDate
	
	
	public static String formatDate( long date, String format ) {
		return formatDate( new Date( date ), format );
	}// formatDate
	
	
    protected boolean loadConfigXmlDoc( String configXmlFilePath ) throws Exception {
    	try {
    		log.debug( "Load XML: " + configXmlFilePath );
			byte[] xmlBytes = FileUtil.readFile( configXmlFilePath );
    		log.debug( "XML bytes loaded: " + xmlBytes.length );
    		
    		VTDGen vGen = new VTDGen();
    		vGen.setDoc( xmlBytes );
    		vGen.parse( false );//set namespace awareness
    		configVN = vGen.getNav();
    		return true;
    		
    	} catch ( Exception e ) {
    		log.error( "Exception loading [" + configXmlFilePath + "]: " + e.getMessage() );
    		throw e;
    	}// try-catch
    	
    	
    }// loadConfigXmlDoc
    
    
    protected VTDNav loadXmlDoc( String xmlFileName ) throws Exception {
    	try {
    		log.debug( "Load XML: " + xmlFileName );
			byte[] xmlBytes = FileUtil.readFile( xmlFileName );
    		log.debug( "XML bytes loaded: " + xmlBytes.length );
    		
    		VTDGen vGen = new VTDGen();
    		vGen.setDoc( xmlBytes );
    		vGen.parse( false );//set namespace awareness
    		VTDNav vn = vGen.getNav();
    		return vn;
    		
    	} catch ( Exception e ) {
    		log.error( "Exception loading [" + xmlFileName + "]: " + e.getMessage() );
    		throw e;
    	}// try-catch
    	
    	
    }// loadXmlDoc
    
	
	/* Getters & Setters */
	
	/**
	 * @return the argMap
	 */
	public Map<String, String> getArgMap() {
		return argMap;
	}


	/**
	 * @param argMap the argMap to set
	 */
	public void setArgMap(Map<String, String> argMap) {
		this.argMap = argMap;
	}
	

}
