package io.pivotal.timetracking.repository.integration;

import io.pivotal.timetracking.Application;
import io.pivotal.timetracking.domain.TimeEntry;
import io.pivotal.timetracking.repository.TimeEntryRepository;

import java.sql.Date;
import java.util.Calendar;
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
@SpringApplicationConfiguration(classes = Application.class)
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
public class TimeEntryRepositoryTests {
	
	
	//The repository to test.
	@Autowired
	TimeEntryRepository timeEntryRepository;
	
	/**
	 * Tests the repository's findAll method, by asserting that
	 * there is more than 0 replies returned.
	 */
	@Test
	public void testFindAll() {
		
		Iterable<TimeEntry> timeEntries = timeEntryRepository.findAll();
		TestCase.assertNotNull(
				"Find all should return at least 1 result.",
				timeEntries.iterator().next());
		
	}
	
	
	/**
	 * Tests the repository's findByFeName method, by
	 * getting the first time entry from findAll, and then 
	 * using that time entry's FE name to call and assert the 
	 * findByFeName method's results.
	 */
	@Test
	public void testFindByFeName() {
		
		TimeEntry firstTimeEntry = timeEntryRepository.findAll().iterator().next();
		List<TimeEntry> resultOfFindByFeName = timeEntryRepository.findByFeName(
				firstTimeEntry.getFeName());
		TestCase.assertEquals(
				firstTimeEntry.getFeName(), 
				resultOfFindByFeName.get(0).getFeName());
		
	}
	
	/**
	 * Tests the repository's findByAccountName method, by
	 * getting the first time entry from findAll, and then 
	 * using that time entry's account name to call and assert the 
	 * findByAccountName method's results.
	 */
	@Test
	public void testFindByAccountName() {
		
		TimeEntry firstTimeEntry = timeEntryRepository.findAll().iterator().next();
		List<TimeEntry> resultOfFindByAccountName = timeEntryRepository.findByAccountName(
				firstTimeEntry.getAccountName());
		TestCase.assertEquals(
				firstTimeEntry.getAccountName(), 
				resultOfFindByAccountName.get(0).getAccountName());
		
	}
	

	/**
	 * Tests the repository's findByDate method, by
	 * getting the first time entry from findAll, and then 
	 * using that time entry's date to call and assert the 
	 * findByDate method's results.
	 */
	@Test
	public void testFindByDate() {
		
		TimeEntry firstTimeEntry = timeEntryRepository.findAll().iterator().next();
		List<TimeEntry> resultOfFindByAccountName = timeEntryRepository.findByDate(
				firstTimeEntry.getDate());
		TestCase.assertEquals(
				firstTimeEntry.getDate(), 
				resultOfFindByAccountName.get(0).getDate());
		
	}
	
	/**
	 * Tests the repository's findOne method, by
	 * getting the first time entry from findAll, and
	 * then using that time entry's ID to call and assert 
	 * the findOne method's result.
	 */
	@Test
	public void testFindOne() {
		
		TimeEntry firstTimeEntry = timeEntryRepository.findAll().iterator().next();
		Long firstTimeEntryId = firstTimeEntry.getTimeEntryId();
		TimeEntry resultFromFindOne = timeEntryRepository.findOne(firstTimeEntryId);
		TestCase.assertEquals(
				firstTimeEntry.getTimeEntryId(), 
				resultFromFindOne.getTimeEntryId());
		TestCase.assertEquals(
				firstTimeEntry.getFeName(),
				resultFromFindOne.getFeName());
		TestCase.assertEquals(
				firstTimeEntry.getAccountName(), 
				resultFromFindOne.getAccountName());
		TestCase.assertEquals(
				firstTimeEntry.getDate(), 
				resultFromFindOne.getDate());
		TestCase.assertEquals(
				firstTimeEntry.getHours(), 
				resultFromFindOne.getHours());
		
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
		TimeEntry firstTimeEntry = timeEntryRepository.findAll().iterator().next();
		Long timeEntryIdToFind = firstTimeEntry.getTimeEntryId();
		firstTimeEntry.setFeName(newFeName);
		timeEntryRepository.save(firstTimeEntry);
		TimeEntry savedTimeEntry = timeEntryRepository.findOne(timeEntryIdToFind);
		TestCase.assertEquals(newFeName, savedTimeEntry.getFeName());
		
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
		TimeEntry savedTimeEntry = 
				timeEntryRepository.findOne(newTimeEntry.getTimeEntryId());
		TestCase.assertEquals(
				newFeName, savedTimeEntry.getFeName());
		TestCase.assertEquals(
				newAccountName, savedTimeEntry.getAccountName());
		TestCase.assertEquals(newDate.toString(), savedTimeEntry.getDate().toString());
		TestCase.assertEquals(newHours, savedTimeEntry.getHours());
		
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
			TestCase.fail("Shouldn't be able to save a TimeEntry with a null feName.");
		} catch (DataIntegrityViolationException dive) {
			//We should throw an exception.  Nothing to do.
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
			TestCase.fail("Shouldn't be able to save a TimeEntry with a null accountName.");
		} catch (DataIntegrityViolationException dive) {
			//We should throw an exception.  Nothing to do.
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
			TestCase.fail("Shouldn't be able to save a TimeEntry with a null date.");
		} catch (DataIntegrityViolationException dive) {
			//We should throw an exception.  Nothing to do.
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
			TestCase.fail("Shouldn't be able to save a TimeEntry with a null hours.");
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
		
		TimeEntry timeEntry = timeEntryRepository.findAll().iterator().next();
		Long timeEntryId = timeEntry.getTimeEntryId();
		timeEntryRepository.delete(timeEntry);
		TimeEntry deletedTimeEntry = timeEntryRepository.findOne(timeEntryId);
		TestCase.assertNull(deletedTimeEntry);
		
	}
	
	
}
