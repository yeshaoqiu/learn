package com.org.hm.ca;

import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import java.io.IOException;

abstract class Coder {

    /**
     * BASE64加密
     * @param data
     * @return
     */
    public static String encryptBASE64(byte[] data){
        return new BASE64Encoder().encodeBuffer(data);
    }

    /**
     * BASE64解密
     * @param key
     * @return
     */
    public static byte[] decryptBASE64(String key) throws IOException {
        return new BASE64Decoder().decodeBuffer(key);
    }

}