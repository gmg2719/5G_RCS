/*
 * Copyright (C) 2015 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.android.messaging.util;

import android.support.annotation.Nullable;
import android.util.Patterns;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextUtil {
    /**
     * Returns true if the string is empty, null or only whitespace.
     */
    public static boolean isAllWhitespace(@Nullable String string) {
      if (string == null || string.isEmpty()) {
        return true;
      }

      for (int i = 0; i < string.length(); ++i) {
        if (!Character.isWhitespace(string.charAt(i))) {
          return false;
        }
      }

      return true;
    }

    /**
     * Taken from PhoneNumberUtils, where it is only available in API 21+ Replaces all unicode
     * (e.g. Arabic, Persian) digits with their decimal digit equivalents.
     *
     * @param number the number to perform the replacement on.
     * @return the replaced number.
     */
    public static String replaceUnicodeDigits(String number) {
        StringBuilder normalizedDigits = new StringBuilder(number.length());
        for (char c : number.toCharArray()) {
            int digit = Character.digit(c, 10);
            if (digit != -1) {
                normalizedDigits.append(digit);
            } else {
                normalizedDigits.append(c);
            }
        }
        return normalizedDigits.toString();
    }

    /**
     * Appends text to the stringBuilder.
     * If stringBuilder already has content, separator is prepended to create a separator between
     * entries.
     * @param stringBuilder The stringBuilder to add to
     * @param text The text to append
     * @param separator The separator to add if there is already text, typically "," or "\n"
     */
    public static void appendWithSeparator(final StringBuilder stringBuilder, final String text,
            final String separator) {
        if (stringBuilder.length() > 0) {
            stringBuilder.append(separator);
        }
        stringBuilder.append(text);
    }

    //add by junwang
    public static String parseTextToGetUrl(String text){
        LogUtil.i("Junwang", "parseTextToGetUrl text = "+ text);
//        ArrayList<String> urls = new ArrayList<>();

        if((text == null) || (text.length() == 0)){
            return null;
        }
//        String regex = "https?://(?:[-\\w.]|(?:%[\\da-fA-F]{2}))+[^\\u4e00-\\u9fa5]+[\\w-_/?&=#%:]{0}";
//        Pattern p = Pattern.compile(regex);
        Pattern p = Pattern.compile(Patterns.WEB_URL.toString());
        Matcher matcher = p.matcher(text);
        if(matcher != null && matcher.matches()){
            LogUtil.i("Junwang", "parseTextToGetUrl matches matcher.group()="+matcher.group());
            return matcher.group();
        }
        return null;
    }
}
