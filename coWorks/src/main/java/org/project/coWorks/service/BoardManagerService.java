package org.project.coWorks.service;

import java.time.LocalDateTime;
import java.util.List;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.BoardManager;
import org.project.coWorks.model.BoardUser;
import org.project.coWorks.repository.BoardManagerRepository;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.BoardUserRepository;
import org.project.coWorks.repository.UserRepository;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class BoardManagerService {
	public static BoardManagerRepository bmRepository;
	public static BoardRepository bRepository;
	public static BoardUserRepository buRepository;
	public static UserRepository uRepository;

//	해당 게시판 운영자 찾기
	public List<BoardManager> findByBoardId(long boardId) {
		Board board = bRepository.findById(boardId).orElse(null);
		if(board == null)
			return null;
		return bmRepository.findByBoardId(board.getBoardId());
	}
//	권한 변경
	public int changToUser(Long userId, String method) {
		if("DELETE".equals(method)) {
			BoardManager bm = bmRepository.findById(userId).orElse(null);
			if(bm == null)
				return CheckResult.NO_EXIST;
			
			BoardUser bu = new BoardUser();
			bu.setBoardId(bm.getBoardId());
			bu.setBoardUserBoardJoinDate(LocalDateTime.now());
			bu.setUserId(bm.getUserId());
			bu.setBoardUserBoardVisitCount(0);
			buRepository.save(bu);
			bmRepository.delete(bm);
			return CheckResult.SUCCESS;
		}else {
			BoardUser bu = buRepository.findById(userId).orElse(null);
			if(bu == null)
				return CheckResult.NO_EXIST;
			BoardManager bm = new BoardManager();
			bm.setBoardId(bu.getBoardId());
//			bm.setPermission(null);
			bm.setUserId(bu.getUserId());
			bmRepository.save(bm);
			buRepository.delete(bu);
			return CheckResult.SUCCESS;
		}
	}
	
	
}
