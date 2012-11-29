(function($) {
    $.fn.extend({
        rdLoad : function(url, params, callback) {
            if (url.indexOf('?') > -1) {
                url += "&r=" + (new Date()).valueOf();
            } else {
                url += "?r=" + (new Date()).valueOf();
            }
            if (params && callback) {
                $(this).load(url, params, callback);
            } else {
                $(this).load(url);
            }
        }
    });
})(jQuery);