package com.disleksi.disleksi;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class BenzerlikHesaplama {
    public static double jaccardBenzerlik(String metin1, String metin2) {
        // Metinleri küçük harfe dönüştürün ve gereksiz karakterleri kaldırın
        metin1 = metin1.toLowerCase().replaceAll("[^a-zA-Z]", " ");
        metin2 = metin2.toLowerCase().replaceAll("[^a-zA-Z]", " ");

        // Metinleri kelimelere ayırın ve kümelere dönüştürün
        Set<String> kume1 = new HashSet<>(Arrays.asList(metin1.split(" ")));
        Set<String> kume2 = new HashSet<>(Arrays.asList(metin2.split(" ")));

        // Jaccard Benzerlik Katsayısını hesaplayın
        Set<String> kesisim = new HashSet<>(kume1);
        kesisim.retainAll(kume2); // Kesişim kümesini bulun

        Set<String> birlesim = new HashSet<>(kume1);
        birlesim.addAll(kume2); // Birleşim kümesini bulun

        double benzerlikKatsayisi = (double) kesisim.size() / birlesim.size();

        // Sonucu yüzde olarak ifade edin
        return benzerlikKatsayisi * 100;
    }

    public static void main(String[] args) {
        String metin1 = "Bu bir örnek metindir.";
        String metin2 = "Bu örnek bir metindir.";

        double benzerlik = jaccardBenzerlik(metin1, metin2);
        System.out.println("Benzerlik Oranı: " + benzerlik + "%");
    }
}
