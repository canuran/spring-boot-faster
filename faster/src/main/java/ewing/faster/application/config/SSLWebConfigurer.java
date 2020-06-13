package ewing.faster.application.config;

import org.apache.catalina.connector.Connector;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.embedded.EmbeddedServletContainerFactory;
import org.springframework.boot.context.embedded.tomcat.TomcatEmbeddedServletContainerFactory;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.Base64Utils;

import java.io.ByteArrayInputStream;
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
@ConditionalOnProperty(name = "server.https.enable", havingValue = "true")
@EnableConfigurationProperties(SSLWebConfigurer.SSLConnectorProperties.class)
public class SSLWebConfigurer {

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
            KeyStore keyStore = KeyStore.getInstance(KEY_STORE_TYPE);
            keyStore.load(new ByteArrayInputStream(Base64Utils.decodeFromString(KEY_STORE)),
                    KEY_STORE_PASSWORD.toCharArray());
            certificate.setCertificateKeystore(keyStore);
        } catch (GeneralSecurityException | IOException e) {
            throw new RuntimeException(e);
        }
        // 使用SSL证书配置连接器
        sslHostConfig.addCertificate(certificate);
        connector.addSslHostConfig(sslHostConfig);
        return connector;
    }

    @ConfigurationProperties(prefix = "server.https")
    public static class SSLConnectorProperties {
        private Integer port = 443;


        public Integer getPort() {
            return port;
        }

        public void setPort(Integer port) {
            this.port = port;
        }
    }

    /**
     * 以下密钥配置仅供参考。
     */
    private static final String KEY_STORE_TYPE = "JKS";
    private static final String KEY_STORE_PASSWORD = "changeit";
    private static final String KEY_STORE = "/u3+7QAAAAIAAAABAAAAAQAGdG9tY2F0AAABXbsu9JUAAAUCMIIE/jAOBgorBgEE" +
            "ASoCEQEBBQAEggTqDCVZUWMX4q463URRfEJzDgmS3MqES8yGuqtToN/5AYzpa4EF9QzHSFcvEtD+vw4nTJYUmCxa5x0x1pr+eGNW" +
            "GexvIqaiT8vm77nmQMtAjSch+GNfpLttZrK/95yfZnkQGxO80c1sB8TimU80Me07ddqMgTcN1/0+YYxbREDWKks64IZK6VV639JB" +
            "BNCcxWyMltBNXb52TzR+HwldZ8W+EPeiCpZIbfAgT3YbXGz8/ciizQfn1MR61TmRJmwbx1neTBBzVDpZwQC5JtMyOrCvQ6s4+lyz" +
            "na2D1wQ5yIjfvPXvIXQjYyGw/dm/wWkBCergxB1ABS6GEw6Qh8gBEB7SSO9R8KHAoJXgTXUcKjZG+2VTfWlgvSiA3F4TM+wxzBM8" +
            "FdU5YUAB2fACj1AyUhPZXwyGmn+M4Kj8qAqV53p6Sp0MlcL1rWEH+aOKL8mw6ipedr87Uqm7pw9XkxYDs2aIgwXFZl4meYdhFj05" +
            "6Q1sgSIxkXiwjjRbEDXxd4gormH7wmlIJ26QJy090NQZ67MY4AuxAeJnC5ab284qDSmR79uRPjYMDgGFmxYpATonoSdxA610J6KD" +
            "f8RSlxU036Fd+3JCHMFVlvp59vnz2/wrlT6gKHGTFR4aACZrq+zF14lZ+H0/YA/31/QgotyowLtOwD4BpAFjygUeNUErUEA/BdZB" +
            "5OOBzipFn/bTTz83J4GGHbKzukqLMjs1ppq8rYV/PBbR0K3hNU2crwTTz9RD9V6sS6wpic2+tLbzs97S9OzSDkPiQmSCWCn2P1P5" +
            "4S/n0FGmoPldeiIyG62zFE322i2b9MoE3xB89OUy+Aei+MXf2rQQM33VOoOVwS5+2KXVH3WNymKvJmQrFvfpg9vcmK4hrqFavcgg" +
            "kfIpVNi3Dna2QGC3xRemriuuWB748k76mqr0uxlsB031okk9qMxpC6GWLWb5/CqUGNei06syyEIC9kRqiibPTScdbngp+0GGNMDc" +
            "iPZT38cnAH1uKQPhEdsxErHCW48fJzB+roSn43w+xm0BDAMxX2WTba1EB+yv8bxoA3jZ+QpL+ng8EkHj8fWeoJQtDUEFfoYKmmKm" +
            "lHLXYlbBKd0UeNPMTuU7h0GMBKFOcLY8gEayeSE3Q8CggUE3utaCqVP9TY4vq8AL4P/g7Y7Apr2K0SHkBKo64bR8ShMg+f/y0wSw" +
            "GiDyjiMq5RAyQY85YWPX6aFpmiSpHq+/7KjWGJefgzmQJbVDxDC8/rHytcAmbvi/U5CDyVDielu0sA3yUOkjhuR84mhz+oA+i6i/" +
            "PONpHZJ0zFqWbQAGD+3tII7REBSV9UJMjhcVM2YJcPnvJaKryLJ9557csNm56zoeZxRKyCadnymRI6A56P07cgdT7QxA24wR8tuo" +
            "40wV8D0AcSd4Ux1Y4lyXQ0qvjZ5+ufFThzx7/JnliDHOtTrd7KDHiAI6Puw1wVCelNKTy0ywG7NNygDTg6CG9OI/8q8Cv6rXOES0" +
            "8mdzFBDLokvbjYANKbdxWbwplinOBLOBMgSNRk8/tqPP6HyhhMIm4gTVR5PyJpk20zQZrqrf/6j+HRs3g/EjxBDsKC6CstSoNCH/" +
            "bKexiZ+Bh86jNb27/cqNzNcVKTf250jUfl2rA9uawHUIOeQnXmNKSVtNPUBuUFFjec53+wMzJz+DBLII+Rn/Gzns2ecqTAAAAAEA" +
            "BVguNTA5AAADUTCCA00wggI1oAMCAQICBAHgCl4wDQYJKoZIhvcNAQELBQAwVzELMAkGA1UEBhMCY24xCzAJBgNVBAgTAmhuMQsw" +
            "CQYDVQQHEwJjczEOMAwGA1UEChMFZXdpbmcxDjAMBgNVBAsTBWV3aW5nMQ4wDAYDVQQDEwVld2luZzAeFw0xNzA4MDcwNTMzMTla" +
            "Fw0xNzExMDUwNTMzMTlaMFcxCzAJBgNVBAYTAmNuMQswCQYDVQQIEwJobjELMAkGA1UEBxMCY3MxDjAMBgNVBAoTBWV3aW5nMQ4w" +
            "DAYDVQQLEwVld2luZzEOMAwGA1UEAxMFZXdpbmcwggEiMA0GCSqGSIb3DQEBAQUAA4IBDwAwggEKAoIBAQCqvizXLQOu5+L9NzIU" +
            "PfYIvy8cq+R0JDyDVj+G8Nr8QInJVJw2FAIticfEDp9Ya4zpIuV8dHaJbr7DR3mxq9mmsCmNwS0Wiphw0h+CYc4VNBecz+UUpRQ1" +
            "zSZmcg0MEjYiKkEQXooCCnP87vITEoDitLHS9Vjonax0ZzbXZKZpLlxONDc1pVF/p3O9dwPAnMvylfksFVKa3tRu2HSCweKr2Eis" +
            "jEV92PFNNGiis+CWwq2EBPaLsObmEGama7AUiBaKSxULc5qqpYcpXsc9ug127p6ZisnEHsa0bcm4RvvhkvOBV84pWdOuyisaY1cC" +
            "Cx3UF8KVNxDlfu+XNIbw0TK7AgMBAAGjITAfMB0GA1UdDgQWBBT97PqV1ZArNvybB5R8FaXe2nYTQDANBgkqhkiG9w0BAQsFAAOC" +
            "AQEAMLPrOYilcKTPg8ciUlPju9cRq8vtM1qoR6ArCVvyDSpdn99ecO0LXB3QcvK8xkNO7c1k5tqlI5fSrNrVc1+3ll9SOvAak2FF" +
            "fFLtWk/+IDrv2DJpq5b2KobO+R9MHNhfp43nboZBkAbmZFUTpVeaw4T0kOwHb/MuNmkbZeLmEpr20Y50gNqqDTis1WysJMJhzW7C" +
            "AerKghvN3yvJixSi0/xBMxoGH1T4d+YoY6e1wtRm1A1T2NP98ixkOfzYneOJMrMTHIDl98HQRvx3jFMMNSMZYPnjED7m0ZPw8n3B" +
            "SVrx9/KQRQZYU4Rx7SmMriFWOZQx6pRQ/SbxVATK+nTXWrE9TQZ942AV8CBguR6I/XP34itK";

}