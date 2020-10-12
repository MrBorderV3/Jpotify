package me.border.jpotify.util;

public class Utils {

    public static String stripExtension(String str){
        int pos = str.lastIndexOf('.');
        if (pos == -1)
            return str;

        return str.substring(0, pos);
    }
}
