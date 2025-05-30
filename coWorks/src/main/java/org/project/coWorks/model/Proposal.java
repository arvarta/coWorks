package org.project.coWorks.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;

@Data
@Entity
@Table(name = "proposal")
public class Proposal {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proposal_id", columnDefinition = "BIGINT")
	private Long proposalId;
	
	@Column(name = "user_id", columnDefinition = "BIGINT")//
    private Long userId;

	@Column(name = "proposal_board_name", length = 100)
	private String proposalBoardName;

	@Column(name = "proposal_board_info", columnDefinition = "TEXT")
	private String proposalBoardInfo;

	@Column(name = "proposal_anonymous", columnDefinition = "BOOLEAN DEFAULT FALSE")
	private Boolean proposalAnonymous;

	@Column(name = "proposal_reason", columnDefinition = "TEXT")
	private String proposalReason;
	
}
