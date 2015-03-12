sammy.get(new RegExp(app.core_regex_base + '\\/(query)$'), function(context) {
	var core_basepath = this.active_core.attr('data-basepath');
	var content_element = $('#content');
	$.get('tpl/query.html', function(template) {
		content_element.html(template);
		var query_element = $('#query', content_element);
		var query_form = $('#form form', query_element);
		var url_element = $('#url', query_element);
		var response_element = $('#response', query_element);

		/* 加载数据 */
		url_element.die('change').live('change', function(event) {
			var wt = $('[name="wt"]', query_form).val();
			var content_generator = {
				_default : function(xhr) {
					return xhr.responseText.esc();
				},
				json : function(xhr) {
					return app.format_json(xhr.responseText);
				}
			};
			$.ajax({
				url : this.href,
				dataType : wt,
				context : response_element,
				beforeSend : function(xhr, settings) {
					this.html('<div class="loader">加载中 ...</div>');
				},
				complete : function(xhr, text_status) {
					var code = $('<pre class="syntax language-' + wt + '"><code>' + (content_generator[wt] || content_generator['_default'])(xhr) + '</code></pre>');
					this.html(code);
					if ('success' === text_status) {
						hljs.highlightBlock(code.get(0));
					}
				}
			});
		})
		/* 拼查询URL */
		query_form.die('submit').live('submit', function(event) {
			var queryString = $(query_form).formSerialize();
			var handler_path = '/select';
			var query_url = window.location.protocol + '//' + window.location.host + core_basepath + handler_path + '?' + queryString;
			url_element.attr('href', query_url).text(query_url).trigger('change');
			return false;
		});
		/* 查询 */
		query_form.trigger('submit');
	});
});