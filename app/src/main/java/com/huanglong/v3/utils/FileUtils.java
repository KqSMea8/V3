package com.huanglong.v3.utils;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Created by bin on 2018/1/11.
 * 文件工具类
 */

public class FileUtils {


    public static String appFiles = "v3";


    public static String rootFolderPath = Environment.getExternalStorageDirectory().getAbsolutePath();
    /**
     * 项目地址
     */
    public static String appPath = rootFolderPath + "/" + appFiles;


    /**
     * 奔溃日志文件名
     */
    public static String crashFileName = "crash.log";


    /**
     * write file
     *
     * @param fileName 路径
     * @param content  上下文
     * @param append   is append, if true, write to the end of file, else clear
     *                 content of file and write into it
     * @return return false if content is empty, true otherwise
     * @throws RuntimeException if an error occurs while operator FileWriter
     */
    public static boolean writeFile(String fileName, String content, boolean append) {

        if (TextUtils.isEmpty(content)) {
            return false;
        }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(fileName, append);
            fileWriter.write(content);
            return true;
        } catch (IOException e) {
            throw new RuntimeException("IOException occurred. ", e);
        } finally {
            IOUtils.close(fileWriter);
        }
    }

    /**
     * 判断文件夹是否存在
     *
     * @param folderName
     */
    public static void makeDir(String folderName) {

        String folderPath = rootFolderPath + "/" + folderName;

        if (TextUtils.isEmpty(folderPath)) {
            return;
        }
        File folder = new File(folderPath);
        if (!folder.exists()) {
            folder.mkdirs();
        }

    }

    /**
     * 创建文件夹
     *
     * @param filePath
     */
    public static void makeDirs2(String filePath) {
        if (TextUtils.isEmpty(filePath)) {
            return;
        }
        File folder = new File(filePath);
        if (!folder.exists()) {
            folder.mkdirs();
        }
    }


    /**
     * @param filePath 路径
     * @return 是否创建成功
     */
    public static boolean makeDirs(String filePath) {
        String folderName = getFolderName(filePath);
        if (TextUtils.isEmpty(folderName)) {
            return false;
        }
        File folder = new File(folderName);
        return (folder.exists() && folder.isDirectory()) ? true : folder.mkdirs();
    }


    /**
     * get folder name from path
     * <p>
     * <pre>
     *      getFolderName(null)               =   null
     *      getFolderName("")                 =   ""
     *      getFolderName("   ")              =   ""
     *      getFolderName("a.mp3")            =   ""
     *      getFolderName("a.b.rmvb")         =   ""
     *      getFolderName("abc")              =   ""
     *      getFolderName("c:\\")              =   "c:"
     *      getFolderName("c:\\a")             =   "c:"
     *      getFolderName("c:\\a.b")           =   "c:"
     *      getFolderName("c:a.txt\\a")        =   "c:a.txt"
     *      getFolderName("c:a\\b\\c\\d.txt")    =   "c:a\\b\\c"
     *      getFolderName("/home/admin")      =   "/home"
     *      getFolderName("/home/admin/a.txt/b.mp3")  =   "/home/admin/a.txt"
     * </pre>
     *
     * @param filePath 路径
     * @return file name from path, include suffix
     */
    public static String getFolderName(String filePath) {

        if (TextUtils.isEmpty(filePath)) {
            return filePath;
        }

        int filePosi = filePath.lastIndexOf(File.separator);
        return (filePosi == -1) ? "" : filePath.substring(0, filePosi);
    }


    /**
     * 获取文件名
     *
     * @param path 文件夹路径
     * @param name 文件名
     * @return
     */
    public static String getFilePath(String path, String name) {
        return path + "/" + name;
    }

    /**
     * 判断文件是否存在
     *
     * @param filePath
     * @return
     */
    public static boolean isFileExists(String filePath) {
        File file = new File(filePath);
        return file.exists();
    }

    /**
     * Uri转File
     *
     * @param context
     * @param uri
     * @return
     */
    public static File uri2File(Context context, Uri uri) {
        String img_path;
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor actualimagecursor = ((Activity) context).managedQuery(uri, proj, null, null, null);
        if (actualimagecursor == null) {
            img_path = uri.getPath();
        } else {
            int actual_image_column_index = actualimagecursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            actualimagecursor.moveToFirst();
            img_path = actualimagecursor
                    .getString(actual_image_column_index);
        }
        File file = new File(img_path);
        return file;
    }


}
