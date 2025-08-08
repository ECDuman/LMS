package com.demo.lms.util;

import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class BrandCodeGenerator {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public BrandCodeGenerator(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    public String generateNextCode() {
        // Locks the 'brands' table
        // This prevents multiple users from running this method at the same time
        jdbcTemplate.execute("LOCK TABLE brands IN SHARE ROW EXCLUSIVE MODE");

        String prefix = "krm";
        String sql = "SELECT MAX(SUBSTRING(code, 4)::integer) FROM brands WHERE code LIKE '" + prefix + "%'";
        Integer maxNumber = jdbcTemplate.queryForObject(sql, Integer.class);

        int nextNumber = (maxNumber == null) ? 1 : maxNumber + 1;

        return String.format(prefix + "%05d", nextNumber);
    }
}