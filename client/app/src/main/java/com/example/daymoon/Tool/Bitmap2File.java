package com.example.daymoon.Tool;

import android.graphics.Bitmap;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

public class Bitmap2File {
    static File convert(Bitmap bitmap, File cacheDir){
        try {
            File file = new File(cacheDir, "portrait");
            boolean result = file.createNewFile();
            if (!result) return null;
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, outputStream);
            byte[] bitmapData = outputStream.toByteArray();
            FileOutputStream fos = new FileOutputStream(file);
            fos.write(bitmapData);
            fos.flush();
            fos.close();
            return file;
        } catch (Exception e){
            e.printStackTrace();
        }
        return null;
    }
}
