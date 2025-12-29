package com.hokinhtaekwondo.hokinh_taekwondo.utils;

import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.regex.Pattern;

public class InstructorCodeGenerator {

    // Remove accents (similar to removeAccents in JS)
    private static String removeAccents(String input) {
        if (input == null) return "";
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        Pattern pattern = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");
        return pattern.matcher(normalized).replaceAll("");
    }

    // Generate student code
    public static String generateInstructorCode(String fullName, LocalDate birthDate, Boolean isCoach) {
        if (fullName == null || fullName.isBlank() || birthDate == null) {
            return "";
        }

        String prefix = isCoach ? "HLV" : "HDV";

        String[] nameParts = fullName.trim().split("\\s+");

        // Main name (last word)
        String name = removeAccents(nameParts[nameParts.length - 1]).toLowerCase();

        // Initials from first name + middle names
        StringBuilder initials = new StringBuilder();
        for (int i = 0; i < nameParts.length - 1; i++) {
            initials.append(
                    removeAccents(String.valueOf(nameParts[i].charAt(0))).toLowerCase()
            );
        }

        // Date format ddMMyy
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("ddMMyy");
        String date = birthDate.format(formatter);

        return prefix + "_" + name + initials + "_" + date;
    }
}

