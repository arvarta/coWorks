package org.project.coWorks.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "board_user")
public class BoardUser {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "board_user_id", columnDefinition = "BIGINT")
    private Long boardUserId;

    @Column(name = "board_id", columnDefinition = "BIGINT")
	private Long boardId;

	@Column(name = "user_id", columnDefinition = "BIGINT")
	private Long userId;

    @Column(name = "level")
    private Integer level;

    @Column(name = "board_user_board_visit_count")
    private Integer boardUserBoardVisitCount = 0;

    @Column(name = "board_user_board_join_date", columnDefinition = "DATETIME")
    private LocalDateTime boardUserBoardJoinDate = LocalDateTime.now();    
}
