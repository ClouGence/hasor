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
	/** 获取或设置选中的索引值集合。（参数和返回值均是string逗号分割）。 */
	selectIndex : function(newVar) {
		if (WebUI.isNaN(newVar) == false) {
			// W
			if (WebUI.isFun(newVar) || WebUI.isObject(newVar) || WebUI.isNaN(newVar) || WebUI.isArray(newVar))
				WebUI.throwError('参数类型错误，参数不能是function、object、Array、undefined。');
			newVar += "";
			this.selectIndexs(newVar.split(','));
		} else {
			// R
			var vList = this.selectValues();
			vList = (WebUI.isNaN(vList) == true || vList.length == 0) ? [] : vList;
			var returnData = "";
			for ( var v in vList)
				returnData += (vList[v][this.keyField()] + ",");
			return WebUI.deleteLast(returnData, ',');// 删掉最后一个&
		}
	},
	/** 获取或设置选中的索引值集合。（参数和返回值均是string逗号分割）。 */
	selectIndexs : function(newVar) {
		WebUI.throwError("抽象方法，子类需要继承实现。");
	},
	/** 获取或设置选中的KEY集合。（参数和返回值均是string逗号分割）。 */
	selectKey : function(newVar) {
		if (WebUI.isNaN(newVar) == false) {
			// W
			if (typeof (newVar) != 'string')
				WebUI.throwError('参数类型错误，期待一个字符串。');
			this.selectKeys(newVar.split(','));
		} else {
			// R
			var vList = this.selectValues();
			vList = (WebUI.isNaN(vList) == true || vList.length == 0) ? [] : vList;
			var returnData = "";
			for ( var v in vList)
				returnData += (vList[v][this.keyField()] + ",");
			return WebUI.deleteLast(returnData, ',');// 删掉最后一个&
		}
	},
	/** 获取或设置选中的Value集合。（参数和返回值均是string逗号分割）。 */
	selectValue : function(newVar) {
		if (WebUI.isNaN(newVar) == false) {
			// W
			if (typeof (newVar) != 'string')
				WebUI.throwError('参数类型错误，期待一个字符串。');
			this.selectValues(newVar.split(','));
		} else {
			// R
			var vList = this.selectValues();
			vList = (WebUI.isNaN(vList) == true || vList.length == 0) ? [] : vList;
			var returnData = "";
			for ( var v in vList)
				returnData += (vList[v][this.varField()] + ",");
			return WebUI.deleteLast(returnData, ',');// 删掉最后一个&
		}
	},
	/** 获取或设置选中的KEY集合。（参数和返回值均是Array）。 */
	selectKeys : function(newVar) {
		if (WebUI.isNaN(newVar) == false) {
			// W
			if (WebUI.isArray(newVar) == false)
				WebUI.throwError('参数类型错误，期待一个Array。');
			var dataList = this.listData();
			var selectKey = new Array();
			for ( var e1 in dataList)
				for ( var e2 in newVar) {
					if (WebUI.isArray(e2) || WebUI.isFun(e2))
						WebUI.throwError('newVar参数元素‘' + e1 + '’格式错误，Array,Fun不应参与。');
					var $e1 = (WebUI.isObject(dataList[e1])) ? dataList[e1][this.keyField()] : dataList[e1];
					var $e2 = (WebUI.isObject(newVar[e2])) ? newVar[e2][this.keyField()] : newVar[e2];
					if ($e1 == $e2)
						selectKey.push(dataList[e1][$e1]);// 将符合目标的KEY加入集合
				}
			this.value(selectKey);
		} else {
			// R
			var dataList = this.listData();
			var indexList = this.selectIndexs();
			indexList = (WebUI.isNaN(indexList) == true) ? [] : indexList;
			var returnData = new Array();
			for ( var i in indexList)
				returnData.push(dataList[indexList[i]][this.keyField()]);
			return returnData; // 选中值
		}
	},
	/** 获取或设置选中的Value集合。（参数和返回值均是Array）。 */
	selectValues : function(newVar) {
		if (WebUI.isNaN(newVar) == false) {
			// W
			if (WebUI.isArray(newVar) == false)
				WebUI.throwError('参数类型错误，期待一个Array。');
			var dataList = this.listData();
			var selectKey = new Array();
			for ( var e1 in dataList)
				for ( var e2 in newVar) {
					if (WebUI.isArray(e2) || WebUI.isFun(e2))
						WebUI.throwError('newVar参数元素‘' + e1 + '’格式错误，Array,Fun不应参与。');
					var $e1 = (WebUI.isObject(dataList[e1])) ? dataList[e1][this.varField()] : dataList[e1];
					var $e2 = (WebUI.isObject(newVar[e2])) ? newVar[e2][this.varField()] : newVar[e2];
					if ($e1 == $e2)
						selectKey.push(dataList[e1][this.keyField()]);// 将符合目标的KEY加入集合
				}
			this.value(selectKey);
		} else {
			// R
			var returnData = new Array();
			var dataList = this.listData();
			var indexList = this.selectIndexs();
			for ( var i in indexList)
				returnData.push(dataList[indexList[i]]);
			return returnData; // 选中值
		}
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
				$this.listData(eval('(' + event.result + ')'));
				$this.render();
				if (WebUI.isFun(ajaxAfter) == true)
					ajaxAfter.call($this, event);
			},
			/* 错误的回调 */
			'ajaxError' : function(event) {
				// B.装载失败
				if (WebUI.isFun(ajaxError) == true)
					ajaxError.call($this, event);
			}
		});
	},
	/** （重写方法）根据自身的dataList值重新刷新数据显示。 */
	render : function() {
		WebUI.throwError("抽象方法，子类需要继承实现。");
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
			if (WebUI.isNaN(returnData) == true)
				return [];
			if (WebUI.isArray(returnData) == true)
				return returnData;
			if (typeof (returnData) == 'string')
				try {
					return eval('(' + returnData + ')');
				} catch (e) {
					return [ returnData ];
				}
			else
				return [ returnData ];
		}, function(newValue) {
			this.getState().set("listData", newValue);
		});
		/** value */
		this.defineProperty("value", function() {
			var selectValues = this.getState().get('value');
			if (typeof (selectValues) == 'string')
				selectValues = selectValues.split(",");
			else if (WebUI.isArray(selectValues) == false)
				selectValues = [ selectValues ];
			return selectValues; // 选中值
		}, function(newValue) {
			if (typeof (newValue) == 'string')
				this.getState().set("value", newValue);
			else if (WebUI.isNaN(newValue) == true)
				this.getState().set("value", null);
			else {
				// 先变成数组
				if (WebUI.isArray(newValue) == false)
					newValue = [ newValue ];
				var newData = '';
				for ( var v in newValue)
					newData += (newValue[v] + ',');
				newData = WebUI.deleteLast(newData, ',');
				this.getState().set("value", newData);
			}
			this.render();// 重新渲染
		});
	}
});