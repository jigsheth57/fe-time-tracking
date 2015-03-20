package io.pivotal.timetracking.domain.unit;

import java.sql.Date;
import java.util.Calendar;

import junit.framework.TestCase;

import org.junit.Before;
import org.junit.Test;

import io.pivotal.timetracking.domain.TimeEntry;

/**
 * Unit tests for the <code>TimeEntry</code> domain object.
 * 
 * @author Brian Jimerson
 *
 */
public class TimeEntryTests {
	
	private TimeEntry timeEntry;
	
	private static final String TIME_ENTRY_TO_STRING_PATTERN = "TimeEntry = [ID: %d, FE Name: '%s', Account Name: '%s', Date: '%tm %te, %tY', Hours: %f]";
	
	/**
	 * Builds the TimeEntry object to test
	 */
	@Before
	public void buildTimeEntry() {
		timeEntry = new TimeEntry(
				"Test FE", 
				"Test Account", 
				new Date(Calendar.getInstance().getTimeInMillis()), 
				2.5);
	}

	/**
	 * Tests that the overridden toString method is correct.
	 */
	@Test
	public void testToString() {
		
		String expectedToString = String.format(
				TimeEntryTests.TIME_ENTRY_TO_STRING_PATTERN,
				timeEntry.getTimeEntryId(),
				timeEntry.getFeName(),
				timeEntry.getAccountName(),
				timeEntry.getDate(),
				timeEntry.getDate(),
				timeEntry.getDate(),
				timeEntry.getHours());
		TestCase.assertEquals(
				"The toString method should match the pattern [" 
						+ expectedToString + "].",
				expectedToString, timeEntry.toString());
	}
}
