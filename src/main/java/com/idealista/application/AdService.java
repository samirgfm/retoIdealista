package com.idealista.application;

import com.idealista.infrastructure.api.PublicAd;
import com.idealista.infrastructure.api.QualityAd;
import com.idealista.infrastructure.persistence.AdRepository;
import com.idealista.infrastructure.persistence.AdVO;
import com.idealista.infrastructure.persistence.PictureVO;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.StringTokenizer;

public class AdService {

    private final AdRepository repository;

    public AdService(final AdRepository repository) {
        this.repository = repository;
    }

    public List<QualityAd> listQualityAds() {
        final List<AdVO> adsVp = repository.getAds();
        final List<PictureVO> adsPictures = repository.getPictures();
        return qualityAdsMapper(adsVp,adsPictures);
    }
    public List<PublicAd> listPublicAds() {
        final List<AdVO> adsVp = repository.getAds();
        final List<PictureVO> adsPictures = repository.getPictures();
        return publicAdsMapper(adsVp,adsPictures);
    }
    private List<QualityAd> qualityAdsMapper(List<AdVO> voList, List<PictureVO> voPictures) {
        List<QualityAd> result = new ArrayList<>();
        for (int i = 0; i < voList.size(); i++) {
            QualityAd ad = new QualityAd();
            ad.setId(voList.get(i).getId());
            ad.setDescription(voList.get(i).getDescription());
            ad.setGardenSize(voList.get(i).getGardenSize());
            ad.setHouseSize(voList.get(i).getHouseSize());
            ad.setIrrelevantSince(voList.get(i).getIrrelevantSince());
            List<String> picturesUrls = new ArrayList<>();
            for(int j=0;j<voPictures.size();j++) {
                picturesUrls.add(voPictures.get(j).getUrl());
            }
            ad.setPictureUrls(picturesUrls);
            ad.setTypology(voList.get(i).getTypology());
            ad.setScore(voList.get(i).getScore());
            result.add(ad);
        }
        return result;
    }

    private List<PublicAd> publicAdsMapper(List<AdVO> voList, List<PictureVO> voPictures) {
        List<PublicAd> result = new ArrayList<>();
        List<AdVO> copia = new ArrayList<>();
        for(AdVO adVOrelleno : voList){
            copia.add(adVOrelleno);
        }

        while (copia.size() != 0) {
            PublicAd ad = new PublicAd();
            AdVO adVOCandidato = new AdVO();
            int score = -1;
            for (AdVO adVO : copia) {
                if (adVO.getScore() > score) {

                    score = adVO.getScore();
                    adVOCandidato=adVO;
                }


            }
            copia.remove(adVOCandidato);
            ad.setId(adVOCandidato.getId());
            ad.setTypology(adVOCandidato.getTypology());
            ad.setDescription(adVOCandidato.getDescription());
            ad.setGardenSize(adVOCandidato.getGardenSize());
            ad.setHouseSize(adVOCandidato.getHouseSize());
            List<String> picturesUrls = new ArrayList<>();
            for (int j = 0; j < voPictures.size(); j++) {
                picturesUrls.add(voPictures.get(j).getUrl());
            }
            ad.setPictureUrls(picturesUrls);
            if(adVOCandidato.getScore()>=40){
                result.add(ad);
            }

        }
        System.out.println(voList.size());
        return result;
    }
    public void calculateScore() {
        final List<AdVO> ads = new ArrayList<>();
        final List<AdVO> adsVp = repository.getAds();
        final List<PictureVO> adsPictures = repository.getPictures();

        for (int i = 0; i < adsVp.size(); i++) {
            int score = 0;
            AdVO ad = adsVp.get(i);
            boolean esChalet = ad.getTypology() == "CHALET";
            boolean esFlat = ad.getTypology() == "FLAT";
            boolean esGarage = ad.getTypology() == "GARAGE";
            int puntosPorPictures = puntosPorPictures(ad, adsPictures);
            int puntosPorDescripcion = puntosPorDescripcion(ad);
            //int puntosPorPictures = 0;
            //int puntosPorDescripcion = 0;
            if ((esChalet && puntosPorPictures > 0 && puntosPorDescripcion > 0 && ad.getHouseSize() != null && ad.getGardenSize() != null) || (esFlat && puntosPorPictures > 0 && puntosPorDescripcion > 0 && ad.getHouseSize() != null) || (esGarage && puntosPorPictures > 0)) {
                score = puntosPorDescripcion + puntosPorPictures + 40;
            } else {
                score = puntosPorDescripcion + puntosPorPictures;
            }
            if (score > 100) {
                ad.setScore(100);
            } else if (score < 0) {
                Date fecha = new Date();
                ad.setIrrelevantSince(fecha);
                ad.setScore(0);


            } else if (score<40 && score>=0){
                Date fecha = new Date();
                ad.setIrrelevantSince(fecha);
                ad.setScore(score);
            }
            else {
                ad.setScore(score);
            }
            ads.add(ad);
        }


        repository.saveAds(ads);
    }

    private int puntosPorPictures(AdVO ad, List<PictureVO> adsPictures) {
        List<Integer> idPictures = ad.getPictures();
        int resultado = 0;

        // puntos por las fotos
        if (idPictures.size() != 0) {

            for (int j = 0; j < idPictures.size(); j++) {
                for (PictureVO pictureVO : adsPictures) {
                    if (pictureVO.getId() == idPictures.get(j)) {
                        switch (pictureVO.getQuality()) {
                            case "HD":
                                resultado += 20;
                                break;
                            case "SD":
                                resultado += 10;
                                break;
                            default:
                                break;
                        }


                    }


                }
            }
        } else {
            resultado -= 10;
        }
        return resultado;
    }

    private int puntosPorDescripcion(AdVO ad) {
        String descripcion = ad.getDescription();
        int resultado=0;
        boolean luminosoPrimero= true;
        boolean nuevoPrimero= true;
        boolean centricoPrimero= true;
        boolean reformadoPrimero= true;
        boolean aticoPrimero= true;
        boolean esChalet = ad.getTypology() == "CHALET";
        boolean esFlat = ad.getTypology() == "FLAT";
        if(descripcion==null){
            return 0;
        }
        if (descripcion.length() > 0) {
            resultado += 5;
            StringTokenizer tokenizer = new StringTokenizer(descripcion);
            int contador = 0;

            while (tokenizer.hasMoreTokens()) {
                contador++;
                String temp= tokenizer.nextToken();
                if ((temp == "Luminoso" || temp == "luminoso") && luminosoPrimero){
                    luminosoPrimero=false;
                    resultado+=5;
                }
                else if((temp == "Nuevo" || temp == "nuevo") && nuevoPrimero){
                    nuevoPrimero=false;
                    resultado+=5;
                }
                else if((temp == "Céntrico" || temp == "céntrico") && centricoPrimero){
                    centricoPrimero=false;
                    resultado+=5;
                }
                else if((temp == "Ático" || temp == "ático") && aticoPrimero){
                    aticoPrimero=false;
                    resultado+=5;
                }
                else if((temp == "Reformado" || temp == "reformado") && reformadoPrimero){
                    reformadoPrimero=false;
                    resultado+=5;
                }
                else{

                }
            }
            if(contador>=20 && contador<= 49 && esFlat){
                resultado+=10;
            }
            else if(contador>=50 && esFlat){
                resultado+=30;
            }
            else if(contador> 50 && esChalet){
                resultado+=20;
            }
            else{

            }


        }
    return resultado;
    }

}
