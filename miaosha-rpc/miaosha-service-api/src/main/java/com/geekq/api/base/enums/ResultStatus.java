package com.geekq.api.base.enums;

/**
 * 普通返回类
 * 1打头 系统系列错误
 * 2 注册登录系列错误
 * 3 check 系列错误
 * 4 秒杀错误
 * 5 商品错误
 * 6 订单错误
 *
 * @author qiurunze
 */
public enum ResultStatus {
    SUCCESS(0, "成功"),
    FAILD(-1, "失败"),
    EXCEPTION(-1, "系统异常"),
    PARAM_ERROR(10000, "参数错误"),
    SYSTEM_ERROR(10001, "系统错误"),
    FILE_NOT_EXIST(10002, "文件不存在"),
    FILE_NOT_DOWNLOAD(10003, "文件没有下载"),
    FILE_NOT_GENERATE(10004, "文件没有生成"),
    FILE_NOT_STORAGE(10005, "文件没有入库"),
    SYSTEM_DB_ERROR(10006, "数据库系统错误"),
    FILE_ALREADY_DOWNLOAD(10007, "文件已经下载"),
    DATA_ALREADY_PEXISTS(10008, "数据已经存在"),


    /**
     * 注册登录
     */
    RESIGETR_SUCCESS(20000, "注册成功!"),
    RESIGETER_FAIL(200001, "注册失败!"),
    CODE_FAIL(200002, "验证码不一致!"),

    /**
     * check
     */
    BIND_ERROR(30001, "参数校验异常：%s"),
    ACCESS_LIMIT_REACHED(30002, "请求非法!"),
    REQUEST_ILLEGAL(30004, "访问太频繁,请稍后再试!!!"),
    SESSION_ERROR(30005, "Session不存在或者已经失效!"),
    PASSWORD_EMPTY(30006, "登录密码不能为空!"),
    MOBILE_EMPTY(30007, "手机号不能为空!"),
    MOBILE_ERROR(30008, "手机号格式错误!"),
    MOBILE_NOT_EXIST(30009, "账号不存在!"),
    PASSWORD_ERROR(30010, "密码错误!"),
    USER_NOT_EXIST(30011, "用户不存在！"),

    /**
     * 订单模块
     */
    ORDER_NOT_EXIST(60001, "订单不存在"),
    ORDER_WAIT_TIPS(60002, "系统处理中，请稍后在我的订单中查看最终秒杀结果"),
    ORDER_MQ_SEND_ERROR(60003, "发送订单MQ失败"),

    /**
     * 秒杀模块
     */
    MIAO_SHA_OVER(40001, "商品已经秒杀完毕"),

    REPEATE_MIAOSHA(40002, "不能重复秒杀"),

    MIAOSHA_FAIL(40003, "秒杀失败"),

    ORDER_GET_FAIL(40004, "订单获取失败"),

    ORDER_CREATE_FAIL(40005, "订单创建失败"),

    /**
     * 商品模块
     */
    GOODS_GET_FAIL(50001, "获取秒杀商品失败"),
    GOODS_REDUCESTOCK_FAIL(50002, "减库存失败");


    private int code;
    private String message;

    ResultStatus(int code, String message) {
        this.code = code;
        this.message = message;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return this.message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getName() {
        return this.name();
    }

    public String getOutputName() {
        return this.name();
    }

    @Override
    public String toString() {
        return this.getName();
    }
}
