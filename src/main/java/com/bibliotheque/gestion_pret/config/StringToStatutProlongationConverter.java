package com.bibliotheque.gestion_pret.config;

import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

import com.bibliotheque.gestion_pret.enums.StatutProlongation;

@Component
public class StringToStatutProlongationConverter implements Converter<String, StatutProlongation> {

    @Override
    public StatutProlongation convert(String source) {
        if (source == null || source.isEmpty()) {
            return null;
        }
        try {
            return StatutProlongation.valueOf(source.toUpperCase());
        } catch (IllegalArgumentException e) {
            System.err.println("Valeur invalide pour StatutProlongation : " + source);
            return null;
        }
    }
}