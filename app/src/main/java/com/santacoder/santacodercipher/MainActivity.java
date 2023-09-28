package com.santacoder.santacodercipher;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import com.santacoder.santacoder_cipher.SantaCoderCipher;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Generate Secret Token
        Map<String, Object> header = new HashMap<>();
        header.put("secret_key", "secret_data");
        JSONObject jsonObject = new JSONObject(header);
        String generatedToken = SantaCoderCipher.Generate(jsonObject.toString(), "santacoder", 60L);
        Log.e("checkData", "generatedToken: "+generatedToken);

        // Verify Secret Token
        try {
            Object verifyObj = SantaCoderCipher.Verify(generatedToken, "santacoder");
            JSONObject jObj = new JSONObject(verifyObj.toString());
            Log.e("checkData", jObj.getString("secret_key"));
        } catch (JSONException e) {
            throw new RuntimeException(e);
        }
    }

    public static String sendTokenData(Map<String, Object> header, long expireTime){
        JSONObject jsonData = new JSONObject(header);
        String jsonString = jsonData.toString();
        return SantaCoderCipher.Generate(jsonString, "santacoder", expireTime);
    }

    public static JSONObject getTokenData(String token, String objName) throws JSONException {
        Object userData = SantaCoderCipher.Verify(token, "santacoder");
        JSONObject obj = new JSONObject(userData.toString());
        return obj.getJSONObject(objName);
    }

}