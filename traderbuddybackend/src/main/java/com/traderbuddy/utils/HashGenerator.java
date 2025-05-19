package com.traderbuddy.utils;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;

public class HashGenerator {
    public static String generateJoinCode(Object... args) {
    	final String ALPHANUMERIC="0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    	StringBuilder raw = new StringBuilder();
    	StringBuilder res=new StringBuilder();
    	for (Object obj : args) {
    	    raw.append(obj).append(":");
    	}
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hash = digest.digest(raw.toString().getBytes(StandardCharsets.UTF_8));
            String code=Base64.getUrlEncoder().withoutPadding().encodeToString(hash).substring(0, 8);
            for(int i=0;i<8;i++) {
            	int ind=(code.charAt(i)&0xff)%ALPHANUMERIC.length();
            	res.append(ALPHANUMERIC.charAt(ind));
            }
            return res.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Error generating join code", e);
        }
    }
}
