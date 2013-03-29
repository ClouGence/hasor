/* -------------------------------------------------------------------- */
/* UILinkButton Component */
/* -------------------------------------------------------------------- */
WebUI.Component.$extends("UILinkButton", "UIButton", {
	/** 构造方法 */
	"<init>" : function() {
		/** 值（RW） */
		this.defineProperty("value", function() {
			return $(this.getElement()).html();
		}, function(newValue) {
			$(this.getElement()).html(newValue);
		});
		/** 表单名（RW） */
		this.defineProperty("name", "RW");
	}
});