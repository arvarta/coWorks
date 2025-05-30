import { getPostLists, getMyBoardList} from "../service/userBoardInfo.js";

const postLists = document.querySelector("#post_list");
const leftBoardList = document.querySelector("#my_board_list");
const paginationContainer = document.querySelector(".pagination");
try {
    // 레프트 메뉴 메뉴 목록
    if (leftBoardList) {
        getMyBoardList(leftBoardList);
    }
    // 특정 게시판의 게시글 목록
	if (postLists) {
        getPostLists(postLists, paginationContainer);
		
    }
	// 특정 게시판 회원 목록
	
} catch (Err) {
    console.error(Err);
}
