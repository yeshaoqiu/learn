package com.org.hm.graphic;

import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileOutputStream;

/**
 * 图形工具
 */
public class GraphicsUtil {
    private static final Color DEFAULT_BACKGROUND_COLOR = new Color(221, 230, 247);
    private static final Color DEFAULT_WORD_COLOR = new Color(86, 135, 238);
    private static final int DEFAULT_WORD_SIZE = 48;
    private static final int DEFAULT_WIDTH = 80;
    private static final int DEFAULT_HEIGHT = 80;

    /**
     * 快速创建头像
     * @param word
     * @return
     */
    public static File fastCreateAvatar(String word){
        return createImage(word, DEFAULT_WORD_SIZE, DEFAULT_WIDTH, DEFAULT_HEIGHT);
    }

    public static File createImage(String word, int wordSize, int width, int height){
        Font font = new Font("宋体", Font.PLAIN, wordSize);
        return createImage(word, wordSize, width, height, DEFAULT_BACKGROUND_COLOR, DEFAULT_WORD_COLOR, font);
    }

    /**
     * 创建图片文件
     * @param word 图片文字
     * @param wordSize 文字大小
     * @param width 高度
     * @param height 宽度
     * @param backgroundColor 背景颜色
     * @param wordColor 文字颜色
     * @param wordFont 文字字体
     * @return
     */
    public static File createImage(String word, int wordSize, int width, int height, Color backgroundColor, Color wordColor, Font wordFont){
        if (word == null || word.length() == 0 || wordSize <= 0 || width <= 0 || height <= 0) {
            return null;
        }
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        Graphics2D main = image.createGraphics();
        //背景颜色
        main.setBackground(backgroundColor);
        main.clearRect(0, 0, width, height);
        //文字颜色
        main.setColor(wordColor);
        main.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        // 计算文字长度，计算居中的x点坐标
        FontMetrics metrics = main.getFontMetrics(wordFont);
        // Determine the X coordinate for the text
        int x = (width - metrics.stringWidth(word)) / 2;
        // Determine the Y coordinate for the text (note we add the ascent, as in java 2d 0 is top of the screen)
        int y = ((height - metrics.getHeight()) / 2) + metrics.getAscent();
        main.setFont(wordFont);
        main.drawString(word, x, y);

        return createImageFile(word, image);
    }

    private static File createImageFile(String fileName, BufferedImage image) {
        if(image == null){ return null; }

        FileOutputStream fos = null;
        File tmpFile = null;
        try {
            File dir = new File("/export/tmp/");
            if (!dir.exists()) {
                dir.mkdirs();
            }

            tmpFile  = new File(String.format("%s%s.%s", "/export/tmp/", fileName, "png"));
            fos = new FileOutputStream(tmpFile);

            JPEGImageEncoder encoder = JPEGCodec.createJPEGEncoder(fos);
            encoder.encode(image);
            fos.flush();
            fos.close();
            return tmpFile;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally{
            closeStream(fos);
        }
    }

    private static void closeStream(Closeable... closeables) {
        if(closeables == null || closeables.length == 0){ return; }
        for(Closeable closeable : closeables){
            try{
                if(closeable == null){ continue; }

                closeable.close();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}
