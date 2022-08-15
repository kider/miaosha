package com.geekq.miasha.utils;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.Random;

/**
 * 验证码
 *
 * @author chenh
 * @version 1.0
 * @date 2022/8/15 15:54
 **/
public class VerifyCodeUtils {

    private static char[] ops = new char[]{'+', '-', '*'};

    public static BufferedImage createVerifyCodeImg(String verifyCode, int width, int height) {
        //create the image
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics g = image.getGraphics();
        // set the background color
        g.setColor(new Color(0xDCDCDC));
        g.fillRect(0, 0, width, height);
        // draw the border
        g.setColor(Color.black);
        g.drawRect(0, 0, width - 1, height - 1);
        // create a random instance to generate the codes
        Random rdm = new Random();
        // make some confusion
        for (int i = 0; i < 50; i++) {
            int x = rdm.nextInt(width);
            int y = rdm.nextInt(height);
            g.drawOval(x, y, 0, 0);
        }
        g.setColor(new Color(0, 100, 0));
        g.setFont(new Font("Candara", Font.BOLD, 24));
        g.drawString(verifyCode, 8, 24);
        g.dispose();
        return image;
    }


    /**
     * 获取验证码值
     *
     * @param exp
     * @return {@link int}
     * @author chenh
     * @date 2022/8/15 15:45
     **/
    public static int calc(String exp) throws ScriptException {
        ScriptEngineManager manager = new ScriptEngineManager();
        ScriptEngine engine = manager.getEngineByName("JavaScript");
        Integer result = (Integer) engine.eval(exp);
        return result;
    }

    /**
     * 公式
     * + - *
     */
    public static String generateVerifyCode() {
        Random rdm = new Random();
        int num1 = rdm.nextInt(10);
        int num2 = rdm.nextInt(10);
        int num3 = rdm.nextInt(10);
        char op1 = ops[rdm.nextInt(3)];
        char op2 = ops[rdm.nextInt(3)];
        return "" + num1 + op1 + num2 + op2 + num3;
    }
}
