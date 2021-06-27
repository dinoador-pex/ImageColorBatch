package com.pex;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.URL;
import java.net.URLConnection;
import java.util.*;
import java.util.List;

public class ColorFinder {

    // Credit: https://stackoverflow.com/questions/10530426/how-can-i-find-dominant-color-of-an-image
    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String[] find(String url) throws IOException {
        ImageInputStream is = convert(url);
        Iterator iter = ImageIO.getImageReaders(is);
        if (!iter.hasNext()) {
            return null;
        }
        ImageReader imageReader = (ImageReader)iter.next();
        imageReader.setInput(is);

        BufferedImage bufferedImage = imageReader.read(0);
        int height = bufferedImage.getHeight();
        int width = bufferedImage.getWidth();

        Map m = new HashMap();
        for(int i=0; i < width ; i++) {
            for(int j=0; j < height ; j++) {
                int rgb = bufferedImage.getRGB(i, j);
                int[] rgbArr = getRGBArr(rgb);
                // Filter out grays
                if (!isGray(rgbArr)) {
                    Integer counter = (Integer) m.get(rgb);
                    if (counter == null)
                        counter = 0;
                    counter++;
                    m.put(rgb, counter);
                }
            }
        }
        return getMostCommonColour(m);
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    public static String[] getMostCommonColour(Map map) {
        List list = new LinkedList(map.entrySet());
        list.sort((o1, o2) -> ((Comparable)((Map.Entry)(o1)).getValue())
            .compareTo(((Map.Entry)(o2)).getValue()));

        Map.Entry entryFirst = (Map.Entry)list.get(list.size() - 1);
        int[] rgbFirst = getRGBArr((Integer)entryFirst.getKey());

        Map.Entry entrySecond = (Map.Entry)list.get(list.size() - 2);
        int[] rgbSecond = getRGBArr((Integer)entrySecond.getKey());

        Map.Entry entryThird = (Map.Entry)list.get(list.size() - 3);
        int[] rgbThird = getRGBArr((Integer)entryThird.getKey());

        return new String[] {
            "#" + Integer.toHexString(rgbFirst[0]) + Integer.toHexString(rgbFirst[1]) +
                Integer.toHexString(rgbFirst[2]),
            "#" + Integer.toHexString(rgbSecond[0]) + Integer.toHexString(rgbSecond[1]) +
                Integer.toHexString(rgbSecond[2]),
            "#" + Integer.toHexString(rgbThird[0]) + Integer.toHexString(rgbThird[1]) +
                Integer.toHexString(rgbThird[2])
        };
    }

    public static int[] getRGBArr(int pixel) {
        //int alpha = (pixel >> 24) & 0xff;
        int red = (pixel >> 16) & 0xff;
        int green = (pixel >> 8) & 0xff;
        int blue = (pixel) & 0xff;
        return new int[]{red,green,blue};
    }

    public static boolean isGray(int[] rgbArr) {
        int rgDiff = rgbArr[0] - rgbArr[1];
        int rbDiff = rgbArr[0] - rgbArr[2];
        // Filter out black, white and grays (tolerance within 10 pixels)
        int tolerance = 10;
        if (rgDiff > tolerance || rgDiff < -tolerance) {
            if (rbDiff > tolerance || rbDiff < -tolerance) {
                return false;
            }
        }
        return true;
    }

    public static ImageInputStream convert(String url) throws IOException {
        InputStream input;
        URLConnection openConnection = new URL(url).openConnection();
        openConnection.addRequestProperty("User-Agent",
            "Mozilla/5.0 (Windows NT 6.3; Win64; x64) " +
            "AppleWebKit/537.36 (KHTML, like Gecko) " +
            "Chrome/61.0.3163.100 Safari/537.36");
        input = openConnection.getInputStream();
        BufferedInputStream in = new BufferedInputStream(input);
        return ImageIO.createImageInputStream(in);
    }
}
