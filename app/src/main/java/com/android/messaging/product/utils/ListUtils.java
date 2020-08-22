package com.android.messaging.product.utils;

import java.util.List;

public class ListUtils {
    public static boolean isEmpty(List list){
        if (list == null){
            return true;
        }
        return list.size() == 0;
    }
}
