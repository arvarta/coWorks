import { addBoardTargetController } from "../service/addBoard.js";

console.log(document.querySelector(".pop_con .btn_core"));

if (document.querySelector(".pop_con .btn_core")) {
    addBoardTargetController.run();
}
