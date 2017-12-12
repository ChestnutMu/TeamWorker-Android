package cn.chestnut.mvvm.teamworker.utils;

import net.sourceforge.pinyin4j.PinyinHelper;
import net.sourceforge.pinyin4j.format.HanyuPinyinCaseType;
import net.sourceforge.pinyin4j.format.HanyuPinyinOutputFormat;
import net.sourceforge.pinyin4j.format.HanyuPinyinToneType;
import net.sourceforge.pinyin4j.format.exception.BadHanyuPinyinOutputFormatCombination;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Copyright (c) 2017, Chestnut All rights reserved
 * Author: Chestnut
 * CreateTime：at 2017/12/10 20:49:36
 * Description：Stinrg工具类
 * Email: xiaoting233zhang@126.com
 */

public class StringUtil {

    public static final String URL_REG_EXPRESSION = "^(https?://)?([a-zA-Z0-9_-]+\\.[a-zA-Z0-9_-]+)+(/*[A-Za-z0-9/\\-_&:?\\+=//.%]*)*";
    public static final String EMAIL_REG_EXPRESSION = "\\w+(\\.\\w+)*@\\w+(\\.\\w+)+";


    public static boolean isEmail(String s) {
        if (s == null) {
            return true;
        }
        return Pattern.matches(EMAIL_REG_EXPRESSION, s);
    }


    /**
     * 读取文件
     *
     * @param f
     * @return
     * @throws IOException
     */
    public static String fromFile(File f) throws
            IOException {
        InputStream is = new FileInputStream(f);
        byte[] bs = new byte[is.available()];
        is.read(bs);
        is.close();
        return new String(bs);
    }

    /**
     * 写入文件
     *
     * @param f
     * @param s
     * @throws IOException
     */
    public static void toFile(File f, String s) throws
            IOException {
        // 只有手机rom有足够的空间才写入本地缓存
        if (CommonUtil.enoughSpaceOnPhone(s.getBytes().length)) {
            FileOutputStream fos = new FileOutputStream(f);
            fos.write(s.getBytes());
            fos.close();
        }
    }

    /**
     * Whether the given string is null or zero-length.
     *
     * @param text the input for this method
     * @return Whether the given string is null or zero-length.
     */
    public static boolean isEmpty(String text) {
        return (text == null) || (text.length() == 0);
    }

    public static boolean isBlank(String text) {
        if (isEmpty(text)) {
            return true;
        }
        for (int i = 0; i < text.length(); i++) {
            if (!Character.isWhitespace(text.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 从头截取到某个字符位置
     *
     * @param text
     * @param separator
     * @return
     */
    public static String substringBefore(String text, char separator) {
        if (isEmpty(text)) {
            return text;
        }
        int sepPos = text.indexOf(separator);
        if (sepPos < 0) {
            return text;
        }
        return text.substring(0, sepPos);
    }

    /**
     * 截取某个字符后的字符串
     *
     * @param text
     * @param separator
     * @return
     */
    public static String substringAfterLast(String text, char separator) {
        if (isEmpty(text)) {
            return text;
        }
        int cPos = text.lastIndexOf(separator);
        if (cPos < 0) {
            return "";
        }
        return text.substring(cPos + 1);
    }

    public static boolean isStringNotNull(String string) {
        return !(string == null || "".equalsIgnoreCase(string) || "null".contains(string));
    }


    /**
     * string 转int
     *
     * @param str
     * @return
     */
    public static int toInt(String str) {
        try {
            int i = Integer.parseInt(str);
            return i;
        } catch (Exception e) {
            return 0;
        }

    }

    /**
     * string 转boolean
     *
     * @param str
     * @return
     */
    public static boolean toBoolean(String str) {
        try {
            boolean i = Boolean.parseBoolean(str);
            return i;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * string 转long
     *
     * @param str
     * @return
     */
    public static long toLong(String str) {
        try {
            long i = Long.parseLong(str);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * string 转 short
     *
     * @param str
     * @return
     */
    public static short toShort(String str) {
        try {
            short i = Short.parseShort(str);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * string 转float
     *
     * @param str
     * @return
     */
    public static float toFloat(String str) {
        try {
            float i = Float.parseFloat(str);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * string 转double
     *
     * @param str
     * @return
     */
    public static double toDouble(String str) {
        try {
            double i = Double.parseDouble(str);
            return i;
        } catch (Exception e) {
            return 0;
        }
    }


    /**
     * 判断电话号码
     *
     * @param phoneNumber
     * @return
     */
    public static boolean checkPhoneNumber(String phoneNumber) {
        String regEx = "^[1]{1}[3,5,8,6]{1}[0-9]{9}$"; // 表示a或f
        boolean p = Pattern.compile(regEx).matcher(phoneNumber).find();
        if (p) {
            return p;
        }
        String tregEx = "^[0]{1}[0-9]{2,3}-[0-9]{7,8}$"; // 表示a或f 0832-80691990
        boolean tp = Pattern.compile(tregEx).matcher(phoneNumber).find();
        if (tp) {
            return tp;
        }
        String tregEx2 = "^[0]{1}[0-9]{2,3} [0-9]{7,8}$"; // 表示a或f 0832-80691990
        boolean tp2 = Pattern.compile(tregEx2).matcher(phoneNumber).find();
        if (tp2) {
            return tp2;
        }

        String tregEx3 = "^[0]{1}[0-9]{2,3}[0-9]{7,8}$"; // 表示a或f 0832-80691990
        boolean tp3 = Pattern.compile(tregEx3).matcher(phoneNumber).find();
        return tp3;

    }

    /**
     * @Description:把list转换为一个用逗号分隔的字符串
     */
    public static String listToString(List list) {
        StringBuilder sb = new StringBuilder();
        if (list != null && list.size() > 0) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }

    /**
     * 将逗号分隔的字符串转换为string类型的list
     *
     * @param s
     * @return
     */
    public static List<String> StringToList(String s) {
        List<String> stringList = new ArrayList<>();
        stringList = Arrays.asList(s.split(","));
        return stringList;
    }

    /**
     * 汉字转为拼音
     *
     * @param chinese
     * @return
     */
    public static String ToPinyin(String chinese) {
        String pinyinStr = "";
        char[] newChar = chinese.toCharArray();
        HanyuPinyinOutputFormat defaultFormat = new HanyuPinyinOutputFormat();
        defaultFormat.setCaseType(HanyuPinyinCaseType.LOWERCASE);
        defaultFormat.setToneType(HanyuPinyinToneType.WITHOUT_TONE);
        for (int i = 0; i < newChar.length; i++) {
            if (newChar[i] > 128) {
                try {
                    pinyinStr += PinyinHelper.toHanyuPinyinStringArray(newChar[i], defaultFormat)[0];
                } catch (BadHanyuPinyinOutputFormatCombination e) {
                    e.printStackTrace();
                }
            } else {
                pinyinStr += newChar[i];
            }
        }
        return pinyinStr;
    }
}
