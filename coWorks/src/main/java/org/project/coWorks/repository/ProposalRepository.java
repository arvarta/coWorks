package org.project.coWorks.repository;

import org.project.coWorks.model.Proposal;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProposalRepository extends JpaRepository<Proposal, Long>{
	Proposal findByProposalId(Long proposalId);
}