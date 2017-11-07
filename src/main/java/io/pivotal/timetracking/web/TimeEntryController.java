package io.pivotal.timetracking.web;

import io.pivotal.timetracking.domain.TimeEntry;
import io.pivotal.timetracking.repository.TimeEntryRepository;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

/**
 * The REST controller for working with <code>TimeEntry</code> resources.
 *
 * @author Brian Jimerson
 *
 */
@RestController
@RequestMapping(value="/entries")
public class TimeEntryController {

	@Autowired
	protected TimeEntryRepository timeEntryRepository;

	private static final Log log = LogFactory.getLog(TimeEntryController.class);

	/**
	 * Gets all of the time entries.
	 * @return A list of all of the time entries.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public @ResponseBody List<TimeEntry> getAllTimeEntries() {

		Iterable<TimeEntry> timeEntries = timeEntryRepository.findAll();
		log.debug(String.format("All time entries fetched: [%s]", timeEntries));
		ArrayList<TimeEntry> timeEntryList = new ArrayList<TimeEntry>();
		for (TimeEntry te : timeEntries) {
			timeEntryList.add(te);
		}
		try {
			//sleep 2 seconds
			Thread.sleep(2000);
			log.debug("Testing...");
		} catch (InterruptedException e) {
			e.printStackTrace();
		}

		return timeEntryList;
	}

	/**
	 * Gets a particular time entry by it's id.
	 * @param timeEntryId The id of the time entry to get.
	 * @return The time entry requested.
	 */
	@RequestMapping(value="/{timeEntryId}", method=RequestMethod.GET)
	public @ResponseBody TimeEntry getTimeEntry(@PathVariable Long timeEntryId) {

		TimeEntry timeEntry = timeEntryRepository.findOne(timeEntryId);
		log.debug(String.format("Found time entry for id %d: [%s]",
				timeEntryId, timeEntry));
		return timeEntry;

	}

	/**
	 * Saves a time entry
	 * @param timeEntry The time entry to save.
	 * @return The time entry saved, with any additional data from the save.
	 */
	@RequestMapping(value="/", method=RequestMethod.POST)
	public @ResponseBody TimeEntry saveTimeEntry(@RequestBody TimeEntry timeEntry) {

		TimeEntry savedEntry = timeEntryRepository.save(timeEntry);
		log.debug(String.format("Saved time entry: [%s]", savedEntry));
		return savedEntry;

	}

	/**
	 * Deletes a time entry.
	 * @param timeEntryId The id of the time entry to delete.
	 */
	@RequestMapping(value="/{timeEntryId}", method=RequestMethod.DELETE)
	public void deleteTimeEntry(@PathVariable Long timeEntryId) {

		log.debug(String.format("Deleting time entry for id %d.", timeEntryId));
		timeEntryRepository.delete(timeEntryId);
	}


}
