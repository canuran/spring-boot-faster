// 全局字典数据
var dictionaries = Utils.getDictionaries(["AUTHORITY_TYPE"]);

// 全局表格对象
var $mainTable = false;
/**
 * 初始方法
 */
$(document).ready(function () {
  // 数据表格初始化
  $mainTable = $('#mainTable').bootstrapTable({
    url: '../security/getAllAuthority',
    method: 'get',
    height: $(window).height() - 20,
    striped: true,
    showRefresh: true,
    showToggle: true,
    showColumns: true,
    clickToSelect: true,
    singleSelect: true,
    minimumCountColumns: 2,
    pagination: false,
    silentSort: true,
    smartDisplay: false,
    uniqueId: 'authorityId',
    idField: 'authorityId',
    parentIdField: 'parentId',
    treeShowField: 'name',
    escape: true,
    toolbar: '#tableToolbar',
    responseHandler: function (result) {
      return result && result.success && result.data || [];
    },
    columns: [
      {field: 'authorityId', title: '标识', align: 'center', valign: 'middle', visible: false},
      {field: 'name', title: '名称', align: 'center', valign: 'middle', switchable: false},
      {field: 'code', title: '编码', align: 'center', valign: 'middle'},
      {
        field: 'type', title: '类型', align: 'center', valign: 'middle',
        formatter: function (value) {
          return (dictionaries['AUTHORITY_TYPE'] || {})[value] || value;
        }
      }, {
        field: 'content', title: '内容', align: 'center', valign: 'middle', visible: false
      },
      {field: 'createTime', title: '创建时间', align: 'center', valign: 'middle'},
      {
        field: 'addAction', title: '新增', width: '30px', cardVisible: false,
        align: 'center', valign: 'middle', formatter: function () {
          return '<div class="add_button pointer_hover">' +
            '<i class="glyphicon glyphicon-plus"></i></div>';
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
    ],
    onClickCell: function (field, value, row) {
      if (field === 'addAction') {
        createAction({addSub: true, parentId: row.authorityId});
      } else if (field === 'editAction') {
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
});

var resetData = false;

/**
 * 重置表单
 */
function resetEditForm() {
  $('#editForm')[0].reset();
  if (resetData && !resetData.addSub) {
    $("input[name='name']").val(resetData.name);
    $("input[name='code']").val(resetData.code);
    $("input[name='type'][value='" + resetData.type + "']").trigger('click');
    $("textarea[name='content']").val(resetData.content);
  } else {
    $("input[name='type'][value='ACTION']").trigger('click');
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
    area: '600px',
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
    action({
      authorityId: editData.authorityId,
      parentId: editData.parentId,
      name: data.field.name,
      code: data.field.code,
      type: data.field.type,
      content: data.field.content
    });
    return false;
  });
}

/**
 * 添加项目
 */
function createAction(row) {
  resetData = row || {};
  openEditWin('新增权限');
  forSubmitAction(resetData, function (afterData) {
    $.ajax({
      url: "../security/addAuthority",
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
  openEditWin('修改权限');
  forSubmitAction(resetData, function (afterData) {
    $.ajax({
      url: "../security/updateAuthority",
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
 * 删除项目
 */
function deleteAction(row) {
  layer.confirm('你确定要删除 ' + row.name + ' ?', {
    title: '<i class="glyphicon glyphicon-question-sign"></i> 确认删除',
    icon: 3
  }, function (index) {
    $.ajax({
      url: "../security/deleteAuthority",
      method: "post",
      data: {authorityId: row.authorityId},
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

