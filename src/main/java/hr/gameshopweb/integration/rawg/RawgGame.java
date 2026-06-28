package hr.gameshopweb.integration.rawg;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RawgGame {

    private Integer id;
    private String name;
    private String slug;
    private String description;

    @JsonProperty("background_image")
    private String backgroundImage;

    private Double metacritic;

    @JsonProperty("genres")
    private List<RawgGenre> genres;

    @JsonProperty("platforms")
    private List<RawgPlatformWrapper> platforms;

    @JsonProperty("clip")
    private RawgClip clip;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawgGenre {
        private String name;
        private String slug;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawgPlatformWrapper {
        private RawgPlatform platform;

        @Data
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class RawgPlatform {
            private String name;
        }
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class RawgClip {
        private String clip;
    }

    public String getFirstGenre() {
        if (genres != null && !genres.isEmpty()) {
            return genres.get(0).getName();
        }
        return null;
    }

    public String getFirstPlatform() {
        if (platforms != null && !platforms.isEmpty()) {
            return platforms.get(0).getPlatform().getName();
        }
        return null;
    }

    public String getTrailerUrl() {
        return clip != null ? clip.getClip() : null;
    }
}
