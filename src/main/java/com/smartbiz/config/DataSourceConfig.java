package com.smartbiz.config;

import com.zaxxer.hikari.HikariDataSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.jdbc.DataSourceProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.util.StringUtils;

import javax.sql.DataSource;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

@Configuration
public class DataSourceConfig {

    private static final Logger log = LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {
        String databaseUrl = System.getenv("DATABASE_URL");

        // Render, Railway, and Heroku provide DATABASE_URL in postgres:// or postgresql:// URI format
        if (StringUtils.hasText(databaseUrl) && (databaseUrl.startsWith("postgres://") || databaseUrl.startsWith("postgresql://"))) {
            try {
                log.info("Detected postgres:// / postgresql:// in DATABASE_URL environment variable. Configuring DataSource for Render PostgreSQL...");

                String normalizedUrl = databaseUrl;
                if (normalizedUrl.startsWith("postgres://")) {
                    normalizedUrl = "postgresql://" + normalizedUrl.substring("postgres://".length());
                }

                URI uri = new URI(normalizedUrl);
                String userInfo = uri.getUserInfo();
                String username = properties.getUsername();
                String password = properties.getPassword();

                if (StringUtils.hasText(userInfo) && userInfo.contains(":")) {
                    String[] parts = userInfo.split(":", 2);
                    username = URLDecoder.decode(parts[0], StandardCharsets.UTF_8);
                    password = URLDecoder.decode(parts[1], StandardCharsets.UTF_8);
                }

                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath(); // includes leading '/'
                String query = uri.getQuery();

                String jdbcUrl = "jdbc:postgresql://" + host + ":" + port + path;
                if (StringUtils.hasText(query)) {
                    jdbcUrl += "?" + query;
                } else if (host != null && host.contains("render.com")) {
                    // Render external host requires sslmode=require
                    jdbcUrl += "?sslmode=require";
                }

                log.info("Configured PostgreSQL JDBC URL: jdbc:postgresql://{}:{}{}", host, port, path);

                HikariDataSource dataSource = new HikariDataSource();
                dataSource.setDriverClassName("org.postgresql.Driver");
                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setUsername(username);
                dataSource.setPassword(password);
                return dataSource;
            } catch (Exception e) {
                log.error("Failed to parse DATABASE_URL: {}. Falling back to standard Spring datasource properties.", e.getMessage(), e);
            }
        }

        // Standard configuration (local MySQL, explicit SPRING_DATASOURCE_URL, etc.)
        return properties.initializeDataSourceBuilder().build();
    }
}
