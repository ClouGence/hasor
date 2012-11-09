/* -------------------------------------------------------------------- */
/* UIInput Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UIInput", "UIOutput", {
	/** 标签元素的change事件处理程序。 */
	onchange : function() {
		var change = $(this.getElement()).attr("_onchange");
		if (WebUI.isNaN(change) == false)
			return WebUI.runSrcipt(change, this, {
				value : this.value()
			});
	},
	/** （重写方法）返回一个值用于表示是否为一个表单元素（只要定义了name属性就成为表单元素） */
	isForm : function() {
		return !WebUI.isNaN(this.name());
	},
	/** 验证表单值内容 */
	doVerification : function() {
		var ver = this.getState().get("verification");
		if (WebUI.isNaN(ver) == true)
			return true;
		var verRegExp = new RegExp(ver);
		return verRegExp.test(this.value());
	},
	/** 构造方法 */
	"<init>" : function() {
		var tagName = this.getElement().tagName.toLowerCase();
		var funChange = this.onchange;
		if (tagName == "input" || tagName == "textarea" || tagName == "select")
			this.bindEvent("change", funChange);// 普通input
		else
			this.bindEvent("onpropertychange", funChange);// 非input
		/** 值（RW） */
		this.defineProperty("value", function() {
			return $(this.getElement()).attr("value");
		}, function(newValue) {
			$(this.getElement()).attr("value", newValue);
		});
		/** 表单名（RW） */
		this.defineProperty("name", function() {
			return $(this.getElement()).attr("name");
		}, function(newName) {
			$(this.getElement()).attr("name", newName);
		});
		/** 验证输入数据的正则表达式（RW） */
		this.defineProperty("verification", "RW");
	}
});