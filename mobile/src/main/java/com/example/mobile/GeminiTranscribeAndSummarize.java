package com.example.mobile;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import com.google.gson.JsonObject;
import com.google.gson.JsonArray;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class GeminiTranscribeAndSummarize {
    public static void start(Context context, String recognizedText) {
        new GeminiSummarizeTask(context, recognizedText).execute();
    }

    private static class GeminiSummarizeTask extends AsyncTask<Void, Void, JsonObject> {
        private final Context context;
        private final String transcript;
        GeminiSummarizeTask(Context context, String transcript) {
            this.context = context;
            this.transcript = transcript;
        }
        @Override
        protected JsonObject doInBackground(Void... voids) {
            try {
                OkHttpClient client = new OkHttpClient();
                String prompt = "Extract the following from this meeting transcript: 1. Action Points 2. Minutes of Meeting 3. Title. Return a JSON object with keys: 'action_points', 'minutes', 'title', 'short_summary'. Transcript: " + transcript;
                JsonObject requestBodyJson = new JsonObject();
                requestBodyJson.addProperty("model", "gemini-1.5-flash-latest");
                JsonArray contents = new JsonArray();
                JsonObject content = new JsonObject();
                content.addProperty("role", "user");
                content.addProperty("parts", prompt);
                contents.add(content);
                requestBodyJson.add("contents", contents);
                RequestBody body = RequestBody.create(MediaType.parse("application/json"), requestBodyJson.toString());
                Request request = new Request.Builder()
                        .url("https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash-latest:generateContent?key=AIzaSyAHmf5T-CuTrlGdzXP4g7qKtnJzL68X7E4")
                        .post(body)
                        .build();
                try (Response response = client.newCall(request).execute()) {
                    if (response.isSuccessful() && response.body() != null) {
                        String resp = response.body().string();
                        return com.google.gson.JsonParser.parseString(resp).getAsJsonObject();
                    }
                }
            } catch (Exception e) {
                Log.e("GeminiSummarizeTask", "Error: ", e);
            }
            return null;
        }
        @Override
        protected void onPostExecute(JsonObject result) {
            String title = "";
            String summary = "";
            String actionPoints = "";
            String minutes = "";
            if (result != null && result.has("candidates")) {
                JsonObject candidate = result.getAsJsonArray("candidates").get(0).getAsJsonObject();
                if (candidate.has("content")) {
                    JsonObject content = candidate.getAsJsonObject("content");
                    if (content.has("parts")) {
                        String text = content.getAsJsonArray("parts").get(0).getAsString();
                        try {
                            JsonObject parsed = com.google.gson.JsonParser.parseString(text).getAsJsonObject();
                            title = parsed.has("title") ? parsed.get("title").getAsString() : "";
                            summary = parsed.has("short_summary") ? parsed.get("short_summary").getAsString() : "";
                            actionPoints = parsed.has("action_points") ? parsed.get("action_points").getAsString() : "";
                            minutes = parsed.has("minutes") ? parsed.get("minutes").getAsString() : "";
                        } catch (Exception e) {
                            summary = text;
                        }
                    }
                }
            }
            Intent intent = new Intent(context, EditSummaryActivity.class);
            intent.putExtra("title", title);
            intent.putExtra("summary", summary);
            intent.putExtra("action_points", actionPoints);
            intent.putExtra("minutes", minutes);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }
    }
}
