package org.project.coWorks.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Data
@Table(name = "post")
public class Post {

public static final String[] STATUS = {
		"postting", "deleted", "notice"
	};
	public static final int POSTTING = 0;
	public static final int DELETED = 1;
	public static final int NOTICE = 2;
	
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "post_id", columnDefinition = "BIGINT")
    private Long postId;

    @Column(name = "post_edit_date")
    private LocalDateTime postEditDate;

    @Column(name = "post_del_date")
    private LocalDateTime postDelDate;

    @Column(name = "post_title", length = 30)
    private String postTitle;

    @Column(name = "post_content", columnDefinition = "TEXT")
    private String postContent;
  
    @Column(name = "post_create_date", columnDefinition = "DATETIME")
    private LocalDateTime postCreateDate;
    
    @Column(name = "post_status", length = 20)
    private String postStatus;

    @Column(name = "board_id", columnDefinition = "BIGINT")//
    private Long boardId;

    @Column(name = "board_user_id", columnDefinition = "BIGINT")//
    private Long boardUserId;
}