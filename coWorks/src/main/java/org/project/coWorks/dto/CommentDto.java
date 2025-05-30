package org.project.coWorks.dto;

import lombok.Data;

@Data
public class CommentDto {
		private Long targetId;
		private String targetType;
	    private String content;
	    private Long userId;
	    private String userName;
	    private String writeDate;
	    private String viewDate;
	    private String profile;
}
