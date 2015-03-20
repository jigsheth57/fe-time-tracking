package io.pivotal.timetracking.web;

import io.pivotal.timetracking.domain.TimeEntry;
import io.pivotal.timetracking.repository.TimeEntryRepository;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.servlet.ModelAndView;

/**
 * The controller for working with <code>TimeEntry</code> resources.
 * 
 * @author Brian Jimerson
 *
 */
@Controller
public class TimeEntryController {
	
	@Autowired
	protected TimeEntryRepository timeEntryRepository;
	
	private static final Log log = LogFactory.getLog(TimeEntryController.class);
	
	/**
	 * Gets all of the time entries.
	 * @return A ModelAndView with all of the time entries.
	 */
	@RequestMapping(value = "entries/all", method = RequestMethod.GET)
	public ModelAndView getAllTimeEntries() {
		
		Iterable<TimeEntry> timeEntries = timeEntryRepository.findAll();
		log.debug(String.format("All time entries fetched: [%s]", timeEntries));
		
		ModelAndView mav = new ModelAndView("entries/list");
		mav.addObject("timeEntries", timeEntries);
		return mav;
	}

}
