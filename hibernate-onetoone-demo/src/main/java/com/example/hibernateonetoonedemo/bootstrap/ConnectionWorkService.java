package com.example.hibernateonetoonedemo.bootstrap;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ConnectionWorkService {

    private final JdbcTemplate jdbcTemplate;

    public ConnectionWorkService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void holdConnectionForSeconds(int seconds) {
        jdbcTemplate.queryForObject("select 1", Integer.class);
        try {
            Thread.sleep(seconds * 1000L);
        } catch (InterruptedException ex) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while holding DB connection", ex);
        }
    }
}
