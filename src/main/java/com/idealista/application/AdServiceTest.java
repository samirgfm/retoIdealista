package com.idealista.application;

import com.idealista.infrastructure.api.QualityAd;
import com.idealista.infrastructure.persistence.MockPersistence;

import java.util.List;

class AdServiceTest {

    void testListQualityAdsWhenNotQualityAds() {
        MockPersistence repository = new MockPersistence();
        AdService service = new AdService(repository);
        List<QualityAd> qualityAds = service.listQualityAds();
        if (qualityAds.size() == 0) {
            System.out.println("funciona");
        }
    }

}