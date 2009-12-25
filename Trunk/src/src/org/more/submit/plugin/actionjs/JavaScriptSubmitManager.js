var more=more;
if (typeof(more)=="undefined")
  more={util:{}};
/*=====================================================================*/
more.util.formatData = function(formatData,formater){
  /*eg:new Data()*/
  var type=Object.prototype.toString.call(formatData);
  if (formatData==null || type!="[object Date]")
    var data=new Date();
  else
    var data=formatData;
  /*eg:format="yyyy-MM-dd hh:mm:ss";*/
  var type=Object.prototype.toString.call(formater);
  if (formater==null || type=="undefined")
    var format="yyyy-MM-dd hh:mm:ss";
  else
    var format=formater;
  var o = {
    "M+" :  data.getMonth()+1,  //month
    "d+" :  data.getDate(),     //day
    "h+" :  data.getHours(),    //hour
    "m+" :  data.getMinutes(),  //minute
    "s+" :  data.getSeconds(), //second
    "q+" :  Math.floor((data.getMonth()+3)/3),  //quarter
    "S"  :  data.getMilliseconds() //millisecond
  }
  if(/(y+)/.test(format))
    format = format.replace(RegExp.$1, (data.getFullYear()+"").substr(4 - RegExp.$1.length));
  for(var k in o)
    if(new RegExp("("+ k +")").test(format))
      format = format.replace(RegExp.$1, RegExp.$1.length==1 ? o[k] : ("00"+ o[k]).substr((""+ o[k]).length));
  return format;
}
more.util.base64 = {
  Base64Chars:"ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@*-",
  encode64: function(s){
	if(!s || s.length == 0) return s;
	var d = "";
	var b = more.util.base64.ucs2_utf8(s);
	var b0, b1, b2, b3;
	var len = b.length;
	var i = 0;
	while(i < len){
	  var tmp = b[i++];
	  b0 = (tmp & 0xfc) >> 2;
	  b1 = (tmp & 0x03) << 4;
	  if(i < len){
		tmp = b[i++];
		b1 |= (tmp & 0xf0) >> 4;
		b2 = (tmp & 0x0f) << 2;
		if(i< len){
		  tmp = b[i++];
		  b2 |= (tmp & 0xc0) >> 6;
		  b3 = tmp & 0x3f;
		}else
		  b3 = 64; // 1 byte "-" is supplement
	  }else
		b2 = b3 = 64; // 2 bytes "-" are supplement
	  d+=more.util.base64.Base64Chars.charAt(b0);
	  d+=more.util.base64.Base64Chars.charAt(b1);
	  d+=more.util.base64.Base64Chars.charAt(b2);
	  d+=more.util.base64.Base64Chars.charAt(b3);
	}
	return d;
  },
  uncoded64: function(s){
	if(!s) return null;
	var len = s.length;
	if(len%4 != 0)
	  throw s+" is not a valid Base64 string.";
	var b = new Array();
	var i=0, j=0, e=0, c, tmp;
	while(i < len){
	  c = more.util.base64.Base64Chars.indexOf(s.charAt(i++));
	  tmp = c << 18;
	  c = more.util.base64.Base64Chars.indexOf(s.charAt(i++));
	  tmp |= c << 12;
	  c = more.util.base64.Base64Chars.indexOf(s.charAt(i++));
	  if(c < 64){
		tmp |= c << 6;
		c = more.util.base64.Base64Chars.indexOf(s.charAt(i++));
		if(c < 64)
		  tmp |= c;
		else
		  e = 1;
	  }else{
		e = 2;
		i++;
	  }
	  b[j+2] = tmp & 0xff;
	  tmp >>= 8;
	  b[j+1] = tmp & 0xff;
	  tmp >>= 8;
	  b[j+0] = tmp & 0xff;
	  j += 3;
	}
	b.splice(b.length-e, e);
	return more.util.base64.utf8_ucs2(b);
  },
  ucs2_utf8: function(s){
	if (!s) return null;
	var d = new Array();
	if (s == "") return d;
	var c = 0, i = 0, j = 0;
	var len = s.length;
	while(i < len){
	  c = s.charCodeAt(i++);
	  if(c <= 0x7f)
		// 1 byte
		d[j++] = c;
	  else if((c >= 0x80) && (c <= 0x7ff)){
		// 2 bytes
		d[j++] = ((c >> 6) & 0x1f) | 0xc0;
		d[j++] = (c & 0x3f) | 0x80;
	  }else{
		// 3 bytes
		d[j++] = (c >> 12) | 0xe0;
		d[j++] = ((c >> 6) & 0x3f) | 0x80;
		d[j++] = (c & 0x3f) | 0x80;
	  }
	}//end whil
	return d;
  },
  utf8_ucs2: function(s){
	if(!s) return null;
	var len = s.length;
	if(len == 0) return "";
	var d = "";
	var c = 0, i = 0, tmp = 0;
	while(i < len){
	  c = s[i++];
	  if((c & 0xe0) == 0xe0){
		// 3 bytes
		tmp = (c & 0x0f) << 12;
		c = s[i++];
		tmp |= ((c & 0x3f) << 6);
		c = s[i++];
		tmp |= (c & 0x3f);
	  }else if((c & 0xc0) == 0xc0){
		// 2 bytes
		tmp = (c & 0x1f) << 6;
		c = s[i++];
		tmp |= (c & 0x3f);
	  }else
		// 1 byte
		tmp = c;
	  d += String.fromCharCode(tmp);
	}
	return d;
  }
};
/*=====================================================================*/
more.util.AjaxCall=function(obj){
  if (typeof(obj)!="object")
	throw "param error!";
  else
	var construction=obj;
  //============================
  this.CreateXmlHttpRequestObject=function(){
	var xmlHttp;
	try{xmlHttp = new XMLHttpRequest();}
	catch(e){
	   var XmlHttpVersions = new Array("MSXML2.XMLHTTP.6.0","MSXML2.XMLHTTP.5.0",
									   "MSXML2.XMLHTTP.4.0","MSXML2.XMLHTTP.3.0",
									   "MSXML2.XMLHTTP","Microsoft.XMLHTTP");
	   for (var i=0; i<XmlHttpVersions.length && !xmlHttp; i++)
		 try{xmlHttp = new ActiveXObject(XmlHttpVersions[i]);}catch (e) {}
	}//end try
	if (!xmlHttp)
	 alert("Error creating the XMLHttpRequest object.");
	else
	  return xmlHttp;
  };
  this.call=function(args){
	var arg="";
	for (var k in args)
	  arg+=k+"="+encodeURI(args[k])+"&";
	if (arg.substr(arg.length-1,arg.length)=="&")
	  arg=arg.substr(0,arg.length-1);
	var ajax=this.CreateXmlHttpRequestObject();
	ajax.open("post",construction.url, construction.synchronize);
	ajax.setRequestHeader("Content-Type","application/x-www-form-urlencoded; charset=UTF-8");
  	ajax.setRequestHeader("Content-Length",arg.length);
	ajax.onreadystatechange=function(){
	  if (ajax.readyState==4){
		if (ajax.status==200)
		  construction.success(ajax);
		else
		  construction.failure(ajax);
	  }
	};
	ajax.send(arg);
	return ajax.responseText;
  };//end function call
};
/*=====================================================================*/
more.util.toString=function(o){
  var type=Object.prototype.toString.call(o);
  if (o==null || type=="undefined")
	//V|null;
	return "V|void";
  else if (type=="[object String]")
	//S|ADSGFEFGDFS=; (Base64)
	return "S|\""+more.util.base64.encode64(o)+"\"";
  else if (type=="[object Number]")
	//N|12;
	return "N|"+o;
  else if (type=="[object Boolean]")
	//B|true
	return "B|"+o;
  else if (type=="[object Date]")
	//D|{2008-12-12 12:12:12}:DataTime
	return "U|{"+more.util.formatData(o)+"}:DataTime";
  else if (type=="[object Array]"){
	var s="["; // A|[ssss,sssss,sssss]
	for (var i=0;i<o.length;i++)
	  s+=more.util.toString(o[i])+",";
	if (s.substr(s.length-1,s.length)==",")
	  s=s.substr(0,s.length-1);
	return "A|"+s+"]";
  }else if (type=="[object Object]"){
	var s="{";// T|{k=v,k=[ssss,ssss,sss]}
	for (var key in o)
	  s+=key+"="+more.util.toString(o[key])+",";
	if (s.substr(s.length-1,s.length)==",")
	  s=s.substr(0,s.length-1);
	return "T|"+s+"}";
  }
};
more.util.toObject=function(str){
  function readString(str,i){
	var returnS="";
	var depth=0;
	for(i;i<str.length;i++){
	  var s_temp=str.charAt(i);
	  if (s_temp=="," && depth==0)
		return [returnS,i];
	  else if (s_temp=="[" || s_temp=="{")
		depth++;
	  else if (s_temp=="]" || s_temp=="}")
		depth--;
	  returnS+=s_temp;
	}
	return [returnS,i];
  };
  if ((new RegExp("^V\|void$","gi")).test(str)==true)
	return null;
  else if ((new RegExp("^S\\|\".*\"$","gi")).test(str)==true){
	var re= new RegExp("^S\\|\"(.*)\"$","gi");
	re.exec(str);	
	return more.util.base64.uncoded64(RegExp.$1);
  }else if ((new RegExp("^N\\|(\\+|-)?\\d{0,}(\\.\\d+){0,}$","gi")).test(str)==true){
	var re= new RegExp("^N\\|(.*)$","gi");
	re.exec(str);
	return new Number(RegExp.$1);
  }else if ((new RegExp("^B\\|(true|false)$","gi")).test(str)==true)
	return (str.indexOf("false")!=-1)?false:true;
  else if ((new RegExp("^U\|\{.*\}:DataTime$","gi")).test(str)==true){
	var re= new RegExp("^U\|\{(\\d+)-(\\d+)-(\\d+) (\\d+):(\\d+):(\\d+)\}:DataTime","gi");
	re.exec(str);
	var data=new Date();
	data.setFullYear(RegExp.$1,RegExp.$2,RegExp.$3);
	data.setHours(RegExp.$4,RegExp.$5,RegExp.$6);
	return data;
  }else if ((new RegExp("^A\|\[.*\](:.*)?$","gi")).test(str)==true){
	var re= new RegExp("^A\\|\\[(.*)\\](:.*)?$","gi");
	re.exec(str);
	var context_str=RegExp.$1;
	var context_index=0;
	var value_array=new Array();
	while(true){
	  if (context_str.length <= context_index)
		break;
	  var res=readString(context_str, context_index);
	  context_index=res[1]+1;
	  value_array.push(more.util.toObject(res[0]));
	}
	return value_array;
  }else if ((new RegExp("^T\\|\{.*\}(:.*)?$","gi")).test(str)==true){
	var re= new RegExp("^T\\|\{(.*)\}(:.*)?$","gi");
	re.exec(str);
	var context_str=RegExp.$1;
	var context_index=0;
	var value_array={};
	while(true){
	  if (context_str.length <= context_index)
		break;
	  var res=readString(context_str, context_index);
	  context_index=res[1]+1;
	  var k=res[0].substring(0,res[0].indexOf("="));
	  var v=res[0].substring(k.length+1,res[0].length);
	  value_array[k]=more.util.toObject(v);
	}
	return value_array;
  }
};
more.util.runScript = function(s) {
  try {
	window.eval(s);
  } catch (e) {
	alert("run script error:"+e);
  }
};
/*=====================================================================*/
more.retain=more.retain;
if (typeof(more.retain)=="undefined")
  more.retain={serverCallURL:""};
more.retain.callServerFunction=function(callName,funArray){
  var type=Object.prototype.toString.call(funArray);
  var argsArray=null;
  if (type!="[object Array]"){
	argsArray={};
	if (funArray.length==1)
	  argsArray=funArray[0];
	else
	  for (var i=0;i<funArray.length;i++)
	    argsArray[i]=funArray[i];
  }else
	argsArray=funArray;
  var ajax = new more.util.AjaxCall({
	url		 : more.retain.serverCallURL,
	synchronize: false,
	success	 : function(ajax){},
	failure	 : function(ajax){throw "more request error.";}
  });
  var argsString=more.util.toString(argsArray);
  var result=ajax.call({callName:callName,args:argsString});
  var result_return=more.util.toObject(result);
  return (typeof(result_return)=="undefined")?result:more.util.toObject(result);
};
/*=====================================================================*/