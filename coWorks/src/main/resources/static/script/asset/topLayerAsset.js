import { searchGroupSelect, openProfile, searchBox, getMyProfile, logout, leftUiOn } from "../service/topLayer.js";
import { getMyBoardList } from "../service/userBoardInfo.js";
import { getDepartmentList } from "../service/userOrg.js";

try {
	
	$("#header").load("/user/header.html", function() {
		
		const searchGroupSelectBtn = document.querySelector(".search_group_select");
		const detailBtn = document.querySelector(".btn_search_detail");
		const detailPop = document.querySelector(".search_detail_pop");
		const searchBtn = document.querySelector(".btn_search"); // 검색 버튼
		const profileArea = document.querySelector(".profile_area");
		const loginUserMenu = document.querySelector(".login_user_menu");
		const userProfileDiv = document.querySelector(".user_profile");
		const logoutBtn = document.querySelector("#logout_btn");
		
		logout(logoutBtn);		
		searchGroupSelect(searchGroupSelectBtn);
		searchBox(detailBtn, detailPop, searchBtn);
		openProfile(profileArea, loginUserMenu);
		getMyProfile(userProfileDiv);
	
	});
	
	$("#left").load("/user/left.html", function() {
		const leftBoardList = document.querySelector("#my_board_list");
		getMyBoardList(leftBoardList);
	});
	$("#left_manager").load("/manager/left_manager.html");
	$("#left_organization").load("/user/left_organization.html", function() {
		const depMenu = document.querySelector("#org_menu");
		getDepartmentList(depMenu);
	});
	$("#left_my").load("/user/left_my.html", () => {
		const menu = document.querySelector("#left_my_list");
		leftUiOn(menu);
	});
	
	$("#header_admin").load("/admin/header_admin.html", function() {
		const logoutBtn = document.querySelector("#logout_btn");
		logout(logoutBtn);
		
	});
	$("#left_admin").load("/admin/left.html");
	$("#header_index").load("/header.html");
	
	
} catch(Err) {
	console.error(Err);
}

