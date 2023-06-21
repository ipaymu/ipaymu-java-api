/**
 * 
 */
package com.ipaymu.java;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import com.ipaymu.java.Exceptions.ApiKeyNotFound;
import com.ipaymu.java.Exceptions.VaNotFound;

/**
 * @author abdur
 *
 */
public class Ipaymu implements Curl {
    public Config config;

    public String apiKey = null;

    public String va = null;

    public boolean prod = false;

    public String amount = "0";

    public String expired = "1";

    public String ureturn = null;

    public String ucancel = null;

    public String unotify = null;

    public HashMap<String, String> buyer = new HashMap<>();

    public HashMap<String, String> cod = new HashMap<>();

    public String comment = null;

    public HashMap<String, List<String>> carts = new HashMap<>();

    private final HashMap<String, String> credentials = new HashMap<>();

    public Ipaymu() {
        this.config = new Config(this.prod);
    }

    public void setApiKey(String apiKey) throws ApiKeyNotFound {
        if (apiKey == null) {
            throw new ApiKeyNotFound();
        }

        this.apiKey = apiKey;
        this.credentials.put("apiKey", apiKey);
    }

    public void setVa(String va) throws VaNotFound {
        if (va == null) {
            throw new VaNotFound();
        }

        this.va = va;
        this.credentials.put("va", va);
    }

    public void setProd(boolean prod) {
        this.config = new Config(this.prod);
        this.prod = prod;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public void setExpired(String expired) {
        this.expired = expired;
    }

    public void setURL(HashMap<String, String> url) {
        this.ureturn = url.get("ureturn");
        this.ucancel = url.get("ucancel");
        this.unotify = url.get("unotify");
    }

    public void setBuyer(HashMap<String, String> buyer) {
        this.buyer = buyer;
    }

    public void setCOD(HashMap<String, String> cod) {
        this.cod = cod;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }
    
    public void addCart(HashMap<String, List<String>> carts) {
        this.carts = carts;
    }

    private HashMap<String, List<String>> buildcarts() {
        HashMap<String, List<String>> params = new HashMap<>();

        List<String> product = new ArrayList<>();
        List<String> quantity = new ArrayList<>();
        List<String> price = new ArrayList<>();
        List<String> description = new ArrayList<>();
        List<String> weight = new ArrayList<>();
        List<String> dimension = new ArrayList<>();

        if (this.carts.get("product").size() > 0) {
            product = this.carts.get("product").stream().map(String::trim).collect(Collectors.toList());
        }

        if (this.carts.get("quantity").size() > 0) {
            quantity = this.carts.get("quantity").stream().map(String::trim).collect(Collectors.toList());
        }

        if (this.carts.get("price").size() > 0) {
            price = this.carts.get("price").stream().map(String::trim).collect(Collectors.toList());
        }

        if (this.carts.get("description").size() > 0) {
            description = this.carts.get("description").stream().map(String::trim).collect(Collectors.toList());
        }

        if (this.carts.get("weight").size() > 0) {
            weight = this.carts.get("weight").stream().map(String::trim).collect(Collectors.toList());
        }

        if (!this.carts.get("length").isEmpty() && !this.carts.get("width").isEmpty()
                && !this.carts.get("height").isEmpty()) {

            for (int i = 0; i < this.carts.get("length").size(); i++) {
                String l = this.carts.get("length").get(i).trim();
                String h = this.carts.get("height").get(i).trim();
                String w = this.carts.get("width").get(i).trim();

                dimension.add(l + ":" + h + ":" + w);
            }
        }

        params.put("product", product);
        params.put("quantity", quantity);
        params.put("price", price);
        params.put("description", description);
        params.put("weight", weight);
        params.put("dimension", dimension);

        return params;
    }

    public String historyTransaction(HashMap<String, List<String>> data) {
        return this.request(this.config.history, data, this.credentials);
    }

    public String checkBalance() {
        HashMap<String, String> params = new HashMap<>();
        params.put("account", this.va);

        return this.request(this.config.balance, params, this.credentials);
    }

    public String checkTransaction(int id) {
        HashMap<String, Integer> params = new HashMap<>();
        params.put("transactionId", id);

        return this.request(this.config.transaction, params, this.credentials);
    }

    public String redirectPayment(HashMap<String, ?> data) {
        HashMap<String, ?> carts = this.buildcarts();
        HashMap<String, Object> params = new HashMap<>();

        params.put("account", this.va);
        params.put("product", carts.get("product"));
        params.put("qty", carts.get("quantity"));
        params.put("price", carts.get("price"));
        params.put("description", carts.get("description"));
        params.put("notifyUrl", this.unotify);
        params.put("returnUrl", this.ureturn);
        params.put("cancelUrl", this.ucancel);
        params.put("weight", carts.get("weight"));
        params.put("dimension", carts.get("dimension"));
        params.put("name", this.buyer.get("name"));
        params.put("email", this.buyer.get("email"));
        params.put("phone", this.buyer.get("phone"));
        params.put("pickupArea", this.cod.get("pickupArea"));
        params.put("pickupAddress", this.cod.get("pickupAddress"));
        params.put("buyerName", this.buyer.get("name"));
        params.put("buyerEmail", this.buyer.get("email"));
        params.put("buyerPhone", this.buyer.get("phone"));
        params.put("referenceId", data != null ? data.get("referenceId") : null);

        return this.request(this.config.redirectpayment, params, this.credentials);
    }

    public String directPayment(HashMap<String, ?> data) {
        HashMap<String, ?> carts = this.buildcarts();
        HashMap<String, Object> params = new HashMap<>();

        params.put("account", this.va);
        params.put("name", this.buyer.get("name"));
        params.put("email", this.buyer.get("email"));
        params.put("phone", this.buyer.get("phone"));
        params.put("amount", data.get("amount"));
        params.put("paymentMethod", data.get("paymentMethod"));
        params.put("paymentChannel", data.get("paymentChannel"));
        params.put("notifyUrl", this.unotify);
        params.put("description", carts.get("description"));
        params.put("referenceId", data.get("referenceId"));
        params.put("product", carts.get("product"));
        params.put("qty", carts.get("qty"));
        params.put("price", carts.get("price"));
        params.put("weight", carts.get("weight"));
        params.put("length", carts.get("length"));
        params.put("width", carts.get("width"));
        params.put("height", carts.get("height"));
        params.put("deliveryArea", this.cod.get("deliveryArea"));
        params.put("deliveryAddress", this.cod.get("deliveryAddress"));
        params.put("pickupArea", this.cod.get("pickupArea"));
        params.put("pickupAddress", this.cod.get("pickupAddress"));
        params.put("expired", (Boolean) data.get("expired") ? data.get("expired") : this.expired);
        params.put("expiredType", (Boolean) data.get("expiredType") ? data.get("expiredType") : "days");

        return this.request(this.config.directpayment, params, this.credentials); 
    }
}
