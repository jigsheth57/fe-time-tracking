package io.pivotal.timetracking.web.integration;

import io.pivotal.timetracking.Application;
import io.pivotal.timetracking.domain.TimeEntry;

import java.sql.Date;
import java.util.Calendar;

import junit.framework.TestCase;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.hamcrest.Matchers;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.embedded.EmbeddedWebApplicationContext;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;


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
@DirtiesContext(classMode = ClassMode.AFTER_EACH_TEST_METHOD)
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
	 * Tests the get all entries method of the controller.
	 */
	@Test
	public void testGetAllEntries() {
		try {
			this.mvc.perform(
					MockMvcRequestBuilders.get("/entries/"))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"));
		} catch (Exception e) {
			log.error(e);
			TestCase.fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the get entry by id method of the controller.
	 */
	@Test
	public void testGetEntryById() {
		try {
			this.mvc.perform(
					MockMvcRequestBuilders.get("/entries/1"))
					.andExpect(MockMvcResultMatchers.status().isOk())
					.andExpect(MockMvcResultMatchers.content().contentType("application/json;charset=UTF-8"))
					.andExpect(MockMvcResultMatchers.jsonPath("$", Matchers.hasEntry("accountName", "Sample account 1")));
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
		TimeEntry entry = new TimeEntry(
				"Test FE", 
				"Test Account", 
				new Date(Calendar.getInstance().getTimeInMillis()), 
				1.5d);
		try {
			this.mvc.perform(
					MockMvcRequestBuilders.post("/entries/save")
					.contentType("application/json")
					.content(new ObjectMapper().writeValueAsString(entry))
			)
			.andDo(MockMvcResultHandlers.print())
			.andExpect(MockMvcResultMatchers.status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.timeEntryId").exists());
		} catch (Exception e) {
			log.error(e);
			TestCase.fail(e.getMessage());
		}
	}
	
	/**
	 * Tests the delete time entry method of the controller.
	 */
	@Test
	public void testDeleteEntry() {
		try {
			this.mvc.perform(
					MockMvcRequestBuilders.delete("/entries/delete/1"))
					.andExpect(MockMvcResultMatchers.status().isOk());
					
		} catch (Exception e) {
			log.error(e);
			TestCase.fail(e.getMessage());			
		}
	}
	
}
