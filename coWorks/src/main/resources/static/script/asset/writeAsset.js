import { showBoards, chooseBoard, checkNull, uploadFileController, whiteController } from "../service/write.js";

document.querySelector("button.group_select").addEventListener("click", showBoards);
document.querySelector(".group_option_item").addEventListener("click", chooseBoard);
try {
	// 에디터 및 파일 첨부 기능 활성화
	whiteController.initBoardList();
	whiteController.runEditor("editor-container");
	uploadFileController.detectStart();
	
} catch(Error) {
	console.error(Error);
}
