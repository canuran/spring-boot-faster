// 全局字典数据
var dictionaries = Utils.getDictionaries(["GENDER"]);

// 全局表格对象
var $mainTable = false;
/**
 * 初始方法
 */
$(document).ready(function () {
  // 数据表格初始化
  $mainTable = $('#mainTable').bootstrapTable({
    url: '../user/findUserWithRole',
    method: 'post',
    queryParams: function (params) {
      params.username = $('#usernameSearch').val();
      params.nickname = $('#nicknameSearch').val();
      return params;
    },
    height: $(window).height() - 20,
    striped: true,
    showRefresh: true,
    showToggle: true,
    showColumns: true,
    clickToSelect: true,
    singleSelect: true,
    minimumCountColumns: 2,
    pagination: true,
    paginationLoop: false,
    sidePagination: 'server',
    pageList: [10, 20, 50, 100],
    silentSort: true,
    smartDisplay: false,
    uniqueId: 'id',
    escape: true,
    toolbar: '#tableToolbar',
    responseHandler: function (result) {
      return result && result.success && result.data || {total: 0};
    },
    columns: [
      {field: 'userId', title: '标识', align: 'center', valign: 'middle', visible: false},
      {field: 'username', title: '用户名', align: 'center', valign: 'middle', switchable: false},
      {field: 'nickname', title: '昵称', align: 'center', valign: 'middle'},
      {
        field: 'gender', title: '性别', align: 'center', valign: 'middle',
        formatter: function (value) {
          return (dictionaries['GENDER'] || {})[value] || value;
        }
      }, {field: 'birthday', title: '出生日期', align: 'center', valign: 'middle'},
      {
        field: 'roles', title: '角色', align: 'center', valign: 'middle',
        width: '40%', formatter: function (roles) {
          return (roles || []).map(function (value) {
            return value.name || value.roleId;
          }).join(',') || null;
        }
      }, {
        field: 'editAction', title: '修改', width: '30px', cardVisible: false,
        align: 'center', valign: 'middle', formatter: function () {
          return '<div class="edit_button pointer_hover">' +
            '<i class="glyphicon glyphicon-edit"></i></div>';
        }
      }, {
        field: 'deleteAction', title: '删除', width: '30px', cardVisible: false,
        align: 'center', valign: 'middle', formatter: function () {
          return '<div class="delete_button pointer_hover">' +
            '<i class="glyphicon glyphicon-remove"></i></div>';
        }
      }
    ], onClickCell: function (field, value, row) {
      if (field === 'editAction') {
        editAction(row);
      } else if (field === 'deleteAction') {
        deleteAction(row);
      }
    }
  });

  // 数据表格动态高度
  $(window).resize(function () {
    $('#mainTable').bootstrapTable('resetView', {
      height: $(window).height() - 20
    });
  });

  // 日期控件初始化
  layui.laydate.render({
    elem: '#dateInput'
  });

});

/**
 * 搜索内容
 */
function searchAction() {
  $mainTable.bootstrapTable('refresh');
}

var resetData = false;

// 角色数据
var allRoles = false;
var $roleSelect = false;

/**
 * 初始化角色选择
 */
function resetRolesSelect(data) {
  if ($roleSelect) {
    $roleSelect.val((data || [])
      .map(function (value) {
        return value && value.roleId;
      })).trigger('change');
  } else {
    $.ajax({
      url: "../user/getAllRoles",
      method: "post",
      dataType: "json",
      async: false,
      success: function (result) {
        allRoles = (result && result.data || []);
        for (var i in allRoles) {
          allRoles[i].id = allRoles[i].roleId;
          allRoles[i].text = allRoles[i].name;
        }
        $roleSelect = $("#rolesSelect").select2({
          width: '100%', language: 'zh-CN', placeholder: "请选择角色",
          allowClear: true, closeOnSelect: false, data: allRoles
        });
        resetRolesSelect(data);
      }
    });
  }
}

/**
 * 重置表单
 */
function resetEditForm() {
  $('#editForm')[0].reset();
  if (resetData) {
    $("input[name='username']").val(resetData.username);
    $("input[name='nickname']").val(resetData.nickname);
    $("input[name='password']").val(resetData.password);
    $("input[name='gender'][value='" + resetData.gender + "']").trigger('click');
    $("input[name='birthday']").val(resetData.birthday);
    resetRolesSelect(resetData.roles);
  } else {
    $("input[name='gender'][value='SECRET']").trigger('click');
    resetRolesSelect();
  }
}

// 窗口索引
var formWinIndex = layer.index;

/**
 * 打开新增窗口
 */
function openEditWin(title) {
  resetEditForm();
  formWinIndex = layer.open({
    id: 'formWinOpen',
    title: '<i class="glyphicon glyphicon-tasks"></i> ' + title,
    offset: '50px',
    area: ['600px'],
    zIndex: 10,
    type: 1,
    content: $('#formWin'),
    end: function () {
      $('#formWin').hide();
    }
  });
}

/**
 * 手动关闭窗口
 */
function closeFormWin() {
  layer.close(formWinIndex);
}

/**
 * 绑定表单提交事件
 */
function forSubmitAction(editData, action) {
  // 提交表单
  layui.form.on('submit', function (data) {
    // 获取选中的角色并转换成角色对象
    editData.roles = ($roleSelect.val() || [])
      .map(function (value) {
        for (var i in allRoles) {
          if (allRoles[i].roleId == value) {
            return allRoles[i];
          }
        }
        return {roleId: value};
      });
    editData.username = data.field.username;
    editData.nickname = data.field.nickname;
    editData.password = data.field.password;
    editData.birthday = data.field.birthday;
    editData.gender = data.field.gender;
    action(editData);
    return false;
  });
}

/**
 * 添加项目
 */
function createAction() {
  resetData = false;
  openEditWin('新增用户');
  $("input[name='username']").removeAttr("disabled");
  forSubmitAction({}, function (afterData) {
    $.ajax({
      url: "../user/addUserWithRole",
      method: "post",
      data: JSON.stringify(afterData),
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function (result) {
        if (result && result.success) {
          $mainTable.bootstrapTable('refresh');
          closeFormWin();
        }
        layer.msg(result.message);
      }
    });
  });
}

/**
 * 编辑项目
 */
function editAction(row) {
  resetData = row;
  openEditWin('修改用户');
  $("input[name='username']").attr("disabled", true);
  forSubmitAction(row, function (afterData) {
    $.ajax({
      url: "../user/updateUserWithRole",
      method: "post",
      data: JSON.stringify(afterData),
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      success: function (result) {
        if (result && result.success) {
          $mainTable.bootstrapTable('updateRow', {row: afterData});
          closeFormWin();
        }
        layer.msg(result.message);
      }
    });
  });
}

/**
 * 删除项目
 */
function deleteAction(row) {
  layer.confirm('你确定要删除 ' + row.username + ' ?', {
    title: '<i class="glyphicon glyphicon-question-sign"></i> 确认删除',
    icon: 3
  }, function (index) {
    $.ajax({
      url: "../user/deleteUser",
      method: "post",
      data: {userId: row.userId},
      dataType: "json",
      success: function (result) {
        if (result && result.success) {
          $mainTable.bootstrapTable('refresh');
        }
        layer.close(index);
        layer.msg(result.message);
      }
    });
  });
}

