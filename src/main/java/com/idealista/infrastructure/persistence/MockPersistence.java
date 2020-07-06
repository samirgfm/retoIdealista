package com.idealista.infrastructure.persistence;

import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

@Repository
public class MockPersistence implements AdRepository {

    private List<AdVO> ads;
    private List<PictureVO> pictures;

    public MockPersistence() {
        ads = new ArrayList<AdVO>();
    }

    public List<AdVO> getAds() {
        return ads;
    }

    @Override
    public void saveAds(List<AdVO> ads) {

    }
    @Override
    public List<PictureVO> getPictures(){
        return pictures;
    }
}
