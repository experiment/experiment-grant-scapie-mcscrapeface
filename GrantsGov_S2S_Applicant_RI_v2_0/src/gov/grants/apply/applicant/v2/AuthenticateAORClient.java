package gov.grants.apply.applicant.v2;


import gov.grants.apply.services.applicantwebservices_v2.ApplicantWebServicesPortType;
import gov.grants.apply.services.applicantwebservices_v2.AuthenticateAORRequest;
import gov.grants.apply.services.applicantwebservices_v2.AuthenticateAORResponse;
import gov.grants.apply.system.grantscommonelements_v1.SecurityMessage;
import gov.grants.apply.system.grantscommonelements_v1.Token;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.ximpleware.VTDNav;

public class AuthenticateAORClient extends BaseApplicantClient {

	private static final String CLASSNAME = AuthenticateAORClient.class.getSimpleName();
	private static final Logger log = Logger.getLogger( CLASSNAME );
	
	
	/**
	 * @param args
	 */
	public static void main( String[] args ) {
		log.debug( "Begin " + CLASSNAME );
		
		try {
			AuthenticateAORClient client = new AuthenticateAORClient();
			
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
			
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.AOR_USER_ID_ELEMENT_NAME );
			String aorUserId = configVN.toNormalizedString( configVN.getText() );
			log.debug( "aorUserId: " + aorUserId );
			
			configVN.toElement( VTDNav.PARENT );
			configVN.toElement( VTDNav.FIRST_CHILD, Globals.AOR_PASSWORD_ELEMENT_NAME );
			String aorPassword = configVN.toNormalizedString( configVN.getText() );
			log.debug( "aorPassword: " + aorPassword );
			
			if ( StringUtils.isBlank( aorUserId ) || StringUtils.isBlank( aorPassword ) ) {
				String s = "aor-user-id and aor-password elements in the config xml cannot be blank";
				log.error( s );
				throw new Exception( s );
			}// if
			
			ApplicantWebServicesPortType port = getApplicantPort();
			
			AuthenticateAORRequest request = new AuthenticateAORRequest();
			request.setAORUserID( aorUserId );
			request.setAORPassword( aorPassword );
			
			AuthenticateAORResponse response = port.authenticateAOR( request );
			StringBuilder sb = new StringBuilder();
			
			Token token = response.getToken();
//			log.debug( "Token: " + ToStringBuilder.reflectionToString( token ) );
			if ( token != null ) {
				sb.append( "\n\n" )
					.append( "TOKEN INFO" )
					.append( "\n==========" )
					.append( "\nUser ID: " + token.getUserID() )
					.append( "\nToken ID: " + token.getTokenId() )
					.append( "\nDUNS: " + token.getDUNS() )
					.append( "\nFull Name: " + token.getFullName() )
					.append( "\nAOR Status: " + token.getAORStatus() )
					.append( "\nToken Expiration: " + token.getTokenExpiration() );
			} else {
				sb.append( "\n\nToken is blank" );
			}// if-else
			
			SecurityMessage msg = response.getSecurityMessage();
			if ( msg != null ) {
				sb.append( "\n\n" )
					.append( "SECURITY MESSAGE INFO" )
					.append( "\n=====================" )
					.append( "\nMessage Code: " + msg.getMessageCode() )
					.append( "\nMessage Text: " + msg.getMessageText() );
			} else {
				sb.append( "\n\nSecurity message is blank" );
			}// if-else
			
			sb.append( "\n\n" );
			log.debug( sb );
			
			// process AuthenticateAORResponseXml
//			AuthenticateAORResponseXml xml = XMLBeansUtil.parseAuthenticateAORResponseXml( response.getAuthenticateAORResponseXml(), true );
//			log.debug( "authenticate AOR response XML:\n " + xml.xmlText( XMLBeansUtil.getXMLBeansXmlOptions( true ) ) );
		

		} catch ( Exception e ) {
			log.error( "Exception: " + e.getMessage() );
			e.printStackTrace();
			throw e;
		}// try-catch
		
	}// makeServiceCall
	
}
