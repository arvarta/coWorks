import {
	setBoardTitle, getPostLists, boardManage
} from "../service/adminBoardInfo.js";

const divTitle = document.querySelector("#admin_title");
const divSubtitle = document.querySelector("#admin_title_sub");
const tabList = document.querySelectorAll("#boardList_ul li");
const postTable = document.querySelector("#post_list");
const postTitle = document.querySelectorAll("input.post_title");
const userList = document.querySelector("div.member_list");
const blind = document.querySelectorAll("input[type='radio']");

try {
	// 상단 게시판 정보
	if(divTitle && divSubtitle && tabList) {
		setBoardTitle(divTitle, divSubtitle, tabList);
	}
	
	// 게시글 목록
	if(postTable) {
		getPostLists(postTable);
	} 
	// 게시판 정보 확인 및 수정
	if(postTitle) {
		boardManage(postTitle, userList, blind);
		
	}
} catch(err) {
	
}
