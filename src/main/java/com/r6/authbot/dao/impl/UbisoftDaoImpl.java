package com.r6.authbot.dao.impl;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.r6.authbot.dao.iUbisoftDao;
import com.r6.authbot.domain.UbisoftProfile;
import com.r6.authbot.enums.APIConfig;

public class UbisoftDaoImpl implements iUbisoftDao {

    @Override
    public void createSession() {
        try {
            String targetUrl = String.format("%s/v3/profiles/sessions", APIConfig.BASE_UBISERVICES.get());
            URL requestUrl = new URL(targetUrl);
            String basicAuthValue = Base64.getEncoder()
                    .encodeToString((APIConfig.API_ACCOUNT_USERNAME.get() + ":" + APIConfig.API_ACCOUNT_PASSWORD.get())
                            .getBytes(StandardCharsets.UTF_8));

            HttpURLConnection http = (HttpURLConnection) requestUrl.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.setRequestProperty("Authorization", "Basic " + basicAuthValue);
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Ubi-AppId", APIConfig.UBISOFT_APPID.get());

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();

            String myResult = "";
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(myResult);

            APIConfig.API_SESSION_TICKET.set(jsonObject.get("ticket").toString());
            APIConfig.API_SESSION_ID.set(jsonObject.get("sessionId").toString());
        } catch (Exception ex) {
            APIConfig.API_SESSION_TICKET.set("");
            APIConfig.API_SESSION_ID.set("");
        }
    }

    @Override
    public String getUserId(String discordUid) {
        try {
            String targetUrl = String.format("%s/v3/profiles?idOnPlatform=%s&platformType=discord",
                    APIConfig.BASE_UBISERVICES.get(), discordUid);
            URL requestUrl = new URL(targetUrl);

            HttpURLConnection http = (HttpURLConnection) requestUrl.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("GET");
            http.setRequestProperty("Authorization", "Ubi_v1 t=" + APIConfig.API_SESSION_TICKET.get());
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Ubi-AppId", APIConfig.UBISOFT_APPID.get());

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();

            String myResult = "";
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(myResult);
            JSONArray profiles = (JSONArray) jsonObject.get("profiles");

            JSONObject userProfile = (JSONObject) profiles.get(0);
            return userProfile.get("userId").toString();
        } catch (Exception ex) {
            return "";
        }
    }

    @Override
    public UbisoftProfile getProfile(String userId) {
        try {
            String targetUrl = String.format("%s/v3/profiles/%s",
                    APIConfig.BASE_UBISERVICES.get(), userId);
            URL requestUrl = new URL(targetUrl);

            HttpURLConnection http = (HttpURLConnection) requestUrl.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("GET");
            http.setRequestProperty("Authorization", "Ubi_v1 t=" + APIConfig.API_SESSION_TICKET.get());
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Ubi-AppId", APIConfig.UBISOFT_APPID.get());

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();

            String myResult = "";
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(myResult);

            UbisoftProfile userProfile = new UbisoftProfile(
                    jsonObject.get("profileId").toString(),
                    jsonObject.get("userId").toString(),
                    jsonObject.get("platformType").toString(),
                    jsonObject.get("idOnPlatform").toString(),
                    jsonObject.get("nameOnPlatform").toString());
            return userProfile;
        } catch (Exception ex) {
            return null;
        }
    }

    @Override
    public Integer getUserMMR(String userId) {
        try {
            String targetUrl = String.format("%s/v2/spaces/0d2ae42d-4c27-4cb7-af6c-2099062302bb/title/r6s/skill/full_profiles?profile_ids=%s&platform_families=pc",
                    APIConfig.BASE_UBISERVICES.get(), userId);
            URL requestUrl = new URL(targetUrl);

            HttpURLConnection http = (HttpURLConnection) requestUrl.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("GET");
            http.setRequestProperty("Authorization", "Ubi_v1 t=" + APIConfig.API_SESSION_TICKET.get());
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Ubi-AppId", APIConfig.UBISOFT_APPID.get());
            http.setRequestProperty("Ubi-SessionId", APIConfig.API_SESSION_ID.get());

            InputStreamReader tmp = new InputStreamReader(http.getInputStream(), "UTF-8");
            BufferedReader reader = new BufferedReader(tmp);
            StringBuilder builder = new StringBuilder();

            String myResult = "";
            String str;
            while ((str = reader.readLine()) != null) {
                builder.append(str + "\n");
            }
            myResult = builder.toString();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(myResult);
            JSONArray platformFamilies = (JSONArray) jsonObject.get("platform_families_full_profiles");

            Integer userRank2MMR = 0;
            for (Object platformFamilyObj : platformFamilies) {
                JSONObject platformFamily = (JSONObject) platformFamilyObj;

                // board_ids_full_profiles 배열 가져오기
                JSONArray boardIds = (JSONArray) platformFamily.get("board_ids_full_profiles");

                // 배열 순회
                for (Object boardIdObj : boardIds) {
                    JSONObject boardId = (JSONObject) boardIdObj;

                    // board_id가 "ranked"인 경우 처리
                    if ("ranked".equals(boardId.get("board_id"))) {
                        JSONArray fullProfiles = (JSONArray) boardId.get("full_profiles");

                        JSONObject fullProfile = (JSONObject) fullProfiles.get(0);
                        JSONObject profile = (JSONObject) fullProfile.get("profile");
                        userRank2MMR = Integer.parseInt(profile.get("rank_points").toString());
                        break;
                    }
                }

                if (userRank2MMR != 0) {
                    break;
                }
            }
            return userRank2MMR;
        } catch (Exception ex) {
            return 0;
        }
    }

    @Override
    public Boolean isTicketValid() {
        try {
            String targetUrl = String.format("%s/v1/profiles/d9311b51-908f-433f-a581-64aebf6b07f1",
                    APIConfig.BASE_UBISERVICES.get());
            URL requestUrl = new URL(targetUrl);

            HttpURLConnection http = (HttpURLConnection) requestUrl.openConnection();
            http.setDefaultUseCaches(false);
            http.setDoInput(true);
            http.setDoOutput(true);
            http.setRequestMethod("GET");
            http.setRequestProperty("Authorization", "Ubi_v1 t=" + APIConfig.API_SESSION_TICKET.get());
            http.setRequestProperty("Content-Type", "application/json");
            http.setRequestProperty("Ubi-AppId", APIConfig.UBISOFT_APPID.get());

            if (http.getResponseCode() == 401) {
                return false;
            }
            return true;
        } catch (Exception ex) {
            return false;
        }
    }
}
