package io.pivotal.timetracking.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Domain class for the reply resource.
 * @author Brian Jimerson
 *
 */
@Entity
public class Reply {

	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long replyId;
	
	@Column(nullable=false)
	private String replyName;
	
	@Column(nullable=false)
	private String replyDescription;
	
	/**
	 * Hides the default constructor so that it is only used by JPA.
	 */
	protected Reply() {
		super();
	}
	
	/**
	 * Public constructor for a reply entity.
	 * @param replyName The name for the reply entity.
	 * @param replyDescription The description for the reply entity.
	 */
	public Reply(String replyName, String replyDescription) {
		this.replyName = replyName;
		this.replyDescription = replyDescription;
	}
	
	/**
	 * Gets the ID of the reply entity.
	 * @return The ID of the reply entity.
	 */
	public Long getReplyId() {
		return replyId;
	}
	
	/**
	 * Sets the ID of the reply entity.
	 * @param replyId The ID of the reply entity to set.
	 */
	public void setReplyId(Long replyId) {
		this.replyId = replyId;
	}
	
	/**
	 * Gets the name of the reply entity.
	 * @return The name of the reply entity.
	 */
	public String getReplyName() {
		return replyName;
	}
	
	/**
	 * Sets the name of the reply entity.
	 * @param replyName The name of the reply entity to set.
	 */
	public void setReplyName(String replyName) {
		this.replyName = replyName;
	}
	
	/**
	 * Gets the description of the reply entity.
	 * @return The description of the reply entity.
	 * 
	 */
	public String getReplyDescription() {
		return replyDescription;
	}
	
	/**
	 * Sets the description of the reply entity.
	 * @param replyDescription The description of the reply entity to set.
	 */
	public void setReplyDescription(String replyDescription) {
		this.replyDescription = replyDescription;
	}
	
	/**
	 * Overrides Object's toString method to return the
	 * state of the Reply object.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"Reply = [ID: %d, Name: '%s', Description: '%s']",
				replyId, 
				replyName, 
				replyDescription);
	}

}
