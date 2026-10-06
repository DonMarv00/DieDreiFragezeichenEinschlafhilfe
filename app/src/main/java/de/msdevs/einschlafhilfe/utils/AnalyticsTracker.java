package de.msdevs.einschlafhilfe.utils;


import java.io.IOException;
import java.nio.charset.StandardCharsets;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import android.util.Log;

public class AnalyticsTracker {
    private static final String APP_ID = "ddf_einschlafhilfe";
    private static final String SECRET = "5b95eaacd1fa7b15a00571c11dd3a330a41723cb54ef12134e3954a18a56e6d2";
    private static final String ENDPOINT = "https://api.citroncode.com/android/tracking/index.php";
    private static final OkHttpClient client = new OkHttpClient();

    public static void track() {
        new Thread(() -> {
            try {
                long timestamp = System.currentTimeMillis() / 1000;
                String dataToSign = APP_ID + ":" + timestamp;
                String signature = hmacSha256(dataToSign, SECRET);

                RequestBody body = new FormBody.Builder()
                        .add("app_id", APP_ID)
                        .add("timestamp", String.valueOf(timestamp))
                        .add("signature", signature)
                        .build();

                Request request = new Request.Builder()
                        .url(ENDPOINT)
                        .post(body)
                        .build();

                try (Response response = client.newCall(request).execute()) {
                    if (!response.isSuccessful()) {
                        String errorBody = response.body().string();
                        Log.e("AnalyticsTracker",
                                "Request failed: " + response.code() +
                                        " Body: " + errorBody);
                    }
                } catch (IOException e) {
                    Log.e("AnalyticsTracker", "Request error", e);
                }

            } catch (Exception e) {
                Log.e("AnalyticsTracker", "Error preparing analytics call", e);
            }
        }).start();
    }

    private static String hmacSha256(String data, String key) throws Exception {
        Mac mac = Mac.getInstance("HmacSHA256");
        mac.init(new SecretKeySpec(key.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
        byte[] bytes = mac.doFinal(data.getBytes(StandardCharsets.UTF_8));
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) sb.append(String.format("%02x", b));
        return sb.toString();
    }
}