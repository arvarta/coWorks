package org.project.coWorks.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.project.coWorks.dto.ProfileRequestDto;
import org.project.coWorks.model.Board;
import org.project.coWorks.model.Post;
import org.project.coWorks.model.Proposal;
import org.project.coWorks.model.User;
import org.project.coWorks.service.BoardService;
import org.project.coWorks.service.EmployeeService;
import org.project.coWorks.service.ProposalService;
import org.project.coWorks.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
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

@RestController
@RequestMapping("/admins")
public class AdminsController {
	
	@Autowired
	private UserService uService;
	@Autowired
	private ProposalService propService;
	@Autowired
	private BoardService bService;
	@Autowired
	private EmployeeService eService;
	
	@GetMapping("/start")
	public Map<String, Object> rootHome(HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);		// http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		result.put("joinSignCount", uService.userCount(User.USER_STATUS[User.STAND]));
		result.put("proposalCount", propService.pCount());
		
		return result;
	}
	
	// 게시판 생성 요청 목록 조회
	@GetMapping("/proposal/list")
	public Map<String, Object> getProposalList(HttpSession session) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(session)) {
			result.put("responseCode", 401);		// http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		List<Proposal> proposalList = propService.getAll();
		int process = CheckResult.processToNull(proposalList);
		result.put("responseCode", process);
		if(process == 200)
			result.put("proposalList", proposalList);
		return result;
		
	}
	
	// 게시판 생성 요청 승인 및 거절
	@RequestMapping(value = "/proposal/{id}", method = {RequestMethod.POST, RequestMethod.DELETE})
	public Map<String, Object> processProposal(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		result.put("responseCode", CheckResult.checkToProcess(propService.processProposal(id, request.getMethod())));
		return result;
	}
	

	// 사용자 목록 조회
	@GetMapping("/user/list")	//오류시 쿼리 문제
	public Map<String, Object> findUserSignList(HttpSession session,
		@RequestParam(required = false) String userStatus, 
		@RequestParam(defaultValue = "1") int page) {
		Map<String, Object> result = new HashMap<String, Object>();
				if(!CheckResult.isLoggedIn(session)) {
						result.put("responseCode", 401);	//	http 코드	로그인 하지 않은 유저의 접근 시도
					return result;
				}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		List<ProfileRequestDto> userList = new ArrayList<>();
		List<User> uList = uService.findByUserStatus(userStatus);
		for(User u : uList) {
			ProfileRequestDto temp = uService.getUserInfo(u.getUserId());
			if(temp !=null) {
				userList.add(temp);
			}
		}
		int process = CheckResult.processToNull(userList);
		result.put("responseCode", process);
		if(process == 200) {
			try {
				result.put("totalCount", userList.size());
				result.put("userList", Pagination.paging(userList, page));
				result.put("totalPage", Pagination.totalPage(userList));
				
			} catch (Exception e) {
				result.put("responseCode", 500);
			}
		}
		return result;
	}
	
	// 유저 승인 허용 및 거부
	@RequestMapping(value = "/user/{id}/sign", method = {RequestMethod.POST, RequestMethod.DELETE})
	public Map<String, Object> updateUserStatus(HttpServletRequest request, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<String, Object>();
//		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
//			return result;
//		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		result.put("responseCode", CheckResult.checkToProcess(uService.updateUser(id, request.getMethod())));
		return result;
		
	}
	
//	유저 목록 조회
	@GetMapping("/user")
	public Map<String, Object> getUserList(HttpSession session,
		String userStatus) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
//		활성화 여부에 따른 사용자 확인 (default = User.ALLOW)
		String status = userStatus != null ? 
				userStatus : User.USER_STATUS[User.USER];
		List<User> userList = uService.findByUserStatus(status);
		int process = CheckResult.processToNull(userList);
		result.put("responseCode", process);
		if(process == 200)
			result.put("userList", userList);
		return result;
	}
	
//	지정 유저 삭제
	@DeleteMapping("/user/{id}")
	public Map<String, Object> deleteUser(HttpSession session, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		result.put("responseCode", CheckResult.checkToProcess(uService.deleteUser(id)));
		
		return result;
	}
	
//	지정 유저 정보 조회
	@GetMapping("/user/{id}")
	public Map<String, Object> userInfo(HttpSession session, @PathVariable Long id) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		Optional<User> user = uService.getUserById(id);
		int process = CheckResult.processToNull(user);
		if(process == 200)
			result.put("user-info", user);
		result.put("responseCode", process);
		
		return result;
	}
	
//	게시판 목록 조회
	@GetMapping("/board")
	public Map<String, Object> getBoardList(HttpSession session) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		List<Board> boardList = bService.getAll();
		int process = CheckResult.processToNull(boardList);
		if(process == 200)
			result.put("boardList", boardList);
		result.put("responseCode", process);
		
		return result;
	}

//	게시판 추가
	@PostMapping("/board")
	public Map<String, Object> addBoard(HttpSession session,
		@RequestBody Map<String, Object> json) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		Board board = (Board)json.get("board");
		
		result.put("responseCode", CheckResult.checkToProcess(bService.save(board)));
		
		return result;
	}
	
//	특정 게시판 삭제 및 숨김
	@RequestMapping(value = "/board/{id}", method = {RequestMethod.DELETE, RequestMethod.POST})
	public Map<String, Object> updateBoard(HttpServletRequest request,@PathVariable Long id) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(request.getSession())) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		result.put("response", 
				CheckResult
				.checkToProcess(
					bService
					.visibleReverseBoard(id, request.getMethod()))
		);
		return result;
	}
	
//	특정 게시판 정보 조회
	@GetMapping("/board/{id}")
	public Map<String, Object> boardInfo(HttpSession session, @PathVariable String id) {
		Map<String, Object> result = new HashMap<>();
		if(!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401);		http 코드	로그인 하지 않은 유저의 접근 시도
			return result;
		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		Board board = bService.findById(Long.parseLong(id));
		int process = CheckResult.processToNull(board);
		if(process == 200)
			result.put("board", board);
		result.put("responseCode", process);
		
		return result;
	}
	
	// 특정 게시판 내 게시글 목록 조회
	@GetMapping("/board/{id}/post")
	public Map<String, Object> boardPostList(@Autowired HttpSession session, @PathVariable String id,
			@RequestParam(defaultValue = "1") int page) {
		Map<String, Object> result = new HashMap<>();
//		if (!CheckResult.isLoggedIn(session)) {
//			result.put("responseCode", 401);		// http 코드 로그인 하지 않은 유저의 접근 시도
//			return result;
//		}
		result.put("userData", User.USER_STATUS[User.ADMIN]);
		List<Post> pList = bService.getPostsByBoardId(Long.parseLong(id));
		Board boardInfo = bService.getBoardByBoardId(Long.parseLong(id));
		try {
			result.put("postList", Pagination.paging(pList, page));
			result.put("totalPage", Pagination.totalPage(pList));
			result.put("boardInfo", boardInfo);
			result.put("responseCode", CheckResult.SUCCESS);
		} catch (Exception e) {
			result.put("responseCode", CheckResult.FAIL);
		}
		return result;		
	}
	
}

//	작성자 및 내용으로 post 찾기
//@GetMapping("/post")
//public Map<String, Object> findPost(HttpSession session, @RequestBody Map<String, Object> json) {
//	Map<String, Object> result = new HashMap<>();
//	User user = (User)session.getAttribute("user-info");
//	if(user == null) {
//			result.put("responseCode", 404);		http 코드 로그인되지 않은 사용자
//			return result;
//	}
//	String keyword = (String)json.get("keyword");
//	if(keyword == null) {
//			result.put("responseCode", 404);		http 코드 키워드(내용/제목/작성자)가 정해지지 않음
//			return result;
//	}
//	String value = (String)json.get("value");
//	if(value == null) {
//			result.put("responseCode", 404);		http 코드 지정 키워드의 내용이 없음(검색어 확인 불가)
//			return result;
//	}
//	List<Post> list = pService.findPostByWriterOrValue(user, keyword, value);
//	if(list != null && list.size() > 0) {
//		result.put("list", list);
//			result.put("responeseCode", 200);			http 코드 성공
//	}else {
//			result.put("responseCode", 204);		http 코드 지정 키워드의 내용이 없음(검색어 확인 불가)
//	}
//	return result;
//}