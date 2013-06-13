/* -------------------------------------------------------------------- */
/* UISelectOnlySelectInput Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UISelectOnlySelectInput", "UISelectInput", {
	/** 标签元素的change事件处理程序。 */
	onchange : function() {
		var change = $(this.getElement()).attr("_onchange");
		var vv = this.selectKey();
		if (WebUI.isNaN(change) == false)
			return WebUI.runSrcipt(change, this, {
				value : vv
			});
	},
	/** （重写方法）获取或设置选中的索引值集合。（参数和返回值均是string逗号分割）。 */
	selectIndexs : function(newVar) {
		if (WebUI.isNaN(newVar) == false) {
			// W
			if (WebUI.isArray(newVar) == false)
				throw WebUI.throwError('参数类型错误，期待一个Array。');
			if (newVar.length > 1)
				throw WebUI.throwError('参数错误，select标签不能接受多个选择值。');
			else if (newVar.length == 0)
				this.getElement().selectedIndex = -1;
			else
				this.getElement().selectedIndex = newVar[0];
			this.render();// 重新渲染
		} else {
			// R
			var e = this.getElement();
			if (e.selectedIndex == -1)
				return [];
			else
				return [ this.getElement().selectedIndex ];
		}
	},
	/** （重写方法）根据自身的dataList值重新刷新数据显示。 */
	render : function() {
		var k = this.keyField();
		var v = this.varField();
		var arrayData = this.listData();
		var value = this.value();
		var e = this.getElement();
		e.options.length = 0;
		//
		var tempEID = 'webui_' + this.clientID;
		$('body').append('<span id=' + tempEID + ' style="display:none;"></span>');
		var tempElement = $('#' + tempEID);
		var selectIndex = 0;
		for ( var i = 0; i < arrayData.length; i++) {
			var _v = (WebUI.isNaN(arrayData[i][v]) == false) ? tempElement.html(arrayData[i][v]).text() : "";
			var _k = (WebUI.isNaN(arrayData[i][k]) == false) ? tempElement.html(arrayData[i][k]).text() : "";
			if (WebUI.isNaN(_v) == false || WebUI.isNaN(_k) == false)
				e.options.add(new Option(_v, _k));
			if (_k == value)
				selectIndex = i;
		}
		tempElement.remove();
		this.getElement().selectedIndex = selectIndex;
	},
	/** 构造方法。 */
	"<init>" : function() {
		/** 数据（RW） */
		this.defineProperty("value", function() {
			return this.getState().get("value");
		}, function(newValue) {
			this.getState().set("value", newValue);
			var options = this.getElement().options;
			for ( var i in options)
				if (options[i].value == newValue)
					this.getElement().selectedIndex = i;
		});
	}
});