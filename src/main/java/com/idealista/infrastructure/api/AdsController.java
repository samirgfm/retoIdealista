package com.idealista.infrastructure.api;

import com.idealista.application.AdService;
import com.idealista.infrastructure.persistence.InMemoryPersistence;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
public class AdsController {
    private  boolean seHaCalculado = false;
    private final AdService adService = new AdService(new InMemoryPersistence());

    @RequestMapping(value = "/internal/listing", method = RequestMethod.GET) // controlador se ejecuta en la página web que aparece aquí
    public ResponseEntity<List<QualityAd>> qualityListing() {
        if(!seHaCalculado){
            adService.calculateScore();
        }
        Optional<List<QualityAd>> ads = Optional.of(adService.listQualityAds());
        return ResponseEntity.of(ads); // Responde con la lista solicitada
    }

    @RequestMapping(value = "/public/listing", method = RequestMethod.GET)
    public ResponseEntity<List<PublicAd>> publicListing() {
        if(!seHaCalculado){
            adService.calculateScore();
        }
        Optional<List<PublicAd>> ads = Optional.of(adService.listPublicAds());
        return ResponseEntity.of(ads); // Responde con la lista solicitada
    }

    @RequestMapping(value = "/", method = RequestMethod.GET) //este debe llamar al metodo que tiene la lógica del calculate
    public ResponseEntity<Void> calculateScore() {
        adService.calculateScore();
        seHaCalculado =true;
        return ResponseEntity.ok().build();//200 ok
    }
}
