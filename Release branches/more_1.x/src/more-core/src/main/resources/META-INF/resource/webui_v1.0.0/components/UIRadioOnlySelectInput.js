/* --------------------------------------------------------------------
 * UIRadioOnlySelectInput Component
 * 
 * HTML原型：UIRadioOnlySelectInput.xhtml
 * 数据模型：
 * [
 *   {key:'',value:'',href:'',checked:true},
 *   {key:'',value:'',href:'',checked:true},
 *   {key:'',value:'',href:'',checked:true},
 *   {key:'',value:'',href:'',checked:true},
 * ]
 * -------------------------------------------------------------------- */
WebUI.Component.$extends("UIRadioOnlySelectInput", "UISelectInput", {
	/** （重写方法）获取被选择的值索引（数组结构）。 */
	selectIndexs : function() {
		var dataList = new Array();
		var index = 0;
		$("#" + this.clientID + " input[type=radio]").each(function() {
			if (this.checked == true)
				dataList.push(index);
			index++;
		});
		return dataList; // 选中值
	},
	/** （重写方法）根据自身的dataList值重新刷新数据显示。 */
	render : function() {
		/** A---选择的值 */
		var selectValues = this.value();
		/** B---Html */
		var itemHtml = "";
		var k = this.keyField();
		var v = this.varField();
		var arrayData = this.listData();
		var jqObject = $(this.getElement());
		for ( var i = 0; i < arrayData.length; i++) {
			var itemData = arrayData[i];
			if (WebUI.isNaN(itemData) == true)
				continue;
			if (WebUI.isNaN(itemData[k]) == true || WebUI.isNaN(itemData[v]) == true)
				continue;
			// 确定checked属性
			if (WebUI.isNaN(itemData['checked']) == false && itemData['checked'] == true) {
				//
			} else {
				for ( var j = 0; j < selectValues.length; j++)
					itemData['checked'] = (selectValues[j] == arrayData[i][k]) ? true : false;
			}
			// 添加元素
			var ortData = null;// JSON.stringify(itemData);
			var ckecked = (WebUI.isNaN(itemData['checked']) == false && itemData['checked'] == true) ? true : false;
			var titleMark = ($(this.getElement()).attr('renderType') == 'onlyTitle') ? " style='display:none;'" : "";
			var href = (WebUI.isNaN(itemData['href']) == true) ? "javascript:void(0)" : itemData['href'];
			var _input = "<input type='radio' forComID='" + this.componentID + "' name='" + this.name() + "' value='" + itemData[k] + "' oriData='" + ortData + "' " + ((ckecked == true) ? "checked='checked'" : "") + titleMark + "/>";
			var _item = "<li class='" + ((ckecked == true) ? "" : "no") + "checked'><a href='" + href + "'><label><em></em>" + _input + "<span>" + itemData[v] + "</span></label></a></li>";
			itemHtml = itemHtml + _item;
		}
		jqObject.html(itemHtml);
		/** C---绑定事件 */
		var fun = this.onchange;
		$("#" + this.clientID + " input[type=radio]").bind("change", function() {
			var comID = $(this).attr("forComID");
			var $this = WebUI(comID);
			$('#' + $this.clientID + ' li').attr('class', "nochecked");
			$(this).closest("li").attr('class', (this.checked == true) ? "checked" : "nochecked");
			// 1.值都加入到集合中
			var arrayData = $this.selectValues();
			var newValues = new Array();
			for ( var v in arrayData)
				newValues.push(arrayData[v][k]);
			// 2.更新value
			$this.value(newValues);
			fun.call($this);
		});
	},
	/** 构造方法 */
	"<init>" : function() {}
});