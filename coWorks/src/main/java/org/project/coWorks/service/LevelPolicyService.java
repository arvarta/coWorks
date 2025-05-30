package org.project.coWorks.service;

import java.util.List;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.LevelPolicy;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.LevelPolicyRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class LevelPolicyService {
	public static LevelPolicyRepository lPRepository;
	public static BoardRepository bRepository;

//	게시판별 모든 레벨별 권한 확인
	public List<LevelPolicy> findByBoard_Id(long boardId) {
		Board board = bRepository.findById(null).orElse(null);
		return lPRepository.findByBoardId(board.getBoardId());
	}

//	게시판별 모든 레벨별 권한 변경
	public int updateLevelPolicy(long boardId, List<LevelPolicy> list) {
		int result = CheckResult.FAIL;
		List<LevelPolicy> oldLPList = findByBoard_Id(boardId);
		if(oldLPList == null ||list == null)
			return CheckResult.NO_EXIST;
		boolean isMatch = true;
		for(LevelPolicy temp : list) {
			for(LevelPolicy temp2 : oldLPList) {
				if(!temp.getLevelPolicyId().equals(temp2.getLevelPolicyId())) {
					isMatch = false;
					continue;
				}else {
					isMatch = true;
					break;
				}
			}
			if(!isMatch) {
				return CheckResult.NO_MATCH;
			}else {
				lPRepository.save(temp);
				result = CheckResult.PARTIAL_SUCCESS;
			}
		}
		return result;
	}
}
