/* -------------------------------------------------------------------- */
/* UISelectOnlySelectInput Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UISelectOnlySelectInput", "UISelectInput", {
	/** （重写方法）从服务器上载入数据。 */
	loadData : function(paramData, funOK, funError) {
		if (WebUI.isNaN(this.getState().get("onLoadDataEL")) == true)
			return;
		var $this = this;
		this.doEvent("OnLoadData", paramData, function(event) {
			// A.成功装载
			if (WebUI.isFun(funOK) == true)
				funOK.call($this, {
					event : event
				});
			else {
				var k = $this.keyField();
				var v = $this.varField();
				var arrayData = eval(event.result);
				var e = $this.getElement();
				e.options.length = 0;
				for ( var i = 0; i < arrayData.length; i++)
					e.options.add(new Option(arrayData[i][v], arrayData[i][k]));
			}
		}, function(event) {
			// B.装载失败
			if (WebUI.isFun(funError) == true)
				funError.call($this, {
					event : event
				});
		});
	},
	/** 根据索引号获取被指定的元素。 */
	indexOf : function(index) {
		var e = this.getElement();
		return e.options[e.selectedIndex]; // 选中值
	},
	/** （重写方法）获取被选择的值索引（数组结构）。 */
	selectIndexs : function() {
		var e = this.getElement();
		if (e.selectedIndex == -1)
			return [];
		else
			return [ this.getElement().selectedIndex ];
	},
	/** （重写方法）获取选中的值（数组结构）。 */
	selectValues : function() {
		var e = this.getElement();
		if (e.selectedIndex == -1)
			return [];
		var jqObject = e.options[e.selectedIndex]; // 选中值
		var atItem = {};
		atItem[this.keyField()] = jqObject.value;
		atItem[this.varField()] = $(this).text();
		return [ atItem ];
	},
	/** （重写方法）数据（R）。 */
	getListData : function() {
		var e = this.getElement();
		var dataList = new Array();
		for ( var i = 0; i < e.options.length; i++) {
			var atItem = {};
			var jqObject = e.options[i];
			atItem[jqObject.value] = jqObject.text;
			dataList.push(atItem);
		}
		return dataList;
	},
	/** 构造方法。 */
	"<init>" : function() {
		/** value */
		this.defineProperty("value", function() {
			var e = this.getElement();
			return [ e.options[e.selectedIndex].value ]; // 选中值
		}, function(newValue) {
			var e = this.getElement();
			this.getState().set("value", newValue);
			e.options[e.selectedIndex].value = newValue; // 选中值
		});
	},
});