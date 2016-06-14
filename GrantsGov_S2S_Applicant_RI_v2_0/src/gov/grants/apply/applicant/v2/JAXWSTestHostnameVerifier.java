package gov.grants.apply.applicant.v2;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.SSLSession;

import org.apache.log4j.Logger;

public class JAXWSTestHostnameVerifier implements HostnameVerifier {
	
	private static final Logger log = Logger.getLogger( JAXWSTestHostnameVerifier.class.getName() );
	
	public boolean verify( String urlHostName, SSLSession session ) {
		log.debug( "urlHostName: " + urlHostName );
//		log.debug( "SSLSession: " + ToStringBuilder.reflectionToString( session ) );
		log.debug( "Warning: URL Host: " + urlHostName + " vs. " + session.getPeerHost() );
		log.debug( "return true" );
		return true;
	}// verify

}
