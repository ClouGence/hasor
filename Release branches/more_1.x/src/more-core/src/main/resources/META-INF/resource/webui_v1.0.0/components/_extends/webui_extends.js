/* ui_ScriptInput组建 */
WebUI.Component.$extends("ui_ScriptInput", "UIInput", {
	/** 构造方法 */
	"<init>" : function() {
		/** 绑定的客户端脚本,读（R） */
		this.defineProperty("varRScript", "RW");
		/** 绑定的客户端脚本,写（R） */
		this.defineProperty("varWScript", "RW");
		/** value */
		this.defineProperty("value", function() {
			return eval(this.varRScript());
		}, function(newValue) {
			this.getState().set("value", newValue);
			var Data = newValue;
			eval(this.varWScript());
		});
	}
});
/** ui_Input：表单元素基类 */
WebUI.Component.$extends("ui_Text", "UIInput", {
	/** 构造方法 */
	"<init>" : function() {
		/** 文本组建是否为多行输入 */
		this.defineProperty("multiLine", "RW");
		/** 该值是当value没有设置值时会用该值替代（RW） */
		this.defineProperty("tipTitle", "RW");
	},
});