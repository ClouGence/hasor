/*!
 * lhgcore Dialog Plugin v4.2.0
 * Date: 2012-04-19 10:55:11 
 * http://code.google.com/p/lhgdialog/
 * Copyright 2009-2012 LiHuiGang
 */

;(function( $, window, undefined ){

var _ie6 = window.ActiveXObject && !window.XMLHttpRequest,
	_fn = function(){},
	_count = 0,
	_rurl = /^url:/,
	_singleton,
	onKeyDown,
	document = window.document,
	expando = 'JDG' + (+new Date),

dialogTpl =
'<table class="ui_border">' +
	'<tbody>' +
		'<tr>' +
			'<td class="ui_lt"></td>' +
			'<td class="ui_t"></td>' +
			'<td class="ui_rt"></td>' +
		'</tr>' +
		'<tr>' +
			'<td class="ui_l"></td>' +
			'<td class="ui_c">' +
				'<div class="ui_inner">' +
				'<table class="ui_dialog">' +
					'<tbody>' +
						'<tr>' +
							'<td colspan="2">' +
								'<div class="ui_title_bar">' +
									'<div class="ui_title" unselectable="on"></div>' +
									'<div class="ui_title_buttons">' +
										'<a class="ui_min" href="javascript:void(0);" title="\u6700\u5C0F\u5316"><b class="ui_min_b"></b></a>' +
										'<a class="ui_max" href="javascript:void(0);" title="\u6700\u5927\u5316"><b class="ui_max_b"></b></a>' +
										'<a class="ui_res" href="javascript:void(0);" title="\u8FD8\u539F"><b class="ui_res_b"></b><b class="ui_res_t"></b></a>' +
										'<a class="ui_close" href="javascript:void(0);" title="\u5173\u95ED(esc\u952E)">\xd7</a>' +
									'</div>' +
								'</div>' +
							'</td>' +
						'</tr>' +
						'<tr>' +
							'<td class="ui_icon"></td>' +
							'<td class="ui_main">' +
								'<div class="ui_content"></div>' +
							'</td>' +
						'</tr>' +
						'<tr>' +
							'<td colspan="2">' +
								'<div class="ui_buttons"></div>' +
							'</td>' +
						'</tr>' +
					'</tbody>' +
				'</table>' +
				'</div>' +
			'</td>' +
			'<td class="ui_r"></td>' +
		'</tr>' +
		'<tr>' +
			'<td class="ui_lb"></td>' +
			'<td class="ui_b"></td>' +
			'<td class="ui_rb"></td>' +
		'</tr>' +
	'</tbody>' +
'</table>',
	
/*!
 * _path 获取组件核心文件lhgdialog.js所在的绝对路径
 * _args 获取lhgdialog.js文件后的url参数组，如：lhgdialog.js?self=true&skin=aero中的?后面的内容
 */
_args, _path = (function( script, i, me )
{
    var l = script.length;
	
	for( ; i < l; i++ )
	{
		me = !!document.querySelector ?
		    script[i].src : script[i].getAttribute('src',4);
		
		if( me.substr(me.lastIndexOf('/')).indexOf('lhgdialog') !== -1 )
		    break;
	}
	
	me = me.split('?'); _args = me[1];
	
	return me[0].substr( 0, me[0].lastIndexOf('/') + 1 );
})(document.getElementsByTagName('script'),0),        

/*!
 * 获取url参数值函数
 * @param  {String}
 * @return {String||null}
 * @demo lhgdialog.js?skin=aero | _getArgs('skin') => 'aero'
 */
_getArgs = function( name )
{
    if( _args )
	{
	    var p = _args.split('&'), i = 0, l = p.length, a;
		for( ; i < l; i++ )
		{
		    a = p[i].split('=');
			if( name === a[0] ) return a[1];
		}
	}
	return null;
},

/*! 取皮肤样式名，默认为 default */
_skin = _getArgs('skin') || 'default',

/*! 获取 lhgdialog 可跨级调用的最高层的 window 对象和 document 对象 */
_doc, _top = (function(w)
{
	try{
	    _doc = w['top'].document;  // 跨域|无权限
		_doc.getElementsByTagName; // chrome 浏览器本地安全限制
	}catch(e){
	    _doc = w.document; return w;
	};
	
	// 如果指定参数self为true则不跨框架弹出，或为框架集则无法显示第三方元素
	if( _getArgs('self') === 'true' ||
	    _doc.getElementsByTagName('frameset').length > 0 )
	{
	    _doc = w.document; return w;
	}
	
	return w['top'];
})(window),

_root = _doc.documentElement, _doctype = _doc.compatMode === 'BackCompat';

_$doc = $(_doc), _$top = $(_top), _$html = $(_doc.getElementsByTagName('html')[0]);

/*! 开启IE6 CSS背景图片缓存 */
try{
	_doc.execCommand( 'BackgroundImageCache', false, true );
}catch(e){};

/*! 在最顶层页面添加样式文件 */
(function(style){
	if(!style)
	{
	    var head = _doc.getElementsByTagName('head')[0],
		    link = _doc.createElement('link');
			
		link.href = _path + 'skins/' + _skin + '.css';
	    link.rel = 'stylesheet';
		link.id = 'lhgdialoglink';
		head.insertBefore(link, head.firstChild);
	}
})(_doc.getElementById('lhgdialoglink'));

/*!
 * IE6下Fixed无抖动静止定位
 * 如果你的页面的html元素设定了背景图片请把设置背景图片的css写到body元素上
 * 如果你不需要组件静止定位（也就是随屏滚动）或此段代码影响了你的页面布局可将此段代码删除
 */
_ie6 && (function(bg){
	if( _$html.css(bg) !== 'fixed' )
	{
		_$html.css({
			zoom: 1,// 避免偶尔出现body背景图片异常的情况
			backgroundImage: 'url(about:blank)',
			backgroundAttachment: 'fixed'
		});
	}
})('backgroundAttachment');

/*!----------------------------------以下为lhgdialog核心代码部分----------------------------------*/

var lhgdialog = function( config )
{
	config = config || {};
	
	var api, setting = lhgdialog.setting;
		
	// 合并默认配置
	for( var i in setting )
	{
		if( config[i] === undefined ) config[i] = setting[i];
	}
	
	config.id = config.id || expando + _count;
	
	// 如果定义了id参数则返回存在此id的窗口对象
	api = lhgdialog.list[config.id];
	if(api) return api.zindex().focus();
	
	// 按钮队列
	config.button = config.button || [];
	
	config.ok &&
	config.button.push({
	    id: 'ok',
		name: config.okVal,
		callback: config.ok,
		focus: config.focus
	});
	
	config.cancel &&
	config.button.push({
	    id: 'cancel',
		name: config.cancelVal,
		callback: config.cancel
	});
	
	// zIndex全局配置
	lhgdialog.setting.zIndex = config.zIndex;
	
	_count++;
	
	return lhgdialog.list[config.id] = _singleton ?
	    _singleton._init(config) : new lhgdialog.fn._init( config );
};

lhgdialog.fn = lhgdialog.prototype =
{
    constructor: lhgdialog,
	
	_init: function( config )
	{
	    var that = this, DOM,
		    content = config.content,
			isIfr = _rurl.test(content);
			
		that.opener = window;
		that.config = config;
		
		that.DOM = DOM = that.DOM || that._getDOM();
		that.closed = false;
		that.data = config.data;
		
		// 假如提示性图标为真默认不显示最小化和最大化按钮
		if( config.icon && !isIfr )
		{
		    config.min = false;
			config.max = false;
			
			DOM.icon[0].style.display = '';
			DOM.icon[0].innerHTML = '<img src="' + config.path + 'skins/icons/' + config.icon + '" class="ui_icon_bg"/>';
		}
		else
		    DOM.icon[0].style.display = 'none';

		DOM.wrap.addClass( config.skin ); // 多皮肤共存
		DOM.rb[0].style.cursor = config.resize ? 'se-resize' : 'auto';
		DOM.title[0].style.cursor = config.drag ? 'move' : 'auto';
		DOM.max[0].style.display = config.max ? 'inline-block' : 'none';
		DOM.min[0].style.display = config.min ? 'inline-block' : 'none';
		DOM.close[0].style.display = config.cancel === false ? 'none' : 'inline-block'; //当cancel参数为false时隐藏关闭按钮
		DOM.content[0].style.padding = config.padding;
		
		that.button.apply( that, config.button );
		
		that.title( config.title )
		.content( content, true, isIfr )
		.size( config.width, config.height )
		.position( config.left, config.top )
		.time( config.time )
		[config.show?'show':'hide'](true).zindex();
		
		config.focus && that.focus();
		config.lock && that.lock();
		that._ie6PngFix()._addEvent();
		
		_singleton = null;
		
		// 假如加载的是单独页面的内容页config.init函数会在内容页加载完成后执行，这里就不执行了
		if( !isIfr && config.init )
		    config.init.call( that, window );
		
		return that;
	},
	
	/**
	 * 自定义按钮
	 * @example
		button({
			name: 'login',
			callback: function(){},
			disabled: false,
			focus: true
		}, .., ..)
	 */
	button: function()
	{
	    var that = this, DOM = that.DOM,
		    buttons = DOM.buttons[0],
			focusButton = 'ui_state_highlight',
			listeners = that._listeners = that._listeners || {},
			ags = [].slice.call(arguments),
			i = 0, item, value, id, isNewButton, button;
			
		for( ; i < ags.length; i++ )
		{
		    item = ags[i];
			
			value = item.name;
			id = item.id || value;
			isNewButton = !listeners[id];
			button = !isNewButton ? listeners[id].elem : _doc.createElement('input');
			
			button.type = 'button';
			
            if( !listeners[id] )
                listeners[id] = {};
				
			if( value )
			    button.value = value;
			
			if( item.callback )
			    listeners[id].callback = item.callback;
				
			if( item.focus )
			{
			    that._focus && that._focus.removeClass(focusButton);
				that._focus = $(button).addClass(focusButton);
				that.focus();
			}
			
			button[expando + 'callback'] = id;
			button.disabled = !!item.disabled;
			
			if( isNewButton )
			{
			    listeners[id].elem = button;
				buttons.appendChild(button);
			}
		}
		
		buttons.style.display = ags.length ? '' : 'none';
		that._ie6SelectFix();
		
		return that;
	},
	
	/**
	 * 设置标题
	 * @param	{String, Boolean}	标题内容. 为false则隐藏标题栏
	 * @return	{this}	如果无参数则返回对象本身
	 */
	title: function( text )
	{
		if( text === undefined ) return this;
		
		var DOM = this.DOM,
			border = DOM.border,
			title = DOM.title[0];
			
		if( text === false )
		{
			title.style.display = 'none';
			title.innerHTML = '';
			border.addClass('ui_state_tips');
		}
		else
		{
			title.style.display = '';
			title.innerHTML = text;
			border.removeClass('ui_state_tips');
		};
		
		return this;
	},
	
	/*!
	 * 设置内容
	 * @param	{String}	内容 (如果内容前3个字符为‘url:’就加载单独页面的内容页)
	 * @param   {Boolean}   是否为后增加的内容
	 * @param   {Boolean}   是否使用iframe方式加载内容页
	 * @return	{this}		如果无参数则返回对象本身
	 */
	content: function( msg, add, frm )
	{
	    if( msg === undefined ) return this;
		
		var that = this, DOM = that.DOM,
		    wrap = DOM.wrap[0],
			width = wrap.offsetWidth,
			height = wrap.offsetHeight,
			left = parseInt(wrap.style.left),
			top = parseInt(wrap.style.top),
			cssWidth = wrap.style.width,
			$content = DOM.content,
			loading = lhgdialog.setting.content;
		
		    // 假如内容中前3个字符为'url:'就加载相对路径的单独页面的内容页
		if( frm )
		{
			$content[0].innerHTML = loading;
			that._iframe( msg.split('url:')[1] );
		}
		else
			$content.html( msg );
		
		// 新增内容后调整位置
		if( !add )
		{
			width = wrap.offsetWidth - width;
			height = wrap.offsetHeight - height;
			left = left - width / 2;
			top = top - height / 2;
			wrap.style.left = Math.max(left, 0) + 'px';
			wrap.style.top = Math.max(top, 0) + 'px';
			
			if( cssWidth && cssWidth !== 'auto' )
				wrap.style.width = wrap.offsetWidth + 'px';
			
			that._autoPositionType();
		}
		
		that._ie6SelectFix();
		
		return that;
	},
	
	/**
	 *	尺寸
	 *	@param	{Number, String}	宽度
	 *	@param	{Number, String}	高度
	 */
	size: function( width, height )
	{
		var that = this, DOM = that.DOM,
			wrap = DOM.wrap[0],
			style = DOM.main[0].style;
		
		wrap.style.width = 'auto';
		
		if( typeof width === 'number' )
		    width = width + 'px';
		
		if( typeof height === 'number' )
		    height = height + 'px';
		
		style.width = width;
		style.height = height;
		
		if( width !== 'auto' )  // 防止未定义宽度的表格遇到浏览器右边边界伸缩
		    wrap.style.width = wrap.offsetWidth + 'px';
		
		that._ie6SelectFix();
		
		return that;
	},
	
	/**
	 * 位置(相对于可视区域)
	 * @param	{Number, String}
	 * @param	{Number, String}
	 */
	position: function( left, top )
	{
		var that = this,
			config = that.config,
			wrap = that.DOM.wrap[0],
			style = wrap.style,
			isFixed = _ie6 ? false : config.fixed,
			ie6Fixed = _ie6 && config.fixed,
			docLeft = _$top.scrollLeft(),
			docTop = _$top.scrollTop(),
			dl = isFixed ? 0 : docLeft,
			dt = isFixed ? 0 : docTop,
			ww = _$top.width(),
			wh = _$top.height(),
			ow = wrap.offsetWidth,
			oh = wrap.offsetHeight;
		
        if( left || left === 0 )
		{
			that._left = left.toString().indexOf('%') !== -1 ? left : null;
			left = that._toNumber(left, ww - ow);
			
			if( typeof left === 'number' )
			{
				left = ie6Fixed ? (left += docLeft) : left + dl;
				left = Math.max(left,dl) + 'px';
			}
			
			style.left = left;
		}
		
		if( top || top === 0 )
		{
			that._top = top.toString().indexOf('%') !== -1 ? top : null;
			top = that._toNumber(top, wh - oh);
			
			if( typeof top === 'number' )
			{
				top = ie6Fixed ? (top += docTop) : top + dt;
				top = Math.max(top,dt) + 'px';
			}
			
			style.top = top;
		}
		
		if( left !== undefined && top !== undefined )
		    that._autoPositionType();
		
		return that;
	},
	
	/*!
	 * 定时关闭
	 * @param	{Number}	单位为秒, 无参数则停止计时器
	 * @param   {Function}  关闭窗口前执行的回调函数
	 */
	time: function( second, callback )
	{
		var that = this,
			timer = that._timer;
			
		timer && clearTimeout(timer);
		callback && callback.call(that);
		
		if(second)
		{
			that._timer = setTimeout(function(){
				that._click('cancel');
			}, 1000 * second);
		}
		
		return that;
	},
	
	/*! 显示对话框 */
	show: function( args )
	{
		this.DOM.wrap[0].style.visibility = 'visible';
		this.DOM.border.addClass('ui_state_visible');
		
		if( !args && this._lock )
		    $('#ldg_lockmask',_doc)[0].style.display = '';
			
		return this;
	},
	
	/*! 隐藏对话框 */
	hide: function( args )
	{
		this.DOM.wrap[0].style.visibility = 'hidden';
		this.DOM.border.removeClass('ui_state_visible');
		
		if( !args && this._lock )
		    $('#ldg_lockmask',_doc)[0].style.display = 'none';
			
		return this;
	},
	
	/*! 置顶对话框 */
	zindex: function()
	{
		var that = this, DOM = that.DOM,
		    load = that._load,
			top = lhgdialog.focus,
			index = lhgdialog.setting.zIndex++;
		
		// 设置叠加高度
		DOM.wrap[0].style.zIndex = index;
		
		// 设置最高层的样式
		top && top.DOM.border.removeClass('ui_state_focus');
		lhgdialog.focus = that;
		DOM.border.addClass('ui_state_focus');
		
		// 扩展窗口置顶功能，只用在iframe方式加载内容
		// 或跨域加载内容页时点窗口内容主体部分置顶窗口
		if( load && load.style.zIndex )
		    load.style.display = 'none';
		if( top && top !== that && top.iframe )
		    top._load.style.display = '';
		
		return that;
	},
	
	/*! 设置焦点 */
	focus: function()
	{
	    try{
		    elemFocus = this._focus && this._focus[0] || this.DOM.close[0];
			elemFocus && elemFocus.focus();
		}catch(e){};
		
		return this;
	},
	
	/*! 锁屏 */
	lock: function()
	{
		var that = this, frm,
		    index = lhgdialog.setting.zIndex - 1,
			config = that.config,
			mask = $('#ldg_lockmask',_doc)[0],
			style = mask ? mask.style : '',
			positionType = _ie6 ? 'absolute' : 'fixed';
		
		if( !mask )
		{
			frm = '<iframe src="javascript:\'\'" style="width:100%;height:100%;position:absolute;' +
			    'top:0;left:0;z-index:-1;filter:alpha(opacity=0)"></iframe>';
				
			mask = _doc.createElement('div');
			mask.id = 'ldg_lockmask';
			mask.style.cssText = 'position:' + positionType + ';left:0;top:0;width:100%;height:100%;overflow:hidden;';
			
			style = mask.style;
			if( _ie6 ) mask.innerHTML = frm;
			
			_doc.body.appendChild( mask );
		}
		
		if( positionType === 'absolute' )
		{
		    style.width = _$top.width();
			style.height = _$top.height();
			style.top = _$top.scrollTop();
			style.left = _$top.scrollLeft();
			
			that._setFixed( mask );
		}

		style.zIndex = index;
		style.display = '';
		
		that.zindex();
		that.DOM.border.addClass('ui_state_lock');
		
		that._lock = true;
			
		return that;
	},
	
	/*! 解除锁屏 */
	unlock: function()
	{
		var that = this,
		    config = that.config,
			mask = $('#ldg_lockmask',_doc)[0];
		
		if( mask && that._lock )
		{
		    // 无限级锁屏
			if( config.parent && config.parent._lock )
			{
			    var index = config.parent.DOM.wrap[0].style.zIndex;
				mask.style.zIndex = parseInt(index,10) - 1;
			}
			else
			    mask.style.display = 'none';
			
			that.DOM.border.removeClass('ui_state_lock');
		}
		
		that._lock = false;
		
		return that;
	},
	
	/*! 关闭对话框 */
	close: function()
	{
		var that = this, DOM = that.DOM,
			wrap = DOM.wrap,
			list = lhgdialog.list,
			fn = that.config.close;
		
		that.time();
		
		// 当使用iframe方式加载内容页时的处理代码
		if( that.iframe )
		{
			if( typeof  fn === 'function' && fn.call(that, that.iframe.contentWindow, window) === false )
			    return that;
			
			// 重要！需要重置iframe地址，否则下次出现的对话框在IE6、7无法聚焦input
			// IE删除iframe后，iframe仍然会留在内存中出现上述问题，置换src是最容易解决的方法
			$(that.iframe).unbind('load',that._fmLoad).attr('src',"javascript:''").remove();
			
			DOM.content.removeClass('ui_state_full');
			if( that._frmTimer ) clearTimeout(that._frmTimer);
		}
		else
		{
		    if( typeof fn === 'function' && fn.call(that, window) === false )
			    return that;
		}
		
		that.unlock();
		
		if( that._maxState )
		{
			_$html.removeClass('ui_lock_scroll');
		    DOM.res[0].style.display = 'none';
		}
		
		if( lhgdialog.focus === that ) lhgdialog.focus = null;
		
		that._removeEvent();
		delete list[that.config.id];
		
		// 移除HTMLElement或重用
		if( _singleton )
		    wrap.remove();
		else
		{
		    _singleton = that;
			
			if( that._minState )
			{
				DOM.main[0].style.display = '';
				DOM.buttons[0].style.display = '';
				DOM.dialog[0].style.width = '';
			}
			
			DOM.wrap[0].style.cssText = 'left:0;top:0;';
			DOM.wrap[0].className = '';
		    DOM.border.removeClass('ui_state_focus');
		    DOM.title[0].innerHTML = '';
		    DOM.content.html('');
			DOM.icon[0].innerHTML = '';
		    DOM.buttons[0].innerHTML = '';
			
			that.hide(true)._setAbsolute();
		
		    // 清空除this.DOM之外临时对象，恢复到初始状态，以便使用单例模式
		    for( var i in that )
		    {
			    if(that.hasOwnProperty(i) && i !== 'DOM') delete that[i];
		    };
		}
		
		that.closed = true;
		return that;
	},
	
	/*! 最大化窗口 */
	max: function()
	{
		var that = this, maxSize,
		    DOM = that.DOM,
			wrapStyle = DOM.wrap[0].style,
			mainStyle = DOM.main[0].style,
			rbStyle = DOM.rb[0].style,
			titleStyle = DOM.title[0].style,
			config = that.config,
		    top = _$top.scrollTop(),
		    left = _$top.scrollLeft();
		
		if( !that._maxState )
		{
		
			_$html.addClass('ui_lock_scroll');
			
			if( that._minState )
			    that.min();
			
			// 存储最大化窗口前的状态
			that._or = {
				t: wrapStyle.top,
				l: wrapStyle.left,
				w: mainStyle.width,
				h: mainStyle.height,
				d: config.drag,
				r: config.resize,
				rc: rbStyle.cursor,
				tc: titleStyle.cursor
			};
			
			wrapStyle.top = top + 'px';
			wrapStyle.left = left + 'px';
			
			maxSize = that._maxSize();
			that.size( maxSize.w, maxSize.h )._setAbsolute();
			
			if( _ie6 && _doctype )
			    wrapStyle.width = _$top.width() + 'px';
			
			config.drag = false;
			config.resize = false;
			rbStyle.cursor = 'auto';
			titleStyle.cursor = 'auto';
			
			DOM.max[0].style.display = 'none';
			DOM.res[0].style.display = 'inline-block';
			
			that._maxState = true;
		}
		else
		{
			_$html.removeClass('ui_lock_scroll');
			
			wrapStyle.top = that._or.t;
			wrapStyle.left = that._or.l;
			that.size( that._or.w, that._or.h )._autoPositionType();
			config.drag = that._or.d;
		    config.resize = that._or.r;
		    rbStyle.cursor = that._or.rc;
		    titleStyle.cursor = that._or.tc;
		
		    DOM.res[0].style.display = 'none';
			DOM.max[0].style.display = 'inline-block';
			
			delete that._or;
			
			that._maxState = false;
		}
		
		return that;
	},
	
	/*! 最小化窗口 */
	min: function()
	{
		var that = this,
		    DOM = that.DOM,
			main = DOM.main[0].style,
			buttons = DOM.buttons[0].style,
			dialog = DOM.dialog[0].style,
			rb = DOM.rb[0].style.cursor,
			resize = that.config.resize;
			
		if( !that._minState )
		{
		    if( that._maxState )
				that.max();
			
			that._minRz = {rzs:resize,btn:buttons.display};
			main.display = 'none';
		    buttons.display = 'none';
		    dialog.width = main.width;
			rb.cursor = 'auto';
			resize = false;
		
		    that._minState = true;
		}
		else
		{
		    main.display = '';
			buttons.display = that._minRz.btn;
			dialog.width = '';
			resize = that._minRz;
			rb.cursor = that._minRz.rzs ? 'se-resize' : 'auto';
			
			delete that._minRz;
			
			that._minState = false;
		}
		
		that._ie6SelectFix();
		
		return that;
	},
	
	/*!
	 * 获取指定id的窗口对象或窗口中iframe加载的内容页的window对象
	 * @param {String} 指定的id
	 * @param {String} 是否返回的为指定id的窗口对象
	 *        用数字1来表示真，如果不写或写其它为false
	 * @return {Object|null}
	 */
	get: function( id, object )
	{
		if( lhgdialog.list[id] )
		{
			if( object === 1 )
			    return lhgdialog.list[id];
			else
			    return lhgdialog.list[id].content || null;
		}
		
		return null;
	},
	
	/**
	 * 刷新或跳转指定页面
	 * @param	{Object, 指定页面的window对象}
	 * @param	{String, 要跳转到的页面地址}
	 */
	reload: function( win, url, callback )
	{
	    win = win || window;
		
		try{
		    win.location.href = url ? url : win.location.href;
		}
		catch(e){ // 跨域
			url = this.iframe.src;
			$(this.iframe).attr('src', url);
		};
		
		callback && callback.call( this );
		
		return this;
	},
	
	/*!
	 * 设置iframe方式加载内容页
	 */
	_iframe: function( url )
	{
	    var that = this, iframe, $iframe, iwin, $idoc, $ibody, iWidth, iHeight,
		    $content = that.DOM.content,
			config = that.config,
			loading = that._load = $('.ui_loading',$content[0])[0],
		    initCss = 'position:absolute;left:-9999em;border:none 0;background:transparent',
		    loadCss = 'width:100%;height:100%;border:none 0;';
		
		// 是否允许缓存. 默认true
		if( config.cache === false )
		{
			var ts = (new Date).getTime(),
				ret = url.replace(/([?&])_=[^&]*/, '$1_=' + ts );
			url = ret + ((ret === url) ? (/\?/.test(url) ? '&' : '?') + '_=' + ts : '');
		}
			
		iframe = that.iframe = _doc.createElement('iframe');
		iframe.name = config.id;
		iframe.style.cssText = initCss;
		iframe.setAttribute('frameborder', 0, 0);
		
		$iframe = $(iframe);
		$content[0].appendChild( iframe );
		
		// 延迟加载iframe的src属性，IE6下不延迟加载会出现加载进度条的BUG
		that._frmTimer = setTimeout(function(){
		    $iframe.attr('src', url);
		}, 1);
		
		// iframe中页面加载完成后执行的函数
		var load = that._fmLoad = function()
		{
			$content.addClass('ui_state_full');
			
			// 增强窗口置顶功能，iframe方式加载内容或跨域加载内容页时点窗口内容部分置顶窗口
			// 通过使用重置loading层来优雅的完成此功能，在focus方法中有此功能的相关代码
			var DOM = that.DOM, ltSize,
			    lt = DOM.lt[0].offsetHeight,
				main = DOM.main[0].style;
				
			loading.style.cssText = 'display:none;position:absolute;background:#FFF;opacity:0;' + 
			    'filter:alpha(opacity=0);z-index:1;width:' + main.width + ';height:' + main.height + ';';
			// 此部分代码结束，在拖动改变大小的_dragEvent.onmove方法中还有此功能的相关代码
			
			try{
			    iwin = that.content = iframe.contentWindow; // 定义窗口对象content属性为内容页的window对象
				$idoc = $(iwin.document);
				$ibody = $(iwin.document.body);
			}catch(e){// 跨域
			    iframe.style.cssText = loadCss;
				return;
			}
			// 获取iframe内部尺寸
			iWidth = config.width === 'auto'
			? $idoc.width() + (_ie6 ? 0 : parseInt($ibody.css('marginLeft')))
			: config.width;
			
			iHeight = config.height === 'auto'
			? $idoc.height() : config.height;
			
			// 适应iframe尺寸
			setTimeout(function(){
			    iframe.style.cssText = loadCss;
			},0);// setTimeout: 防止IE6~7对话框样式渲染异常
		
			// 窗口最大化时这里不用再计算窗口的尺寸和位置了，如果再计算窗口会出现错位
			if( !that._maxState )
			{
			    that.size( iWidth, iHeight )
			    .position( config.left, config.top );
			}
			
			// 非跨域时还要对loading层重设大小，要不宽和度都为'auto'
			loading.style.width = main.width;
			loading.style.height = main.height;
			
			config.init && config.init.call( that, iwin, _top );
		};
		
		// 绑定iframe元素api属性为窗口自身对象，在内容页中此属性很重要
		that.iframe.api = that;
		$iframe.bind( 'load', load );
	},
	
	/*! 获取窗口元素 */
	_getDOM: function()
	{
		var wrap = _doc.createElement('div'),
		    body = _doc.body;
		
		wrap.style.cssText = 'position:absolute;left:0;top:0;visibility:hidden;';
		wrap.innerHTML = dialogTpl;
		
        var name, i = 0,
			DOM = { wrap: $(wrap) },
			els = wrap.getElementsByTagName('*'),
			len = els.length;
			
		for( ; i < len; i ++ )
		{
			name = els[i].className.split('ui_')[1];
			if(name) DOM[name] = $(els[i]);
		};
		
		body.insertBefore(wrap, body.firstChild);
		
		return DOM;
	},
	
	/*!
	 * px与%单位转换成数值 (百分比单位按照最大值换算)
	 * 其他的单位返回原值
	 */
	_toNumber: function( thisValue, maxValue )
	{
		if( typeof thisValue === 'number' )
			return thisValue;
		
		if( thisValue.indexOf('%') !== -1 )
			thisValue = parseInt(maxValue * thisValue.split('%')[0] / 100);
		
		return thisValue;
	},
	
	/*! 计算最大化窗口时窗口的尺寸 */
	_maxSize: function()
	{
	    var that = this, DOM = that.DOM,
		    wrap = DOM.wrap[0],
			main = DOM.main[0],
			maxWidth, maxHeight;
			
		maxWidth = _$top.width() - wrap.offsetWidth + main.offsetWidth;
		maxHeight = _$top.height() - wrap.offsetHeight + main.offsetHeight;
		
		return { w: maxWidth, h: maxHeight };
	},
	
	/*! 让IE6 CSS支持PNG背景 */
	_ie6PngFix: function()
	{
	    if( _ie6 )
		{
			var i = 0, elem, png, pngPath, runtimeStyle,
				path = lhgdialog.setting.path + '/skins/',
				list = this.DOM.wrap[0].getElementsByTagName('*');
			
			for( ; i < list.length; i ++ )
			{
				elem = list[i];
				png = elem.currentStyle['png'];
				if( png )
				{
					pngPath = path + png;
					runtimeStyle = elem.runtimeStyle;
					runtimeStyle.backgroundImage = 'none';
					runtimeStyle.filter = "progid:DXImageTransform.Microsoft." +
						"AlphaImageLoader(src='" + pngPath + "',sizingMethod='scale')";
				};
			}
		}
		
		return this;
	},
	
	/*! 强制覆盖IE6下拉控件 */
	_ie6SelectFix: _ie6 ? function(){
		var $wrap = this.DOM.wrap,
			wrap = $wrap[0],
			expando = expando + 'iframeMask',
			iframe = $wrap[expando],
			width = wrap.offsetWidth,
			height = wrap.offsetHeight;

		width = width + 'px';
		height = height + 'px';
		if(iframe)
		{
			iframe.style.width = width;
			iframe.style.height = height;
		}else{
			iframe = wrap.appendChild(_doc.createElement('iframe'));
			$wrap[expando] = iframe;
			iframe.src = "javascript:''";
			iframe.style.cssText = 'position:absolute;z-index:-1;left:0;top:0;'
			+ 'filter:alpha(opacity=0);width:' + width + ';height:' + height;
		}
	} : _fn,
	
	/*! 自动切换定位类型 */
	_autoPositionType: function()
	{
		this[this.config.fixed ? '_setFixed' : '_setAbsolute']();
	},
	
	/*! 设置静止定位 */
	_setFixed: function( el )
	{
		var style = el ? el.style : this.DOM.wrap[0].style;
		
		if( _ie6 )
		{
			var sLeft = _$top.scrollLeft(),
				sTop = _$top.scrollTop(),
				left = parseInt(style.left) - sLeft,
				top = parseInt(style.top) - sTop,
				txt = _doctype ? 'this.ownerDocument.body' :
				    'this.ownerDocument.documentElement';
			
			this._setAbsolute();
			
			style.setExpression( 'left', txt + '.scrollLeft +' + left );
			style.setExpression( 'top', txt + '.scrollTop +' + top );
		}
		else
			style.position = 'fixed';
	},
	
	/*! 设置绝对定位 */
	_setAbsolute: function()
	{
		var style = this.DOM.wrap[0].style;
			
		if(_ie6)
		{
			style.removeExpression('left');
			style.removeExpression('top');
		}

		style.position = 'absolute';
	},
	
	/*! 按钮回调函数触发 */
	_click: function( name )
	{ 
		var that = this,
			fn = that._listeners[name] && that._listeners[name].callback;
		return typeof fn !== 'function' || fn.call(that, window) !== false ?
			that.close() : that;
	},
	
	/*! 重置位置与尺寸 */
	_reset: function()
	{
		var test = !!window.ActiveXObject,
		    newSize,
			that = this,
			tw = _$top.width(),
			tt = _$top.height(),
			oldSize = that._winSize || tw * tt,
			oldWidth = that._lockDocW || tw,
			left = that._left,
			top = that._top;
		
		if(test)
		{
			//IE6下遮罩大小改变
			if( that._lock && _ie6 )
			    $('#ldg_lockmask',_doc).css({ width:tw + 'px', height:tt + 17 + 'px' });
			
			newWidth = that._lockDocW = tw;
			//IE6~7 window.onresize bug
			newSize = that._winSize =  tw * tt;
			if( oldSize === newSize ) return;
		};
		
		if( that._maxState )
		{
		    var size = that._maxSize();
			that.size( size.w, size.h );
		}
		
		//IE6~8会出现最大化还原后窗口重新定位，锁定滚动条在IE下就会触发resize事件BUG 
		if( test && Math.abs(oldWidth - newWidth) === 17 ) return;
		
		if( left || top )
			that.position( left, top );
	},
	
	_addEvent: function()
	{
		var resizeTimer,
			that = this,
			config = that.config,
			DOM = that.DOM;
		
		// 窗口调节事件
		that._winResize = function()
		{
			resizeTimer && clearTimeout(resizeTimer);
			resizeTimer = setTimeout(function()
			{
				that._reset();
			}, 140);
		};
		_$top.bind('resize', that._winResize);
		
		// 监听点击
		DOM.wrap.bind('click', function(event){
			var target = event.target, callbackID;
			
			if( target.disabled ) return false; // IE BUG
			
			if( target === DOM.close[0] )
			{
				that._click('cancel');
				return false;
			}
			else if( target === DOM.max[0] || target === DOM.res[0] || target === DOM.max_b[0]
			    || target === DOM.res_b[0] || target === DOM.res_t[0] )
			{
			    that.max();
				return false;
			}
			else if( target === DOM.min[0] || target === DOM.min_b[0] )
			{
				that.min();
				return false;
			}
			else
			{
				callbackID = target[expando + 'callback'];
				callbackID && that._click(callbackID);
			}
		}).bind('mousedown',function(event){
		    that.zindex();
			
			var target = event.target;
			
			if( config.drag !== false && target === DOM.title[0]
			|| config.resize !== false && target === DOM.rb[0] )
			{
				_use(event);
				return false;
			}
		});
		
		// 双击标题栏最大化还窗口事件
		if( config.max )
		    DOM.title.bind('dblclick',function(){ that.max(); return false; });
	},
	
	/*!  卸载事件代理 */
	_removeEvent: function()
	{
		var that = this,
			DOM = that.DOM;
		
		DOM.wrap.unbind();
		DOM.title.unbind();
		_$top.unbind('resize', that._winResize);
	}
};

lhgdialog.fn._init.prototype = lhgdialog.fn;

/*! 此对象用来存储获得焦点的窗口对象实例 */
lhgdialog.focus = null;

/*! 存储窗口实例的对象列表 */
lhgdialog.list = {};

/*!
 * 全局快捷键
 * 由于跨框架时事件是绑定到最顶层页面，所以当当前页面卸载时必须要除移此事件
 * 所以必须unbind此事件绑定的函数，所以这里要给绑定的事件定义个函数
 * 这样在当前页面卸载时就可以移此事件绑定的相应函数，不而不影响顶层页面此事件绑定的其它函数
 */
onKeyDown = function(event)
{
	var target = event.target,
		api = lhgdialog.focus,
		keyCode = event.keyCode;

	if( !api || !api.config.esc || api.config.cancel === false ) return;
		
	keyCode === 27 && api._click(api.config.cancelVal);
};

_$doc.bind('keydown',onKeyDown);

/*!
 * 框架页面卸载前关闭所有穿越的对话框
 * 同时移除拖动层和遮罩层
 */
_top != window && $(window).bind('unload',function()
{
    var list = lhgdialog.list;
	for( var i in list )
	{
	    if(list[i])
		    list[i].close();
	}
	_singleton && _singleton.DOM.wrap.remove();
	
	_$doc.unbind('keydown',onKeyDown);
	
	$('#ldg_lockmask',_doc)[0] && $('#ldg_lockmask',_doc).remove();
	$('#ldg_dragmask',_doc)[0] && $('#ldg_dragmask',_doc).remove();
});

/*! lhgdialog 的全局默认配置 */
lhgdialog.setting =
{
    content: '<div class="ui_loading"><span>loading...</span></div>',
	title: '\u89C6\u7A97 ',     // 标题,默认'视窗'
	button: null,	     		// 自定义按钮
	ok: null,					// 确定按钮回调函数
	cancel: null,				// 取消按钮回调函数
	init: null,					// 对话框初始化后执行的函数
	close: null,				// 对话框关闭前执行的函数
	okVal: '\u786E\u5B9A',		// 确定按钮文本,默认'确定'
	cancelVal: '\u53D6\u6D88',	// 取消按钮文本,默认'取消'
	skin: '',					// 多皮肤共存预留接口
	esc: true,					// 是否支持Esc键关闭
	show: true,					// 初始化后是否显示对话框
	width: 'auto',				// 内容宽度
	height: 'auto',				// 内容高度
	icon: null,					// 消息图标名称
	path: _path,                // lhgdialog路径
	lock: false,				// 是否锁屏
	focus: true,                // 窗口是否自动获取焦点
	parent: null,               // 打开子窗口的父窗口对象，主要用于多层锁屏窗口
	padding: '10px',		    // 内容与边界填充距离
	fixed: false,				// 是否静止定位
	left: '50%',				// X轴坐标
	top: '38.2%',				// Y轴坐标
	max: true,                  // 是否显示最大化按钮
	min: true,                  // 是否显示最小化按钮
	zIndex: 1976,				// 对话框叠加高度值(重要：此值不能超过浏览器最大限制)
	resize: true,				// 是否允许用户调节尺寸
	drag: true, 				// 是否允许用户拖动位置
	cache: true,                // 是否缓存窗口内容页
	data: null,                 // 传递各种数据
	extendDrag: false           // 增加lhgdialog拖拽体验
};

/*!
 *------------------------------------------------
 * 对话框模块-拖拽支持（可选外置模块）
 *------------------------------------------------
 */
var _use, _isSetCapture = 'setCapture' in _root,
	_isLosecapture = 'onlosecapture' in _root;

lhgdialog.dragEvent =
{
    onstart: _fn,
	start: function(event)
	{
	    var that = lhgdialog.dragEvent;
		
		_$doc
		.bind( 'mousemove', that.move )
		.bind( 'mouseup', that.end );
		
		that._sClientX = event.clientX;
		that._sClientY = event.clientY;
		that.onstart( event.clientX, event.clientY );
		
		return false;
	},
	
	onmove: _fn,
	move: function(event)
	{
	    var that = lhgdialog.dragEvent;
		
		that.onmove(
		    event.clientX - that._sClientX,
			event.clientY - that._sClientY
		);
		
		return false;
	},
	
	onend: _fn,
	end: function(event)
	{
	    var that = lhgdialog.dragEvent;
		
		_$doc
		.unbind('mousemove', that.move)
		.unbind('mouseup', that.end);
		
		that.onend(  event.clientX, event.clientY );
		return false;
	}
};

_use = function(event)
{
	var limit, startWidth, startHeight, startLeft, startTop, isResize,
		api = lhgdialog.focus,
		config = api.config,
		DOM = api.DOM,
		wrap = DOM.wrap[0],
		title = DOM.title,
		main = DOM.main[0],
		_dragEvent = lhgdialog.dragEvent,
	
	// 清除文本选择
	clsSelect = 'getSelection' in _top ?
	function(){
		_top.getSelection().removeAllRanges();
	}:function(){
		try{_doc.selection.empty();}catch(e){};
	};
	
	// 对话框准备拖动
	_dragEvent.onstart = function( x, y )
	{
		if( isResize )
		{
			startWidth = main.offsetWidth;
			startHeight = main.offsetHeight;
		}
		else
		{
			startLeft = wrap.offsetLeft;
			startTop = wrap.offsetTop;
		};
		
		_$doc.bind( 'dblclick', _dragEvent.end );
		
		!_ie6 && _isLosecapture
		? title.bind('losecapture',_dragEvent.end )
		: _$top.bind('blur',_dragEvent.end);
		
		_isSetCapture && title[0].setCapture();
		
		DOM.border.addClass('ui_state_drag');
		api.focus();
	};
	
	// 对话框拖动进行中
	_dragEvent.onmove = function( x, y )
	{
		if( isResize )
		{
			var wrapStyle = wrap.style,
				style = main.style,
				width = x + startWidth,
				height = y + startHeight;
			
			wrapStyle.width = 'auto';
			config.width = style.width = Math.max(0,width) + 'px';
			wrapStyle.width = wrap.offsetWidth + 'px';
			
			config.height = style.height = Math.max(0,height) + 'px';
			//api._ie6SelectFix();
		    // 使用loading层置顶窗口时窗口大小改变相应loading层大小也得改变
			api._load && $(api._load).css({width:style.width, height:style.height});
		}
		else
		{
			var style = wrap.style,
				left = x + startLeft,
				top = y + startTop;

			config.left = Math.max( limit.minX, Math.min(limit.maxX,left) );
			config.top = Math.max( limit.minY, Math.min(limit.maxY,top) );
			style.left = config.left + 'px';
			style.top = config.top + 'px';
		}
			
		clsSelect();
	};
	
	// 对话框拖动结束
	_dragEvent.onend = function( x, y )
	{
		_$doc.unbind('dblclick',_dragEvent.end);
		
		!_ie6 && _isLosecapture
		? title.unbind('losecapture',_dragEvent.end)
		: _$top.unbind('blur',_dragEvent.end);
		
		_isSetCapture && title[0].releaseCapture();
		
		_ie6 && api._autoPositionType();
		
		DOM.border.removeClass('ui_state_drag');
	};
	
	isResize = event.target === DOM.rb[0] ? true : false;
	
	limit =	(function(fixed)
	{
		var	ow = wrap.offsetWidth,
			// 向下拖动时不能将标题栏拖出可视区域
			oh = title[0].offsetHeight || 20,
			ww = _$top.width(),
			wh = _$top.height(),
			dl = fixed ? 0 : _$top.scrollLeft(),
			dt = fixed ? 0 : _$top.scrollTop();
		    // 坐标最大值限制(在可视区域内)	
		    maxX = ww - ow + dl;
		    maxY = wh - oh + dt;
		
		return {
			minX: dl,
			minY: dt,
			maxX: maxX,
			maxY: maxY
		};
	})(wrap.style.position === 'fixed');
	
	_dragEvent.start(event);
};

/*! 
 * 页面DOM加载完成执行的代码
 */
$(function(){
	// 触发浏览器预先缓存背景图片
	setTimeout(function()
	{
	    if(_count) return;
		lhgdialog({left:'-9999em',time:9,fixed:false,lock:false,focus:false});
	},150);
	
	// 增强lhgdialog拖拽体验（可选外置模块，如不需要可删除）
	// 防止鼠标落入iframe导致不流畅，对超大对话框拖动优化
	lhgdialog.setting.extendDrag &&
	(function(dragEvent){
	    var mask = _doc.createElement('div'),
		    style = mask.style,
			positionType = _ie6 ? 'absolute' : 'fixed';
		mask.id = 'ldg_dragmask';
		
		style.cssText = 'display:none;position:' + positionType + ';left:0;top:0;width:100%;height:100%;'
		+ 'cursor:move;filter:alpha(opacity=0);opacity:0;background:#FFF;pointer-events:none;';
		
		_doc.body.appendChild(mask);
		
		dragEvent._start = dragEvent.start;
		dragEvent._end = dragEvent.end;
		
		dragEvent.start = function()
		{
			var api = lhgdialog.focus,
				main = api.DOM.main[0],
				iframe = api.iframe;
			
			dragEvent._start.apply(this, arguments);
			style.display = 'block';
			style.zIndex = lhgdialog.setting.zIndex + 3;
			
			if(positionType === 'absolute')
			{
				style.width = _$top.width() + 'px';
				style.height = _$top.height() + 'px';
				style.left = _$doc.scrollLeft() + 'px';
				style.top = _$doc.scrollTop() + 'px';
			};
			
			if( iframe && main.offsetWidth * main.offsetHeight > 307200 )
				main.style.visibility = 'hidden';
		};
		
		dragEvent.end = function()
		{
			var api = lhgdialog.focus;
			dragEvent._end.apply(this, arguments);
			style.display = 'none';
			if(api) api.DOM.main[0].style.visibility = 'visible';
		};
	})(lhgdialog.dragEvent);
});

/*! 使用jQ方式调用窗口 */
$.fn.dialog = function()
{
	var config = arguments;
	this.bind('click',function(){ lhgdialog.apply(this,config); return false; });
	return this;
};
		
window.lhgdialog = $.dialog = lhgdialog;

})( this.jQuery || this.lhgcore, this );

/*!
 *------------------------------------------------
 * 对话框其它功能扩展模块（可选外置模块）
 *------------------------------------------------
 */
;(function( $, lhgdialog, undefined ){

var _zIndex = function()
{
    return lhgdialog.setting.zIndex;
};

/**
 * 警告
 * @param	{String}	消息内容
 */
lhgdialog.alert = function( content, callback, parent )
{
	return lhgdialog({
		title: '警告',
		id: 'Alert',
		zIndex: _zIndex(),
		icon: 'alert.gif',
		fixed: true,
		lock: true,
		content: content,
		ok: true,
		resize: false,
		close: callback,
		parent: parent || null
	});
};

/**
 * 确认
 * @param	{String}	消息内容
 * @param	{Function}	确定按钮回调函数
 * @param	{Function}	取消按钮回调函数
 */
lhgdialog.confirm = function( content, yes, no, parent )
{
	return lhgdialog({
		title: '确认',
		id: 'confirm.gif',
		zIndex: _zIndex(),
		icon: 'confirm.gif',
		fixed: true,
		lock: true,
		content: content,
		resize: false,
		parent: parent || null,
		ok: function(here){
			return yes.call(this, here);
		},
		cancel: function(here){
			return no && no.call(this, here);
		}
	});
};

/**
 * 提问
 * @param	{String}	提问内容
 * @param	{Function}	回调函数. 接收参数：输入值
 * @param	{String}	默认值
 */
lhgdialog.prompt = function( content, yes, value, parent )
{
	value = value || '';
	var input;
	
	return lhgdialog({
		title: '提问',
		id: 'Prompt',
		zIndex: _zIndex(),
		icon: 'prompt.gif',
		fixed: true,
		lock: true,
		parent: parent || null,
		content: [
			'<div style="margin-bottom:5px;font-size:12px">',
				content,
			'</div>',
			'<div>',
				'<input value="',
					value,
				'" style="width:18em;padding:6px 4px" />',
			'</div>'
			].join(''),
		init: function(){
			input = this.DOM.content[0].getElementsByTagName('input')[0];
			input.select();
			input.focus();
		},
		ok: function(here){
			return yes && yes.call(this, input.value, here);
		},
		cancel: true
	});
};

/**
 * 短暂提示
 * @param	{String}	提示内容
 * @param   {Number}    显示时间 (默认1.5秒)
 * @param	{String}	提示图标 (注意要加扩展名)
 * @param   {Function}  提示关闭时执行的回调函数
 */
lhgdialog.tips = function( content, time, icon, callback )
{
	var reIcon = icon ? function(){
		this.DOM.icon[0].innerHTML = '<img src="' + this.config.path + 'skins/icons/' + icon + '" class="ui_icon_bg"/>';
		this.DOM.icon[0].style.display = '';
		if( callback ) this.config.close = callback;
	} : function(){
	    this.DOM.icon[0].style.display = 'none';
		if( callback ) this.config.close = callback;
	};
	
	return lhgdialog({
		id: 'Tips',
		zIndex: _zIndex(),
		title: false,
		cancel: false,
		fixed: true,
		lock: false,
		resize: false
	})
	.content(content)
	.time(time || 1.5, reIcon);
};

})( this.jQuery||this.lhgcore, this.lhgdialog );