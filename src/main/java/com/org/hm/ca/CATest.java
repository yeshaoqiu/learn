package com.org.hm.ca;

import java.text.SimpleDateFormat;

public class CATest {
    /** 加密密码 */
    private String password = "jdai618victory";
    /** 别名 */
    private String alias = "www.jd.ai";
    /** 证书路径 */
    private String certificatePath = "/Users/hanming/jdai.crt";
    /** key store路径 */
    private String keyStorePath = "/Users/hanming/jdai.keystore";

    public static void main(String[] args) throws Exception {
        CATest caTest = new CATest();
        caTest.test();
        caTest.testSign();
    }

    public void test() throws Exception {
        System.err.println("公钥加密——私钥解密");

        String inputStr = "Ceritifcate";
        byte[] data = inputStr.getBytes();
        byte[] encrypt = CertificateCoder.encryptByPublicKey(data, certificatePath);

        byte[] decrypt = CertificateCoder.decryptByPrivateKey(encrypt, keyStorePath, alias, password);
        String outputStr = new String(decrypt);

        System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);

        // 验证证书有效
        boolean status = CertificateCoder.verifyCertificate(certificatePath);
        System.err.println("证书状态:\n\r" + status);

        System.err.println("证书过期时间:\n\r" + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(CertificateCoder.getCertificateExpire(certificatePath)));
    }

    public void testSign() throws Exception {
        System.err.println("私钥加密——公钥解密");

        String inputStr = "license:jsf:v1.0:huaxi:2019-09-10";
        byte[] data = inputStr.getBytes();
        byte[] encodedData = CertificateCoder.encryptByPrivateKey(data, keyStorePath, alias, password);
        System.err.println("密文: " + new String(encodedData) + "\n\r");

        byte[] decodedData = CertificateCoder.decryptByPublicKey(encodedData, certificatePath);
        String outputStr = new String(decodedData);

        System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);

        System.err.println("私钥签名——公钥验证签名");

        // 产生签名
        String sign = CertificateCoder.sign(encodedData, keyStorePath, alias, password);
        System.err.println("签名:\n\r" + sign);

        // 验证签名
        boolean status = CertificateCoder.verify(encodedData, sign, certificatePath);
        System.err.println("状态:\n\r" + status);
    }

}
