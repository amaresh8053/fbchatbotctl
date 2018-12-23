package com.ctl.hyperconverse.util;

import static java.util.Optional.empty;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

public final class Messenger {
	
	@Autowired
	private Environment env;
	 /**
     * Constant for the {@code hub.mode} request parameter name.
     */
    public static final String MODE_REQUEST_PARAM_NAME = "hub.mode";

    /**
     * Constant for the {@code hub.challenge} request parameter name.
     */
    public static final String CHALLENGE_REQUEST_PARAM_NAME = "hub.challenge";

    /**
     * Constant for the {@code hub.verify_token} request parameter name.
     */
    public static final String VERIFY_TOKEN_REQUEST_PARAM_NAME = "hub.verify_token";

    /**
     * Constant for the {@code X-Hub-Signature} header name.
     */
    public static final String SIGNATURE_HEADER_NAME = "X-Hub-Signature";

    private static final String OBJECT_TYPE_PAGE = "page";
    public static final String HUB_MODE_SUBSCRIBE = "subscribe";

    public static final String FB_GRAPH_API_URL_MESSAGES = "https://graph.facebook.com/v3.1/me/messages?access_token=%s";
    public static final String FB_GRAPH_API_URL_MESSENGER_PROFILE = "https://graph.facebook.com/v3.1/me/messenger_profile?access_token=%s";
    public static final String FB_GRAPH_API_URL_USER = "https://graph.facebook.com/v3.1/%s?fields=id,name,first_name," +
            "last_name,profile_pic,locale,timezone,gender&access_token=%s";
    
    
    public final String messagesRequestUrl=String.format(FB_GRAPH_API_URL_MESSAGES, env.getProperty("messenger4j.pageAccessToken"));


    
}
