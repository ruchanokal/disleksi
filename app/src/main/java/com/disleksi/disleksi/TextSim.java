/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.disleksi.disleksi;

/**
 *
 * @author Dehkhargani
 */
public class TextSim {
    public static double sim(String s1,String s2) {
        String[] tokens1 = s1.split(" ");
        String[] tokens2 = s2.split(" ");
        int score = 0, penalty = 0, i1= 0, i2= 0, extra = 0;

        for(int i = 0 ; i < tokens2.length && i < tokens1.length ; i++) {
                boolean found = false;
                for(int j = Math.max(0, i-1); j < i+3 && !found && j < tokens2.length ; j++)
                    if(tokens1[i].compareTo(tokens2[j]) == 0)
                    {
                        found = true;
                        //if(i == j)
                            score++;
                        //else 
                            penalty += Math.abs(i-j);
                    }
                if(!found)
                    extra ++;    
        }

        penalty += Math.abs(tokens2.length - tokens1.length);
        double acc;
        if(extra == 0)
        acc = (double)(score - penalty) / tokens1.length;
        else 
            acc = (double)(score - extra) / tokens1.length;
        System.out.println("Length of S1: " + tokens1.length + "\nLength of S2: " + tokens2.length + "\nScore: " + score + "\nPenalty: " + penalty + "\nextra: " + extra);
        return acc;
    }

    public static void main(String[] args) {
        String s1 = "kaplumbağı tavşan karşısına geçmiş ben senden daha hızlı koşarım demiş tavşan git işine demişse dinlememiş sonunda bakmış olmayacak yarış etmeye hazır olmuş gün gösterip sözler sözler sonra ayrılmış günü gelmiş tavşan nasıl konuştuğunu biliyor koştuğunu hiç aldırmamış yoluna kıvrılmış uyumuş ama kaplumbağa konuşmayacağını biliyor bir dakikasına bile geçirmemiş hemen yola düşmüş gidecekleri yere tavşandan önce varmış", 
               s2 = "kaplumbağı tavşan karşısına geçmiş ben senden daha hızlı koşarım demiş tavşan git işine demişse dinlememiş sonunda bakmış olmayacak yarış etmeye hazır olmuş gün gösterip sözler sözler sonra ayrılmış günü gelmiş tavşan nasıl konuştuğunu biliyor koştuğunu hiç aldırmamış yoluna kıvrılmış uyumuş ama kaplumbağa konuşmayacağını biliyor bir dakikasına bile geçirmemiş hemen yola düşmüş gidecekleri yere tavşandan önce varmış";
        
        double acc = sim(s1,s2);
        System.out.println("\nThe accuracy is: " + acc);
    }
}
