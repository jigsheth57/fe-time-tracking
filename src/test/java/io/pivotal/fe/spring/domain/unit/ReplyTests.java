package io.pivotal.fe.spring.domain.unit;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import io.pivotal.timetracking.domain.Reply;

/**
 * Unit tests for the <code>Reply</code> domain object.
 * 
 * @author Brian Jimerson
 *
 */
public class ReplyTests {
	
	private Reply reply;
	
	private static final String REPLY_TO_STRING_PATTERN = "Reply = [ID: %d, Name: '%s', Description: '%s']";
	
	/**
	 * Builds the Reply object to test
	 */
	@Before
	public void buildReply() {
		
		reply = new Reply("Test reply", "Description for test reply");
	}

	/**
	 * Tests that the overridden toString method is correct.
	 */
	@Test
	public void testToString() {
		
		String expectedToString = String.format(
				ReplyTests.REPLY_TO_STRING_PATTERN,
				reply.getReplyId(),
				reply.getReplyName(),
				reply.getReplyDescription());
		TestCase.assertEquals(expectedToString, reply.toString());
	}
}
