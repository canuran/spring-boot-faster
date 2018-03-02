// 全局表格对象
var $mainTable = false;
/**
 * 初始方法
 */
$(document).ready(function () {
  // 数据表格初始化
  $mainTable = $('#mainTable').bootstrapTable({
    url: '../security/findRoleWithAuthority',
    method: 'post',
    queryParams: function (params) {
      params.search = $('#searchInput').val();
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
    uniqueId: 'roleId',
    escape: true,
    toolbar: '#tableToolbar',
    responseHandler: function (result) {
      return result && result.success && result.data || {total: 0};
    },
    columns: [
      {field: 'roleId', title: '标识', align: 'center', valign: 'middle', visible: false},
      {field: 'name', title: '名称', align: 'center', valign: 'middle', switchable: false},
      {
        field: 'authorities', width: '50%', title: '权限', align: 'center', valign: 'middle',
        formatter: function (value) {
          var names = '';
          (value || []).forEach(function (each) {
            names += (names ? ',' : '') + (each && each.name || '');
          });
          return names || null;
        }
      },
      {field: 'createTime', title: '创建时间', align: 'center', valign: 'middle'},
      {
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

  resetZTree();
});

/**
 * 搜索内容
 */
function searchAction() {
  $mainTable.bootstrapTable('refresh');
}

var resetData = false;

// zTree对象
var $zTreeObj = false;

/**
 * 初始化zTree
 */
function resetZTree(data) {
  if ($zTreeObj) {
    $zTreeObj.checkAllNodes(false);
    // 使用自定义数据重置树
    data = data || [];
    data.forEach(function (value) {
      Utils.traverseTree($zTreeObj.getNodes(), function (node) {
        if (node.authorityId === value.authorityId) {
          $zTreeObj.checkNode(node, true, false);
        }
      });
    });
  } else {
    // 如果树不存在，先获取数据再初始化
    $.getJSON("../security/getAuthorityTree", function (result) {
      var zTreeNodes = result && result.data || [];
      $zTreeObj = $.fn.zTree.init($("#zTreeAuths"), {
        view: {showLine: false},
        check: {enable: true, chkboxType: {"Y": "p", "N": "s"}}
      }, zTreeNodes);
      resetZTree(data);
    });
  }
}

/**
 * 重置表单
 */
function resetEditForm() {
  $('#editForm')[0].reset();
  if (resetData) {
    $("input[name='name']").val(resetData.name);
    resetZTree(resetData.authorities);
  } else {
    resetZTree();
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
    editData.name = data.field.name;
    editData.authorities = [];
    $zTreeObj.getCheckedNodes().forEach(function (node) {
      editData.authorities.push({
        authorityId: node.authorityId, name: node.name
      });
    });
    action(editData);
    return false;
  });
}

/**
 * 添加项目
 */
function createAction() {
  resetData = false;
  openEditWin('新增角色');
  forSubmitAction({}, function (afterData) {
    $.ajax({
      url: "../security/addRoleWithAuthority",
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
  openEditWin('修改角色');
  forSubmitAction(row, function (afterData) {
    $.ajax({
      url: "../security/updateRoleWithAuthority",
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
  layer.confirm('你确定要删除 ' + row.name + ' ?', {
    title: '<i class="glyphicon glyphicon-question-sign"></i> 确认删除',
    icon: 3
  }, function (index) {
    $.ajax({
      url: "../security/deleteRole",
      method: "post",
      data: {roleId: row.roleId},
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

