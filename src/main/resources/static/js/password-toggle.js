document.addEventListener("DOMContentLoaded",()=>{
    document.querySelectorAll(".password-toggle").forEach(el => {
        el.addEventListener("click",function () {
            const targetId=this.getAttribute("data-target");
            const input=document.getElementById(targetId);
            const icon=document.getElementById("icon-"+targetId);

            if(!input) return;
            const isPassword =input.type==="password";
            //切り替え
            input.type=isPassword ? "text":"password";

            //アイコン切り替え
            icon.classList.toggle(("bi-eye"));
            icon.classList.toggle("bi-eye-slash");
        });
    });
})