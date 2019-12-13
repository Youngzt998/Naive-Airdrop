package com.example.androidtopc;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherTool {

    public static byte[] encrypt(String text, String key, String iv)
    {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] byteKey = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(byteKey, "AES");

            IvParameterSpec IV = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, IV);

            return cipher.doFinal(text.getBytes());

        }catch (Exception e){
            e.printStackTrace();
        }

        return null;
    }



    public static String decrypt(byte[] code, String key, String iv)
    {
        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            byte[] byteKey = key.getBytes();
            SecretKeySpec secretKeySpec = new SecretKeySpec(byteKey, "AES");

            IvParameterSpec IV = new IvParameterSpec(iv.getBytes());
            cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, IV);

            byte[] buffer = cipher.doFinal(code);

            return (new String(buffer, 0, buffer.length));

        }catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }

}
