package io.pivotal.timetracking.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

import io.pivotal.timetracking.domain.TimeEntry;

/**
 * JPA CRUD repository interface for a <code>TimeEntry</code> 
 * domain object.
 * 
 * @author Brian Jimerson
 *
 */
public interface TimeEntryRepository extends JpaRepository<TimeEntry, Long> {
	
	/**
	 * Find all of the time entries with the specified FE name.
	 * @param feName The name of the FE to find entries for.
	 * @return A list of time entries with a matching FE name.
	 */
	List<TimeEntry> findByFeName(@Param("feName") String feName); 
	
	/**
	 * Find all of the time entries with the specified account name.
	 * @param accountName The name of the account to find entries for.
	 * @return A list of time entries with a matching account name.
	 */
	List<TimeEntry> findByAccountName(@Param("accountName") String accountName);
}
