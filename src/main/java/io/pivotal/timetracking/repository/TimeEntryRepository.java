package io.pivotal.timetracking.repository;

import io.pivotal.timetracking.domain.TimeEntry;

import java.sql.Date;
import java.util.List;

import org.springframework.data.repository.CrudRepository;

/**
 * JPA CRUD repository interface for a <code>TimeEntry</code> 
 * domain object.
 * 
 * @author Brian Jimerson
 *
 */
public interface TimeEntryRepository extends CrudRepository<TimeEntry, Long> {
	
	/**
	 * Find all of the time entries with the specified FE name.
	 * @param feName The name of the FE to find entries for.
	 * @return A list of time entries with a matching FE name.
	 */
	List<TimeEntry> findByFeName(String feName); 
	
	/**
	 * Find all of the time entries with the specified account name.
	 * @param accountName The name of the account to find entries for.
	 * @return A list of time entries with a matching account name.
	 */
	List<TimeEntry> findByAccountName(String accountName);

	/**
	 * Find all of the time entries with the specified date.
	 * @param date The date to find entries for.
	 * @return A list of time entries with a matching date.
	 */
	List<TimeEntry> findByDate(Date date);

}
