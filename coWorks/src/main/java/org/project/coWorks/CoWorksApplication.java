package org.project.coWorks;

import java.time.LocalDateTime;

import org.project.coWorks.model.Board;
import org.project.coWorks.model.User;
import org.project.coWorks.repository.BoardRepository;
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
	CommandLineRunner init(UserRepository userRepository, BoardRepository boardRepository, BCryptPasswordEncoder passwordEncoder) {
	    return args -> {
	        // Admin 계정 초기화
	        if (userRepository.count() == 0) {
	            User user = new User();
	            user.setUserId(1234L);
	            user.setUserPw(passwordEncoder.encode("1234"));
	            user.setUserStatus("admin");
	            user.setUserMessage("어드민 계정입니다.");
	            userRepository.save(user);
	        }

	     // 기본 게시판 초기화
            if (boardRepository.count() == 0) {
                Board notice = new Board();
                notice.setBoardName("공지사항");
                notice.setBoardInfo("전사적 공지사항을 올리는 게시판입니다.");
                notice.setBoardCreateDate(LocalDateTime.now());
                notice.setBoardStatus(Board.STATUS[Board.VISIBLE]);  // "VISIBLE"
                boardRepository.save(notice);
                
                Board free = new Board();
                free.setBoardName("자유게시판");
                free.setBoardInfo("자유로운 글을 올리는 게시판입니다.");
                free.setBoardCreateDate(LocalDateTime.now());
                free.setBoardStatus(Board.STATUS[Board.VISIBLE]);  // "VISIBLE"
                boardRepository.save(free);
            }
	    };
	}
}
