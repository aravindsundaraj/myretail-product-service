package com.myretail.resource;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.myretail.model.CurrentPrice;
import com.myretail.model.Product;
import com.myretail.service.ProductService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.skyscreamer.jsonassert.JSONAssert;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

@RunWith(SpringRunner.class)
@ContextConfiguration(classes = ProductController.class)
@WebMvcTest(value = ProductController.class, secure = false)
public class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    @Qualifier(value = "productService")
    private ProductService productService;

    private String PRODUCT_ID = "13860428";

    Product product = new Product();
    CurrentPrice currentPrice = new CurrentPrice();

    @Before
    public void setup() {
        product.setId("13860428");
        product.setName("The Big Lebowski (Blu-ray)");
        currentPrice.setValue("2000");
        currentPrice.setCurrency_code("USD");
        product.setCurrent_price(currentPrice);
    }

    @Test
    public void findProductById() throws Exception {
        Mockito.when(productService.findById(PRODUCT_ID)).thenReturn(product);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.get(
                "http://localhost:8080/products/" + PRODUCT_ID).accept(
                MediaType.APPLICATION_JSON);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        String expected = "{\"id\":\"13860428\",\"name\":\"The Big Lebowski (Blu-ray)\",\"current_price\":{\"value\":\"2000\",\"currency_code\":\"USD\"}}";
        JSONAssert.assertEquals(expected, result.getResponse()
                .getContentAsString(), false);
    }

    @Test
    public void updateProductById() throws Exception {
        Product product = new Product();
        product.setId(PRODUCT_ID);
        product.setName("The Big Lebowski (Blu-ray)");
        currentPrice.setValue("300");
        currentPrice.setCurrency_code("USD");
        product.setCurrent_price(currentPrice);
        ObjectMapper objectMapper = new ObjectMapper();
        String productString = objectMapper.writeValueAsString(product);
        Mockito.when(productService.updateByID(PRODUCT_ID, product)).thenReturn(productString);
        RequestBuilder requestBuilder = MockMvcRequestBuilders.put(
                "/products/" + PRODUCT_ID)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"id\":" + PRODUCT_ID + ",\"name\":\"The Big Lebowski (Blu-ray)\",\"current_price\":{\"value\":\"300\",\"currency_code\":\"USD\"}}")
                .contentType(MediaType.APPLICATION_JSON_VALUE);
        MvcResult result = mockMvc.perform(requestBuilder).andReturn();
        Assert.assertEquals(HttpStatus.OK.value(), result.getResponse().getStatus());
    }


}
