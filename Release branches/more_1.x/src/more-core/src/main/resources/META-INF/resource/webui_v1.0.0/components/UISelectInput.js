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
	/** 获取选中的索引，返回值是数组形式（抽象方法，子类需要继承实现）。 */
	selectIndexs : function() {
		throw "抽象方法，子类需要继承实现。";
	},
	/** 获取选中的值，返回值是数组形式（抽象方法，子类需要继承实现）。 */
	selectValues : function() {
		throw "抽象方法，子类需要继承实现。";
	},
	/** 获取数据集合，返回值是数组形式（抽象方法，子类需要继承实现）。 */
	getListData : function() {
		throw "抽象方法，子类需要继承实现。";
	},
	/** 构造方法 */
	"<init>" : function() {
		/** 显示名称字段（R） */
		this.defineProperty("keyField", "RW");
		/** 值字段（R） */
		this.defineProperty("varField", "RW");
	}
});