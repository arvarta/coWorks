import {
	getMyProfile, checkFaq,
	updateMyProfile, changeMyPassword, getMypostList,
	verifyBeforeBye, userGoodBye
} from "../service/userMypage.js";

const profile = document.querySelector(".profile");
const inforList = document.querySelectorAll(".infor_list li");
const questionDiv = document.querySelectorAll(".faq_question");
const answerDiv = document.querySelectorAll(".faq_answer");
const myPostList = document.querySelector("table#my_post_list");
const withdrawConfirm = document.querySelector("#withdraw_confirm");
const withdrawSubmit = document.querySelector("#withdraw_verify");
const pwInputs = document.querySelectorAll("input[type='password']");

try {
	// 마이페이지
	if(profile && inforList) {
		getMyProfile(profile, inforList);
		
	} 
	
	// faq
	if(questionDiv && answerDiv) {
		checkFaq(questionDiv, answerDiv);
	}
	
	// 내가 쓴 글 목록
	if(myPostList !== null) {
		getMypostList(myPostList);
		
	// 회원 탈퇴 절차 1
	} else if (withdrawConfirm) {
		userGoodBye(withdrawConfirm);
	
	// 회원 탈퇴 절차 2
	} else if(withdrawSubmit) {
		verifyBeforeBye(withdrawSubmit);
	}
	// 비밀번호 변경
	changeMyPassword(pwInputs);
} catch(Err) {
	console.error(Err);
}