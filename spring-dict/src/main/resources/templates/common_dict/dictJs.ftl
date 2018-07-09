
// 默认不做初始化,需要的自己加载
//lzDict.init();

var lzDict = {
		
	/** 服务器端过来的数据 */
	data : ${jsonStr},
	
	/** 是否测试模式，测试模式时，鼠标移动到文字上的时候，有提示key是什么 */
	debugMode : ${debugMode},
	
	/** 初始化 */
	init : function() {
		var that=this;
		$("[data-dict]").each(function() {
			var obj=$(this);
			var key = obj.attr("data-dict");
			//根据key找不到value给段大红文字提示
			var v = that.data[key];
			if(v == null || (typeof (v) == undefined) || (typeof (v) == 'undefined') || v == ''){
				v = '<label style="color: red;">这里有个key:'+key+'</label>';
				obj.html(v);
			}else{
				obj.html(v);
			}
			
			if (that.debugMode) {
				obj.attr("title" , "key=" + key);
			}
		});
	},
}

window.lzDict = lzDict;