package io.pivotal.timetracking.configuration;

import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.ExpressionUrlAuthorizationConfigurer.AuthorizedUrl;

@Configuration
@EnableAutoConfiguration
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
   protected void configure(HttpSecurity httpSecurity) throws Exception {
      ((AuthorizedUrl)((HttpSecurity)((AuthorizedUrl)httpSecurity.authorizeRequests().antMatchers(new String[]{"/"})).permitAll().and()).authorizeRequests().antMatchers(new String[]{"/console/**"})).permitAll();
      httpSecurity.csrf().disable();
      httpSecurity.headers().frameOptions().disable();
   }
}
