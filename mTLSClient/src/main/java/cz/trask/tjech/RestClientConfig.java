package cz.trask.tjech;

import org.apache.hc.client5.http.impl.io.PoolingHttpClientConnectionManagerBuilder;
import org.apache.hc.client5.http.impl.classic.HttpClients;
import org.apache.hc.client5.http.impl.classic.CloseableHttpClient;
import org.apache.hc.client5.http.io.HttpClientConnectionManager;
import org.apache.hc.client5.http.ssl.NoopHostnameVerifier;
import org.apache.hc.client5.http.ssl.SSLConnectionSocketFactory;
import org.apache.hc.core5.ssl.SSLContextBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;
import javax.net.ssl.SSLContext;


@Configuration
public class RestClientConfig {

    // Load keystore and truststore locations and passwords
    @Value("${client.ssl.trust-store}")
    private Resource trustStore;
    @Value("${client.ssl.key-store}")
    private Resource keyStore;
    @Value("${client.ssl.trust-store-password}")
    private String trustStorePassword;
    @Value("${client.ssl.key-store-password}")
    private String keyStorePassword;

    @Bean
    public RestTemplate restTemplate() throws Exception {
        // Set up SSL context with truststore and keystore
        SSLContext sslContext = new SSLContextBuilder()
                .loadKeyMaterial(
                        keyStore.getURL(),
                        keyStorePassword.toCharArray(),
                        keyStorePassword.toCharArray()
                )
                .loadTrustMaterial(
                        trustStore.getURL(),
                        trustStorePassword.toCharArray()
                )
                .build();

        // Configure the SSLConnectionSocketFactory to use NoopHostnameVerifier
        SSLConnectionSocketFactory sslConFactory = new SSLConnectionSocketFactory(sslContext, new NoopHostnameVerifier());

        // Use a connection manager with the SSL socket factory
        HttpClientConnectionManager cm = PoolingHttpClientConnectionManagerBuilder.create()
                .setSSLSocketFactory(sslConFactory)
                .build();

        // Build the CloseableHttpClient and set the connection manager
        CloseableHttpClient httpClient = HttpClients.custom()
                .setConnectionManager(cm)
                .build();

        // Set the HttpClient as the request factory for the RestTemplate
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        return new RestTemplate(requestFactory);
    }
}
