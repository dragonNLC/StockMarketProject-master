package com.dragon.devl.stock_market;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class StockMarket {

    public static void main(String[] args) {
        Frame frame = new Frame("日常数据");
        frame.setLocation(1920 - 240, 1080 - 340);
        frame.setSize(240, 300);

        JLabel label = new JLabel("等待数据反馈...");
        label.setSize(240, 300);//000725


        JLabel label2 = new JLabel(String.valueOf(System.currentTimeMillis()));
        label2.setSize(40, 40);//000725

        label2.setVerticalAlignment(JLabel.BOTTOM);
        label2.setHorizontalAlignment(JLabel.CENTER);

        StockMarketThread mStockMarketThread = new StockMarketThread(label, label2);
        frame.addWindowListener(new WindowListener() {
            @Override
            public void windowOpened(WindowEvent e) {

            }

            @Override
            public void windowClosing(WindowEvent e) {
                mStockMarketThread.shutdown();
                Window window = e.getWindow();
                window.setVisible(false);
                window.dispose();
                System.exit(-1);
            }

            @Override
            public void windowClosed(WindowEvent e) {

            }

            @Override
            public void windowIconified(WindowEvent e) {

            }

            @Override
            public void windowDeiconified(WindowEvent e) {

            }

            @Override
            public void windowActivated(WindowEvent e) {

            }

            @Override
            public void windowDeactivated(WindowEvent e) {

            }
        });
        frame.add(label);
        frame.add(label2);
        frame.setVisible(true);
        frame.setAlwaysOnTop(true);
        mStockMarketThread.addRequest("http://hq.sinajs.cn/?list=s_sh000001");
        mStockMarketThread.addRequest("http://hq.sinajs.cn/?list=s_sz000725");
        mStockMarketThread.addRequest("http://hq.sinajs.cn/?list=s_sz002006");

        mStockMarketThread.start();
    }

    public static class StockMarketThread extends Thread {

        private List<String> requests;

        private JLabel mLabel;

        private JLabel mTimeLabel;

        private boolean mRunning;

        private HttpURLConnection mHuc;

        public StockMarketThread(JLabel label, JLabel timeLabel) {
            requests = new ArrayList<>();
            mRunning = true;
            this.mLabel = label;
            this.mTimeLabel = timeLabel;
        }

        @Override
        public void run() {
            super.run();
            while (mRunning) {
                if (requests.size() > 0) {
                    String content = "<html>";
                    String requestUrl = requests.get(0);
                    try {
                        mHuc = getConnection(requestUrl);
                        String result = doRequest(mHuc);
                        if (!"".equals(result)) {
                            String[] values = result.split("=");
                            if (values.length > 1) {
                                String value = values[1];
                                values = value.split(",");
                                if (values.length > 4) {
                                    result = "<p>";
                                    result += values[1] + "</p><p>" + values[2] + "</p><p>" + values[3] + "</p>";
                                }
                            }
                        }
                        content += result;

                        if (requests.size() > 1) {
                            requestUrl = requests.get(1);
                            mHuc = getConnection(requestUrl);
                            result = doRequest(mHuc);
                            if (!"".equals(result)) {
                                String[] values = result.split("=");
                                if (values.length > 1) {
                                    String value = values[1];
                                    values = value.split(",");
                                    if (values.length > 4) {
                                        result = "<p>";
                                        result += values[1] + "</p><p>" + values[2] + "</p><p>" + values[3] + "</p>";
                                    }
                                }
                            }
                        }
                        content += result;

                        if (requests.size() > 2) {
                            requestUrl = requests.get(2);
                            mHuc = getConnection(requestUrl);
                            result = doRequest(mHuc);
                            if (!"".equals(result)) {
                                String[] values = result.split("=");
                                if (values.length > 1) {
                                    String value = values[1];
                                    values = value.split(",");
                                    if (values.length > 4) {
                                        result = "<p>";
                                        result += values[1] + "</p><p>" + values[2] + "</p><p>" + values[3] + "</p>";
                                    }
                                }
                            }
                        }
                        content += result + "</html>";
                        mLabel.setVerticalAlignment(JLabel.CENTER);
                        mLabel.setHorizontalAlignment(JLabel.CENTER);
                        mLabel.setText(content);
                    } catch (Exception e) {
                        e.printStackTrace();
                    } finally {
                        mHuc = null;
                    }
                    mTimeLabel.setText(String.valueOf(System.currentTimeMillis()));
                    try {
                        Thread.sleep(3000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }

        public void addRequest(String requestUrl) {
            this.requests.add(requestUrl);
        }

        public void shutdown() {
            mRunning = false;
            if (mHuc != null) {
                mHuc.disconnect();
            }
        }

    }

    public static HttpURLConnection getConnection(String httpUrl) throws Exception {
        URL url = new URL(httpUrl);
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
        urlConnection.setRequestMethod("GET");
        urlConnection.addRequestProperty("Content-Type", "application/octet-stream");
        urlConnection.addRequestProperty("Connection", "Keep-Alive");
        urlConnection.setDoOutput(true);
        urlConnection.setDoInput(true);
        urlConnection.setConnectTimeout(2000);
        urlConnection.setReadTimeout(2000);
        urlConnection.connect();
        return urlConnection;
    }

    public static String doRequest(HttpURLConnection urlConnection) throws IOException {
        InputStream fis = null;
        StringBuilder sb = new StringBuilder();
        try {
            fis = urlConnection.getInputStream();
            byte[] buffer = new byte[1024];
            int length = 0;
            while ((length = fis.read(buffer)) > 0) {//读取请求数据
                sb.append(new String(buffer, 0, length));
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new IOException(e.getMessage());
        } finally {
            if (fis != null) {
                try {
                    fis.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return sb.toString();
    }

}
