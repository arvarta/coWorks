package org.project.coWorks.dto;

import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PostDto {
		private Long postId;
	    private String postTitle;
	    private String postContent;
	    private Long boardId;
	    private String boardName;
	    private Long boardUserId;
	    private String employeeName;
	    private String postStatus;
	    private LocalDateTime postCreateDate;
	    private String postSimpleDate;
	    private LocalDateTime postEditDate;
	    private LocalDateTime postDelDate;
}
