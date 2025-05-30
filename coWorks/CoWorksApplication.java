package org.project.coWorks;

import org.project.coWorks.model.User;
import org.project.coWorks.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
public class CoWorksApplication {
	
	public static void main(String[] args) {
		SpringApplication.run(CoWorksApplication.class, args);
	}

	@Bean
	CommandLineRunner init(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder) {
		return args -> {
			User user = new User();
			user.setUserId(1234L);
			user.setUserPw(passwordEncoder.encode("1234"));
			user.setUserStatus("admin");
			user.setUserMessage("어드민 계정입니다.");
			userRepository.save(user);
		};
	}
}
