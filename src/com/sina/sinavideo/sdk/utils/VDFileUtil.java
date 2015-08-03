
package com.sina.sinavideo.sdk.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.channels.FileChannel;

public class VDFileUtil {

    private static final int DEFAULT_BUFFER_SIZE = 8 * 1024;

    /**
     * 递归删除文件目录
     * 
     * @param dir 文件目录
     */
    public static void deleteFileDir(File dir) {
        try {
            if (dir.exists() && dir.isDirectory()) {// 判断是文件还是目录
                if (dir.listFiles().length == 0) {// 若目录下没有文件则直接删除
                    dir.delete();
                } else {// 若有则把文件放进数组，并判断是否有下级目录
                    File delFile[] = dir.listFiles();
                    int len = dir.listFiles().length;
                    for (int j = 0; j < len; j++) {
                        if (delFile[j].isDirectory()) {
                            deleteFileDir(delFile[j]);// 递归调用deleteFileDir方法并取得子目录路径
                        } else {
                            delFile[j].delete();// 删除文件
                        }
                    }
                    delFile = null;
                }
                deleteFileDir(dir);// 递归调用
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除单个文件
     * 
     * @param dir 文件目录
     */
    public static void deleteFile(File file) {
        try {
            if (file != null && file.isFile() && file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除文件
     * 
     * @param name
     */
    public static void deleteFile(String path) {
        try {
            File file = new File(path);
            if (file.exists()) {
                file.delete();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /** 检测父文件夹是否存在,若不存在创建父目录 **/
    public static boolean checkParentIsCreate(File file) {// 该方法是异步的
                                                          // cacheDir.mkdirs()的时候有可能是并发访问，所以return的时候要cacheDir.exists()
        File cacheDir = file.getParentFile();
        if (cacheDir == null || (!cacheDir.exists() && !cacheDir.mkdirs())) {
            return cacheDir.exists();
        }
        return true;
    }

    /**
     * 写字符串到文件，文件父目录如果不存在，会自动创建
     * 
     * @param file
     * @param content
     * @param isAppend
     * @return
     */
    public static boolean writeStringToFile(File file, String content, boolean isAppend) {
        boolean isWriteOk = false;
        char[] buffer = null;
        int count = 0;
        BufferedReader br = null;
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                createNewFileAndParentDir(file);
            }
            if (file.exists()) {
                br = new BufferedReader(new StringReader(content));
                bw = new BufferedWriter(new FileWriter(file, isAppend));
                buffer = new char[DEFAULT_BUFFER_SIZE];
                int len = 0;
                while ((len = br.read(buffer, 0, DEFAULT_BUFFER_SIZE)) != -1) {
                    bw.write(buffer, 0, len);
                    count += len;
                }
                bw.flush();
            }
            isWriteOk = content.length() == count;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                    bw = null;
                }
                if (br != null) {
                    br.close();
                    br = null;
                }
                buffer = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return isWriteOk;
    }

    /**
     * 创建文件及其父目录
     * 
     * @param file
     * @return
     */
    public static boolean createNewFileAndParentDir(File file) {
        boolean isCreateNewFileOk = true;
        isCreateNewFileOk = createParentDir(file);
        // 创建父目录失败，直接返回false，不再创建子文件
        if (isCreateNewFileOk) {
            if (!file.exists()) {
                try {
                    isCreateNewFileOk = file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                    isCreateNewFileOk = false;
                }
            }
        }
        return isCreateNewFileOk;
    }

    public static boolean createNewFileAndParentDir(String path) {
        return createNewFileAndParentDir(new File(path));
    }

    /**
     * 创建文件父目录
     * 
     * @param file
     * @return
     */
    public static boolean createParentDir(File file) {
        boolean isMkdirs = true;
        if (!file.exists()) {
            File dir = file.getParentFile();
            if (!dir.exists()) {
                isMkdirs = dir.mkdirs();
            }
        }
        return isMkdirs;
    }

    /**
     * 使用文件通道的方式复制文件
     * 
     * @param s 源文件
     * @param t 复制到的新文件
     */
    public static void fileChannelCopy(File s, File t) {
        FileInputStream fi = null;
        FileOutputStream fo = null;
        FileChannel in = null;
        FileChannel out = null;
        try {
            fi = new FileInputStream(s);
            fo = new FileOutputStream(t);
            in = fi.getChannel();// 得到对应的文件通道
            out = fo.getChannel();// 得到对应的文件通道
            in.transferTo(0, in.size(), out);// 连接两个通道，并且从in通道读取，然后写入out通道
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fi != null) {
                try {
                    fi.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fo != null) {
                try {
                    fo.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
    }
}
