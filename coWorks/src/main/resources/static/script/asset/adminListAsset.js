import { userList, boardList, getSignInUsers, getSignOutUsers, getAddLists } from "../service/adminList.js";

const listUl = document.querySelector("#allLists");
const countEm = document.querySelector("em");
const listTable = document.querySelector("#listTable");
const signIns = document.querySelector("#signIns");
const signOuts = document.querySelector("#signOuts");
const addLists = document.querySelector("#addLists");
const popTop = document.querySelector(".pop_top");

// 페이지네이션 컨테이너 선택
const paginationContainer = document.querySelector(".pagination");

try {
	// 전체 사용자
	if (listUl && countEm && paginationContainer) {
		userList(listUl, countEm, paginationContainer);

	// 회원가입 신청 내역
	} else if (signIns && countEm && paginationContainer) {
		getSignInUsers(signIns, countEm, paginationContainer);

	// 회원탈퇴 신청 내역
	} else if (signOuts && countEm && paginationContainer) {
		getSignOutUsers(signOuts, countEm, paginationContainer);

	// 게시판 목록
	} else if (listTable) {
		boardList(listTable, paginationContainer);

	// 게시판 추가 신청 내역
	} else if (addLists && paginationContainer) {
		getAddLists(addLists, paginationContainer);
	}
} catch (err) {
	console.error(err);
}
