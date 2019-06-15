package io.pivotal.timetracking.configuration;

import org.h2.server.web.WebServlet;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.context.embedded.ServletRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableAutoConfiguration
public class WebConfiguration {
   @Bean
   ServletRegistrationBean h2servletRegistration() {
      ServletRegistrationBean registrationBean = new ServletRegistrationBean(new WebServlet(), new String[0]);
      registrationBean.addUrlMappings(new String[]{"/console/*"});
      return registrationBean;
   }
}
