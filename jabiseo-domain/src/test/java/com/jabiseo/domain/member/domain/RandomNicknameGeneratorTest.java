package com.jabiseo.domain.member.domain;

import com.jabiseo.domain.member.domain.RandomNicknameGenerator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@DisplayName("랜덤 닉네임 생성기 테스트")
class RandomNicknameGeneratorTest {

    @InjectMocks
    RandomNicknameGenerator generator;


    @Test
    @DisplayName("랜덤 닉네임 생성 시 문자열 뒤 숫자 4자리의 난수가 생성된다.")
    void randomNicknameSuccess(){
        //given
        //when
        String generate = generator.generate();
        String isSuffix = generate.substring(generate.length()-4);

        //then
        assertThat(isSuffix).matches("\\d{4}");
    }
}
