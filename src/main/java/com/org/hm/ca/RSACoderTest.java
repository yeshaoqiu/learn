package com.org.hm.ca;

import java.security.Key;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.util.HashMap;
import java.util.Map;

/** 
 *  
 * RSA 这种算法1978年就出现了，它是第一个既能用于数据加密也能用于数字签名的算法。<br> 
 * RSA同时有两把钥匙，公钥与私钥。同时支持数字签名。<br> 
 * 数字签名的意义在于，对传输过来的数据进行校验。确保数据在传输过程中不被修改。 
 * <ul> 
 * 流程分析： 
 * <li>甲方构建密钥对儿，将公钥公布给乙方，将私钥保留。</li> 
 * <li>甲方使用私钥加密数据，然后用私钥对加密后的数据签名，发送给乙方签名以及加密后的数据；乙方使用公钥、签名来验证待解密数据是否有效，如果有效使用公钥对数据解密。</li> 
 * <li>乙方使用公钥加密数据，向甲方发送经过加密后的数据；甲方获得加密数据，通过私钥解密。</li> 
 * <ul> 
 *
 */  
public class RSACoderTest {
    private static final String PUBLIC_KEY = "RSAPublicKey";
    private static final String PRIVATE_KEY = "RSAPrivateKey";

    private String publicKey;  
    private String privateKey;

    public static void main(String[] args) throws Exception {
        RSACoderTest coder = new RSACoderTest();
        coder.init();
        coder.test();
        coder.testSign();
    }

    public void init() throws Exception {
        Map<String, Object> keyMap = initKey();
  
        publicKey = getPublicKey(keyMap);
        privateKey = getPrivateKey(keyMap);
        System.err.println("公钥: \n\r" + publicKey);  
        System.err.println("私钥： \n\r" + privateKey);  
    }

    public void test() throws Exception {
        System.err.println("公钥加密——私钥解密");  
        String inputStr = "data:abc123";
        byte[] data = inputStr.getBytes();  
        byte[] encodedData = RSACoder.encryptByPublicKey(data, publicKey);
        byte[] decodedData = RSACoder.decryptByPrivateKey(encodedData, privateKey);
  
        String outputStr = new String(decodedData);  
        System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);
    }

    public void testSign() throws Exception {
        System.err.println("私钥加密——公钥解密");  
        String inputStr = "data:sign";
        byte[] data = inputStr.getBytes();  
        byte[] encodedData = RSACoder.encryptByPrivateKey(data, privateKey);
        byte[] decodedData = RSACoder.decryptByPublicKey(encodedData, publicKey);
  
        String outputStr = new String(decodedData);  
        System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);  

        System.err.println("私钥签名——公钥验证签名");  
        // 产生签名  
        String sign = RSACoder.sign(encodedData, privateKey);
        System.err.println("签名:\r" + sign);  
  
        // 验证签名  
        boolean status = RSACoder.verify(encodedData, publicKey, sign);
        System.err.println("状态:\r" + status);
    }

    /**
     * 初始化密钥
     *
     * @return
     * @throws Exception
     */
    private Map<String, Object> initKey() throws Exception {
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance(RSACoder.KEY_ALGORITHM);
        keyPairGen.initialize(1024);

        KeyPair keyPair = keyPairGen.generateKeyPair();

        // 公钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();

        // 私钥
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();

        Map<String, Object> keyMap = new HashMap<String, Object>(2);

        keyMap.put(PUBLIC_KEY, publicKey);
        keyMap.put(PRIVATE_KEY, privateKey);
        return keyMap;
    }

    /**
     * 取得私钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    private String getPrivateKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PRIVATE_KEY);

        return Coder.encryptBASE64(key.getEncoded());
    }

    /**
     * 取得公钥
     *
     * @param keyMap
     * @return
     * @throws Exception
     */
    private String getPublicKey(Map<String, Object> keyMap) throws Exception {
        Key key = (Key) keyMap.get(PUBLIC_KEY);

        return Coder.encryptBASE64(key.getEncoded());
    }
}  
  
