package com.test;

import com.alibaba.druid.filter.config.ConfigTools;
import com.alibaba.druid.util.DruidPasswordCallback;

/**
 * 数据库密码
 *
 * @author chenh
 * @version 1.0
 * @date 2022/8/16 14:11
 **/
public class DesPassword extends DruidPasswordCallback {

    /**
     * 生成加密数据库密码
     *
     * @param args
     * @throws Exception
     */
    public static void main(String[] args) throws Exception {
        String password = "123456";
        getEncryptPwd(password);
    }


    public static void getEncryptPwd(String password) throws Exception {
        String[] arr = ConfigTools.genKeyPair(512);
        System.out.println("privateKey:" + arr[0]);
        System.out.println("publicKey:" + arr[1]);
        System.out.println("password:" + ConfigTools.encrypt(arr[0], password));
    }

}
