package io.pivotal.timetracking.web;

import io.pivotal.timetracking.domain.Reply;
import io.pivotal.timetracking.repository.ReplyRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * The controller for working with <code>Reply</code> resources.
 * 
 * @author Brian Jimerson
 *
 */
@RestController
public class ReplyController {
	
	@Autowired
	protected ReplyRepository replyRepository;
	
	private static final Log log = LogFactory.getLog(ReplyController.class);

	/**
	 * Generates a ReplyResponse with the number of replies.
	 * @param numberOfReplies The number of replies to return.
	 * @return A ReplyResponse with the number of replies specified.
	 */
	@RequestMapping("/reply/generateReply")
	public ReplyResponse generateReply(
			@RequestParam(value="numberOfReplies", required=false, defaultValue="1") Integer numberOfReplies) {
		
		log.debug(String.format(
				"Processing generate reply request with %d replies.", numberOfReplies));
		ReplyResponse response = new ReplyResponse();
		Iterable<Reply> allReplies = replyRepository.findAll();
		List<String> prunedReplies = this.pruneReplies(allReplies, numberOfReplies);
		response.setReplies(prunedReplies);
		return response;
		
	}
	
	/**
	 * Helper method to prune and convert the list of replies.
	 * @param allReplies An iterable collection of all replies.
	 * @param numberOfReplies The number of replies to prune to.
	 * @return A pruned list of reply descriptions.
	 */
	private List<String> pruneReplies(Iterable<Reply> allReplies, Integer numberOfReplies) {

		List<Reply> allRepliesList = new ArrayList<Reply>();
		for (Reply reply : allReplies) {
			allRepliesList.add(reply);
		}
		
		List<String> prunedReplies = new ArrayList<String>();
		Random random = new Random();
		for (int i = 0; i < numberOfReplies; i++) {
			Reply reply = allRepliesList.get(random.nextInt(allRepliesList.size()));
			log.debug(String.format("Randomly selected reply: [%s].", reply));
			prunedReplies.add(reply.getReplyDescription());
		}

		return prunedReplies;		
	}
}
