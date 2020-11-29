package com.sun.sunaccount;

import android.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sunronggui on 2018/11/29.
 */

public class DESede
{
    /*解密方式*/
    private static final String ENCRYPTION_MANNER="DESede";

    //加密函数
    public static String encrypt3DES(byte[] data,byte[] key)throws Exception
    {
        SecretKey secretKey=new SecretKeySpec(key,ENCRYPTION_MANNER);
        Cipher cipher=Cipher.getInstance(ENCRYPTION_MANNER);
        cipher.init(Cipher.ENCRYPT_MODE,secretKey);
        byte[] encrypt=cipher.doFinal(data);
        return new String(Base64.encode(encrypt,Base64.DEFAULT),"UTF-8");
    }
    //解密函数
    public static String decrypt3DES(String data,byte[] key) throws Exception
    {
        SecretKey secretKey=new SecretKeySpec(key,ENCRYPTION_MANNER);
        Cipher cipher=Cipher.getInstance(ENCRYPTION_MANNER);
        cipher.init(Cipher.DECRYPT_MODE,secretKey);
        byte[] bytes=Base64.decode(data.getBytes("UTF-8"),Base64.DEFAULT);
        byte[] plain=cipher.doFinal(bytes);
        return new String(plain,"utf-8");
    }
}
