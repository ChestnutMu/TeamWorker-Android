package cn.chestnut.mvvm.teamworker.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2018, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2018/1/20 22:46:16
 * Description：Emoji表情转义
 * Email: xiaoting233zhang@126.com
 */

public class EmojiUtil {
    /**
     * @param str 待转换字符串
     * @return 转换后字符串
     * @throws UnsupportedEncodingException exception
     * @Description 将字符串中的emoji表情转换成可以在utf-8字符集数据库中保存的格式
     */
    public static String emojiConvert(String str)
            throws UnsupportedEncodingException {
        String patternString = "([\\x{10000}-\\x{10ffff}\ud800-\udfff])";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);
        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(
                        sb,
                        "[[" +
                                URLEncoder.encode(matcher.group(1),
                                        "UTF-8") + "]]");
            } catch (UnsupportedEncodingException e) {
                Log.e("emojiConvert error: " + e);
                throw e;
            }
        }
        matcher.appendTail(sb);
        Log.d("emojiConvert " + str + " to " + sb.toString()
                + ", len：" + sb.length());
        return sb.toString();
    }

    /**
     * @param str 转换后的字符串
     * @return 转换前的字符串
     * @throws UnsupportedEncodingException exception
     * @Description 还原utf8数据库中保存的含转换后emoji表情的字符串
     */
    public static String emojiRecovery(String str)
            throws UnsupportedEncodingException {
        String patternString = "\\[\\[(.*?)\\]\\]";

        Pattern pattern = Pattern.compile(patternString);
        Matcher matcher = pattern.matcher(str);

        StringBuffer sb = new StringBuffer();
        while (matcher.find()) {
            try {
                matcher.appendReplacement(sb,
                        URLDecoder.decode(matcher.group(1), "UTF-8"));
            } catch (UnsupportedEncodingException e) {
                Log.e("emojiRecovery error: " + e);
                throw e;
            }
        }
        matcher.appendTail(sb);
        Log.d("emojiRecovery " + str + " to " + sb.toString());
        return sb.toString();
    }
}
