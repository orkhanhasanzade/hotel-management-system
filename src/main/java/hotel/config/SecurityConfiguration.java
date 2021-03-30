package hotel.config;

import java.util.Arrays;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.rememberme.JdbcTokenRepositoryImpl;
import org.springframework.security.web.authentication.rememberme.PersistentTokenRepository;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter 
{
	@Autowired
	private UserDetailsService customUserDetailsService;

	@Bean
    public PasswordEncoder passwordEncoder() 
	{
        return new BCryptPasswordEncoder();
    }
	
	@Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception 
	{ 
        auth
        	.userDetailsService(customUserDetailsService) 
        	.passwordEncoder(passwordEncoder()); 
    }

	@Override
	protected void configure(HttpSecurity http) throws Exception 
	{						
		http
    	.csrf().disable()
        .authorizeRequests()
        	.antMatchers("/login").permitAll()
			.antMatchers("/registration").permitAll()			
			.antMatchers("/forgot-password").permitAll()	
			.antMatchers("/reset").permitAll()	
            .antMatchers("/admin/**").hasRole("ADMIN").anyRequest().authenticated()
            .and()   
        .formLogin()   
            .loginPage("/login")	
            						
            .defaultSuccessUrl("/dashboard")  
            								  
            		 
            .failureUrl("/login?error=true")
            .permitAll()   
            .and() 
           
        .logout()
        	.deleteCookies("JSESSIONID")
        	.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
        	.logoutSuccessUrl("/login")
            .permitAll();	
		

        http.rememberMe()  
                .tokenRepository(persistentTokenRepository())
                .rememberMeParameter("myRememberMeParameterName") 
                .rememberMeCookieName("my-remember-me")    		  
                .tokenValiditySeconds(86400);	
        
        http.sessionManagement()
        			.invalidSessionUrl("/login?invalid-session=true");
	}			
	
	@Autowired
	DataSource dataSource;
	
	@Bean
	public PersistentTokenRepository persistentTokenRepository() 
	{
	      JdbcTokenRepositoryImpl db = new JdbcTokenRepositoryImpl();
	      db.setDataSource(dataSource);
	      return db;
	}
	
	@Override
	public void configure(WebSecurity web) throws Exception 
	{
	    web
	       .ignoring()
	       .antMatchers("/resources/**", "/static/**", "/assets/**", "/css/**", "/js/**", "/images/**");
	}
	

}