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
@Table(name = "proposal_board_user")
public class ProposalBoardUser {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "proposal_board_user_id")
	private Long proposalBoardUserId;

	@Column(name = "user_id",columnDefinition = "BIGINT")//
	private Long userId;

	@Column(name = "proposal_board_user_status", length = 10)
	private String proposalBoardUserStatus;

	@Column(name = "proposal_id",columnDefinition = "BIGINT")//
	private Long proposalId;
}
