package org.project.coWorks.dto;

import lombok.Data;

@Data
public class ProposalRequestDto {
	private Long userId;
	private String boardName;
	private String boardInfo;
	private Boolean anonymous;
	private String reason;	
}
