package com.kanban.config;

import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * TiDB Serverless rejects plaintext MySQL connections. Connector/J also treats
 * {@code useSSL=true} as deprecated; without {@code sslMode} the handshake can
 * still go out as insecure. Rewrite the JDBC URL before Hikari opens the pool.
 */
@Configuration
@Profile("prod")
public class MysqlTlsUrlEnforcer {

    private static final String TLS_PARAMS = "sslMode=VERIFY_IDENTITY&enabledTLSProtocols=TLSv1.2,TLSv1.3";

    @Bean
    public static BeanPostProcessor mysqlTlsUrlBeanPostProcessor() {
        return new BeanPostProcessor() {
            @Override
            public Object postProcessBeforeInitialization(Object bean, String beanName) throws BeansException {
                if (bean instanceof HikariDataSource hikari) {
                    String url = hikari.getJdbcUrl();
                    if (url != null && url.startsWith("jdbc:mysql:")) {
                        hikari.setJdbcUrl(ensureTls(url));
                    }
                }
                return bean;
            }
        };
    }

    static String ensureTls(String url) {
        String cleaned = url
                .replace("useSSL=false", "useSSL=true")
                .replace("requireSSL=false", "requireSSL=true")
                .replace("sslMode=DISABLED", "sslMode=VERIFY_IDENTITY")
                .replace("sslMode=PREFERRED", "sslMode=VERIFY_IDENTITY");
        if (!cleaned.toLowerCase().contains("sslmode=")) {
            cleaned += (cleaned.contains("?") ? "&" : "?") + TLS_PARAMS;
        }
        if (!cleaned.contains("enabledTLSProtocols=")) {
            cleaned += (cleaned.contains("?") ? "&" : "?") + "enabledTLSProtocols=TLSv1.2,TLSv1.3";
        }
        return cleaned;
    }
}
