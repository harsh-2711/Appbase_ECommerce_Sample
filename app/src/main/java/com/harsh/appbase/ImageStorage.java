package com.harsh.appbase;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.io.FileOutputStream;

public class ImageStorage {


    public static String saveInternalStorage(Context context, Bitmap bitmap, String filename) {

        String stored = null;

        File sdcard = context.getFilesDir();

        Log.d("Path", sdcard.getAbsoluteFile().toString());
        File folder = new File(sdcard.getAbsoluteFile(), "/Images/");
        folder.mkdir();
        File file = new File(folder.getAbsoluteFile(), filename + ".jpg") ;

        if (file.exists())
            return stored ;

        try {
            FileOutputStream out = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out);
            out.flush();
            out.close();
            stored = "success";
        } catch (Exception e) {
            FirebaseCrash.report(e);
            e.printStackTrace();
        }
        return stored;
    }

    public static File getImage(Context context,String imagename) {

        File mediaImage = null;
        try {
            String root = context.getFilesDir().toString();
            File myDir = new File(root);
            if (!myDir.exists())
                return null;

            mediaImage = new File(myDir.getPath() + "/Images/"+imagename);
        } catch (Exception e) {
            FirebaseCrash.report(e);
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return mediaImage;
    }
    public static boolean checkifImageExists(Context context,String imagename)
    {
        Bitmap b = null ;
        File file = ImageStorage.getImage(context,"/"+imagename+".jpg");
        String path = file.getAbsolutePath();

        if (path != null)
            b = BitmapFactory.decodeFile(path);

        if(b == null ||  b.equals(""))
        {
            return false;
        }
        return true ;
    }
}