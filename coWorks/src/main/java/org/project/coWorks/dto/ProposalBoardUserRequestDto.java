package org.project.coWorks.dto;

import lombok.Data;

@Data
public class ProposalBoardUserRequestDto {
	private Long userId;
	private String BoardUserStatus;
	private Long proposalId;
}

