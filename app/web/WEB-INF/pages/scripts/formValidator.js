$(function () {
    $("form[name='loginForm']").validate({
        rules: {
            login: "required",
            password: "required"
        },
        messages: {
            login: "Please enter your login",
            password: "Please provide a password",
        },
        submitHandler: function (form) {
            form.submit();
        }
    });
});