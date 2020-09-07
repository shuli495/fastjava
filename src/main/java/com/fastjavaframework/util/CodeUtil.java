package com.fastjavaframework.util;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * @author wangshuli
 */
public class CodeUtil {

    /**
     * 验证码
     * @param type 类型：str字符串 num数字 strAndNum字符串数字随机
     * @param number 个数
     * @return 随机验证码
     */
    public static String randomCode(String type, int number) {
        ArrayList<Object> list = new ArrayList<>();
        // 验证码类型
        String typeNum = "num",typeStr = "str";
        char beginChar = 'a',endChar='z';
        int maxNumber = 9;

        if (!typeNum.equals(type)) {
            for (char c = beginChar; c <= endChar; c++) {
                list.add(c);
            }
        }
        if (!typeStr.equals(type)) {
            for (int i = 0; i <= maxNumber; i++) {
                list.add(i);
            }
        }

        StringBuffer str = new StringBuffer();
        for (int i = 0; i < number; i++) {
            int num = (int) (Math.random() * list.size());
            str.append(list.get(num));
        }
        return str.toString();
    }

    /**
     * 验证码图片
     * @return code:验证码字符串 image:BufferedImage图片对象
     */
    public static Map<String, Object> codeImg() {
        int width = 140;
        int height = 42;
        char[] codeSequence = { 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R',
                'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9' };
        Map<String, Object> result = new HashMap<>(2);

        BufferedImage buffImg = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics gd = buffImg.getGraphics();


        Random random = new Random();
        int red = 0, green = 0, blue = 0;

        // 色块
        int count = 1;
        int x=0;
        int y=0;
        //色块大小
        int lump = 14;
        //色块总数
        int lumpMax = 31;
        while (count < lumpMax) {

            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);
            gd.setColor(new Color(red, green, blue));
            gd.fillRect(x, y, lump, lump);

            if(count % 10 == 0) {
                x=0;
                y += lump;
            } else {
                x += lump;
            }
            count++;
        }

        // 设置字体。
        ((Graphics2D)gd).setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        Font font = new Font("", Font.BOLD, 34);
        gd.setFont(font);

        // 验证码
        int codeNum = 4;
        StringBuilder codeStr = new StringBuilder();
        for (int i = 0; i < codeNum; i++) {
            String code = String.valueOf(codeSequence[random.nextInt(36)]);
            red = random.nextInt(255);
            green = random.nextInt(255);
            blue = random.nextInt(255);

            gd.setColor(new Color(red, green, blue));
            gd.drawString(code, (i + 1) * 23, 33);

            codeStr.append(code);
        }

        result.put("code", codeStr.toString());
        result.put("image", buffImg);

        return result;
    }
}
