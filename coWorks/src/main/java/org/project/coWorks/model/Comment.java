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
@Table(name = "comment")
public class Comment{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "comment_id", nullable = false, columnDefinition = "BIGINT")
	private Long commentId;

	@Column(name = "board_user_id", columnDefinition = "BIGINT")
    private Long boardUserId;

	@Column(name = "comment_target_id", columnDefinition = "BIGINT")
	private Long commentTargetId;

	@Column(name = "comment_target_type", length = 10)
	private String commentTargetType;

	@Column(name = "comment_content", columnDefinition = "TEXT")
	private String commentContent;

	@Column(name = "comment_write_date", columnDefinition = "DATETIME")
	private LocalDateTime commentWriteDate = LocalDateTime.now();

	@Column(name = "comment_edit_date", columnDefinition = "DATETIME")
	private LocalDateTime commentEditDate;

	@Column(name = "comment_del_date", columnDefinition = "DATETIME")
	private LocalDateTime commentDelDate;
}
