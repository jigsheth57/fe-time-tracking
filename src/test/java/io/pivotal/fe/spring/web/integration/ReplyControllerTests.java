package io.pivotal.fe.spring.web.integration;

import io.pivotal.timetracking.Application;
import io.pivotal.timetracking.web.ReplyResponse;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.web.client.RestTemplate;


/**
 * Integration tests for the <code>ReplyController</code> class.
 * 
 * The <code>SpringApplicationConfiguration</code>
 * annotation ensures that the proper configuration (i.e.
 * embedded database and data source) is applied.  The 
 * <code>IntegrationTest</code> annotation starts the 
 * embedded Tomcat server for the controller.
 * 
 * @author Brian Jimerson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class ReplyControllerTests {
	
	private static final Log log = LogFactory.getLog(ReplyControllerTests.class);
	private static final String REPLY_CONTROLLER_PATH = "/reply";
	private static final String GENERATE_REPLY_PATH = "/generateReply?numberOfReplies=";
	private static final int NUMBER_OF_REPLIES = 3;
		
	@Autowired
	EmbeddedWebApplicationContext server;
	
	@Value("${local.server.port}")
	int embeddedServerPort;

	/**
	 * Gets the URL for the reply controller.
	 * @return The URL for the reply controller.
	 */
	private String replyControllerUrl() {
		
		StringBuilder sb = new StringBuilder();
		sb.append("http://localhost:");
		sb.append(embeddedServerPort);
		sb.append("/");
		sb.append(ReplyControllerTests.REPLY_CONTROLLER_PATH);
		log.debug(String.format("Reply Controller URL = [%s].", sb.toString()));
		return sb.toString();
		
	}
	
	/**
	 * Gets the URL for the generate reply service.
	 * @return The URL for the generate reply service.
	 */
	private String generateReplyUrl() {
		
		StringBuilder sb = new StringBuilder();
		sb.append(replyControllerUrl());
		sb.append(ReplyControllerTests.GENERATE_REPLY_PATH);
		sb.append(ReplyControllerTests.NUMBER_OF_REPLIES);
		log.debug(String.format("Generate Reply URL = [%s]", sb.toString()));
		return sb.toString();
		
	}
	
	/**
	 * Tests the generate reply service by sending a request
	 * with the static number of replies variable, and asserting the
	 * response contains that number of replies and at least one reply.
	 * This inherently tests that the service invocation is generally 
	 * successful too.
	 */
	@Test
	public void testGenerateReply() {
		
		RestTemplate restTemplate = new TestRestTemplate("user", "password");
		ResponseEntity<ReplyResponse> responseEntity = restTemplate.getForEntity(
				generateReplyUrl(), ReplyResponse.class);
		ReplyResponse replyResponse = responseEntity.getBody();
		TestCase.assertEquals(
				ReplyControllerTests.NUMBER_OF_REPLIES, 
				replyResponse.getReplies().size());
		TestCase.assertNotNull(replyResponse.getReplies().get(0));
		
	}
	
	/**
	 * Tests that the generate reply service requires
	 * authentication, by not sending Basic authentication
	 * credentials, and asserting that the response status
	 * code is a 401 (unauthorized).
	 */
	@Test
	public void testGenerateReplyRequiresAuthentication() {

		/*
		 * Note that the response entity is of type String, not
		 * ReplyResponse.  This is because the response will be an
		 * unknown JSON string with the error in it, and not a JSON
		 * version of ReplyReponse.
		 */
		RestTemplate restTemplate = new TestRestTemplate();
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
					generateReplyUrl(), String.class);
		TestCase.assertEquals(HttpStatus.UNAUTHORIZED, responseEntity.getStatusCode());

	}
	
	/**
	 * Tests that the generate reply service accepts valid
	 * authentication, by sending Basic authentication credentials
	 * and asserting that the response status code is 200 (OK).
	 */
	@Test
	public void testGenerateReplyAcceptsAuthentication() {
		
		/*
		 * The response entity could be of type String or ReplyResponse. It
		 * doesn't really matter for this test, since we're simply confirming 
		 * the response's status code.
		 */
		RestTemplate restTemplate = new TestRestTemplate("user", "password");
		ResponseEntity<String> responseEntity = restTemplate.getForEntity(
				generateReplyUrl(), String.class);
		TestCase.assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
		
	}
}
