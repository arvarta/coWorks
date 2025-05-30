import { getOrgUserList } from "../service/userOrg.js";

const memberListDiv = document.querySelector(".member_list");

try {
	// 사용자 상세 목록
	if(memberListDiv) {
		getOrgUserList(memberListDiv);
	}
	
} catch(Err) {
	console.error(Err);
}

