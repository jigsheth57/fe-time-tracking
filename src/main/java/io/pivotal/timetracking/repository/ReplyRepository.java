package io.pivotal.timetracking.repository;

import io.pivotal.timetracking.domain.Reply;

import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * JPA CRUD repository interface for a <code>Reply</code> 
 * domain object.
 * 
 * @author Brian Jimerson
 *
 */
public interface ReplyRepository extends CrudRepository<Reply, Long> {
	
	/**
	 * Find all of the replies with the specified name.
	 * @param replyName The name of the replies to find.
	 * @return A list of replies with a matching name.
	 */
	List<Reply> findByReplyName(String replyName); 

}
