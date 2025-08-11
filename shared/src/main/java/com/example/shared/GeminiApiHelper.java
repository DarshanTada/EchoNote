package com.example.shared;


import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class GeminiApiHelper {

    private static final String TAG = "GeminiApiHelper";

    // Your API Key
    private static final String API_KEY = "AIzaSyAHmf5T-CuTrlGdzXP4g7qKtnJzL68X7E4";

    // Example endpoint — replace with actual Gemini 2.0 API URL
    private static final String API_URL = "https://gemini.googleapis.com/v1/transcriptions";

    /**
     * Call Gemini API to transcribe audio.
     * @param audioBase64 Base64 encoded audio string.
     * @return Transcribed text or null.
     */
    public static String transcribeAudio(String audioBase64) {
        HttpURLConnection connection = null;
        try {
            URL url = new URL(API_URL + "?key=" + API_KEY);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setDoOutput(true);

            // Build JSON payload for transcription request (example format)
            String jsonInputString = "{"
                    + "\"audio\": {\"content\": \"" + audioBase64 + "\"},"
                    + "\"config\": {"
                    +     "\"languageCode\": \"en-US\""
                    + "}"
                    + "}";

            OutputStream os = connection.getOutputStream();
            os.write(jsonInputString.getBytes("UTF-8"));
            os.close();

            int responseCode = connection.getResponseCode();
            if (responseCode == 200) {
                BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = br.readLine()) != null) {
                    response.append(line);
                }
                br.close();

                // Parse response JSON to extract transcript (simple example)
                // You'll want to use a proper JSON parser like Gson or org.json
                String fullResponse = response.toString();
                Log.d(TAG, "Response: " + fullResponse);

                // TODO: Extract transcript text from JSON response here
                return fullResponse;

            } else {
                Log.e(TAG, "API call failed with response code " + responseCode);
            }
        } catch (Exception e) {
            Log.e(TAG, "Error calling Gemini API", e);
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
        return null;
    }
}
