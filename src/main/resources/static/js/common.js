/**
 * Created by wuketao on 2017/12/6.
 */
/**
 * 保存搜索区域数据
 * @param regionid 搜索区域id
 * @param keyArray 标签id
 * @param valueArray 标签值
 */
function saveSearchRegionData(regionid, keyArray, valueArray) {
    //清空旧数据
    keyArray.length = 0;
    valueArray.length = 0;
    //过滤input text
    $('#' + regionid + ' input:text').each(function () {
        keyArray.push($(this).attr('id'));
        valueArray.push($(this).val().trim())
    });
    //过滤select
    $('#' + regionid + ' select').each(function () {
        keyArray.push($(this).attr('id'));
        valueArray.push($(this).val().trim())
    });
}

/**
 * 恢复搜索区域数据
 * @param keyArray 标签id
 * @param valueArray 标签值
 */
function recoverSearchRegionData(keyArray, valueArray) {
    //通过遍历key数组，来为所有标签赋值
    $.each(keyArray, function (index, value) {
        $('#' + value).val(valueArray[index]);
    });
}

/**
 清空搜索区域
 */
function clearSearchRegion(regionid){
    //清空input text
    $('#' + regionid + ' input').each(function () {
        $(this).val('');
    });
    //过滤select
    $('#' + regionid + ' select').each(function () {
        $(this).val('');
    });
    //过滤 textarea
    $('#' + regionid + ' textarea').each(function () {
        $(this).val('');
    });
}

/**
获取所有区域的json对象
*/
function getSearchRegionJsonObject(regionid){
    var obj = {};
    //过滤input text
    $('#' + regionid + ' input').each(function () {
        obj[$(this).attr('name')]= $(this).val().trim();
    });
    //过滤select
    $('#' + regionid + ' select').each(function () {
        obj[$(this).attr('name')]= $(this).val().trim();
    });
    //过滤 textarea
    $('#' + regionid + ' textarea').each(function () {
        obj[$(this).attr('name')]= $(this).val().trim();
    });
    return obj;
}


//初始化行选中操作
function initTrSelected(tableId) {
    $("#"+tableId+" tr").click(function() {
        $(this).addClass("success").siblings().removeClass("success");
    });
};

//获取选中行
function getSelectRow(tableId,colNo){
    return $("#"+tableId+" .success").children().eq(colNo).html();
}

/**
    用js对象填充区域
*/
function fillRegionByObj(regionId,obj){
    for ( var key in obj ){
        $('#'+regionId+' input ').each(function(){
              if($(this).attr('name')==key){
                    $(this).val(obj[key])
                }
            })
        $('#'+regionId+' textarea ').each(function(){
              if($(this).attr('name')==key){
                    $(this).val(obj[key])
                }
            })
        $('#'+regionId+' select ').each(function(){
              if($(this).attr('name')==key){
                    // 对于字符串类型和对象类型分别处理
                    if('object'==typeof(obj[key])){
                        $(this).val(obj[key]['$name'])
                    }else{
                        $(this).val(obj[key])
                    }
                }
            })
        }
}

function formatTime(nS) {
    return new Date(parseInt(nS)).toLocaleString().replace(/:\d{1,2}$/,' ');
}


//时间戳转换
template.helper('dateFormat', function (date, format) {
    date = new Date(date);

    var map = {
        "M": date.getMonth() + 1, //月份
        "d": date.getDate(), //日
        "h": date.getHours(), //小时
        "m": date.getMinutes(), //分
        "s": date.getSeconds(), //秒
        "q": Math.floor((date.getMonth() + 3) / 3), //季度
        "S": date.getMilliseconds() //毫秒
    };
    format = format.replace(/([yMdhmsqS])+/g, function (all, t) {
        var v = map[t];
        if (v !== undefined) {
            if (all.length > 1) {
                v = '0' + v;
                v = v.substr(v.length - 2);
            }
            return v;
        } else if (t === 'y') {
            return (date.getFullYear() + '').substr(4 - all.length);
        }
        return all;
    });
    return format;
});

//icheck初始化方法
function icheckInit(divid){
	$('#'+divid+' input').iCheck({
		checkboxClass: 'icheckbox-blue',
		radioClass: 'iradio-blue',
		increaseArea: '20%'
	})
}

jQuery.Huifold = function(obj,obj_c,speed,obj_type,Event){
	if(obj_type == 2){
		$(obj+":first").find("b").html("-");
		$(obj_c+":first").show()}
	$(obj).bind(Event,function(){
		if($(this).next().is(":visible")){
			if(obj_type == 2){
				return false}
			else{
				$(this).next().slideUp(speed).end().removeClass("selected");
				$(this).find("b").html("+")}
		}
		else{
			if(obj_type == 3){
				$(this).next().slideDown(speed).end().addClass("selected");
				$(this).find("b").html("-")}else{
				$(obj_c).slideUp(speed);
				$(obj).removeClass("selected");
				$(obj).find("b").html("+");
				$(this).next().slideDown(speed).end().addClass("selected");
				$(this).find("b").html("-")}
		}
	})}
