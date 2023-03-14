package com.rayosoft.security;

import javax.sql.DataSource;

import org.springframework.context.annotation.*;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.*;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class DatabaseWebSecurity {

	@Bean
	public UserDetailsManager usersCustom(DataSource dataSource) {
		JdbcUserDetailsManager users = new JdbcUserDetailsManager(dataSource);
		users.setUsersByUsernameQuery("select username, password, estatus from Usuarios where username=?");
		users.setAuthoritiesByUsernameQuery("select u.username, p.perfil from UsuarioPerfil up " + 
											"inner join Usuarios u on u.id = up.idUsuario "	+ 
											"inner join Perfiles p on p.id = up.idPerfil " + "where u.username = ?");
		return users;
	}

	@Bean
	public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
		http.authorizeHttpRequests()

				.requestMatchers("/bootstrap/**", "/images/**", "/tinymce/**", "/logos/**").permitAll()

				.requestMatchers("/", "/login", "/about", "/bcrypt/**","/signup", "/search", "/vacantes/view/**").permitAll()
				.requestMatchers("/solicitudes/create/**","/solicitudes/save/**").hasAnyAuthority("USUARIO")
				.requestMatchers("/vacantes/**").hasAnyAuthority("SUPERVISOR", "ADMINISTRADOR")
				.requestMatchers("/solicitudes/**").hasAnyAuthority("SUPERVISOR", "ADMINISTRADOR")
				.requestMatchers("/categorias/**").hasAnyAuthority("SUPERVISOR", "ADMINISTRADOR")
				.requestMatchers("/usuarios/**").hasAnyAuthority("ADMINISTRADOR")
				.anyRequest().authenticated().and()
				
				.formLogin().loginPage("/login").permitAll().and()
				.logout().permitAll();
		return http.build();
	}
	
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
}
