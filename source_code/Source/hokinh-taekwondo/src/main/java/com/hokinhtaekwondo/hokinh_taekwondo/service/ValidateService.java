package com.hokinhtaekwondo.hokinh_taekwondo.service;

import org.springframework.context.support.DefaultMessageSourceResolvable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.validation.BindingResult;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@Service
public class ValidateService {

    public ResponseEntity<?> checkBindingResult(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {

            Object target = bindingResult.getTarget();
            List<String> fieldOrder = new ArrayList<>();

            if (target != null) {
                for (Field field : target.getClass().getDeclaredFields()) {
                    fieldOrder.add(field.getName());
                }
            }
            List<String> errors = bindingResult.getFieldErrors().stream()
                    .sorted(Comparator.comparingInt(e -> {
                        int idx = fieldOrder.indexOf(e.getField());
                        return idx == -1 ? Integer.MAX_VALUE : idx;
                    }))
                    .map(DefaultMessageSourceResolvable::getDefaultMessage)
                    .toList();

            return ResponseEntity.badRequest().body(errors.getFirst());
        }
        return null;
    }
}
