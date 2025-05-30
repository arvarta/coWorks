package org.project.coWorks.service;

import java.time.LocalDateTime;
import java.util.List;

import org.project.coWorks.controller.CheckResult;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.BoardManager;
import org.project.coWorks.model.BoardUser;
import org.project.coWorks.model.LevelPolicy;
import org.project.coWorks.model.Proposal;
import org.project.coWorks.model.ProposalBoardUser;
import org.project.coWorks.model.User;
import org.project.coWorks.repository.BoardManagerRepository;
import org.project.coWorks.repository.BoardRepository;
import org.project.coWorks.repository.BoardUserRepository;
import org.project.coWorks.repository.LevelPolicyRepository;
import org.project.coWorks.repository.PermissionRepository;
import org.project.coWorks.repository.ProposalBoardUserRepository;
import org.project.coWorks.repository.ProposalRepository;
import org.project.coWorks.repository.UserRepository;
import org.springframework.stereotype.Service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Service
public class ProposalService {
	private final ProposalRepository propRepository;
	private final ProposalBoardUserRepository pbulRepository;
	private final BoardRepository bRepository;
	private final BoardUserRepository buRepository;
	private final UserRepository uRepository;
	private final BoardManagerRepository bmRepository;
	private final PermissionRepository pmsRepository;
	private final LevelPolicyRepository lpRepository;

	public List<Proposal> getAll(){
		return propRepository.findAll();
	}
	
//	DB에 board 추가
	public Board saveBoard(Proposal p) {
		Board board = new Board();
		board.setBoardName(p.getProposalBoardName());
		board.setBoardInfo(p.getProposalBoardInfo());
		board = bRepository.save(board);
		return board;
	}
	
//	DB에 boardManager 추가
	public int saveBoardManager(Board b,User user) {
		BoardManager bm = new BoardManager();
		bm.setBoardId(b.getBoardId());
		bm.setUserId(user.getUserId());
		bm.setPermissionName(
			pmsRepository.findByPermissionName(
				User.BOARD_USER_STATUS[User.MANAGER])
		);
		BoardManager oldBm = bmRepository.findByUserIdAndBoardId(b.getBoardId(), user.getUserId());
		if(oldBm == null) {
			bmRepository.save(bm);
		}else {
			bm.setBoardManagerId(oldBm.getBoardManagerId());
			bmRepository.save(bm);
		}
		return CheckResult.PARTIAL_SUCCESS;
	}
	
//	DB에 boardUser 추가
	public int saveBoardUser(Board b,User user) {
		BoardUser bu = new BoardUser();
		bu.setBoardId(b.getBoardId());
		bu.setBoardUserBoardJoinDate(LocalDateTime.now());
		bu.setUserId(user.getUserId());
		bu.setLevel(LevelPolicy.LEVEL_DEFAULT[0][0]);
		bu.setBoardUserBoardVisitCount(0);
		BoardUser oldBu = buRepository.findByUserIdAndBoardId(user.getUserId(), b.getBoardId());
		if(oldBu != null)
			bu.setBoardUserId(oldBu.getBoardUserId());
		buRepository.save(bu);
		return CheckResult.PARTIAL_SUCCESS;
	}
	
// DB에 LevelPolicy 추가
	private void saveLevelPolicy(Board b, int[] levelDefault) {
		LevelPolicy lp = new LevelPolicy();
		lp.setBoardId(b.getBoardId());
		lp.setLevel(levelDefault[0]);
		lp.setWritePostCount(levelDefault[1]);
		lp.setWriteCommentCount(levelDefault[2]);
		lp.setBoardVisitCount(levelDefault[3]);
		lp.setBoardJoinedAt(levelDefault[4]);
//		lp.setPermission();				권한 설정
		lpRepository.save(lp);
	}
	
//	문제시 proposal null OR ProposalUserList null OR ProposalBoardUserStatus unmatched
//		 LevelPolicy set fail
	@Transactional
	public int processProposal(Long id, String method) {
		int result = CheckResult.NO_EXIST;
		Proposal p = propRepository.findById(id).orElse(null);
		if(p == null)
			return result;
		List<ProposalBoardUser> userList = pbulRepository.findByProposalId(p.getProposalId());
		if(p != null && userList != null && !userList.isEmpty()) {
			result = CheckResult.FAIL;
			if("POST".equals(method)) {
				Board b = saveBoard(p);
				for(ProposalBoardUser propUser : userList) {
					User target = uRepository.findByUserId(propUser.getUserId());
					String userStatus = propUser.getProposalBoardUserStatus();
					if(userStatus.equals(User.BOARD_USER_STATUS[User.MANAGER])) {
						if(result != saveBoardManager(b, target)) {
							result = CheckResult.PARTIAL_SUCCESS;
						}
					}else if(userStatus.equals(User.BOARD_USER_STATUS[User.USER])) {
						if(result != saveBoardUser(b,target))
							result = CheckResult.PARTIAL_SUCCESS;
					}else {
//						LogService.addLog("proposal", p.getProposalId(), result);	//시스템 로그 확인
						return result;
					}
				}
				for(int[] levelDefault : LevelPolicy.LEVEL_DEFAULT) {
					if(lpRepository.findByBoardIdAndLevel(b.getBoardId(), levelDefault[0]) == null) {
						saveLevelPolicy(b, levelDefault);
					}
				}
			}
			propRepository.delete(p);
			result = CheckResult.SUCCESS;
		}	
//		LogService.addLog("proposal", (p != null ? p.getProposalId() : -1), result); //시스템 로그 확인
		return result;
	}
	
//	요청 수 확인
	public Long pCount() {
		return propRepository.count();
	}
}
