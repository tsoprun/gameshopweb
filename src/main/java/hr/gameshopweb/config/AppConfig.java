package hr.gameshopweb.config;

import com.paypal.base.rest.APIContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.web.client.RestTemplate;

@Configuration
@EnableAsync
public class AppConfig {

    @Value("${paypal.client.id}") private String paypalClientId;

    @Value("${paypal.client.secret}") private String paypalClientSecret;

    @Value("${paypal.mode}") private String paypalMode;

    @Bean
    public APIContext apiContext() {
        return new APIContext(paypalClientId, paypalClientSecret, paypalMode);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}
