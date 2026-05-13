let copiedData = null;
let copiedRow = null;

//行選択
document.querySelectorAll(".attendance-row")
    .forEach(row => {
        row.addEventListener("click", function (e) {
            //Input押下時無視
            if (e.target.tagName === "INPUT") {
                return;
            }

            //全選択解除
            document.querySelectorAll(".attendance-row")
                .forEach(r =>
                    r.classList.remove("selected-row"));

            //選択
            this.classList.add("selected-row");
        });
    });

//Copy
document.getElementById("copyBtn")
    .addEventListener("click", function () {
        const row = document.querySelector(".selected-row");
        if (!row) {
            alert("Select row.");
            return;
        }

        //コピー元色解除
        document.querySelectorAll(".attendance-row")
            .forEach(r => r.classList.remove("copied-row"))

        //コピー元設定
        row.classList.add("copied-row");

        copiedRow = row;
        copiedData = {
            start: row.querySelector(
                'input[name*="startTime"]'
            ).value,
            end: row.querySelector(
                'input[name*="endTime"]'
            ).value,
            break_start:row.querySelector(
                'input[name*="breakStart"]'
            ).value,
            break_end:row.querySelector(
                'input[name*="breakEnd"]'
            ).value,
            note: row.querySelector(
                'input[name*="note"]'
            ).value
        };
    });

//Paste
document.getElementById("pasteBtn")
    .addEventListener("click", function () {
        //コピー末実行
        if (!copiedData) {
            alert("Copy row first.");
            return;
        }

        //貼り付け先
        const targetRow = document.querySelector(".selected-row");
        if (!targetRow) {
            alert("Select paste target row.");
            return;
        }
        //同一行禁止
        if (targetRow === copiedRow) {
            return;
        }
        //貼り付け
        const startInput=targetRow.querySelector('input[name*="startTime"]'
        );
        startInput.value = copiedData.start;
        const endInput=targetRow.querySelector('input[name*="endTime"]'
        );
        endInput.value = copiedData.end;
        const breakStartInput=targetRow.querySelector('input[name*="breakStart"]'
        );
        breakStartInput.value=copiedData.break_start;
        const breakEndInput=targetRow.querySelector('input[name*="breakEnd"]'
        );
        breakEndInput.value=copiedData.break_end;
        targetRow.querySelector('input[name*="note"]'
        ).value = copiedData.note;

        // リアルタイム再計算
        startInput.dispatchEvent(
            new Event("input")
        );

        endInput.dispatchEvent(
            new Event("input")
        );

        breakStartInput.dispatchEvent(
            new Event("input")
        );

        breakEndInput.dispatchEvent(
            new Event("input")
        );
    });
