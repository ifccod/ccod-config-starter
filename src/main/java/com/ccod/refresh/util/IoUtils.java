package com.ccod.refresh.util;

import java.io.Closeable;

/**
 * @author ccod
 * @date 2022/5/5 2:05 PM
 **/
public class IoUtils {

    public static void close(Closeable closeable){
        if (closeable != null) {
            try {
                closeable.close();
            } catch (Exception e) {
                // skip
            }
        }
    }

}
