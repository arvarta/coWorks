package org.project.coWorks.service;

import java.util.List;

import org.project.coWorks.model.ProposalBoardUser;
import org.project.coWorks.repository.ProposalBoardUserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProposalBoardService {
	@Autowired
	private ProposalBoardUserRepository proposalBoardUserRepository;
	public List<ProposalBoardUser> findByPropsalId(Long proposalId) {
		return proposalBoardUserRepository.findByProposalId(proposalId);
	}

}
