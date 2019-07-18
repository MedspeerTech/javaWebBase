package com.piotics.common.utils;

import org.springframework.stereotype.Component;

import java.util.regex.Matcher;
import java.util.regex.Pattern;


@Component
public class UtilityManager {


    public boolean isEmail(String username) {
        Pattern pattern = Pattern.compile("[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,4}");
        Matcher mat = pattern.matcher(username);

        if (mat.matches()) {

            return true;
        } else {

            return false;
        }
    }
}
