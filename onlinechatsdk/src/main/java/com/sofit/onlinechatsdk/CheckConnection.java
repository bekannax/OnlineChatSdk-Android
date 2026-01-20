package com.sofit.onlinechatsdk;

import java.net.HttpURLConnection;
import java.net.URL;

public class CheckConnection {

//    private final ExecutorService executor;
//    private final AtomicBoolean isCancelled;
//    private Future<?> currentOperation;
    private Boolean needUseAlternativeUrl = null;

//    public CheckConnection() {
//        this.executor = Executors.newFixedThreadPool(2);
//        this.isCancelled = new AtomicBoolean(false);
//    }

    String getDomain() {
        if (isNeedUseAlternativeUrl()) {
            return "admin.verbox.me";
        }
        return "admin.verbox.ru";
    }

    // https://widget.apibcknd.com/comet/getClientUnreadMessageCount?orgId=593adecd804fc4e32e7e865d659f2356&siteId=747fo05s39hn1hwc0s0xdoacp5ei59ew&clientId=8f9565e45f8113418c44694dc06620f1
    // https://widget.site-chat.me/comet/getClientUnreadMessageCount?orgId=593adecd804fc4e32e7e865d659f2356&siteId=747fo05s39hn1hwc0s0xdoacp5ei59ew&clientId=8f9565e45f8113418c44694dc06620f1
    public String getApiDomain() {
        if (isNeedUseAlternativeUrl()) {
            return "widget.site-chat.me";
        }
        return "widget.apibcknd.com";
    }

    Boolean isNeedUseAlternativeUrl() {
        if (needUseAlternativeUrl != null) {
            return needUseAlternativeUrl;
        }

        String check = "https://operator.me-talk.ru/cabinet/assets/operatorApplication/checkConnection.json";
        String checkAlternative = "https://operator.site-chat.me/cabinet/assets/operatorApplication/checkConnection.json"; // operator.verbox.me

        String result = requestGet(check);
        if (result.isEmpty()) {
            needUseAlternativeUrl = false;
            return false;
        }
        result = requestGet(checkAlternative);
        if (result.isEmpty()) {
            needUseAlternativeUrl = true;
            return true;
        }
        return false;

//        String url = requestsAsync(
//            check,
//            checkAlternative
//        );
//        if (url.isEmpty()) {
//            return false;
//        } else if (url.equals(check)) {
//            needUseAlternativeUrl = false;
//            return false;
//        } else {
//            needUseAlternativeUrl = url.equals(checkAlternative);
//            return needUseAlternativeUrl;
//        }
    }

    private String requestGet(String sUrl) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(sUrl);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(3000);
            connection.setReadTimeout(3000);
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                return "";
            } else {
                return "HTTP error code: " + responseCode;
            }

        } catch (Exception e) {
            return "URL failed: " + e.getMessage();
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

//    private String requestsAsync(
//        String url1,
//        String url2
//    ) {
////        try {
////            Thread.sleep(5000);
////        } catch (InterruptedException e) {
////            throw new RuntimeException(e);
////        }
//
//        isCancelled.set(false);
//
//        CountDownLatch completionLatch = new CountDownLatch(1);
//        AtomicReference<String> result = new AtomicReference<>();
//
//        Future<?> future1 = executor.submit(() -> {
//            if (isCancelled.get()) return;
//            String requestGetResult = requestGet(url1);
//            if (!isCancelled.get() && completionLatch.getCount() > 0 && requestGetResult.isEmpty()) {
//                result.set(url1);
//                completionLatch.countDown();
//            }
//        });
//
//        Future<?> future2 = executor.submit(() -> {
//            if (isCancelled.get()) return;
//            String requestGetResult = requestGet(url2);
//            if (!isCancelled.get() && completionLatch.getCount() > 0 && requestGetResult.isEmpty()) {
//                result.set(url2);
//                completionLatch.countDown();
//            }
//        });
//
//        currentOperation = future1;
//
//        try {
//            boolean completed = completionLatch.await(3, TimeUnit.SECONDS);
//            if (isCancelled.get()) {
//                future1.cancel(true);
//                future2.cancel(true);
//                return "";
//            }
//            if (!completed) {
//                future1.cancel(true);
//                future2.cancel(true);
//                return "";
//            }
//            if (!future1.isDone()) future1.cancel(true);
//            if (!future2.isDone()) future2.cancel(true);
//            return result.get();
//
//        } catch (InterruptedException e) {
//            future1.cancel(true);
//            future2.cancel(true);
//            return "";
//        } finally {
//            currentOperation = null;
//        }
//    }

//    private void cancel() {
//        isCancelled.set(true);
//        if (currentOperation != null && !currentOperation.isDone()) {
//            currentOperation.cancel(true);
//        }
//    }
//
//    public boolean isRunning() {
//        return currentOperation != null && !currentOperation.isDone();
//    }

//    private String readStreamWithCancellation(InputStream inputStream) {
//        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
//        StringBuilder response = new StringBuilder();
//        String line;
//
//        while ((line = reader.readLine()) != null) {
//            // Проверяем отмену во время чтения
//            if (isCancelled.get()) {
//                reader.close();
//                throw new InterruptedException("Reading cancelled");
//            }
//            response.append(line);
//        }
//        reader.close();
//        return response.toString();
//    }

//    public void close() {
//        cancel();
//        executor.shutdown();
//    }
}