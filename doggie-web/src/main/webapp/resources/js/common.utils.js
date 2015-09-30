(function (factory) {
    if (typeof define === 'function' && define.amd) {
        define('BkUtils', ['jquery'], factory);
    } else {
        factory(jQuery);
    }
}(function ($) {

    Date.prototype.format = function(fmt)
    { //author: meizz
        var o = {
            "M+" : this.getMonth()+1,                 //月份
            "d+" : this.getDate(),                    //日
            "h+" : this.getHours(),                   //小时
            "m+" : this.getMinutes(),                 //分
            "s+" : this.getSeconds(),                 //秒
            "q+" : Math.floor((this.getMonth()+3)/3), //季度
            "S"  : this.getMilliseconds()             //毫秒
        };
        if(/(y+)/.test(fmt))
            fmt=fmt.replace(RegExp.$1, (this.getFullYear()+"").substr(4 - RegExp.$1.length));
        for(var k in o)
            if(new RegExp("("+ k +")").test(fmt))
                fmt = fmt.replace(RegExp.$1, (RegExp.$1.length==1) ? (o[k]) : (("00"+ o[k]).substr((""+ o[k]).length)));
        return fmt;
    }

    BkUtils = function (options) {

    };
    $.extend(BkUtils.prototype, {
        formatMoney: function (money, scale) {
            scale = scale > 0 && scale <= 20 ? scale : 2;
            money = parseFloat((money + "").replace(/[^\d\.-]/g, "")).toFixed(scale) + "";
            var l = money.split(".")[0].split("").reverse(), r = money.split(".")[1];
            t = "";
            for (i = 0; i < l.length; i++) {
                t += l[i] + ((i + 1) % 3 == 0 && (i + 1) != l.length ? "," : "");
            }
            return t.split("").reverse().join("") + "." + r;
        },
        reverseMoney: function () {
            return parseFloat(s.replace(/[^\d\.-]/g, ""));
        },
        formatDate: function (timestamp, pattern) {
            return (new Date(timestamp)).format(pattern);
        }
    });

    return BkUtils;
}));

var BkUtils = new BkUtils();
