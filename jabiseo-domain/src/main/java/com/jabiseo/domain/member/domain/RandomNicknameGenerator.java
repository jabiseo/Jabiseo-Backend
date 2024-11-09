package com.jabiseo.domain.member.domain;

import org.springframework.stereotype.Component;

import java.util.Random;

@Component
public class RandomNicknameGenerator {

    private static final String[] prefixStrings = {"자격증마스터", "공부의왕", "백점만이살길", "하루공부", "재밌는자격증"};
    private final Random random = new Random();

    public String generate() {
        int prefixIndex = random.nextInt(prefixStrings.length);
        String prefix = prefixStrings[prefixIndex];
        String suffixString = generateRandomNumber();

        return prefix + suffixString;
    }

    private String generateRandomNumber() {
        String randomNumber;
        do {
            int number = random.nextInt(9000) + 1000; // 1000부터 9999 사이의 난수 생성
            randomNumber = String.valueOf(number);
        } while (!isValid(randomNumber));

        return randomNumber;
    }

    private static boolean isValid(String number) {
        return number.charAt(0) != '0';
    }
}
