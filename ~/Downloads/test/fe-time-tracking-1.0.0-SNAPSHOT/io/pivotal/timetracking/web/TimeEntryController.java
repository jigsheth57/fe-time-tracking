package io.pivotal.timetracking.web;

import io.pivotal.timetracking.domain.TimeEntry;
import io.pivotal.timetracking.repository.TimeEntryRepository;
import java.util.ArrayList;
import java.util.Iterator;
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

@RestController
@RequestMapping({"/entries"})
public class TimeEntryController {
   @Autowired
   protected TimeEntryRepository timeEntryRepository;
   private static final Log log = LogFactory.getLog(TimeEntryController.class);

   @RequestMapping(
      value = {"/"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public List getAllTimeEntries() {
      Iterable timeEntries = this.timeEntryRepository.findAll();
      log.debug(String.format("All time entries fetched: [%s]", timeEntries));
      ArrayList timeEntryList = new ArrayList();
      Iterator var3 = timeEntries.iterator();

      while(var3.hasNext()) {
         TimeEntry te = (TimeEntry)var3.next();
         timeEntryList.add(te);
      }

      return timeEntryList;
   }

   @RequestMapping(
      value = {"/{timeEntryId}"},
      method = {RequestMethod.GET}
   )
   @ResponseBody
   public TimeEntry getTimeEntry(@PathVariable Long timeEntryId) {
      TimeEntry timeEntry = (TimeEntry)this.timeEntryRepository.findOne(timeEntryId);
      log.debug(String.format("Found time entry for id %d: [%s]", timeEntryId, timeEntry));
      return timeEntry;
   }

   @RequestMapping(
      value = {"/"},
      method = {RequestMethod.POST}
   )
   @ResponseBody
   public TimeEntry saveTimeEntry(@RequestBody TimeEntry timeEntry) {
      TimeEntry savedEntry = (TimeEntry)this.timeEntryRepository.save(timeEntry);
      log.debug(String.format("Saved time entry: [%s]", savedEntry));
      return savedEntry;
   }

   @RequestMapping(
      value = {"/{timeEntryId}"},
      method = {RequestMethod.DELETE}
   )
   public void deleteTimeEntry(@PathVariable Long timeEntryId) {
      log.debug(String.format("Deleting time entry for id %d.", timeEntryId));
      this.timeEntryRepository.delete(timeEntryId);
   }
}
