$(function () {

    //登录输入框效果
    $('.form_text_ipt input').focus(function () {
        $(this).parent().css({
            'box-shadow': '0 0 3px #bbb',
        });
    });
    $('.form_text_ipt input').blur(function () {
        $(this).parent().css({
            'box-shadow': 'none',
        });
    });

    //表单验证
    $('.form_text_ipt input').bind('input propertychange', function () {
        if ($(this).val() == "") {
            $(this).css({
                'color': 'red',
            });
            $(this).parent().css({
                'border': 'solid 1px red',
            });
        } else {
            $(this).css({
                'color': '#ccc',
            });
            $(this).parent().css({
                'border': 'solid 1px #ccc',
            });
        }
    });

});
