package ewing.common;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT生成与解析类。
 */
public class JWTUtils {

    private static String secret = "1234567890ABCDEF";

    /**
     * 根据Map参数生成Token，若没有指定失效时间，默认为一个月。
     *
     * @param claims Map参数。
     * @return 生成的Token。
     */
    public static String generateToken(Map<String, Object> claims) {
        if (!claims.containsKey("exp"))
            claims.put("exp", monthExp());
        return Jwts.builder()
                .setClaims(claims)
                .signWith(SignatureAlgorithm.HS256, secret)
                .compact();
    }

    /**
     * 根据参数生成Token，若没有指定失效时间，默认为一个月。
     *
     * @param keyValues 键值对：key1,values1,key2,values2等。
     * @return 根据参数生成的Token。
     */
    public static String generateToken(Object... keyValues) {
        // 移位后 若keyValue为奇数 最后一个将被忽略
        int total = keyValues.length >> 1;
        Map<String, Object> claims = new HashMap<>(total);
        total <<= 1;
        for (int i = 0; i < total; i++)
            claims.put(String.valueOf(keyValues[i++]), keyValues[i]);
        return generateToken(claims);
    }

    /**
     * 往已有的Token的Claims中存放数据。
     *
     * @param token     已有的Token。
     * @param keyValues 键值对，已存在则覆盖。
     * @return 存放数据后的Token。
     */
    public static String putClaimsData(String token, Object... keyValues) {
        Claims claims = getClaimsValidate(token);
        // 移位后 若keyValue为奇数 最后一个将被忽略
        int total = (keyValues.length >> 1) << 1;
        for (int i = 0; i < total; i++)
            claims.put(String.valueOf(keyValues[i++]), keyValues[i]);
        return generateToken(claims);
    }

    /**
     * 获取Token的负载信息。
     *
     * @param token Token。
     * @return 负载信息。
     */
    public static Claims getTokenClaims(String token) {
        try {
            if (token.startsWith("Bearer "))
                token = token.substring(7);
            return Jwts.parser()
                    .setSigningKey(secret)
                    .parseClaimsJws(token)
                    .getBody();
        } catch (Exception e) {
            throw new RuntimeException("解析Token失败。", e);
        }
    }

    /**
     * 获取Token的负载信息并校验有效期。
     *
     * @param token Token。
     * @return 负载信息。
     */
    public static Claims getClaimsValidate(String token) {
        Claims claims = getTokenClaims(token);
        // 存在exp在解析时会自动校验
        if (claims == null || !claims.containsKey("exp"))
            throw new RuntimeException("无效的Token。");
        return claims;
    }

    /**
     * 从Token中获取负载中的值，适用于只取一个值的情况。
     */
    public static Object getFromToken(String token, String key) {
        return getClaimsValidate(token).get(key);
    }

    public static long secondsExp(int seconds) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.SECOND, seconds);
        return calendar.getTime().getTime() / 1000;
    }

    public static long daysExp(int days) {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_YEAR, days);
        return calendar.getTime().getTime() / 1000;
    }

    public static long monthExp() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, 1);
        return calendar.getTime().getTime() / 1000;
    }

}
