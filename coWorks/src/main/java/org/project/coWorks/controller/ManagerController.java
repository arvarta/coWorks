package org.project.coWorks.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.project.coWorks.model.Board;
import org.project.coWorks.model.BoardManager;
import org.project.coWorks.model.LevelPolicy;
import org.project.coWorks.model.Post;
import org.project.coWorks.model.User;
import org.project.coWorks.service.BoardManagerService;
import org.project.coWorks.service.BoardService;
import org.project.coWorks.service.LevelPolicyService;
import org.project.coWorks.service.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;


@RequiredArgsConstructor
@RestController
@RequestMapping("/managers/board")
public class ManagerController {
	@Autowired
	public static BoardService bService;
	@Autowired
	public static LevelPolicyService lPService;
	@Autowired
	public static PostService pService;
	@Autowired
	public static BoardManagerService bmService;
	
//	지정 게시판 정보 조회
	@GetMapping("/{id}")
	public Map<String, Object> findById(HttpSession session, @PathVariable long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		Board board = bService.findById(id);
		int process = CheckResult.processToNull(board);
		if(process == 200)
			result.put("board", board);
		result.put("responseCode", process);
		return result;
	}
	
//	지정 게시판 정보 변경
	@PostMapping("/{id}")
	public Map<String, Object> updateBoard(HttpServletRequest request, @PathVariable long id,
		Map<String, Object> json) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		result.put("responseCode",
			(bService.updateBoard(id,
				(Board)json.get("board")
		)));
		return result;
	}
	
//	지정 게시판 레벨별 권환 확인
	@GetMapping("/{id}/levels")
	public Map<String, Object> findLevelPoilcyByBoard(HttpServletRequest request, @PathVariable long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		List<LevelPolicy> lPList = lPService.findByBoard_Id(id);
		int process = CheckResult.processToNull(lPList);
		if(process == 200)
			result.put("levelPolicyList", lPList);
		result.put("responseCode", process);
		return result;
	}
	
//	지정 게시판 레벨별 권환 변경
	@PostMapping("/{id}/levels")
	public Map<String, Object> updateLevelPolicy(HttpServletRequest request, @PathVariable long id,
		Map<String, Object> json) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		List<LevelPolicy> lPList = null;
//		lPlist = (List<LevelPolicy>)json.get("levelPolicyList");	받을 방법이 떠오르질 않음
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		result.put("responseCode",
			CheckResult.checkToProcess(lPService.updateLevelPolicy(id, lPList)));
		return result;
	}
	
//	지정 게시판의 숨겨진 게시물 목록 조회
	@GetMapping("/{id}/post/hidden")
	public Map<String, Object> getHiddenPostList(HttpServletRequest request,
		@PathVariable long boardId, @RequestParam(defaultValue = "1")int page) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		List<Post> postList = pService.findbyBoardId(boardId, "hidden");
		int process = CheckResult.processToNull(postList);
		result.put("responseCode", process);
		if(process == 200){
			try {
				postList = Pagination.paging(postList, page);
				result.put("postList", postList);
				result.put("totalPage", Pagination.totalPage(postList));
			} catch (Exception e) {
				result.put("responseCode", 404);
			}
		}
		return result;
	}
	
//	지정 숨겨진 게시물 목록 조회
	@GetMapping("/post/{id}/hidden")
	public Map<String, Object> getHiddenPost(HttpServletRequest request, @PathVariable long postId) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		Post post = pService.findByPostId(postId);
		int process = CheckResult.processToNull(post);
		if(process == 200)
			result.put("post", post);
		result.put("responseCode", process);
		return result;
	}
	
//	지정 숨겨진 게시물 숨김 취소 및 삭제
	@RequestMapping(value = "/post/{id}/hidden",
		method = {RequestMethod.POST,RequestMethod.DELETE})
	public Map<String, Object> editHiddenPost(HttpServletRequest request,
		@PathVariable long postId, @RequestBody Map<String, Object> json) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		int process = CheckResult.NO_EXIST;
		if("DELETE".equals(request.getMethod())) {
//			process = pService.deletePost((Long)json.get("postId"));
		}else if("POST".equals(request.getMethod())) {
//			process = pService.visibleReversePost((Post)json.get("post"));
		}
		result.put("responseCode", process);
		return result;
	}
	
//	특정 게시판 매니저 목록 조회
	@GetMapping("/{id}/managers")
	public Map<String, Object> getManagerList(HttpServletRequest request,
		@PathVariable long boardId, @RequestParam(defaultValue = "1")int page) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		List<BoardManager> bmList = bmService.findByBoardId(boardId);
		int process = CheckResult.processToNull(bmList);
		result.put("responseCode", process);
		if(process == 200) {
			try {
				bmList = Pagination.paging(bmList, page);
				result.put("boardManagerList", bmList);
				result.put("totalPage", Pagination.totalPage(bmList));
			} catch (Exception e) {
				result.put("responseCode", 404);
			}
		}
		return result;
	}

////	해당 게시판의 로그 목록 조회
//	@GetMapping("/logs")
//	public Map<String, Object> getLogList(HttpSession session,
//		@PathVariable Long boardId, @RequestParam(defaultValue = "1")int page){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		Map<String, Object> temp = new AdminsController().findByLogs(session, boardId,page);
//		result.put("responseCode", temp.get("responseCode"));
//		if(temp.get("logList") != null) {
//			result.put("logList", temp.get("logList"));
//			result.put("totalPage", temp.get("totalPage"));
//		}
//		return result;
//	}
	
////	해당 게시판의 차단 유저 목록 조회
//	@GetMapping("/user/ban")
//	public Map<String, Object> getBanList(HttpSession session,
//		@PathVariable Long boardId, @RequestParam(defaultValue = "1")int page){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		
//		List<BoardUser> buList = buService.getBanUsers(boardId);
//		int process = CheckResult.nullToProcess(buList);
//		result.put("responseCode", process);
//		if(process != 200)
//			return result;
//		
//
//		List<Ban> bList = banService.getBanListByBoardId(boardId);
//		process = CheckResult.nullToProcess(bList);
//		if(process != 200)
//			return result;
//		
//		if(process == 200) {
//			try {
//				bList = Pagination.paging(bList, page);
//				buList = Pagination.paging(buList, page);
//				result.put("boardUserList", buList);
//				result.put("banList", bList);
//				result.put("totalPage", Pagination.perPage(bList));
//			} catch (Exception e) {
//				result.put("responseCode", 404);
//			}
//		}
//		
//		return result;
//	}
	
////	해당 게시판의 특정 유저 차단 시간 조절 및 해제
//	@RequestMapping(value = "/user/{id}/ban",
//		method = {RequestMethod.DELETE, RequestMethod.POST})
//	public Map<String, Object> editBanUser(HttpSession session,
//		@PathVariable Long boardId,@PathVariable Long userId,
//		@RequestBody Map<String, Object> json){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		int banDate = json.get("banDate") != null ? (int)json.get("banDate") : 0;
//		result.put("responseCode", CheckResult.checkToProcess(banService.updateBanUser(userId, banDate)));
//		return result;
//	}
	
////	해당 게시판의 신고 목록 확인
//	@GetMapping("/report")
//	public Map<String, Object> findReportList(HttpSession session,@PathVariable Long boardId,
//		@RequestBody Map<String, Object> json, @RequestParam(defaultValue = "1")int page){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		Map<String, Object> temp = 
//			new AdminsController()
//			.getBoardReport(session, boardId, json, page);
//		int process = (int) temp.get("responseCode");
//		result.put("responseCode", process);
//		if(process == 200) {
//			result.put("reportList", temp.get("reportList"));
//			result.put("totalPage", temp.get("totalPage"));
//		}
//		return result;
//	}
//	
////	신고 확인
//	@GetMapping("/report/{id}")
//	public Map<String, Object> findReport(HttpSession session,@PathVariable Long boardId,
//		Long reportId, @RequestBody Map<String, Object> json){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		Map<String, Object> temp = 
//			new AdminsController()
//			.getReport(session, reportId);
//		int process = (int) temp.get("responseCode");
//		result.put("responseCode", process);
//		if(process == 200)
//			result.put("report", temp.get("report"));
//		return result;
//	}
//	
////	신고 처리
//	@PostMapping("/report/{id}")
//	public Map<String, Object> updateReport(HttpSession session,@PathVariable Long boardId,
//		Long reportId, @RequestBody Map<String, Object> json){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		Map<String, Object> temp = 
//			new AdminsController()
//			.updateReport(session, reportId, json);
//		result.put("responseCode", temp.get("responseCode"));
//		return result;
//	}
//	
////	신고 기각
//	@PostMapping("/report/{id}/pass")
//	public Map<String, Object> passReport(HttpSession session,@PathVariable Long boardId,
//		Long reportId){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		Map<String, Object> temp = 
//			new AdminsController()
//			.passReport(session, reportId);
//		result.put("responseCode", temp.get("responseCode"));
//		return result;
//	}
	
//	본인 운영자 권한 소거 및 운영자 추가
	@RequestMapping(value = "/manager/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
	public Map<String, Object> changeToBoardUser(HttpServletRequest request, Long userId){
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
			return result;
		}
		result.put("userData", User.USER_STATUS[User.MANAGER]);
		result.put("responseCode", CheckResult.checkToProcess(
			bmService.changToUser(userId, request.getMethod())));
		return result;
	}
	
////	해당 게시판의 신고 목록 확인
//	@GetMapping("/question")
//	public Map<String, Object> findQuestionList(HttpSession session,@PathVariable Long boardId,
//		@RequestParam(defaultValue = "1")int page){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		
//		List<Question> qList = qService.findByBoardId(boardId);
//		int process = CheckResult.nullToProcess(qList);
//		result.put("responseCode", process);
//		if(process == 200) {
//			try {
//				qList = Pagination.paging(qList, page);
//				result.put("questionList", qList);
//				result.put("totalPage", Pagination.perPage(qList));
//			} catch (Exception e) {
//				result.put("responseCode", 404);
//			}
//		}
//		return result;
//	}
//	
////	해당 게시판의 신고 목록 확인
//	@GetMapping("/question/{id}")
//	public Map<String, Object> answerQuestion(HttpSession session,@PathVariable Long boardId,
//		@PathVariable Long questionId, @RequestBody Map<String, Object> json){
//		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(session)) {
////				result.put("responseCode", 401)		http 코드	로그인되지 않은 사용자
//			return result;
//		}
//		result.put("userData", User.USER_STATUS[User.MANAGER]);
//		
//		Map<String, Object> temp = new AdminsController().updateQuestion(session, questionId, result);
//		
//		result.put("responseCode", temp.get("responseCode"));
//		return result;
//	}
}
