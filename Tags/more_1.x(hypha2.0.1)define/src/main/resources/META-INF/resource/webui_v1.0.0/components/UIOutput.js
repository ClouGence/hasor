/* -------------------------------------------------------------------- */
/* UIOutput Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UIOutput", "UIComponent", {
	/** 构造方法 */
	"<init>" : function() {
		this.defineProperty("value", function() {
			return this.getState().get("value");
		}, function(newValue) {
			this.getState().set("value", newValue);
			$(this.getElement()).html(newValue);
		});
	}
});