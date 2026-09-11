package com.daniel_havlin.url_shortener_java.services;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ShortCodeGeneratorTests {
    @Mock
    private Random random;

    @InjectMocks
    private ShortCodeGenerator shortCodeGenerator;

    @Test
    void createsSixDigitShortCode() {
        when(random.nextInt(0, 16)).thenReturn(0,1,2,6,7,8);
        String shortCode = shortCodeGenerator.generate();

        assertEquals("abc123", shortCode);
    }
}
