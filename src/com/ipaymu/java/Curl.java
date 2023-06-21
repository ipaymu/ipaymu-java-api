/**
 * 
 */
package com.ipaymu.java;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.ipaymu.java.Exceptions.Unauthorized;

import org.json.JSONObject;

/**
 * @author abdur
 *
 */
public interface Curl {
    static String genSignature(HashMap<String, ?> data, HashMap<String, String> credentials) {
        MessageDigest digest;
        Mac mac;

        try {
            digest = MessageDigest.getInstance("SHA-256");
            mac = Mac.getInstance("HmacSHA256");

            JSONObject body = new JSONObject(data);
            System.out.println(body.toString());
            String reqBody = bytesToHex(digest.digest(body.toString().getBytes()));
            String secret = credentials.get("apiKey");
            String va = credentials.get("va");
            String stringToSign = "POST:" + va + ":" + reqBody + ":" + secret;

            SecretKeySpec key = new SecretKeySpec(secret.getBytes(), "HmacSHA256");
            mac.init(key);

            return bytesToHex(mac.doFinal(stringToSign.getBytes()));
        } catch (Exception e) {
            e.printStackTrace();

            return e.getMessage();
        }
    }

    static String bytesToHex(byte[] hash) {
        StringBuilder hexString = new StringBuilder(2 * hash.length);
        for (byte b : hash) {
            String hex = Integer.toHexString(0xff & b);
            if (hex.length() == 1) {
                hexString.append('0');
            }
            hexString.append(hex);
        }
        return hexString.toString();
    }

    default String request(String config, HashMap<String, ?> params, HashMap<String, String> credentials) {
        String signature = genSignature(params, credentials);
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String body = new JSONObject(params).toString();

        try {
            URL url = new URL(config);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json");
            conn.setRequestProperty("Accept", "*/*");
            conn.setRequestProperty("va", credentials.get("va"));
            conn.setRequestProperty("signature", signature);
            conn.setRequestProperty("timestamp", timestamp);
            conn.setRequestProperty("Connection", "keep-alive");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.getOutputStream().write(body.getBytes());

            InputStream stream;
            switch (conn.getResponseCode()) {
                case HttpURLConnection.HTTP_UNAUTHORIZED:
                    throw new Unauthorized();

                case HttpURLConnection.HTTP_OK:
                    stream = conn.getInputStream();
                    break;

                default:
                    stream = conn.getErrorStream();
                    break;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8));
            String line = null;
            StringBuilder response = new StringBuilder();
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            conn.disconnect();

            return response.toString();
        } catch (Exception e) {
            e.printStackTrace();

            return e.getMessage();
        }
    }
}
