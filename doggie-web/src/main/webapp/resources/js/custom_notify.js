var Noty = {
    error: function (message) {
        new PNotify({
            title: '错误',
            text: message,
            icon: 'glyphicon glyphicon-remove-circle',
            type: 'error',
            delay: 5000
        });
    },
    info: function (message) {
        new PNotify({
            title: '提醒',
            text: message,
            icon: 'glyphicon glyphicon-info-sign',
            type: 'info',
            delay: 5000
        });
    }
};