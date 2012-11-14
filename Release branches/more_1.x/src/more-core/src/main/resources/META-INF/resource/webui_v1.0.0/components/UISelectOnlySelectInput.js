/* -------------------------------------------------------------------- */
/* UISelectOnlySelectInput Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UISelectOnlySelectInput", "UISelectInput", {
	/** 标签元素的change事件处理程序。 */
	onchange : function() {
		var change = $(this.getElement()).attr("_onchange");
		if (WebUI.isNaN(change) == false)
			return WebUI.runSrcipt(change, this, {
				value : this.value()
			});
	},
	/** （重写方法）获取被选择的值索引（数组结构）。 */
	selectIndexs : function() {
		var e = this.getElement();
		if (e.selectedIndex == -1)
			return [];
		else
			return [ this.getElement().selectedIndex ];
	},
	/** （重写方法）根据自身的dataList值重新刷新数据显示。 */
	render : function() {
		var k = this.keyField();
		var v = this.varField();
		var arrayData = this.listData();
		var vals = this.value();
		var value = (vals.length != 0) ? vals[0] : "";
		var e = this.getElement();
		e.options.length = 0;
		//
		var tempEID = 'webui_' + this.clientID;
		$('body').append('<' + tempEID + ' id=' + tempEID + ' style="display:none;"></' + tempEID + '>');
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

	}
});