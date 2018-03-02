// 设置命名空间
var Utils = {
  /**
   * 递归遍历树的节点，符合前序遍历顺序
   */
  traverseTree: function (treeNodes, callback) {
    if (treeNodes && treeNodes.length) {
      treeNodes.forEach(function (node) {
        callback(node);
        if (node.children) {
          Utils.traverseTree(node.children, callback);
        }
      });
    }
  },
  /**
   * 获取当前yyyy-MM-dd HH:mm:ss格式的时间
   */
  getDateString: function () {
    return Utils.formatDate(new Date());
  },
  /**
   * 格式化时间为yyyy-MM-dd HH:mm:ss格式
   */
  formatDate: function (date) {
    var month = date.getMonth() + 1;
    var strDate = date.getDate();
    if (month <= 9) {
      month = "0" + month;
    }
    if (strDate <= 9) {
      strDate = "0" + strDate;
    }
    return date.getFullYear() + "-"
      + month + "-"
      + strDate + " " +
      date.getHours() + ":" +
      date.getMinutes() + ":" +
      date.getSeconds();
  },
  /**
   * 同步获取字典值
   */
  getDictionaries: function (rootValues) {
    var data = {};
    $.ajax({
      url: "../dictionary/findDictionaryTrees",
      method: "post",
      data: JSON.stringify(rootValues),
      contentType: "application/json; charset=utf-8",
      dataType: "json",
      async: false,
      success: function (result) {
        (result && result.data || []).map(function (dictionary) {
          var children = {};
          (dictionary.children || []).forEach(function (child) {
            children[child.value] = child.name;
          });
          data[dictionary.value] = children;
        });
      }
    });
    return data;
  }
};

