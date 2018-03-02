$(function () {
  // 使用顶层窗口登录
  if (window !== window.top) {
    window.top.location.href = 'login.html';
  }

  // Waves初始化
  Waves.displayEffect();
  // 输入框获取焦点后出现下划线
  $('.form-control').focus(function () {
    $(this).parent().addClass('fg-toggled');
  }).blur(function () {
    $(this).parent().removeClass('fg-toggled');
  });

  // 点击登录按钮
  $('#login-button').click(loginAction);
  // 回车事件
  $('#username, #password').keypress(function (event) {
    if (13 === event.keyCode) {
      loginAction();
    }
  });
});

/**
 * 登录请求
 */
function loginAction() {
  $.ajax({
    url: 'login',
    type: 'POST',
    dataType: 'json',
    data: {
      username: $('#username').val(),
      password: $('#password').val()
    },
    success: function (result) {
      if (result.success) {
        window.top.location.href = 'index.html';
      } else {
        loginFailure();
      }
    },
    error: loginFailure
  });
}

/**
 * 登录失败
 */
function loginFailure() {
  $('#login-alert').text('登录失败，请检查账号密码是否正确！').show();
  setTimeout(function () {
    $('#login-alert').hide();
  }, 8000);
}