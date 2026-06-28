package hr.gameshopweb.integration.rawg;


import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RawgClient {

    private static final String GAMES = "games";

    private final RestTemplate restTemplate;

    @Value("${rawg.api.key}")
    private String apiKey;

    @Value("${rawg.api.base-url}")
    private String baseUrl;

    public RawgPagedResponse searchGames(String query, int page, int pageSize) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/games")
                .queryParam("key", apiKey)
                .queryParam("search", query)
                .queryParam("page", page)
                .queryParam("page_size", pageSize)
                .toUriString();

        return restTemplate.getForObject(url, RawgPagedResponse.class);
    }

    public RawgPagedResponse getGames(int page, int pageSize, String genre) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(baseUrl + "/games")
                .queryParam("key", apiKey)
                .queryParam("page", page)
                .queryParam("page_size", pageSize)
                .queryParam("ordering", "-metacritic");

        if (genre != null && !genre.isBlank()) {
            builder.queryParam("genres", genre);
        }

        return restTemplate.getForObject(builder.toUriString(), RawgPagedResponse.class);
    }

    public RawgGame getGameBySlug(String slug) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + GAMES + "/" + slug)
                .queryParam("key", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, RawgGame.class);
    }

    public RawgGame getGameById(Integer id) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + GAMES + "/" + id)
                .queryParam("key", apiKey)
                .toUriString();

        return restTemplate.getForObject(url, RawgGame.class);
    }

    public List<RawgScreenshot> getScreenshots(Integer gameId) {
        String url = UriComponentsBuilder.fromHttpUrl(baseUrl + "/" + GAMES + "/" + gameId + "/screenshots")
                .queryParam("key", apiKey)
                .toUriString();

        RawgScreenshotResponse response = restTemplate.getForObject(url, RawgScreenshotResponse.class);
        return response != null ? response.getResults() : List.of();
    }
}
