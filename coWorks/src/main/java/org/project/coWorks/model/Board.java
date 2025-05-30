package org.project.coWorks.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.Data;

@Data
@Entity
public class Board{
	public static final String[] STATUS = {
			"UNVISIBLE",
			"UNVISIBLE_ANONYMOUS",
			"VISIBLE",
			"VISIBLE_ANONYMOUS"
	};
	public static final int UNVISIBLE = 0;
	public static final int UNVISIBLE_ANONYMOUS = 1;
	public static final int VISIBLE = 2;
	public static final int VISIBLE_ANONYMOUS = 3;
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "board_id", columnDefinition = "BIGINT")
	private Long boardId;

	@Column(name = "board_name", length = 30)
	private String boardName;
	
	@Column(name = "board_info", columnDefinition = "TEXT")
	private String boardInfo;

	@Column(name = "board_create_date", columnDefinition = "DATETIME")
	private LocalDateTime boardCreateDate;
	
	@Column(name = "board_status", length = 30)
	private String boardStatus;
}
