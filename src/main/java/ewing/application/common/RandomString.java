package ewing.application.common;

import java.security.SecureRandom;

/**
 * 随机字符串生成器。
 *
 * @author Ewing
 * @since 2017/2/15
 */
public class RandomString {

    private static char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k',
            'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w',
            'x', 'y', 'z', '0', '1', '2', '3', '4', '5', '6', '7', '8', '9'};

    /**
     * 获取一定长度的随机字符串。
     *
     * @param length 指定字符串长度。
     * @return 一定长度的字符串。
     */
    public static String randomString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            int number = new SecureRandom().nextInt(chars.length);
            stringBuilder.append(chars[number]);
        }
        return stringBuilder.toString();
    }

    /**
     * 获取一定长度的随机数字字符串。
     *
     * @param length 指定字符串长度。
     * @return 一定长度的数字字符串。
     */
    public static String randomNumberString(int length) {
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < length; i++) {
            stringBuilder.append(new SecureRandom().nextInt(10));
        }
        return stringBuilder.toString();
    }

    /**
     * 生成指定长度的中文字符串。
     *
     * @param length 长度。
     * @return 中文字符串。
     */
    public static String randomChinese(int length) {
        StringBuilder builder = new StringBuilder();
        while (length-- > 0) {
            builder.append((char) (0x4e00 + new SecureRandom().nextInt(0x9fa5 - 0x4e00 + 1)));
        }
        return builder.toString();
    }

    /**
     * 主测试方法。
     */
    public static void main(String[] args) {
        for (int i = 0; i < 10; i++) {
            System.out.println(randomString(10));
            System.out.println(randomNumberString(10));
            System.out.println(randomChinese(6));
        }
    }

}