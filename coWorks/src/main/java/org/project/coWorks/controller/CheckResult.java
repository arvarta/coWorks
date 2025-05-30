package org.project.coWorks.controller;

import jakarta.servlet.http.HttpSession;

public class CheckResult {
	public static final int EXIST = -5;
	public static final int NO_EXIST_CHAINGE_INFO = -4;
	public static final int NO_MATCH = -3;
	public static final int NO_EXIST_BOARD_USER = -2;
	public static final int NO_EXIST = -1;
	public static final int FAIL = 0;
	public static final int SUCCESS = 1;
	public static final int PARTIAL_SUCCESS = 2;
	
	public static boolean isLoggedIn(HttpSession session) {
		return (session.getAttribute("user") != null);
	}
	
//	프로세스 처리 여부에 따른 코드 번호 출력
	public static int checkToProcess(int process) {
		switch(process) {
			case NO_EXIST_BOARD_USER :
			case EXIST :
			case NO_EXIST : {
				return 204;		//http 코드	해당 요청의 콘텐츠가 존재하지 않음
			}case NO_EXIST_CHAINGE_INFO :{
				return 304;	//	http 코드	바꿀 데이터가 없음(수정 안됨)
			}case SUCCESS : {
				return 200;//		http 코드	성공 (활성화 혹은 거부)
			}case PARTIAL_SUCCESS : {
				return 206;		//http 코드	부분 저장됨
			}
		}
		return 500;
	}
	
//	null 여부에 따른 코드 번호 출력
	public static int processToNull(Object o) {
		if(o != null) {
			return 200;	//	http 코드 성공
		}else {			
			return 204;	//	http 코드 승인된 계정이 없음
		}
	}
}
