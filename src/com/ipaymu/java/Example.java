package com.ipaymu.java;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Example {
    public static void main(String[] args) {
        Ipaymu ipaymu = new Ipaymu();

        try {
            ipaymu.setApiKey("SANDBOX76A13A0D-AD50-4061-9832-F5AF22B541D8-20220316213052");
            ipaymu.setVa("0000008999927923");
        } catch (Exception e) {
            e.printStackTrace();
        }

        List<String> product = Arrays.asList("product 1 ", "product2 ");
        List<String> price = Arrays.asList("10000", "50000");
        List<String> quantity = Arrays.asList("1", "2");
        List<String> description = Arrays.asList("product-desc", "product-desc 2");
        List<String> weight = Arrays.asList("1", "2");
        List<String> length = Arrays.asList("10", "10");
        List<String> width = Arrays.asList("30", "40");
        List<String> height = Arrays.asList("10", "50");

        HashMap<String, List<String>> cart = new HashMap<>() {
            {
                put("product", product);
                put("price", price);
                put("quantity", quantity);
                put("description", description);
                put("weight", weight);
                put("length", length);
                put("width", width);
                put("height", height);
            }
        };
        
        HashMap<String, String> url = new HashMap<>() {
            {
                put("ureturn", "https://google.com");
                put("ucancel", "https://google.com");
                put("unotify", "https://google.com");
            }
        };

        ipaymu.addCart(cart);
        ipaymu.setURL(url);
        
        String test = ipaymu.redirectPayment(null);
        System.out.println(test);
    }
}
