package hr.gameshopweb.service;


import hr.gameshopweb.entity.Product;
import hr.gameshopweb.entity.ProductImage;
import hr.gameshopweb.integration.rawg.RawgClient;
import hr.gameshopweb.integration.rawg.RawgGame;
import hr.gameshopweb.integration.rawg.RawgScreenshot;
import hr.gameshopweb.repository.ProductImageRepository;
import hr.gameshopweb.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class RawgSyncService {

    private final RawgClient rawgClient;
    private final ProductRepository productRepository;
    private final ProductImageRepository productImageRepository;

    public void syncGames() {
        var response = rawgClient.getGames(1, 40, null);
        if (response == null || response.getResults() == null) return;

        for (RawgGame game : response.getResults()) {
            if (productRepository.existsByRawgId(game.getId())) continue;

            Product p = new Product();
            p.setRawgId(game.getId());
            p.setName(game.getName());
            p.setSlug(game.getSlug());
            p.setImageUrl(game.getBackgroundImage());
            p.setGenre(game.getFirstGenre());
            p.setPlatform(game.getFirstPlatform());
            p.setMetacriticScore(game.getMetacritic());
            p.setPrice(BigDecimal.valueOf(29.99));
            Product saved = productRepository.save(p);
            List<RawgScreenshot> shots = rawgClient.getScreenshots(game.getId());
            for (RawgScreenshot shot : shots) {
                ProductImage img = new ProductImage();
                img.setProduct(saved);
                img.setImageUrl(shot.getImage());
                productImageRepository.save(img);
            }
            log.info("Synced: {}", game.getName());
        }
    }
}
