package com.santacoder.santacoder_cipher;

import android.util.Base64;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class SantaCoderCipher {
    public static String Generate(String data, String key, Long expire) {
        // Generate Header
        JSONObject header = new JSONObject();
        try {
            header.put("algo", "HS256");
            header.put("type", "HWT");
            if (expire != null) {
                header.put("expire", String.valueOf(expire + System.currentTimeMillis() / 1000));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String headerEncrypted = Base64.encodeToString(header.toString().getBytes(), Base64.NO_WRAP);

        // Generate Payload
        String payload = Base64.encodeToString(data.getBytes(), Base64.NO_WRAP);

        // Generate Signature
        String finalSignature = null;
        try {
            byte[] byteSignature = hmac("HmacSHA256", key.getBytes(), (headerEncrypted + payload).getBytes());
            String byteToString = bytesToHex(byteSignature);
            finalSignature = Base64.encodeToString(byteToString.getBytes(), Base64.NO_WRAP);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }
        String token = headerEncrypted + "." + payload + "." + finalSignature;

        // Return header, payload and signature as a token
        return Base64.encodeToString(token.getBytes(), Base64.NO_WRAP);
    }


    public static Object Verify(String tokenString, String key) {
        byte[] token_base64 = Base64.decode(tokenString, Base64.DEFAULT);
        String token = null;
        try {
            token = new String(token_base64, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        if (token.split("\\.").length != 3) {
            return "Invalid Token.";
        }
        String[] parts = token.split("\\.");

        try {
            byte[] byteSignature = hmac("HmacSHA256", key.getBytes(), (parts[0] + parts[1]).getBytes());
            String byteToString = bytesToHex(byteSignature);
            String finalSignature = Base64.encodeToString(byteToString.getBytes(), Base64.NO_WRAP);

            if (!finalSignature.equals(parts[2])) {
                return "Invalid Token.";
            }
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            e.printStackTrace();
        }

        try {
            byte[] decodedHeader = Base64.decode(parts[0], Base64.DEFAULT);
            String headerString = new String(decodedHeader, "UTF-8");
            JSONObject header = new JSONObject(headerString);

            if (header.has("expire")) {
                long expireTime = header.getLong("expire");
                if (expireTime < System.currentTimeMillis() / 1000) {
                    return "Token Expired.";
                }
            }
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        try {
            byte[] decodedPayload = Base64.decode(parts[1], Base64.DEFAULT);
            String payloadString = new String(decodedPayload, "UTF-8");

            return new JSONObject(payloadString);
        } catch (JSONException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static byte[] hmac(String algorithm, byte[] key, byte[] message) throws NoSuchAlgorithmException, InvalidKeyException {
        Mac mac = Mac.getInstance(algorithm);
        mac.init(new SecretKeySpec(key, algorithm));
        return mac.doFinal(message);
    }


    public static String bytesToHex(byte[] bytes) {
        final char[] hexArray = "0123456789abcdef".toCharArray();
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0, v; j < bytes.length; j++) {
            v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
