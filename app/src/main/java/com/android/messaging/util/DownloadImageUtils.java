package com.android.messaging.util;

import android.content.ContentValues;
import android.content.Context;
import android.os.Looper;
import android.provider.MediaStore;

import com.bumptech.glide.load.engine.DiskCacheStrategy;

import java.io.File;
import java.util.ArrayList;

import io.reactivex.BackpressureStrategy;
import io.reactivex.Flowable;
import io.reactivex.FlowableEmitter;
import io.reactivex.FlowableOnSubscribe;
import io.reactivex.functions.Consumer;

public class DownloadImageUtils {
    static boolean result = false;
    /**
     * 保存图片到本地
     *
     * @param context
     * @param imagePath
     */
    public static void saveImageToLocal(final Context context, final String imagePath){

            Flowable.create(new FlowableOnSubscribe<File>() {
                @Override
                public void subscribe(FlowableEmitter<File> e) throws Exception {
                    e.onNext(GlideApp.with(context).asFile()
                            .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                            .load(imagePath)
                            .downloadOnly(200, 200/*Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL*/)
                            .get());
                    e.onComplete();
                }
            }, BackpressureStrategy.BUFFER)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            try {
                                //系统相册目录
//                            File appDir = new File(Environment.getExternalStorageDirectory()
//                                    + File.separator + Environment.DIRECTORY_DCIM
//                                    +File.separator+"Camera"+File.separator);
//                            if (!appDir.exists()) {
//                                appDir.mkdirs();
//                            }
                                File appDir = context.getFilesDir();
//                                File appDir = new File(context.getCacheDir(), "mediascratchspace");
//                            File appDir = context.getCacheDir();
//                                String fileName = System.currentTimeMillis() + ".jpg";
                                String fileName = imagePath.substring(imagePath.lastIndexOf("/")+1);
                                File destFile = new File(appDir, fileName);
                                if(destFile.exists()){
                                    destFile.delete();
                                }
//                                final Uri tmpUri = MediaScratchFileProvider.buildMediaScratchSpaceUri("gif");
//                                final File outputFile = MediaScratchFileProvider.getFileFromUri(tmpUri);
//                                final String outputFilePath = outputFile.getAbsolutePath();
////                                String path = tmpUri.getPath();
//                                LogUtil.i("Junwang", "outputFilePath="+outputFilePath);


                                //把gilde下载得到图片复制到定义好的目录中去
                                if (CopyFileUtils.copyFile(file, destFile)) {
                                    result = true;
                                    //插入刷新本地图库
//                                    ContentValues values = new ContentValues(2);
//                                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
//                                    values.put(MediaStore.Images.Media.DATA, destFile.getAbsolutePath());
//                                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                    //Looper.prepare();
                                    LogUtil.i("Junwang", destFile+" 保存成功");
                                    //Looper.loop();
                                } else {
                                    result = false;
                                    Looper.prepare();
                                    LogUtil.i("Junwang", "保存失败");
                                    Looper.loop();
                                }
                            } catch (Exception e) {
                                result = false;
                                Looper.prepare();
                                LogUtil.i("Junwang", "保存失败");
                                Looper.loop();
                            }
                        }
                    });
//            return result;
        }

    /**
     * 批量保存图片到本地
     *
     * @param context
     * @param imagePaths
     */
    public static void saveImagesToLocal(final Context context, final ArrayList<String> imagePaths){
        for(final String imagePath : imagePaths) {
            Flowable.create(new FlowableOnSubscribe<File>() {
                @Override
                public void subscribe(FlowableEmitter<File> e) throws Exception {
                    try {
                        /*for(String imagePath : imagePaths)*/
                        {
                            e.onNext(GlideApp.with(context).asFile()
                                    .diskCacheStrategy(DiskCacheStrategy.RESOURCE)
                                    .load(imagePath)
                                    .downloadOnly(200, 200/*Target.SIZE_ORIGINAL,Target.SIZE_ORIGINAL*/)
                                    .get());
                        }
                        e.onComplete();
                    } catch (Exception e1) {
                        e.onError(e1);
                    }
                }
            }, BackpressureStrategy.BUFFER)
                    .subscribeOn(io.reactivex.schedulers.Schedulers.io())
                    .observeOn(io.reactivex.schedulers.Schedulers.newThread())
                    .subscribe(new Consumer<File>() {
                        @Override
                        public void accept(File file) {
                            try {
                                //系统相册目录
//                            File appDir = new File(Environment.getExternalStorageDirectory()
//                                    + File.separator + Environment.DIRECTORY_DCIM
//                                    +File.separator+"Camera"+File.separator);
//                            if (!appDir.exists()) {
//                                appDir.mkdirs();
//                            }
                                File appDir = context.getFilesDir();
//                            File appDir = context.getCacheDir();
//                                String fileName = System.currentTimeMillis() + ".jpg";
                                String fileName = imagePath.substring(imagePath.lastIndexOf("/")+1);
                                //String filename = file.getName();
                                LogUtil.i("Junwang", "getFileName = " + file.getName());
                                File destFile = new File(appDir, fileName);
                                if(destFile.exists()){
                                    destFile.delete();
                                }
                                //把gilde下载得到图片复制到定义好的目录中去
                                if (CopyFileUtils.copyFile(file, destFile)) {
                                    //插入刷新本地图库
                                    ContentValues values = new ContentValues(2);
                                    values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
                                    values.put(MediaStore.Images.Media.DATA, destFile.getAbsolutePath());
                                    context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                                    Looper.prepare();
                                    LogUtil.i("Junwang", "保存成功");
                                    Looper.loop();
                                } else {
                                    Looper.prepare();
                                    LogUtil.i("Junwang", "保存失败");
                                    Looper.loop();
                                }
                            } catch (Exception e) {
                                Looper.prepare();
                                LogUtil.i("Junwang", "保存失败");
                                Looper.loop();
                            }
                        }
                    });
        }
    }
}
