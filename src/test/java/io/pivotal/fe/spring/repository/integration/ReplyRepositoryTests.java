package io.pivotal.fe.spring.repository.integration;

import io.pivotal.timetracking.Application;
import io.pivotal.timetracking.domain.Reply;
import io.pivotal.timetracking.repository.ReplyRepository;

import java.util.List;

import junit.framework.TestCase;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Integration tests for the <code>ReplyRepository</code>
 * JPA repository interface.
 * 
 * The <code>SpringApplicationConfiguration</code> annotation
 * ensures that the embedded database is started and configured
 * for the integration tests.
 * 
 * Most of the methods tested (<code>findOne</code>, <code>save</code>),
 * are provided by the base CrudRepository class.
 * 
 * @author Brian Jimerson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class ReplyRepositoryTests {
	
	
	@Autowired
	ReplyRepository replyRepository;
	
	/**
	 * Tests the repository's findAll method, by asserting that
	 * there is more than 0 replies returned.
	 */
	@Test
	public void testFindAll() {
		
		Iterable<Reply> replies = replyRepository.findAll();
		TestCase.assertNotNull(replies.iterator().next());
		
	}
	
	
	/**
	 * Tests the repository's findByReplyName method, by
	 * getting the first reply from findAll, and then 
	 * using that reply's name to call and assert the 
	 * findByReplyName method's results.
	 */
	@Test
	public void testFindByReplyName() {
		
		Reply firstReply = replyRepository.findAll().iterator().next();
		List<Reply> resultOfFindByReplyName = replyRepository.findByReplyName(
				firstReply.getReplyName());
		TestCase.assertEquals(
				firstReply.getReplyName(), 
				resultOfFindByReplyName.get(0).getReplyName());
		
	}
	
	/**
	 * Tests the repository's findOne method, by
	 * getting the first reply from findAll, and
	 * then using that reply's ID to call and assert 
	 * the findOne method's result.
	 */
	@Test
	public void testFindOne() {
		
		Reply firstReply = replyRepository.findAll().iterator().next();
		Long firstReplyId = firstReply.getReplyId();
		Reply resultFromFindOne = replyRepository.findOne(firstReplyId);
		TestCase.assertEquals(firstReply.getReplyId(), resultFromFindOne.getReplyId());
		TestCase.assertEquals(firstReply.getReplyName(), resultFromFindOne.getReplyName());
		TestCase.assertEquals(
				firstReply.getReplyDescription(), 
				resultFromFindOne.getReplyDescription());
		
	}

	/**
	 * Tests the repository's save method, by
	 * getting the first reply from findAll,
	 * modifying the reply's name, saving it, 
	 * getting it back from the repository, and 
	 * asserting that the name is correct.
	 */
	@Test
	public void testSaveExistingReply() {
		
		final String newReplyName = "Testing changed name.";
		Reply firstReply = replyRepository.findAll().iterator().next();
		Long replyIdToFind = firstReply.getReplyId();
		firstReply.setReplyName(newReplyName);
		replyRepository.save(firstReply);
		Reply savedReply = replyRepository.findOne(replyIdToFind);
		TestCase.assertEquals(newReplyName, savedReply.getReplyName());
		
	}
	
	/**
	 * Tests the repository's save method, by
	 * creating a new Reply object, saving it,
	 * fetching it back from the repository, and
	 * asserting that it was fetched properly.
	 */
	@Test
	public void testSaveNewReply() {
		
		final String newReplyName = "New reply name";
		final String newReplyDescription = "New reply description";
		Reply newReply = new Reply(newReplyName, newReplyDescription);
		newReply = replyRepository.save(newReply);
		Reply savedReply = replyRepository.findOne(newReply.getReplyId());
		TestCase.assertEquals(newReplyName, savedReply.getReplyName());
		TestCase.assertEquals(newReplyDescription, savedReply.getReplyDescription());
		
	}
	
	/**
	 * Tests that the replyName field on the Reply
	 * domain object is not nullable by trying save
	 * a Reply with a null reply name, and asserting 
	 * that the save fails.
	 */
	@Test
	public void testReplyNameNotNullable() {
		
		Reply newReply = new Reply("Reply name", "Reply description");
		newReply.setReplyName(null);
		try {
			replyRepository.save(newReply);
			TestCase.fail("Shouldn't be able to save a Reply with a null replyName.");
		} catch (DataIntegrityViolationException dive) {
			//We should throw an exception.  Nothing to do.
		}
	}

	/**
	 * Tests that the replyDescription field on the Reply
	 * domain object is not nullable by trying save
	 * a Reply with a null reply description, and asserting 
	 * that the save fails.
	 */
	@Test
	public void testReplyDescriptionNotNullable() {
		
		Reply newReply = new Reply("Reply name", "Reply description");
		newReply.setReplyDescription(null);
		try {
			replyRepository.save(newReply);
			TestCase.fail("Shouldn't be able to save a Reply with a null replyDescription.");
		} catch (DataIntegrityViolationException dive) {
			//We should throw an exception.  Nothing to do.
		}
	}
	
	/**
	 * Tests the repository's delete method, by
	 * getting the first reply from findAll, deleting
	 * it, and asserting that findOne with the deleted
	 * reply's ID doesn't return a reply.
	 */
	@Test
	public void testDelete() {
		
		Reply firstReply = replyRepository.findAll().iterator().next();
		Long firstReplyId = firstReply.getReplyId();
		replyRepository.delete(firstReply);
		Reply deletedReply = replyRepository.findOne(firstReplyId);
		TestCase.assertNull(deletedReply);
		
	}
	
	
}
