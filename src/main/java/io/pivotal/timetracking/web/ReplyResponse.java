package io.pivotal.timetracking.web;

import java.util.List;

/**
 * Class for JSON responses in the <code>ReplyController</code>. 
 * This class will be automatically unmarshalled to JSON as part
 * of the service response.
 * 
 * @author Brian Jimerson
 *
 */
public class ReplyResponse {
	
	private List<String> replies;

	/**
	 * Gets the list of replies for the response.
	 * @return The list of replies for the response.
	 */
	public List<String> getReplies() {
		return replies;
	}

	/**
	 * Sets the list of replies for the response.
	 * @param replies The list of replies to set.
	 */
	public void setReplies(List<String> replies) {
		this.replies = replies;
	}
	
	/**
	 * Overrides Object's toString method to return the
	 * state of the ReplyReponse object.  Relies on
	 * the Reply object in the replies list overriding
	 * Object's toString method too.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format("Replies: [%s]", replies.toString());
	}
	
	

}
