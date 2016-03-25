package com.autumn.core.security;

import java.io.FileInputStream;
import java.io.InputStream;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.cert.Certificate;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.Date;
import javax.crypto.Cipher;
import javax.net.ssl.*;

/**
 * <p>Title: Java密钥库(Java Key Store，JKS)KEY_STORE</p>
 *
 * <p>Copyright: Autumn Copyright (c) 2011</p>
 *
 * <p>Company: Autumn </p>
 *
 * @author 刘社朋
 * @version 2.0
 *
 */
public abstract class CertificateCoder extends Coder {

    public static final String KEY_STORE = "JKS";
    public static final String X509 = "X.509";
    public static final String SunX509 = "SunX509";
    public static final String SSL = "SSL";

    /**
     * 由KeyStore获得私钥
     *
     * @param keyStorePath
     * @param alias
     * @param password
     * @return PrivateKey
     * @throws Exception
     */
    public static PrivateKey getPrivateKey(String keyStorePath, String alias,
            String password) throws Exception {
        KeyStore ks = getKeyStore(keyStorePath, password);
        PrivateKey key = (PrivateKey) ks.getKey(alias, password.toCharArray());
        return key;
    }

    /**
     * 由Certificate获得公钥
     *
     * @param certificatePath
     * @return PublicKey
     * @throws Exception
     */
    public static PublicKey getPublicKey(String certificatePath)
            throws Exception {
        Certificate certificate = getCertificate(certificatePath);
        PublicKey key = certificate.getPublicKey();
        return key;
    }

    /**
     * 获得Certificate
     *
     * @param certificatePath
     * @return Certificate
     * @throws Exception
     */
    public static Certificate getCertificate(String certificatePath)
            throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);
        FileInputStream in = new FileInputStream(certificatePath);
        Certificate certificate = certificateFactory.generateCertificate(in);
        in.close();
        return certificate;
    }

    /**
     * 获得Certificate
     *
     * @param in
     * @return Certificate
     * @throws Exception
     */
    public static Certificate getCertificate(InputStream in)
            throws Exception {
        CertificateFactory certificateFactory = CertificateFactory.getInstance(X509);
        Certificate certificate = certificateFactory.generateCertificate(in);
        in.close();
        return certificate;
    }

    /**
     * 获得Certificate
     *
     * @param keyStorePath
     * @param alias
     * @param password
     * @return Certificate
     * @throws Exception
     */
    public static Certificate getCertificate(String keyStorePath,
            String alias, String password) throws Exception {
        KeyStore ks = getKeyStore(keyStorePath, password);
        Certificate certificate = ks.getCertificate(alias);
        return certificate;
    }

    /**
     * 获得KeyStore
     *
     * @param keyStorePath
     * @param password
     * @return KeyStore
     * @throws Exception
     */
    private static KeyStore getKeyStore(String keyStorePath, String password)
            throws Exception {
        FileInputStream is = new FileInputStream(keyStorePath);
        KeyStore ks = KeyStore.getInstance(KEY_STORE);
        ks.load(is, password.toCharArray());
        is.close();
        return ks;
    }

    /**
     * 获得KeyStore
     *
     * @param is
     * @param password
     * @return KeyStore
     * @throws Exception
     */
    public static KeyStore getKeyStore(InputStream is, String password)
            throws Exception {
        KeyStore ks = KeyStore.getInstance(KEY_STORE);
        ks.load(is, password.toCharArray());
        is.close();
        return ks;
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param keyStorePath
     * @param alias
     * @param password
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, String keyStorePath,
            String alias, String password) throws Exception {
        // 取得私钥   
        PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);
        // 对数据加密   
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥加密
     *
     * @param data
     * @param privateKey
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encryptByPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param keyStorePath
     * @param alias
     * @param password
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, String keyStorePath,
            String alias, String password) throws Exception {
        // 取得私钥   
        PrivateKey privateKey = getPrivateKey(keyStorePath, alias, password);
        // 对数据解密   
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 私钥解密
     *
     * @param data
     * @param privateKey
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decryptByPrivateKey(byte[] data, PrivateKey privateKey) throws Exception {
        // 对数据解密   
        Cipher cipher = Cipher.getInstance(privateKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param certificatePath
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, String certificatePath)
            throws Exception {
        // 取得公钥   
        PublicKey publicKey = getPublicKey(certificatePath);
        // 对数据加密   
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥加密
     *
     * @param data
     * @param publicKey
     * @return byte[]
     * @throws Exception
     */
    public static byte[] encryptByPublicKey(byte[] data, PublicKey publicKey)
            throws Exception {
        // 对数据加密   
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param certificatePath
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, String certificatePath)
            throws Exception {
        // 取得公钥   
        PublicKey publicKey = getPublicKey(certificatePath);
        // 对数据解密   
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 公钥解密
     *
     * @param data
     * @param publicKey
     * @return byte[]
     * @throws Exception
     */
    public static byte[] decryptByPublicKey(byte[] data, PublicKey publicKey)
            throws Exception {
        // 对数据解密  
        Cipher cipher = Cipher.getInstance(publicKey.getAlgorithm());
        cipher.init(Cipher.DECRYPT_MODE, publicKey);
        return cipher.doFinal(data);
    }

    /**
     * 验证Certificate
     *
     * @param certificatePath
     * @return boolean
     */
    public static boolean verifyCertificate(String certificatePath) {
        return verifyCertificate(new Date(), certificatePath);
    }

    /**
     * 验证Certificate
     *
     * @param certificate
     * @return boolean
     */
    public static boolean verifyCertificate(Certificate certificate) {
        return verifyCertificate(new Date(), certificate);
    }

    /**
     * 验证Certificate是否过期或无效
     *
     * @param date
     * @param certificatePath
     * @return boolean
     */
    public static boolean verifyCertificate(Date date, String certificatePath) {
        try {
            // 取得证书   
            Certificate certificate = getCertificate(certificatePath);
            // 验证证书是否过期或无效   
            return verifyCertificate(date, certificate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证证书是否过期或无效
     *
     * @param date
     * @param certificate
     * @return boolean
     */
    public static boolean verifyCertificate(Date date, Certificate certificate) {
        boolean status = true;
        try {
            X509Certificate x509Certificate = (X509Certificate) certificate;
            x509Certificate.checkValidity(date);
        } catch (Exception e) {
            status = false;
        }
        return status;
    }

    /**
     * 签名
     *
     * @param keyStorePath
     * @param alias
     * @param password
     *
     * @return String
     * @throws Exception
     */
    public static String sign(byte[] sign, String keyStorePath, String alias,
            String password) throws Exception {
        // 获得证书   
        X509Certificate x509Certificate = (X509Certificate) getCertificate(
                keyStorePath, alias, password);
        // 获取私钥   
        KeyStore ks = getKeyStore(keyStorePath, password);
        // 取得私钥   
        PrivateKey privateKey = (PrivateKey) ks.getKey(alias, password.toCharArray());
        // 构建签名   
        Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
        signature.initSign(privateKey);
        signature.update(sign);
        return encryptBASE64(signature.sign());
    }

    /**
     * 签名
     *
     * @param sign
     * @param certificate
     * @param privateKey
     * @return String
     * @throws Exception
     */
    public static String sign(byte[] sign, Certificate certificate, PrivateKey privateKey) throws Exception {
        // 获得证书   
        X509Certificate x509Certificate = (X509Certificate) certificate;
        // 构建签名   
        Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
        signature.initSign(privateKey);
        signature.update(sign);
        return encryptBASE64(signature.sign());
    }

    /**
     * 验证签名
     *
     * @param data
     * @param sign
     * @param certificatePath
     * @return boolean
     * @throws Exception
     */
    public static boolean verify(byte[] data, String sign,
            String certificatePath) throws Exception {
        // 获得证书   
        X509Certificate x509Certificate = (X509Certificate) getCertificate(certificatePath);
        // 获得公钥   
        PublicKey publicKey = x509Certificate.getPublicKey();
        // 构建签名   
        Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(decryptBASE64(sign));
    }

    /**
     * 验证签名
     *
     * @param data
     * @param sign
     * @param certificate
     * @return boolean
     * @throws Exception
     */
    public static boolean verify(byte[] data, String sign,
            Certificate certificate) throws Exception {
        // 获得证书   
        X509Certificate x509Certificate = (X509Certificate) certificate;
        // 获得公钥   
        PublicKey publicKey = x509Certificate.getPublicKey();
        // 构建签名   
        Signature signature = Signature.getInstance(x509Certificate.getSigAlgName());
        signature.initVerify(publicKey);
        signature.update(data);
        return signature.verify(decryptBASE64(sign));
    }

    /**
     * 验证Certificate
     *
     * @param keyStorePath
     * @param alias
     * @param password
     * @return boolean
     */
    public static boolean verifyCertificate(Date date, String keyStorePath, String alias, String password) {
        try {
            Certificate certificate = getCertificate(keyStorePath, alias,
                    password);
            return verifyCertificate(date, certificate);
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * 验证Certificate
     *
     * @param keyStorePath
     * @param alias
     * @param password
     * @return boolean
     */
    public static boolean verifyCertificate(String keyStorePath, String alias,
            String password) {
        return verifyCertificate(new Date(), keyStorePath, alias, password);
    }

    /**
     * 获得SSLSocektFactory
     *
     * @param password 密码
     * @param keyStorePath 密钥库路径
     * @param trustKeyStorePath 信任库路径
     * @return SSLSocketFactory
     * @throws Exception
     */
    private static SSLSocketFactory getSSLSocketFactory(String password,
            String keyStorePath, String trustKeyStorePath) throws Exception {
        // 初始化密钥库   
        KeyManagerFactory keyManagerFactory = KeyManagerFactory.getInstance(SunX509);
        KeyStore keyStore = getKeyStore(keyStorePath, password);
        keyManagerFactory.init(keyStore, password.toCharArray());
        // 初始化信任库   
        TrustManagerFactory trustManagerFactory = TrustManagerFactory.getInstance(SunX509);
        KeyStore trustkeyStore = getKeyStore(trustKeyStorePath, password);
        trustManagerFactory.init(trustkeyStore);
        // 初始化SSL上下文   
        SSLContext ctx = SSLContext.getInstance(SSL);
        ctx.init(keyManagerFactory.getKeyManagers(), trustManagerFactory.getTrustManagers(), null);
        SSLSocketFactory sf = ctx.getSocketFactory();
        return sf;
    }

    /**
     * 为HttpsURLConnection配置SSLSocketFactory
     *
     * @param conn HttpsURLConnection
     * @param password 密码
     * @param keyStorePath 密钥库路径
     * @param trustKeyStorePath 信任库路径
     * @throws Exception
     */
    public static void configSSLSocketFactory(HttpsURLConnection conn,
            String password, String keyStorePath, String trustKeyStorePath)
            throws Exception {
        conn.setSSLSocketFactory(getSSLSocketFactory(password, keyStorePath, trustKeyStorePath));
    }

    public static void test(String[] args) throws Exception {
        String password = "autumn";
        String alias = "www.autumn.com";
        String certificatePath = "E:\\reidx\\autumn.cer";
        String keyStorePath = "E:\\reidx\\autumn.keystore";
        System.err.println("私钥加密——公钥解密");
        String inputStr = "sign";
        byte[] data = inputStr.getBytes();
        byte[] encodedData = CertificateCoder.encryptByPrivateKey(data,
                keyStorePath, alias, password);
        byte[] decodedData = CertificateCoder.decryptByPublicKey(encodedData,
                certificatePath);
        String outputStr = new String(decodedData);
        System.err.println("加密前: " + inputStr + "\n\r" + "解密后: " + outputStr);
        System.err.println("私钥签名——公钥验证签名");
        //encodedData = Function.readFileTobyte("d:\\openssl-0.9.8l.tar.gz");
        //encodedData=("测撒撒撒撒撒撒撒撒撒撒撒撒撒撒撒撒撒撒撒撒撒").getBytes();
        // 产生签名   
        String sign = CertificateCoder.sign(encodedData, keyStorePath, alias,
                password);
        System.err.println("签名:\r" + sign);
        // 验证签名   
        boolean status = CertificateCoder.verify(encodedData, sign,
                certificatePath);
        System.err.println("状态:\r" + status);
    }
}