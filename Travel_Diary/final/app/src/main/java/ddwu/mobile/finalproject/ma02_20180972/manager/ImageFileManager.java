package ddwu.mobile.finalproject.ma02_20180972.manager;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageFileManager {

        final static String TAG = "ImageFileManager";
        final static String IMG_EXT = ".jpg";

        private Context context;

        public ImageFileManager (Context context) {
            this.context = context;
        }

        public String saveBitmapToTemporary(Bitmap bitmap, String url) {
            String fileName = null;
            try {
                fileName = getFileNameFromUrl(url);

                File saveFile = new File(context.getFilesDir(), fileName);

                FileOutputStream fos = new FileOutputStream(saveFile);

                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);

                fos.flush();
                fos.close();

            } catch (FileNotFoundException e) {
                e.printStackTrace();
                fileName = null;
            } catch (IOException e) {
                e.printStackTrace();
                fileName = null;
            }
            return fileName;
        }

        public Bitmap getBitmapFromTemporary(String url) {
            String fileName = getFileNameFromUrl(url);;
            String path = context.getFilesDir().getPath() + "/" + fileName;

            // 비트맵일 경우의 읽기
            Bitmap bitmap = null;
            bitmap = BitmapFactory.decodeFile(path);
            Log.i(TAG, path);

            return bitmap;
        }

        public Bitmap getBitmapFromExternal(String fileName) {
            if (!isExtStorageWritable()) return null;
            String path = context.getExternalFilesDir(Environment.DIRECTORY_PICTURES) + "/" + fileName;
            Log.i(TAG, path);
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            return bitmap;
        }

        public void clearTemporaryFiles() {
            File internalStoragefiles = context.getFilesDir();
            File[] files = internalStoragefiles.listFiles();
            for (File target : files) {
                target.delete();
            }

        }

        public String moveFileToExt(String url) {
            if (!isExtStorageWritable()) return null;

            if(url != null) {
                String fileName = getFileNameFromUrl(url);

                File internalFile = new File(context.getFilesDir(), fileName);

                String newfileName = getCurrentTime() + IMG_EXT;
                File externalFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), newfileName);

                boolean result = copyFile(internalFile, externalFile);

                if (result) {
                    internalFile.delete();

                    return newfileName;
                }
            }

            return null;
        }

        public boolean removeImageFromExt(String fileName) {
            if (!isExtStorageWritable()) return false;
            File targetFile = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), fileName);
            return targetFile.delete();
        }

        public String getFileNameFromUrl(String url) {
            String fileName = Uri.parse(url).getLastPathSegment();
            return fileName.replace("\n", "");
        }

        private String getCurrentTime() {
            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd_HHmmss");
            return dateFormat.format(new Date());
        }

        public boolean copyFile(File targetFile, File destFile) {
            FileInputStream fis = null;
            FileOutputStream fos = null;
            byte[] buffer = null;
            int length = 0;
            try {
                fis = new FileInputStream(targetFile);

                fos = new FileOutputStream(destFile);

                buffer = new byte[1024];
                while((length=fis.read(buffer)) != -1){
                    fos.write(buffer, 0, length);
                }
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            } finally{
                try {
                    fis.close();
                    fos.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
            return true;
        }

        private boolean isExtStorageWritable() {
            String state = Environment.getExternalStorageState();
            if (Environment.MEDIA_MOUNTED.equals(state)) {
                return true;
            }
            return false;
        }


    }
