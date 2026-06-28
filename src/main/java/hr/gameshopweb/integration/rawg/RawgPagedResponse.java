package hr.gameshopweb.integration.rawg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgPagedResponse {
    private Integer count;
    private String next;
    private String previous;
    private List<RawgGame> results;
}
