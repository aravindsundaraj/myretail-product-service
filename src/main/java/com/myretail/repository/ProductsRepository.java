package com.myretail.repository;

import com.myretail.model.Product;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;

@RepositoryRestResource(collectionResourceRel = "products", path = "products")
public interface ProductsRepository extends MongoRepository<Product, String> {

    Product findById(@Param("id") Integer id);

}