package com.example.account.dto;

import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkEvent;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Dialog;

import java.io.IOException;

public class ProfileUpdater {

    public void updateProfile(String phoneNumber, String firstName, String lastName, String password, String email) {
        String url = "http://172.30.1.32:8080/api/users/profile/" + phoneNumber;  // 전화번호 기반으로 URL 변경

        ConnectionRequest req = new ConnectionRequest();
        req.setUrl(url);
        req.setHttpMethod("PUT");
        req.setContentType("application/json");

        String jsonData = "{"
                + "\"firstName\":\"" + firstName + "\","
                + "\"lastName\":\"" + lastName + "\","
                + "\"email\":\"" + email + "\","
                + "\"password\":\"" + password + "\","
                + "\"phoneNumber\":\"" + phoneNumber + "\""
                + "}";

        req.setRequestBody(jsonData);

        // 리스너를 사용하여 응답 처리
        req.addResponseListener((NetworkEvent evt) -> {
            try {
                String response = new String(req.getResponseData(), "UTF-8");
                System.out.println("Response: " + response);
                if (req.getResponseCode() == 200) {
                    Dialog.show("성공", "프로필 수정 완료", "확인", null);
                } else {
                    Dialog.show("에러", "서버에서 오류가 발생했습니다.", "확인", null);
                }
            } catch (IOException e) {
                e.printStackTrace();
                Dialog.show("에러", "응답 처리 중 오류 발생", "확인", null);
            }
        });

        req.addResponseListener((NetworkEvent evt) -> {
            Dialog.show("에러", "서버 요청 실패: " + evt.getError(), "확인", null);
        });

        NetworkManager.getInstance().addToQueue(req);
    }
}
