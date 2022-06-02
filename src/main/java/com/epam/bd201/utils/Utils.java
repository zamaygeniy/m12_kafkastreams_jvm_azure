package com.epam.bd201.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.time.Duration;
import java.time.LocalDate;
import java.util.Map;

public class Utils {
    private final static String DATE_REGEX = "^\\d{4}-\\d{2}-\\d{2}$";

    public static String addStayField(String value) {
        try {
            int flag = -1;
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> record = objectMapper.readValue(value, Map.class);

            String checkinString = record.get("srch_ci");
            String checkoutString = record.get("srch_co");

            if (checkinString != null && checkoutString != null && checkinString.matches(DATE_REGEX) && checkoutString.matches(DATE_REGEX)) {
                LocalDate checkinDate = LocalDate.parse(checkinString);
                LocalDate checkoutDate = LocalDate.parse(checkoutString);
                long stay = Duration.between(checkinDate.atStartOfDay(), checkoutDate.atStartOfDay()).toDays() + 1;

                if (stay >= 1 && stay <= 4) {
                    flag = 0;
                } else {
                    if (stay >= 5 && stay <= 10) {
                        flag = 1;
                    } else {
                        if (stay >= 11 && stay <= 14) {
                            flag = 2;
                        } else {
                            if (stay > 14) {
                                flag = 3;
                            }
                        }
                    }
                }
            }
            record.put("stay", String.valueOf(flag));
            return objectMapper.writeValueAsString(record);
        } catch (IOException e) {
            e.printStackTrace();
            return value;
        }
    }

}