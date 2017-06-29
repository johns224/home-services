package org.rossjohnson.homeservices;

import org.apache.catalina.connector.Connector;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.embedded.EmbeddedServletContainerCustomizer;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.orm.jpa.JpaVendorAdapter;
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter;

import javax.persistence.spi.PersistenceProvider;
import java.util.Collections;
import java.util.Map;


@SpringBootApplication
public class Application {

    @Value("${tomcat.port:9090}")
    private String tomcatPort;

    @Value("${ajp.port:9091}")
    private String ajpPort;

    @Value("${database.file:test.odb}")
    private String databaseFile;

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public EmbeddedServletContainerCustomizer configureTomcat() {
        return c -> {
            TomcatEmbeddedServletContainerFactory tomcat = (TomcatEmbeddedServletContainerFactory) c;
            tomcat.setPort(Integer.parseInt(tomcatPort));

            Connector ajpConnector = new Connector("AJP/1.3");
            ajpConnector.setPort(Integer.parseInt(ajpPort));
            tomcat.addAdditionalTomcatConnectors(ajpConnector);
        };
    }

    @Bean
    JpaVendorAdapter JpaVendorAdapter() {
        return new AbstractJpaVendorAdapter() {
            @Override
            public PersistenceProvider getPersistenceProvider() {
                return new com.objectdb.jpa.Provider();
            }

            @Override
            public Map<String, ?> getJpaPropertyMap() {
                return Collections.singletonMap(
                        "javax.persistence.jdbc.url", databaseFile);
            }
        };
    }

}
