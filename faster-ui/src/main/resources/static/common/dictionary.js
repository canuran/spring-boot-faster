// 全局表格对象
var $mainTable = false;
/**
 * 初始方法
 */
$(document).ready(function () {
  // 数据表格初始化
  $mainTable = $('#mainTable').bootstrapTable({
    url: '../dictionary/findWithSubDictionary',
    method: 'post',
    queryParams: function (params) {
      params.name = $('#nameSearch').val();
      params.value = $('#valueSearch').val();
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
    pageList: [10],
    silentSort: true,
    smartDisplay: false,
    uniqueId: 'dictionaryId',
    idField: 'dictionaryId',
    parentIdField: 'parentId',
    treeShowField: 'name',
    escape: true,
    toolbar: '#tableToolbar',
    responseHandler: function (result) {
      return result && result.success && result.data || {total: 0};
    },
    columns: [
      {field: 'dictionaryId', title: '标识', align: 'center', valign: 'middle', visible: false},
      {field: 'name', title: '字典名', align: 'center', valign: 'middle', switchable: false},
      {field: 'value', title: '字典值', align: 'center', valign: 'middle'},
      {field: 'detail', title: '详情', align: 'center', valign: 'middle'},
      {field: 'createTime', title: '创建时间', align: 'center', valign: 'middle'},
      {
        field: 'addAction', title: '新增', width: '30px', cardVisible: false,
        align: 'center', valign: 'middle', formatter: function (value, row) {
          // 只有父字典才能新增字典项
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
        createAction({parentId: row.dictionaryId});
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

/**
 * 搜索内容
 */
function searchAction() {
  $mainTable.bootstrapTable('refresh');
}

var resetData = false;

/**
 * 重置表单
 */
function resetEditForm() {
  $('#editForm')[0].reset();
  if (resetData) {
    $("input[name='name']").val(resetData.name);
    $("input[name='value']").val(resetData.value);
    $("textarea[name='detail']").val(resetData.detail);
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
      dictionaryId: editData.dictionaryId,
      parentId: editData.parentId,
      name: data.field.name,
      value: data.field.value,
      detail: data.field.detail
    });
    return false;
  });
}

/**
 * 添加项目
 */
function createAction(row) {
  resetData = row || {};
  openEditWin('新增字典');
  forSubmitAction(resetData, function (afterData) {
    $.ajax({
      url: "../dictionary/addDictionary",
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
  openEditWin('修改字典');
  forSubmitAction(resetData, function (afterData) {
    $.ajax({
      url: "../dictionary/updateDictionary",
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
      url: "../dictionary/deleteDictionary",
      method: "post",
      data: {dictionaryId: row.dictionaryId},
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

