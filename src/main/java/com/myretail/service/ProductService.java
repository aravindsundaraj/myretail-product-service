package com.myretail.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.model.Product;
import com.myretail.repository.ProductsRepository;
import org.json.JSONException;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.Optional;
import java.util.logging.Logger;

@Component
public class ProductService {

    private final Logger logger = Logger.getLogger(ProductService.class.getName());

    public static final String PRODUCT = "product";
    public static final String ITEM = "item";
    public static final String PRODUCT_DESCRIPTION = "product_description";
    public static final String TCIN = "tcin";
    public static final String TITLE = "title";


    @Autowired
    private ProductsRepository productsRepository;

    @Value("${target.rest.url}")
    private String targetUrl;

    @Value("${target.rest.parameters}")
    private String targetParameters;

    public Product findById(String id) {
        if (StringUtils.isEmpty(id)) {
            logger.severe("Product ID is blank in the request");
            return null;
        }

        Product externalProduct = getExternalAPIData(id);
        Optional<Product> product = productsRepository.findById(id);

        if (product != null) {
            logger.info("Retrieved product information from Mongo DB. Initial Product: " + product);
            if (product.isPresent()) {
                product.get().setId(externalProduct.getId());
                product.get().setName(externalProduct.getName());
            }
            logger.info("Retrieved product information from Mongo DB. Final Product: " + product);
        }
        return product.get();
    }

    public String updateByID(String id, Product product) {
        JSONObject jsonString = new JSONObject();
        try {
            if (product != null && !product.getId().equals(id)) {
                jsonString.put("code", "409");
                jsonString.put("response", "ID did not match");
                return jsonString.toString();
            } else if (product != null && product.getCurrent_price().getValue() == null
                    || product.getCurrent_price().getValue().isEmpty()) {
                jsonString.put("code", "406");
                jsonString.put("response", "Price is null or empty");
                return jsonString.toString();
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Optional<Product> newProduct = productsRepository.findById(id); // Finds product by ID From DB
        newProduct.get().setCurrent_price(product.getCurrent_price()); // Update price

        try {
            jsonString.put("code", "200");
            jsonString.put("response", "Price has been updated");
        } catch (JSONException e) {
            logger.severe("Construction of JSON failed for ID: " + id + " and product: " + product);
            return jsonString.toString();
        }
        productsRepository.save(newProduct.get());
        return jsonString.toString();
    }

    @SuppressWarnings("unchecked")
    private Product getExternalAPIData(String id) {
        RestTemplate restTemplate = new RestTemplate();
        Product product = new Product();

        String URL = targetUrl + id + targetParameters;

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map> map;
        HttpStatus responseCode = HttpStatus.OK;
        try {
            ResponseEntity<String> response = restTemplate.getForEntity(URL, String.class);
            responseCode = response.getStatusCode();
            map = mapper.readValue(response.getBody(), Map.class);
            Map<String, Map> productMap = map.get(PRODUCT);
            Map<String, Map> itemMap = productMap.get(ITEM);
            Map<String, String> itemMapString = productMap.get(ITEM);
            Map<String, String> productDescriptionMap = itemMap.get(PRODUCT_DESCRIPTION);

            if (responseCode == HttpStatus.OK) {
                product.setId(itemMapString.get(TCIN));
                product.setName(productDescriptionMap.get(TITLE));
            } else {
                logger.severe("Response code invalid: " + responseCode);
                return null;
            }

        } catch (Exception e) {
            logger.severe("Could not fetch data from external API. Response code: " + responseCode);
            return null;
        }

        return product;
    }

}
