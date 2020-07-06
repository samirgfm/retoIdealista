package com.idealista.infrastructure.persistence;

import java.util.List;

public interface AdRepository { //interface que tiene como objetivo protegernos en caso de que el repositorio esté vacío
    List<AdVO> getAds();
    void saveAds(List<AdVO> ads);
    List<PictureVO> getPictures();

}
