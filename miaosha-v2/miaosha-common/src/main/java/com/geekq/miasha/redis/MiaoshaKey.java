package com.geekq.miasha.redis;

public class MiaoshaKey extends BasePrefix {

    public static MiaoshaKey isGoodsOver = new MiaoshaKey(0, "go");
    public static MiaoshaKey getMiaoshaPath = new MiaoshaKey(60, "mp");
    public static MiaoshaKey getMiaoshaVerifyCode = new MiaoshaKey(300, "vc");
    public static MiaoshaKey getMiaoshaVerifyCodeRegister = new MiaoshaKey(300, "register");
    public static MiaoshaKey getMiaoshaLock = new MiaoshaKey(3, "lock");

    private MiaoshaKey(int expireSeconds, String prefix) {
        super(expireSeconds, prefix);
    }

}
