// 全局表格对象
var $mainTable = false;
/**
 * 初始方法
 */
$(document).ready(function () {
  // 数据表格初始化
  $mainTable = $('#mainTable').bootstrapTable({
    url: 'data.json',
    //method:'post',
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
    uniqueId: 'id',
    escape: true,
    toolbar: '#tableToolbar',
    columns: [
      {field: 'id', title: '标识', align: 'center', valign: 'middle', visible: false},
      {field: 'name', title: '名称', align: 'center', valign: 'middle', switchable: false},
      {field: 'career', title: '职业', align: 'center', valign: 'middle'},
      {field: 'dateTime', title: '时间', align: 'center', valign: 'middle'},
      {field: 'style', title: '风格', align: 'center', valign: 'middle'},
      {field: 'tags', title: '标签', align: 'center', valign: 'middle'},
      {field: 'remark', title: '备注', align: 'center', valign: 'middle'},
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
    elem: '#dateTime',
    type: 'datetime'
  });
});

/**
 * 搜索内容
 */
function searchAction() {
  $mainTable.bootstrapTable('refresh');
}

// zTree对象
var $zTreeObj = false;
var initSelectNodes = false;

/**
 * 初始化zTree
 */
function resetZTree(nodes) {
  if ($zTreeObj) {
    $zTreeObj.checkAllNodes(false);
    // 使用自定义数据重置树
    nodes = nodes || initSelectNodes || [];
    nodes.forEach(function (value) {
      Utils.traverseTree($zTreeObj.getNodes(), function (node) {
        if (node.name === value.name) {
          $zTreeObj.checkNode(node, true, false);
        }
      });
    });
  } else {
    // 如果树不存在，先获取数据再初始化
    $.getJSON("tree.json", function (result) {
      $zTreeObj = $.fn.zTree.init($("#zTreeDiv"), {
        view: {showLine: false},
        check: {enable: true, chkboxType: {"Y": "p", "N": "s"}}
      }, result || []);
      initSelectNodes = $zTreeObj.getCheckedNodes();
      resetZTree(nodes);
    });
  }
}

var resetData = false;

/**
 * 重置表单
 */
function resetEditForm() {
  $('#editForm')[0].reset();
  if (resetData) {
    $("input[name='name']").val(resetData.name);
    $("input[name='career'][value='" + resetData.career + "']").trigger('click');
    $("select[name='style']").val(resetData.style);
    $("input[name='dateTime']").val(resetData.dateTime);
    $("textarea[name='remark']").val(resetData.remark);
    resetZTree(resetData.tags.map(function (value) {
      return {name: value};
    }));
  } else {
    $("input[name='career'][value='演员']").trigger('click');
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
    editData.tags = $zTreeObj.getCheckedNodes()
      .map(function (node) {
        return node.name;
      });
    editData.name = data.field.name;
    editData.career = data.field.career;
    editData.style = data.field.style;
    editData.dateTime = data.field.dateTime;
    editData.remark = data.field.remark;
    action(editData);
    return false;
  });
}

/**
 * 添加项目
 */
function createAction() {
  resetData = false;
  openEditWin('新增项目');
  forSubmitAction({}, function (afterData) {
    afterData.id = afterData.id || 1;
    $mainTable.bootstrapTable('getData').forEach(function (data) {
      afterData.id = data.id < afterData.id ? afterData.id : data.id + 1;
    });
    $mainTable.bootstrapTable('append', [afterData]);
    closeFormWin();
  });
}

/**
 * 编辑项目
 */
function editAction(row) {
  resetData = row;
  openEditWin('修改项目');
  forSubmitAction(row, function (afterData) {
    $mainTable.bootstrapTable('updateRow', {row: afterData});
    closeFormWin();
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
    $mainTable.bootstrapTable('removeByUniqueId', row.id);
    layer.close(index);
  });
}
