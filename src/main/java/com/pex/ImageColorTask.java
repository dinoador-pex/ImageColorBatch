package com.pex;

import java.io.IOException;

public class ImageColorTask implements Runnable {

    private final String url;

    public ImageColorTask(String url) {
        this.url = url;
    }

    @Override
    public void run() {
        ImageColorBatch.taskStart();
        String[] csvLine = new String[3];
        try {
            csvLine = ColorFinder.find(this.url);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(csvLine != null) {
            ImageColorBatch.TOTAL_IMAGES_PROCESSED++;
        }
        csvLine = ImageColorBatch.add2BeginningOfArray(csvLine, url);
        ImageColorBatch.write(csvLine);
        ImageColorBatch.taskEnd();
    }
}
