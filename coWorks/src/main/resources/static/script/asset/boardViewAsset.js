import { boardViewController, viewPost, viewComment, addComment} from "../service/boardView.js";

const commentSubmit = document.querySelector(".comment_submit");

const postView = document.querySelector(".post_view");
const commentView = document.querySelector(".comment_list");

try {
	viewPost(postView);
	viewComment(commentView);
	boardViewController.listenerStart();
	commentSubmit.addEventListener("click", addComment);
} catch(Err) {
	console.error(Err);
}