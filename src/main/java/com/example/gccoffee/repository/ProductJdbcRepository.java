package com.example.gccoffee.repository;

import com.example.gccoffee.model.Category;
import com.example.gccoffee.model.Product;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.*;

import static com.example.gccoffee.utl.Utils.*;

@Repository
public class ProductJdbcRepository implements ProductRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public ProductJdbcRepository(NamedParameterJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public List<Product> findAll() {
        return jdbcTemplate.query("select * from products", productRowMapper);
    }

    @Override
    public Product insert(Product product) {
        int update = jdbcTemplate.update("INSERT INTO products(product_id, product_name, category, price, description, created_at, updated_at) " +
                "VALUES(UNHEX(REPLACE(:productId, '-', '')), :productName, :category, :price, :description, :createdAt, :updatedAt)", toParamMap(product));
        if (update != 1) {
            throw new RuntimeException("Nothing was inserted");
        }

        return product;
    }

    @Override
    public Product update(Product product) {
        int update = jdbcTemplate.update("UPDATE products SET product_name = :productName, category = :category, price = :price, description = :description, updated_at = :updatedAt " +
                "WHERE product_id = UNHEX(REPLACE(:productId, '-', ''))", toParamMap(product));
        if(update != 1) {
            throw new RuntimeException("Nothing was updated");
        }
        return product;
    }

    @Override
    public Optional<Product> findById(UUID productId) {
        try {
            return Optional.of(
                    jdbcTemplate.queryForObject("SELECT * FROM products WHERE product_id = UNHEX(REPLACE(:productId, '-', ''))",
                            Collections.singletonMap("productId", productId.toString().getBytes()), productRowMapper)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<Product> findByName(String productName) {
        try {
            return Optional.of(
                    jdbcTemplate.queryForObject("SELECT * FROM products WHERE product_name = :productName",
                            Collections.singletonMap("productName", productName), productRowMapper)
            );
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Product> findByCategory(Category category) {
        return jdbcTemplate.query("SELECT * FROM products WHERE category = :category",
                Collections.singletonMap("category", category.toString()), productRowMapper);
    }

    @Override
    public void deleteAll() {
        jdbcTemplate.update("DELETE FROM products", Collections.emptyMap());
    }

    private static final RowMapper<Product> productRowMapper = (rs, i) -> {
        UUID productId = toUUID(rs.getBytes("product_id"));
        String productName = rs.getString("product_name");
        Category category = Category.valueOf(rs.getString("category"));
        long price = rs.getLong("price");
        String description = rs.getString("description");
        LocalDateTime createdAt = toLocalDateTime(rs.getTimestamp("created_at"));
        LocalDateTime updatedAt = toLocalDateTime(rs.getTimestamp("updated_at"));

        return new Product(
                productId,
                productName,
                category,
                price,
                description,
                createdAt,
                updatedAt
        );
    };

    private Map<String, Object> toParamMap(Product product) {
        HashMap<String, Object> paramMap = new HashMap<>();
        paramMap.put("productId", product.getProductId().toString().getBytes());
        paramMap.put("productName", product.getProductName());
        paramMap.put("category", product.getCategory().toString());
        paramMap.put("price", product.getPrice());
        paramMap.put("description", product.getDescription());
        paramMap.put("createdAt", product.getCreatedAt());
        paramMap.put("updatedAt", product.getUpdatedAt());
        return paramMap;
    }
}
