package cz.trask.tjech;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class ClientApplication  implements CommandLineRunner {

    @Autowired
    RestClientConfig restTemplateConfig;

    public static void main(String[] args) throws Exception {
       SpringApplication.run(ClientApplication.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        RestTemplate restTemplate = restTemplateConfig.restTemplate();
        String resourceUrl
                = "https://localhost:8443/connect";
        ResponseEntity<String> response
                = restTemplate.getForEntity(resourceUrl, String.class);
        System.out.println(response.toString());
    }
}
