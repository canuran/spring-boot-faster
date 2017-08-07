package ewing.config;

import org.apache.catalina.connector.Connector;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 进入 JDK 的 bin 目录运行以下命令生成 keystore
 * keytool -genkey -alias tomcat -keyalg RSA
 * 自己生成的密码必须是 changeit
 * 生成的 .keystore 文件在个人 home 目录下
 */
@Configuration
@EnableConfigurationProperties(SSLWebConfigurer.SSLConnectorProperties.class)
public class SSLWebConfigurer extends WebMvcConfigurerAdapter {

    @Bean
    public EmbeddedServletContainerFactory servletContainer(SSLConnectorProperties properties) {
        TomcatEmbeddedServletContainerFactory tomcat = new TomcatEmbeddedServletContainerFactory();
        tomcat.addAdditionalTomcatConnectors(createSSlConnector(properties));
        return tomcat;
    }

    private Connector createSSlConnector(SSLConnectorProperties properties) {
        Connector connector = new Connector();
        connector.setPort(properties.getPort() == null ? 443 : properties.getPort());
        connector.setSecure(true);
        connector.setScheme("https");
        connector.setProperty("SSLEnabled", "true");
        File file = new File("resources/" + properties.getKeyStore());
        if (!file.getParentFile().exists())
            file.getParentFile().mkdirs();
        try {
            FileCopyUtils.copy(SSLWebConfigurer.class.getClassLoader()
                            .getResourceAsStream(properties.getKeyStore()),
                    new FileOutputStream(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        connector.setProperty("keystoreFile", file.getAbsolutePath());
        connector.setProperty("keystorePassword", properties.getKeyStorePassword());
        return connector;
    }

    @ConfigurationProperties(prefix = "sslserver")
    public static class SSLConnectorProperties {
        /*
        application.yml 配置：
        sslserver:
          port: 443
          keyStore: .keystore
          keyStorePassword: changeit
          keyAlias: tomcat
        */

        private Integer port;
        private String keyStore;
        private String keyStorePassword;
        private String keyAlias;

        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }

        public String getKeyStore() {
            return keyStore;
        }

        public void setKeyStore(String keyStore) {
            this.keyStore = keyStore;
        }

        public String getKeyStorePassword() {
            return keyStorePassword;
        }

        public void setKeyStorePassword(String keyStorePassword) {
            this.keyStorePassword = keyStorePassword;
        }

        public String getKeyAlias() {
            return keyAlias;
        }

        public void setKeyAlias(String keyAlias) {
            this.keyAlias = keyAlias;
        }
    }

}