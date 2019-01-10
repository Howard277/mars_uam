package com.mars.mars_uam.common;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * 加密 解密方法
 * Created by wuketao on 2018/3/2.
 */
public class HBEncrypt {

    /**
     * 私有构造函数
     */
    private HBEncrypt(){
    }
    /**
     * SHA-256加密
     *
     * @param sPassWord
     * @return
     * @throws Exception
     */
    public static String shaDes(String sPassWord) throws UnsupportedEncodingException, NoSuchAlgorithmException {
        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        digest.update(sPassWord.getBytes("GBK"));
        return bytes2Hex(digest.digest());
    }

    /**
     * 解析
     *
     * @param bts
     * @return
     */
    private static String bytes2Hex(byte[] bts) {
        StringBuilder des = new StringBuilder();
        String tmp = null;
        for (int i = 0; i < bts.length; i++) {
            tmp = (Integer.toHexString(bts[i] & 0xFF));
            if (tmp.length() == 1) {
                des.append("0");
            }
            des.append(tmp);
        }
        return des.toString();
    }
}
