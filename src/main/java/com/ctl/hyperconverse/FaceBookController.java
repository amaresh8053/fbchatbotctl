package com.ctl.hyperconverse;

import static com.ctl.hyperconverse.util.Messenger.CHALLENGE_REQUEST_PARAM_NAME;
import static com.ctl.hyperconverse.util.Messenger.FB_GRAPH_API_URL_MESSAGES;
import static com.ctl.hyperconverse.util.Messenger.MODE_REQUEST_PARAM_NAME;
import static com.ctl.hyperconverse.util.Messenger.SIGNATURE_HEADER_NAME;
import static com.ctl.hyperconverse.util.Messenger.VERIFY_TOKEN_REQUEST_PARAM_NAME;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.ctl.hyperconverse.request.service.FacebookService;
import com.ctl.hyperconverse.response.model.FacebookSendModel;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/callback")
public class FaceBookController {

	@Autowired
	private Environment env;

	@Autowired
	private FacebookService facebookService;

	private static final Logger logger = LoggerFactory.getLogger(FaceBookController.class);

	@RequestMapping(method = RequestMethod.GET)
	public ResponseEntity<String> verifyWebhook(@RequestParam(MODE_REQUEST_PARAM_NAME) final String mode,
			@RequestParam(VERIFY_TOKEN_REQUEST_PARAM_NAME) final String verifyToken,
			@RequestParam(CHALLENGE_REQUEST_PARAM_NAME) final String challenge) {
		logger.info("-----------------veryfying the webhook----------------------------------");
		if (facebookService.verifyWebhook(mode, verifyToken))
			return ResponseEntity.ok(challenge);
		return ResponseEntity.status(HttpStatus.FORBIDDEN).body("Could not verify webhook");
	}

	@RequestMapping(method = RequestMethod.POST)
	public ResponseEntity<Void> handleCallback(@RequestBody final String payload,
			@RequestHeader(SIGNATURE_HEADER_NAME) final String signature) throws IOException {

		String messagesRequestUrl = String.format(FB_GRAPH_API_URL_MESSAGES,
				env.getProperty("messenger4j.pageAccessToken"));

		logger.info("incoming payload------->   " + payload);

		
		facebookService.eventHandler(payload);
		
		
		
		ObjectMapper mapper = new ObjectMapper();

		JsonNode request = mapper.readTree(payload);

		/*HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_JSON);

		HttpEntity<ResponsePayLoad> replyPayload = new HttpEntity<>(response, headers);

		if (request.path("entry").path(0).path("messaging").path(0).has("message")
				&& !request.path("entry").path(0).path("messaging").path(0).path("message").has("is_echo")) {

			System.out.println("sending message --------------------------------");
			String typingOn = "{\r\n" + "  \"recipient\":{\r\n" + "    \"id\":\"2369123066436324\"\r\n" + "  },\r\n"
					+ "  \"sender_action\":\"typing_on\"\r\n" + "}";
			String typingOff = "{\r\n" + "  \"recipient\":{\r\n" + "    \"id\":\"2369123066436324\"\r\n" + "  },\r\n"
					+ "  \"sender_action\":\"typing_off\"\r\n" + "}";
			String msgSeen = "{\r\n" + "  \"recipient\":{\r\n" + "    \"id\":\"2369123066436324\"\r\n" + "  },\r\n"
					+ "  \"sender_action\":\"mark_seen\"\r\n" + "}";
			HttpEntity<String> typingOnPayload = new HttpEntity<>(typingOn, headers);
			HttpEntity<String> typingOffPayload = new HttpEntity<>(typingOff, headers);
			HttpEntity<String> msgSeenPayload = new HttpEntity<>(msgSeen, headers);
			// this.messenger.send(msgSeenPayload);
			// this.messenger.send(typingOn);
			// this.messenger.send(response);

			RestTemplate restTemplate = new RestTemplate();
			restTemplate.postForObject(messagesRequestUrl, msgSeenPayload, String.class);
			restTemplate.postForObject(messagesRequestUrl, typingOnPayload, String.class);

			String result = restTemplate.postForObject(messagesRequestUrl, replyPayload, String.class);
			restTemplate.postForObject(messagesRequestUrl, typingOffPayload, String.class);

		}*/
		return ResponseEntity.status(HttpStatus.OK).build();
	}
	
	@PostMapping("/send")
	public ResponseEntity<String> sendTextMessage(@RequestBody final FacebookSendModel textMsg){
		return ResponseEntity.status(HttpStatus.OK).body(facebookService.sendText(textMsg.getRecipientId(), textMsg.getMessage()));
	}
	
	@GetMapping("/sendmain")
	public void sebdmain() {
		facebookService.sendMainMenu("2369123066436324");
	}

}
