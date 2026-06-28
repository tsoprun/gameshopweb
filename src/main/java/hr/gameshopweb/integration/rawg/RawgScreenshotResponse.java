package hr.gameshopweb.integration.rawg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgScreenshotResponse {
    private List<RawgScreenshot> results;
}
