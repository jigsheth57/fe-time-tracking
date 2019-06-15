package io.pivotal.timetracking.repository.integration;

import static org.assertj.core.api.Assertions.*;
import io.pivotal.timetracking.domain.TimeEntry;
import io.pivotal.timetracking.repository.TimeEntryRepository;
import junit.framework.TestCase;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaObjectRetrievalFailureException;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityNotFoundException;
import java.sql.Date;
import java.util.Calendar;
import java.util.List;

/**
 * Integration tests for the <code>TimeEntryRepository</code>
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
@DataJpaTest
@Transactional
public class TimeEntryRepositoryTests {
	
	
	//The repository to test.
	@Autowired
	TimeEntryRepository timeEntryRepository;

//	@Before
//	public void setUp() {
//		TimeEntry te = new TimeEntry("Sample feName 1","Sample account 1",new Date(Calendar.getInstance().getTimeInMillis()),1.0d);
//		timeEntryRepository.save(te);
//	}
	/**
	 * Tests the repository's findAll method, by asserting that
	 * there is more than 0 replies returned.
	 */
	@Test
	public void testFindAll() {
		Iterable<TimeEntry> timeEntries = timeEntryRepository.findAll();
		assertThat(timeEntries).isNotEmpty();
	}
	
	
	/**
	 * Tests the repository's findByFeName method, by
	 * getting the first time entry from findAll, and then 
	 * using that time entry's FE name to call and assert the 
	 * findByFeName method's results.
	 */
	@Test
	public void testFindByFeName() {
		List<TimeEntry> resultOfFindByFeName = timeEntryRepository.findByFeName("Sample feName 1");
		assertThat("Sample feName 1").isEqualTo(resultOfFindByFeName.get(0).getFeName());
	}
	
	/**
	 * Tests the repository's findByAccountName method, by
	 * getting the first time entry from findAll, and then 
	 * using that time entry's account name to call and assert the 
	 * findByAccountName method's results.
	 */
	@Test
	public void testFindByAccountName() {
		List<TimeEntry> resultOfFindByAccountName = timeEntryRepository.findByAccountName("Sample account 1");
		assertThat("Sample account 1").isEqualTo(resultOfFindByAccountName.get(0).getAccountName());
	}
	
	/**
	 * Tests the repository's findOne method, by
	 * getting the first time entry from findAll, and
	 * then using that time entry's ID to call and assert 
	 * the findOne method's result.
	 */
	@Test
	public void testFindOne() {
		TimeEntry firstTimeEntry = timeEntryRepository.findByFeName("Sample feName 1").get(0);
		Long firstTimeEntryId = firstTimeEntry.getTimeEntryId();
		TimeEntry resultFromFindOne = timeEntryRepository.getOne(firstTimeEntryId);
		assertThat(firstTimeEntry.getTimeEntryId()).isEqualTo(resultFromFindOne.getTimeEntryId());
		assertThat(firstTimeEntry.getFeName()).isEqualTo(resultFromFindOne.getFeName());
		assertThat(firstTimeEntry.getAccountName()).isEqualTo(resultFromFindOne.getAccountName());
		assertThat(firstTimeEntry.getDate()).isEqualTo(resultFromFindOne.getDate());
		assertThat(firstTimeEntry.getHours()).isEqualTo(resultFromFindOne.getHours());
	}

	/**
	 * Tests the repository's save method, by
	 * getting the first reply from findAll,
	 * modifying the reply's FE name, saving it, 
	 * getting it back from the repository, and 
	 * asserting that the FE name is correct.
	 */
	@Test
	public void testSaveExistingReply() {
		
		final String newFeName = "Testing changed name.";
		TimeEntry firstTimeEntry = timeEntryRepository.findByFeName("Sample feName 1").get(0);
		Long timeEntryIdToFind = firstTimeEntry.getTimeEntryId();
		firstTimeEntry.setFeName(newFeName);
		timeEntryRepository.save(firstTimeEntry);
		TimeEntry savedTimeEntry = timeEntryRepository.getOne(timeEntryIdToFind);
		assertThat(newFeName).isEqualTo(savedTimeEntry.getFeName());
	}
	
	/**
	 * Tests the repository's save method, by
	 * creating a new Time Entry object, saving it,
	 * fetching it back from the repository, and
	 * asserting that it was fetched properly.
	 */
	@Test
	public void testSaveNewReply() {
		
		final String newFeName = "New entry name";
		final String newAccountName = "New account name";
		final Date newDate = new Date(Calendar.getInstance().getTimeInMillis());
		final Double newHours = 1.5d;
		TimeEntry newTimeEntry = new TimeEntry(newFeName, newAccountName, newDate, newHours);
		newTimeEntry = timeEntryRepository.save(newTimeEntry);
		TimeEntry savedTimeEntry = timeEntryRepository.getOne(newTimeEntry.getTimeEntryId());
		assertThat(newFeName).isEqualTo(savedTimeEntry.getFeName());
		assertThat(newAccountName).isEqualTo(savedTimeEntry.getAccountName());
		assertThat(newDate).isEqualTo(savedTimeEntry.getDate());
		assertThat(newHours).isEqualTo(savedTimeEntry.getHours());
	}
	
	/**
	 * Tests that the feName field on the TimeEntry
	 * domain object is not nullable by trying save
	 * a TimeEntry with a null fe name, and asserting 
	 * that the save fails.
	 */
	@Test
	public void testFeNameNotNullable() {
		
		TimeEntry timeEntry = new TimeEntry(
				"FE Name", 
				"Account Name",
				new Date(Calendar.getInstance().getTimeInMillis()),
				1.5d);
		timeEntry.setFeName(null);
		try {
			timeEntryRepository.save(timeEntry);
		} catch (Exception e) {
			assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> timeEntryRepository.save(timeEntry));
		}
	}

	/**
	 * Tests that the accountName field on the TimeEntry
	 * domain object is not nullable by trying save
	 * a TimeEntry with a null accountName, and asserting 
	 * that the save fails.
	 */
	@Test
	public void testAccountNameNotNullable() {
		
		TimeEntry timeEntry = new TimeEntry(
				"FE Name", 
				"Account Name",
				new Date(Calendar.getInstance().getTimeInMillis()),
				1.5d);
		timeEntry.setAccountName(null);
		try {
			timeEntryRepository.save(timeEntry);
		} catch (Exception e) {
			assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> timeEntryRepository.save(timeEntry));
		}
	}

	/**
	 * Tests that the date field on the TimeEntry
	 * domain object is not nullable by trying save
	 * a TimeEntry with a null date, and asserting 
	 * that the save fails.
	 */
	@Test
	public void testDateNotNullable() {
		
		TimeEntry timeEntry = new TimeEntry(
				"FE Name", 
				"Account Name",
				new Date(Calendar.getInstance().getTimeInMillis()),
				1.5d);
		timeEntry.setDate(null);
		try {
			timeEntryRepository.save(timeEntry);
		} catch (Exception e) {
			assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> timeEntryRepository.save(timeEntry));
		}
	}
	
	/**
	 * Tests that the hours field on the TimeEntry
	 * domain object is not nullable by trying save
	 * a TimeEntry with a null hours, and asserting 
	 * that the save fails.
	 */
	@Test
	public void testHoursNotNullable() {
		
		TimeEntry timeEntry = new TimeEntry(
				"FE Name", 
				"Account Name",
				new Date(Calendar.getInstance().getTimeInMillis()),
				1.5d);
		timeEntry.setHours(null);
		try {
			timeEntryRepository.save(timeEntry);
		} catch (Exception e) {
			assertThatExceptionOfType(DataIntegrityViolationException.class).isThrownBy(() -> timeEntryRepository.save(timeEntry));
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
		
		TimeEntry timeEntry = timeEntryRepository.findAll().iterator().next();
		Long timeEntryId = timeEntry.getTimeEntryId();
		timeEntryRepository.delete(timeEntry);
		try {
			TimeEntry deletedTimeEntry = timeEntryRepository.getOne(timeEntryId);
		} catch (Exception e) {
			assertThatExceptionOfType(JpaObjectRetrievalFailureException.class).isThrownBy(() -> timeEntryRepository.getOne(timeEntryId)).withMessageContaining("Unable to find io.pivotal.timetracking.domain.TimeEntry with id "+timeEntryId);
		}
	}

}
