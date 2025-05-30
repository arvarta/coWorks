import { checkUserSession } from "../util/checking.js";
/*
	회원 가입 신청목록
	게시판 생성 신청 목록
	신고 목록
	1:1 문의 목록
 */
const anchors = [
	"join-list",
	"proposal",
	"report",
	"question"
];

// 대시보드 카드
export async function buttonAnchor(cardColumns, itemData) {
	// 받아온 대시보드 통계들
	const res = await fetch("/admins/start");
	const data = await res.json();
	checkUserSession(data);
	
	const statsCount = [
		data.joinSignCount,
		data.proposalCount
	];
	
	// 대시보드에 a태그, 통계 차례로 넣기
	cardColumns.forEach((item, idx) => {
		item.addEventListener('click', () => {
			location.href = anchors[idx];
		});
		console.log(statsCount[idx]);
		itemData[idx].innerText = statsCount[idx];
	});
}
