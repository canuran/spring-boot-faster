package ewing.application.config;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.KeyStore;

/**
 * 进入 JDK 的 bin 目录运行以下命令生成 keystore
 * keytool -genkey -alias tomcat -keyalg RSA
 * 生成的 .keystore 文件在个人 home 目录下
 *
 * @author Ewing
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
        connector.setPort(properties.getPort());
        connector.setSecure(true);
        connector.setScheme("https");
        connector.setProperty("SSLEnabled", "true");
        SSLHostConfig sslHostConfig = new SSLHostConfig();
        SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(
                sslHostConfig, SSLHostConfigCertificate.Type.RSA);
        try {
            // 证书类型：JKS JCEKS PKCS12 BKS UBER
            KeyStore keyStore = KeyStore.getInstance(properties.getKeyStoreType());
            keyStore.load(SSLWebConfigurer.class.getClassLoader().getResourceAsStream(
                    properties.getKeyStore()), properties.getKeyStorePassword().toCharArray());
            certificate.setCertificateKeystore(keyStore);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        // 使用SSL证书配置连接器
        sslHostConfig.addCertificate(certificate);
        connector.addSslHostConfig(sslHostConfig);
        return connector;
    }

    @ConfigurationProperties(prefix = "sslserver")
    public static class SSLConnectorProperties {
        private Integer port = 443;
        private String keyStore;
        private String keyStorePassword;
        private String keyStoreType;

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

        public String getKeyStoreType() {
            return keyStoreType;
        }

        public void setKeyStoreType(String keyStoreType) {
            this.keyStoreType = keyStoreType;
        }
    }

}