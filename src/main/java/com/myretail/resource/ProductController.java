package com.myretail.resource;

import com.myretail.model.Product;
import com.myretail.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.logging.Logger;

@RestController
public class ProductController {

    private final Logger logger = Logger.getLogger(ProductController.class.getName());

    @Autowired
    private ProductService productService;

    @RequestMapping(method = RequestMethod.GET, value = "/products/{id}", produces = "application/json")
    public Product findByid(@PathVariable String id) {
        return productService.findById(id);
    }

    @RequestMapping(method = RequestMethod.PUT, value = "/products/{id}", produces = "application/json")
    public String updateByID(@PathVariable String id, @RequestBody Product product) {
        return productService.updateByID(id, product);
    }

}
