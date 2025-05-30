package org.project.coWorks.dto;

import lombok.Data;

@Data
public class QuestionRequestDto {
	private Long employeeId;
	private Long targetId;
	private String targetType;
	private String questionType ;
	private String title;
	private String content;
}
