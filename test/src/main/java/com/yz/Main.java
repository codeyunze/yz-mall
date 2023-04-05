package com.yz;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Base64;

/**
 * @Author yunze
 * @Date 2023/4/5 20:59
 * @Version 1.0
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println("Hello world!");
        /*int i;
        while ((i = NumberUtil.generateRandomNumber(-1, 10, 1)[0]) != -1) {
            System.out.println(i);
        }*/

        File file = new File("C:\\Users\\gaohan\\Pictures\\Camera Roll\\4k\\164274878312.jpg");
        FileInputStream fis = null;
        FileOutputStream fos = null;

        try {
            fis = new FileInputStream(file);

            // 最终结果容器
            byte[] img = new byte[(int) file.length()];

            fis.read(img);

            String str = Base64.getEncoder().encodeToString(img);
            System.out.println(str);

            byte[] newImg = Base64.getDecoder().decode(str);
            fos = new FileOutputStream("C:\\Users\\gaohan\\Pictures\\Camera Roll\\4k\\temp.jpg");
            fos.write(newImg);

        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            assert fis != null;
            fis.close();
            assert fos != null;
            fos.close();
        }

    }
}