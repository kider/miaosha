package com.test.base;

import com.alibaba.druid.filter.config.ConfigTools;
import org.junit.Test;

/**
 * 数据库密码
 *
 * @author chenh
 * @version 1.0
 * @date 2022/8/16 14:11
 **/
public class DesPassword {

    /**
     * 生成加密数据库密码
     */
    @Test
    public void getEncryptPwd() throws Exception {
        String password = "123456";
        String[] arr = ConfigTools.genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + ConfigTools.encrypt(arr[0], password));
    }

}
