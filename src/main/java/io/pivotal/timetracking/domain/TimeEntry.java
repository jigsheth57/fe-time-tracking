package io.pivotal.timetracking.domain;

import java.sql.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

/**
 * Domain class for a time entry resource.
 * @author Brian Jimerson
 *
 */
@Entity
public class TimeEntry {

	// The columns / fields for this entity.
	
	@Id
	@GeneratedValue(strategy=GenerationType.AUTO)
	private Long timeEntryId;
	
	@Column(nullable=false)
	private String feName;
	
	@Column(nullable=false)
	private String accountName;
	
	@Column(nullable=false)
	private Date date;
	
	@Column(nullable=false)
	private Double hours;
	
	/**
	 * Hides the default constructor so that it is only used by JPA.
	 */
	protected TimeEntry() {
		super();
	}
	
	/**
	 * Public constructor for the TimeEntry entry.
	 * @param feName The FE's name.
	 * @param accountName The account name.
	 * @param date The date for the entry.
	 * @param hours The number of hours spent.
	 */
	public TimeEntry(String feName, String accountName, Date date, Double hours) {
		this.feName = feName;
		this.accountName = accountName;
		this.date = date;
		this.hours = hours;
	}
	
	/**
	 * Gets the time entry id.
	 * @return The time entry id.
	 */
	public Long getTimeEntryId() {
		return timeEntryId;
	}

	/**
	 * Sets the time entry id.
	 * @param timeEntryId The time entry id.
	 */
	public void setTimeEntryId(Long timeEntryId) {
		this.timeEntryId = timeEntryId;
	}

	/**
	 * Gets the FE's name.
	 * @return The FE's name.
	 */
	public String getFeName() {
		return feName;
	}

	/**
	 * Sets the FE's name.
	 * @param feName The FE's name.
	 */
	public void setFeName(String feName) {
		this.feName = feName;
	}

	/**
	 * Gets the account name.
	 * @return The account name.
	 */
	public String getAccountName() {
		return accountName;
	}

	/**
	 * Sets the account name.
	 * @param accountName The account name.
	 */
	public void setAccountName(String accountName) {
		this.accountName = accountName;
	}

	/**
	 * Gets the date for the entry.
	 * @return The date for the entry.
	 */
	public Date getDate() {
		return date;
	}

	/**
	 * Sets the date for the entry.
	 * @param date The date for the entry.
	 */
	public void setDate(Date date) {
		this.date = date;
	}

	/**
	 * Gets the number of hours spent.
	 * @return The number of hours spent.
	 */
	public Double getHours() {
		return hours;
	}

	/**
	 * Sets the number of hours spent.
	 * @param hours The number of hours spent.
	 */
	public void setHours(Double hours) {
		this.hours = hours;
	}

	/**
	 * Overrides Object's toString method to return the
	 * state of the TimeEntry object.
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return String.format(
				"TimeEntry = [timeEntryId: %d, feName: '%s', accountName: '%s', date: '%tB %te, %tY', hours: %f]",
				timeEntryId, 
				feName, 
				accountName,
				date,
				date,
				date,
				hours);
	}

}
