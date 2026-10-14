
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

    private static final Logger log =
            LoggerFactory.getLogger(DataSourceConfig.class);

    @Bean
    @Primary
    public DataSource dataSource(DataSourceProperties properties) {

        String databaseUrl = System.getenv("DATABASE_URL");

        if (StringUtils.hasText(databaseUrl)
                && (databaseUrl.startsWith("postgres://")
                || databaseUrl.startsWith("postgresql://"))) {

            try {
                URI uri = new URI(databaseUrl);

                String host = uri.getHost();
                int port = uri.getPort() > 0 ? uri.getPort() : 5432;
                String path = uri.getPath();
                String query = uri.getQuery();

                String username = properties.getUsername();
                String password = properties.getPassword();

                String userInfo = uri.getRawUserInfo();

                if (StringUtils.hasText(userInfo)
                        && userInfo.contains(":")) {

                    String[] parts = userInfo.split(":", 2);

                    username = URLDecoder.decode(
                            parts[0], StandardCharsets.UTF_8);

                    password = URLDecoder.decode(
                            parts[1], StandardCharsets.UTF_8);
                }

                if (!StringUtils.hasText(host)
                        || !StringUtils.hasText(path)
                        || path.equals("/")) {

                    throw new IllegalArgumentException(
                            "DATABASE_URL must contain a database name");
                }

                String jdbcUrl = "jdbc:postgresql://"
                        + host + ":" + port + path;

                if (StringUtils.hasText(query)) {
                    jdbcUrl += "?" + query;
                } else {
                    jdbcUrl += "?sslmode=require";
                }

                HikariDataSource dataSource = new HikariDataSource();

                dataSource.setDriverClassName("org.postgresql.Driver");
                dataSource.setJdbcUrl(jdbcUrl);
                dataSource.setUsername(username);
                dataSource.setPassword(password);

                log.info(
                        "Configured PostgreSQL datasource for host {}",
                        host);

                return dataSource;

            } catch (Exception e) {
                throw new IllegalStateException(
                        "Could not configure PostgreSQL from DATABASE_URL. "
                                + "Check the Render database URL and credentials.",
                        e);
            }
        }

        log.info("Using standard Spring datasource configuration");

        return properties.initializeDataSourceBuilder().build();
    }
}
