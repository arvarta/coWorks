package org.project.coWorks.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
@CrossOrigin
public class RootController {
	
	// 로그인 시작화면
	@GetMapping(value={"/", "/users"})
	public String index() {
		return "login";
	}
	
	// 회원가입
	@GetMapping("/membership")
	public String join() {
		return "join";
	}
	
	// 비밀번호 찾기
	@GetMapping("/password")
	public String findPassword() {
		return "pw_find";
	}
	
	// 새 비밀번호 설정
	@GetMapping("/account")
	public String newPassword() {
		return "pw_renew";
	}
	
	// 로그아웃
	@GetMapping("/logout")
	public String logout() {
		return "redirect:/users";
	}
	
	// ============================== users
	
	// 사용자 : 메인화면, 게시글 목록
	@GetMapping(value={"/user/main", "/user/board", "/user/board/{boardId}"})
	public String userMain() {
		return "user/main";
	}
	
	// 사용자 : 마이페이지(프로필)
	@GetMapping("/user/mypage")
	public String userMyPage() {
		return "user/my_info";
	}
	
	// 사용자 : 프로필 수정
	@GetMapping("/user/profile")
	public String userProfile() {
		return "user/my_info_modify";
	}
	
	// 사용자 : 내가 쓴 글들 확인
	@GetMapping("/user/post-list")
	public String userPostList() {
		return "user/my_post";
	}
	
	// 사용자 : 게시판 생성 신청 내역
	@GetMapping("/user/proposal-list")
	public String userBoardAddList() {
		return "user/my_boardAdd";
	}
	
	// 사용자 : 게시판 생성 신청 내역 상세
	@GetMapping("/user/proposal-info")
	public String userBoardAddView() {
		return "user/my_boardAdd_view";
	}
	
	// 사용자 : 자주하는 질문
	@GetMapping("/user/faq")
	public String userFAQ() {
		return "user/my_faq";
	}
	
	// 사용자 : 검색
	@GetMapping("/user/result")
	public String userSearch() {
		return "user/main_search_result";
	}
	
	// 사용자 : 글쓰기 글작성
	@GetMapping("/user/post")
	public String userPost() {
		return "user/post_write";
	}
	
	// 사용자 : 글보기
	@GetMapping("/user/post/{postId}")
	public String userPostView() {
		return "user/post_view";
	}
	
	// 사용자 : 게시판 추가 요청
	@GetMapping("/user/proposal")
	public String userBoardAdd() {
		return "user/main_addBoard";
	}
	
	// 사용자 : 조직도
	@GetMapping(value={"/user/organization", "/user/organization/{deptId}"})
	public String userOrganization() {
		return "user/organization_chart";
	}
	
	// 사용자 : 회원 탈퇴
	@GetMapping("/user/membership")
	public String userByebye1() {
		return "user/my_sighOut_step01";
	}
	
	// 사용자 : 회원 탈퇴
	@GetMapping("/user/account")
	public String userByebye2() {
		return "user/my_sighOut_step02";
	}
	
	// 사용자 : 찾아오는 길
	@GetMapping("/user/map")
	public String userMap() {
		return "user/map";
	}
	
	
	// ============================== manager
	
	// 운영자 : 메인 + 사용자 목록
	@GetMapping(value={"/manager/main", "/manager/user"})
	public String managerMain() {
		return "manager/memberList";
	}
	
	// 운영자 : 정보 관리
	@GetMapping("/manager/info")
	public String managerInfo() {
		return "manager/info";
	}
	
	// 운영자 : 레벨
	@GetMapping("/manager/level")
	public String managerLevel() {
		return "manager/level";
	}
	
	// 운영자 : 숨겨진 게시글 관리
	@GetMapping(value={"/manager/hiddens", "/manager/post"})
	public String managerHiddenPost() {
		return "manager/hiddenList";
	}
	
	// 운영자 : 숨겨진 댓글 관리
	@GetMapping("/manager/comment")
	public String managerHiddenComment() {
		return "manager/hiddenList_comment";
	}
	
	// 운영자 : 운영자 관리
	@GetMapping("/manager/list")
	public String managerList() {
		return "manager/managerList";
	}
	
	
	// ============================== admins

	// 관리자 : 메인화면(대시보드)
	@GetMapping(value={"/admin", "/admin/main"})
	public String adminMain() {
		return "admin/main";
	}
	
	// 관리자 : 게시판 관리
	@GetMapping("/admin/board")
	public String adminBoard() {
		return "admin/boardList";
	}
	
	// 관리자 : 전체 사용자
	@GetMapping("/admin/user")
	public String adminUserList() {
		return "admin/userList_all";
	}
	
	// 관리자 : 회원 가입 신청목록
	@GetMapping("/admin/join-list")
	public String adminJoinList() {
		return "admin/userList_signIn";
	}
	
	// 관리자 : 회원 탈퇴 신청 내역
	@GetMapping("/admin/out-list")
	public String adminOutList() {
		return "admin/userList_signOut";
	}
	
	// 관리자 : 게시판 생성 신청 목록
	@GetMapping("/admin/proposal")
	public String adminProposal() {
		return "admin/addBoardList";
	}
	
	// 관리자 : 게시판 상세 - 게시글 목록
	@GetMapping(value={"/admin/board-post", "/admin/board/{boardId}", "/admin/board/{boardId}/post"})
	public String boardInfo() {
		return "admin/boardList_post";
	}
	
	// 관리자 : 게시판 상세 - 게시판 정보 관리
	@GetMapping(value={"/admin/board-info", "/admin/board/{boardId}/info"})
	public String boardManage() {
		return "admin/boardList_info";
	}
	
	// 관리자 : 게시판 상세 - 숨겨진 게시글
	@GetMapping(value={"/admin/board-hidden", "/admin/board/{boardId}/hidden"})
	public String boardHidden() {
		return "admin/boardList_hiddenPost";
	}
	
	// 관리자 : 게시판 상세 - 1:1 문의 목록
	@GetMapping("/admin/board-question")
	public String boardQuestion() {
		return "admin/boardList_question";
	}
	
	// 관리자 : 글 작성
	@GetMapping("/admin/post")
	public String adminWrite() {
		return "admin/post_write";
	}
	
}
