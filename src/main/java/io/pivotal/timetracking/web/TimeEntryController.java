package io.pivotal.timetracking.web;

import io.pivotal.timetracking.domain.TimeEntry;
import io.pivotal.timetracking.repository.TimeEntryRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 * The controller for working with <code>TimeEntry</code> resources.
 * 
 * @author Brian Jimerson
 *
 */
@Controller
@RequestMapping(value="/entries")
public class TimeEntryController {
	
	@Autowired
	protected TimeEntryRepository timeEntryRepository;
	
	private static final Log log = LogFactory.getLog(TimeEntryController.class);
	
	/**
	 * Gets all of the time entries.
	 * @param model The model to use for this controller method.
	 * @return The path to the desired view.
	 */
	@RequestMapping(value="/", method=RequestMethod.GET)
	public String getAllTimeEntries(Model model) {
		
		Iterable<TimeEntry> timeEntries = timeEntryRepository.findAll();
		log.debug(String.format("All time entries fetched: [%s]", timeEntries));
		
		model.addAttribute("timeEntries", timeEntries);
		return "/entries/list";
	}
	
	/**
	 * Gets a particular time entry by it's id.
	 * @param timeEntryId The id of the time entry to get.
	 * @param model The model to use for this controller method.
	 * @return The path to the desired view.
	 */
	@RequestMapping(value="/{timeEntryId}", method=RequestMethod.GET)
	public String getTimeEntry(@PathVariable Long timeEntryId, Model model) {
		
		TimeEntry timeEntry = timeEntryRepository.findOne(timeEntryId);
		log.debug(String.format("Found time entry for id %d: [%s]",
				timeEntryId, timeEntry));
		
		model.addAttribute("timeEntry", timeEntry);
		return "/entry/view";
		
	}
	

}
