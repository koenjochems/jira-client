package net.rcarz.jiraclient;

/**
 * Abstract client class
 * 
 * @author Koen Jochems
 *
 */
public abstract class AClient {
	protected RestClient restclient = null;
	
	protected AClient(RestClient restclient) {
		this.restclient = restclient;
	}
}
