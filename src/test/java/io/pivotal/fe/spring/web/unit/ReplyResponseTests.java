package io.pivotal.fe.spring.web.unit;

import io.pivotal.timetracking.web.ReplyResponse;

import java.util.ArrayList;
import java.util.List;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

/**
 * Unit tests for the <code>ReplyResponse</code> object.
 * 
 * @author Brian Jimerson
 *
 */
public class ReplyResponseTests {
	
	private ReplyResponse replyResponse;
	
	private static final String REPLY_RESPONSE_TO_STRING_PATTERN = "Replies: [%s]";
	
	/**
	 * Builds the ReplyResponse object to test.
	 */
	@Before
	public void buildReplyResponse() {
		replyResponse = new ReplyResponse();
		List<String> replies = new ArrayList<String>(7);
		for (int i = 0; i < 5; i++) {
			replies.add(String.format("Description for reply %s", i));
		}
		replyResponse.setReplies(replies);
	}
	
	/**
	 * Tests that the overridden toString method is correct.
	 */
	@Test
	public void testToString() {
	
		String expectedToString = String.format(
				ReplyResponseTests.REPLY_RESPONSE_TO_STRING_PATTERN, 
				replyResponse.getReplies().toString());
		TestCase.assertEquals(expectedToString, replyResponse.toString());
	}

}
