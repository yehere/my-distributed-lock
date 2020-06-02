package com.test.lock.util;

import java.io.*;

/**
 * @Author heye
 * @Date 2020/01/04
 * @Decription
 */
public class FileUtils {


    public static String getScript(String fileName) {
//        String path = FileUtils.class.getClassLoader().getResource(fileName).getPath();
//        return readFileByLines(path);
        return readJar("/" + fileName);
    }

    public static String readJar(String filename) {
        InputStream is = null;
        BufferedReader br = null;
        try {
            is = FileUtils.class.getResourceAsStream(filename);
            br = new BufferedReader(new InputStreamReader(is, "utf-8"));
            String content = "", s = null;
            while ((s = br.readLine()) != null) {
//                System.out.println(s);
                content += s;
            }
            return content;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                if (null != br) {
                    br.close();
                }
                if (null != is) {
                    is.close();
                }
            } catch (Exception e) {
            }
        }
    }

    public static String readFileByLines(String fileName) {
        FileInputStream file = null;
        BufferedReader reader = null;
        InputStreamReader inputFileReader = null;
        String content = "";
        String tempString = null;
        try {
            file = new FileInputStream(fileName);
            inputFileReader = new InputStreamReader(file, "utf-8");
            reader = new BufferedReader(inputFileReader);
            // 一次读入一行，直到读入null为文件结束
            while ((tempString = reader.readLine()) != null) {
                content += tempString;
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e1) {
                }
            }
        }
        return content;
    }

    public static void main(String[] args) {
        String path = FileUtils.class.getClassLoader().getResource("unlock.lua").getPath();
        String script = FileUtils.readFileByLines(path);
        System.out.println(script);
    }
}
