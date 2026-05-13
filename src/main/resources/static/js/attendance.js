document.addEventListener("DOMContentLoaded", () => {
    function calc(row) {
        const start = row.querySelector("input[name*='startTime']").value;
        const end = row.querySelector("input[name*='endTime']").value;
        const breakStart = row.querySelector("input[name*='breakStart']").value;
        const breakEnd = row.querySelector("input[name*='breakEnd']").value;
        const output = row.querySelector(".work-time");

        if (!start || !end) {
            output.textContent = "-";
            return;
        }

        //時刻→分変換
        const startMin = toMinutes(start);
        const endMin = toMinutes(end);

        //invalid
        if (startMin == null || endMin == null) {
            output.textContent = "-";
            return;
        }

        //就業時間
        let diff = endMin - startMin;
        //休憩時間
        if (breakStart && breakEnd) {
            const breakStartMin = toMinutes(breakStart);
            const breakEndMin = toMinutes(breakEnd);

            //validのみ計算
            if (breakStartMin != null && breakEndMin != null) {
                const breakDiff = breakEndMin - breakStartMin;
                diff -= breakDiff;
            }
        }
        if (diff <= 0) {
            output.textContent = "-";
            return;
        }

        const h = Math.floor(diff / 60);
        const m = diff % 60;
        output.textContent = h + "h" + (m ? m + "m" : "");
    }

    //hh:mm→分変換
    //invalidはNull
    function toMinutes(value) {
        const regex = /^([01]\d|2[0-3]):([0-5]\d)$/;
        if (!regex.test(value)) {
            return null;
        }
        const [h, m] = value.split(":").map(Number);
        return h * 60 + m;
    }

    //イベント
    document.querySelectorAll("tbody tr").forEach(row => {
        row.querySelectorAll(".time-text").forEach(input => {
            input.addEventListener("input", () => calc(row));
        });
        calc(row);
    });
    //時間自動フォーマット
    //0900→09:00
    document.querySelectorAll(".time-text").forEach(input => {
        //入力時
        input.addEventListener("input", function () {
            //数字以外削除
            let value = this.value.replace(/\D/g, "");
            //最大４桁
            value = value.substring(0, 4);
            //4桁時
            if (value.length >= 3) {
                value = value.substring(0, 2) + ":" + value.substring(2);
            }
            this.value = value;
        });
    });

    window.addEventListener("resize", () => {
        document.querySelector(".table-wrapper").scrollTop = 0;
    });
    window.addEventListener("orientationchange", () => {
        location.reload();
    });

    //保存前バリデーション
    document.getElementById("attendanceForm")
        .addEventListener("submit", function (e) {
            let hasError = false;

            //全時間入力
            document.querySelectorAll(".time-text")
                .forEach(input => {
                    //エラー初期値
                    input.classList.remove("time-error");
                    const error = input.parentElement.querySelector(".invalid-feedback");
                    if (error) {
                        error.classList.remove("show-error");
                    }
                    //未入力OK
                    if (!input.value) {
                        return;
                    }

                    //hh:mmチェック
                    const regex = /^([01]\d|2[0-3]):([0-5]\d)$/;
                    if (!regex.test(input.value)) {
                        hasError = true;

                        //赤枠
                        input.classList.add("time-error");

                        //メッセージ表示
                        if (error) {
                            error.classList.add("show-error");
                        }
                    }
                });
            //保存停止
            if (hasError) {
                e.preventDefault();
                alert("Invalid time format exists.");
            }
        });
});