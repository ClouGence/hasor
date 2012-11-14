/* -------------------------------------------------------------------- */
/* UISelectInput Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UISelectInput", "UIInput", {
	/** 标签元素的change事件处理程序。 */
	onchange : function() {
		var change = $(this.getElement()).attr("_onchange");
		if (WebUI.isNaN(change) == false) {
			var vList = this.selectValues();
			var val = null;
			if (vList.length == 0)
				val = null;
			else if (vList.length == 1)
				val = vList[0];
			else
				val = vList;
			return WebUI.runSrcipt(change, this, {
				value : val
			});
		}
	},
	/** 获取被选择的值索引（字符串逗号分割）。 */
	selectIndex : function() {
		var vList = this.selectIndexs();
		vList = (WebUI.isNaN(vList) == true || vList.length == 0) ? [] : vList;
		var returnData = "";
		for ( var v in vList)
			returnData += (vList[v] + ",");
		return WebUI.deleteLast(returnData, ',');// 删掉最后一个&
	},
	/** 获取选中的值 */
	selectKey : function() {
		var vList = this.selectValues();
		vList = (WebUI.isNaN(vList) == true || vList.length == 0) ? [] : vList;
		var returnData = "";
		for ( var v in vList)
			returnData += (vList[v][this.keyField()] + ",");
		return WebUI.deleteLast(returnData, ',');// 删掉最后一个&
	},
	/** 获取选中的值 */
	selectValue : function() {
		var vList = this.selectValues();
		vList = (WebUI.isNaN(vList) == true || vList.length == 0) ? [] : vList;
		var returnData = "";
		for ( var v in vList)
			returnData += (vList[v][this.varField()] + ",");
		return WebUI.deleteLast(returnData, ',');// 删掉最后一个&
	},
	/** 获取选中的值，返回值是数组形式。 */
	selectValues : function() {
		var returnData = new Array();
		var dataList = this.listData();
		var indexList = this.selectIndexs();
		for ( var i in indexList)
			returnData.push(dataList[indexList[i]]);
		return returnData; // 选中值
	},
	/** 根据索引号获取被指定的数据 */
	indexOf : function(index) {
		var data = this.listData();
		if (data.length > index)
			return data[index];
		return null;
	},
	/** （重写方法）从服务器上载入数据 */
	loadData : function(paramData, ajaxAfter, ajaxError) {
		if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
			return;
		var $this = this;
		this.doEvent('OnLoadData', {
			/* 携带的参数 */
			'dataMap' : paramData,
			/* 正确的回调 */
			'ajaxAfter' : function(event) {
				// A.成功装载
				if (WebUI.isFun(ajaxAfter) == true)
					ajaxAfter.call($this, event);
				else
					$this.listData(eval('(' + event.result + ')'));
				$this.render();
			},
			/* 错误的回调 */
			'ajaxError' : function(event) {
				// B.装载失败
				if (WebUI.isFun(ajaxError) == true)
					ajaxError.call($this, event);
			}
		});
	},
	/** 获取选中的索引，返回值是数组形式（抽象方法，子类需要继承实现）。 */
	selectIndexs : function() {
		throw "抽象方法，子类需要继承实现。";
	},
	/** （重写方法）根据自身的dataList值重新刷新数据显示。 */
	render : function() {
		throw "抽象方法，子类需要继承实现。";
	},
	/** 构造方法 */
	"<init>" : function() {
		/** 显示名称字段（RW） */
		this.defineProperty("keyField", "RW");
		/** 值字段（RW） */
		this.defineProperty("varField", "RW");
		/** 数据（RW） */
		this.defineProperty("listData", function() {
			var returnData = this.getState().get("listData");
			return (WebUI.isNaN(returnData) == true) ? [] : WebUI.isArray(returnData) ? returnData : [ returnData ];
		}, function(newValue) {
			this.getState().set("listData", newValue);
		});
		/** 数据（RW） */
		this.defineProperty("value", function() {
			var returnData = this.getState().get("value");
			return (WebUI.isNaN(returnData) == true) ? [] : WebUI.isArray(returnData) ? returnData : [ returnData ];
		}, function(newValue) {
			this.getState().set("value", newValue);
		});
	}
});