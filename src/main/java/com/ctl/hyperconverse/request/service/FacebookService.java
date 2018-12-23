package com.ctl.hyperconverse.request.service;

import static com.ctl.hyperconverse.util.Messenger.FB_GRAPH_API_URL_MESSAGES;
import static com.ctl.hyperconverse.util.Messenger.FB_GRAPH_API_URL_USER;
import static com.ctl.hyperconverse.util.Messenger.HUB_MODE_SUBSCRIBE;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.ctl.hyperconverse.request.model.UserProfile;
import com.ctl.hyperconverse.response.model.Message;
import com.ctl.hyperconverse.response.model.Postback;
import com.ctl.hyperconverse.response.model.Recipient;
import com.ctl.hyperconverse.response.model.ResponsePayLoad;
import com.ctl.hyperconverse.response.model.SenderActions;
import com.ctl.hyperconverse.util.ClassPathResourceReader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

@Service
public class FacebookService {

	@Autowired
	private Environment env;

	private static final Logger logger = LoggerFactory.getLogger(FacebookService.class);

	public Boolean verifyWebhook(final String mode, final String verifyToken) {
		if (mode.equalsIgnoreCase(HUB_MODE_SUBSCRIBE)
				&& verifyToken.equalsIgnoreCase(env.getProperty("messenger4j.verifyToken")))
			return true;
		return false;
	}

	public Boolean eventHandler(String payload) {
		ObjectMapper mapper = new ObjectMapper();

		try {
			JsonNode request = mapper.readTree(payload);

			if (request.path("entry").path(0).path("messaging").path(0).has("message")
					&& request.path("entry").path(0).path("messaging").path(0).has("message")
					&& !request.path("entry").path(0).path("messaging").path(0).path("message").has("is_echo")) {

				logger.info("its a text message");
				handleTextMessge(request.findValue("sender").path("id").asText(),request.findValue("message").path("text").asText());
				
			} else if (request.path("entry").path(0).path("messaging").path(0).has("postback")) {
				Postback postback = new Postback();
				postback.setPayload(request.path("entry").path(0).path("messaging").path(0).path("postback")
						.path("payload").asText());
				postback.setTitle(request.path("entry").path(0).path("messaging").path(0).path("postback").path("title")
						.asText());
				handlePostback(
						request.findValue("sender").path("id").asText(),
						postback);
			}

			return true;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

	}

	public void handleTextMessge(String recipient, String message) {

		logger.info("Test message is handled sender is--->"+recipient);
		// callling hyperconverse here and based on the success response send the mark
				// seen
	}

	public void handlePostback(String recipient, Postback postback) throws JsonProcessingException, IOException {

		if (postback.getPayload().equalsIgnoreCase("Main_Menu")) {
			sendMarkSeen(recipient);
			sendMainMenu(recipient);
			logger.info("User profile details--->"+getUserProfile("recipient"));
		}
		if(postback.getPayload().equalsIgnoreCase("Products and Services: Internet, TV, Phone") && postback.getTitle().equalsIgnoreCase("Products")) {
			sendMarkSeen(recipient);
			sendProductMenu(recipient); 
		}
		if(postback.getPayload().equalsIgnoreCase("Internet Issue") && postback.getTitle().equalsIgnoreCase("Internet Issue")) {
			sendMarkSeen(recipient);
			//getLocationAndPhoneNumber(recipient);
			getLocation(recipient); 
		}

		SenderActions senderActions = new SenderActions();
		senderActions.setRecipient(new Recipient(recipient));
		senderActions.setSender_action("mark_seen");
		//send(senderActions);

		// callling hyperconverse here and based on the success response send the mark
		// seen
		logger.info("Post Back is handled--->");
	}
	
	public void sendMarkSeen(String recipient) {
		SenderActions senderActions = new SenderActions();
		senderActions.setRecipient(new Recipient(recipient));
		senderActions.setSender_action("mark_seen");
		send(senderActions);
	}
	
	public void getPhoneNumber(String recipient) {
		logger.info("Product Post Back is handled--->");
		ObjectMapper mapper = new ObjectMapper();
		try {
			/*JsonNode mainMenuJson =mapper.readTree(new ClassPathResource(
				      "templates/getPhoneNumber.json").getFile());*/
			
			JsonNode mainMenuJson =mapper.readTree(new ClassPathResourceReader("templates/getPhoneNumber.json").getContent());
			
			JsonNode recipientNode=mainMenuJson.path("recipient");
			((ObjectNode) recipientNode).put("id", recipient);
			System.out.println(recipientNode);
			((ObjectNode) mainMenuJson).put("recipient",recipientNode);
			send(mainMenuJson); //Send call 
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void getLocation(String recipient) {
		logger.info("Product Post Back is handled--->");
		ObjectMapper mapper = new ObjectMapper();
		try {
			/*JsonNode mainMenuJson =mapper.readTree(new ClassPathResource(
				      "templates/getlocation.json").getFile());*/
			
			
			JsonNode mainMenuJson =mapper.readTree(new ClassPathResourceReader("templates/getlocation.json").getContent());
			
			
			JsonNode recipientNode=mainMenuJson.path("recipient");
			((ObjectNode) recipientNode).put("id", recipient);
			System.out.println(recipientNode);
			((ObjectNode) mainMenuJson).put("recipient",recipientNode);
			send(mainMenuJson); //Send call 
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	public void sendProductMenu(String recipient) {
		logger.info("Product Post Back is handled--->");
		ObjectMapper mapper = new ObjectMapper();
		try {
			/*JsonNode mainMenuJson =mapper.readTree(new ClassPathResource(
				      "templates/ProductsMaster.json").getFile());*/
			
			JsonNode mainMenuJson =mapper.readTree(new ClassPathResourceReader("templates/ProductsMaster.json").getContent());
			
			
			JsonNode recipientNode=mainMenuJson.path("recipient");
			((ObjectNode) recipientNode).put("id", recipient);
			System.out.println(recipientNode);
			((ObjectNode) mainMenuJson).put("recipient",recipientNode);
			send(mainMenuJson); //Send call 
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	/*This Method invoke the main menu template and update the recipient and call the send ()*/
	public void sendMainMenu(String recipient)  {
		logger.info("Main Menu Post Back is handled--->");
		ObjectMapper mapper = new ObjectMapper();
		try {
			//JsonNode mainMenuJson =mapper.readTree(new ClassPathResource(
			//	      "templates/MainMenu.json").getFile());
			
			JsonNode mainMenuJson =mapper.readTree(new ClassPathResourceReader("templates/MainMenu.json").getContent());
			
			
			JsonNode recipientNode=mainMenuJson.path("recipient");
			((ObjectNode) recipientNode).put("id", recipient);
			System.out.println(recipientNode);
			((ObjectNode) mainMenuJson).put("recipient",recipientNode);
			send(mainMenuJson); //Send call 
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public String sendText(String recipient, String message) {
		ResponsePayLoad response = new ResponsePayLoad();
		response.setMessage(new Message(message));
		response.setMessaging_type("RESPONSE");
		response.setRecipient(new Recipient(recipient));
		return send(response);
	}
	
	public UserProfile getUserProfile(String recipient) {
		
		String userProfileApiURL=String.format(FB_GRAPH_API_URL_USER,recipient,
				env.getProperty("messenger4j.pageAccessToken"));
		HttpHeaders headers = new HttpHeaders();
		
		headers.setContentType(MediaType.APPLICATION_JSON);
		
		RestTemplate restTemplate = new RestTemplate();
		
		return restTemplate.getForObject(userProfileApiURL, UserProfile.class);
	}

	public String send(Object responsePayLoad) {

		logger.info("Sending paylo->"+responsePayLoad);
		String messagesRequestUrl = String.format(FB_GRAPH_API_URL_MESSAGES,
				env.getProperty("messenger4j.pageAccessToken"));

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);
		HttpEntity<Object> replyPayload = new HttpEntity<>(responsePayLoad, headers);

		RestTemplate restTemplate = new RestTemplate();

		return restTemplate.postForObject(messagesRequestUrl, replyPayload, String.class);
	}

}
