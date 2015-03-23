package io.pivotal.timetracking.web.integration;

import java.sql.Date;
import java.util.Calendar;

import io.pivotal.timetracking.Application;
import io.pivotal.timetracking.domain.TimeEntry;
import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;


/**
 * Integration tests for the <code>TimeEntryController</code> class.
 * 
 * The <code>SpringApplicationConfiguration</code>
 * annotation ensures that the proper configuration (i.e.
 * embedded database and data source) is applied.  The 
 * <code>IntegrationTest</code> annotation starts the 
 * embedded Tomcat server for the controller.
 * 
 * @author Brian Jimerson
 *
 */
@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = Application.class)
@WebAppConfiguration
@IntegrationTest("server.port:0")
public class TimeEntryControllerTests {
	
	private static final Log log = LogFactory.getLog(TimeEntryControllerTests.class);
		
	@Autowired
	EmbeddedWebApplicationContext server;
	
	private MockMvc mvc;
	
	/**
	 * Sets up this test suite.
	 */
	@Before
	public void setup() {
		this.mvc = MockMvcBuilders.webAppContextSetup(server).build();
	}

	/**
	 * Tests the entry list method by getting the entry list
	 * and asserting that results are returned.
	 */
	@Test
	public void testListEntries() {
		try {
			this.mvc.perform(
					MockMvcRequestBuilders.get("/entries/"))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.model().attributeExists(
							"timeEntries"))
					.andExpect(MockMvcResultMatchers.view().name("/entries/list"));
		} catch (Exception e) {
			log.error(e);
			TestCase.fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the get entry by id method of the controller.
	 */
	@Test
	public void testGetEntry() {
		try {
			this.mvc.perform(
					MockMvcRequestBuilders.get("/entries/1"))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.model().attributeExists(
							"timeEntry"))
					.andExpect(MockMvcResultMatchers.view().name("/entry/view"));
		} catch (Exception e) {
			log.error(e);
			TestCase.fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the save time entry method of the controller.
	 */
	@Test
	public void testSaveEntry() {
		TimeEntry timeEntry = new TimeEntry(
				"Test FE",
				"Test account",
				new Date(Calendar.getInstance().getTimeInMillis()),
				1.5d);
		try {
			this.mvc.perform(
					MockMvcRequestBuilders.post("/entries/save", timeEntry))
					.andExpect(MockMvcResultMatchers.status().isOk());
		} catch (Exception e) {
			log.error(e);
			TestCase.fail(e.getMessage());
		}
	}
	
}
