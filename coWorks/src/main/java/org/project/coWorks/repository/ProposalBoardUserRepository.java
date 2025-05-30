package org.project.coWorks.repository;

import java.util.List;

import org.project.coWorks.model.ProposalBoardUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalBoardUserRepository extends JpaRepository<ProposalBoardUser, Long>{

	boolean existsByProposalIdAndUserId(Long proposalId, Long userId);

	List<ProposalBoardUser> findByProposalId(Long proposalId);
}
