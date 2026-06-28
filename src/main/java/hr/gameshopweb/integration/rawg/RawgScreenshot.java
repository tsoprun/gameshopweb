package hr.gameshopweb.integration.rawg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgScreenshot {
    private Integer id;
    private String image;
}
