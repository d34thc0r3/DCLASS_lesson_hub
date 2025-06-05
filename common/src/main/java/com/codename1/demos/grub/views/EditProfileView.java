package com.codename1.demos.grub.views;

import com.codename1.demos.grub.interfaces.Account;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkManager;
import com.codename1.rad.controllers.ActionSupport;
import com.codename1.rad.controllers.FormController;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;

import java.util.Map;

public class EditProfileView extends AbstractEntityView {
    protected Entity entity;
    private TextField firstName, lastName, emailAddress, phoneNumber, password;
    private Property firstNameProp, lastNameProp, emailProp, passwordProp, mobileNumberProp;
    private Node viewNode;

    public EditProfileView(Entity entity, Node viewNode) {
        super(entity);
        this.entity = entity;
        this.viewNode = viewNode;
        setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));

        Container wrapper = new Container(new BoxLayout(BoxLayout.Y_AXIS));
        wrapper.setScrollVisible(false);
        wrapper.setScrollableY(true);
        setUIID("EditProfile");

        // Property 정의
        firstNameProp = entity.findProperty(Account.firstName);
        lastNameProp = entity.findProperty(Account.lastName);
        emailProp = entity.findProperty(Account.emailAddress); // 이메일을 아직 사용하지만 나중에 제거
        passwordProp = entity.findProperty(Account.password);
        mobileNumberProp = entity.findProperty(Account.mobileNumber);

        // UI 구성
        Button backButton = new Button(FontImage.MATERIAL_KEYBOARD_ARROW_LEFT);
        backButton.setUIID("EditProfileBackButton");
        backButton.addActionListener(evt -> {
            evt.consume();
            ActionSupport.dispatchEvent(new FormController.FormBackEvent(backButton));
        });

        Label headerLabel = new Label("프로필 수정", "EditProfileHeaderLabel");
        Container headerCnt = BorderLayout.center(headerLabel).add(BorderLayout.WEST, backButton);
        headerCnt.setUIID("EditProfileHeaderCnt");
        add(BorderLayout.NORTH, headerCnt);

        // 텍스트 필드 추가
        firstName = new TextField("", "이름", 20, TextArea.ANY);
        firstName.setUIID("EditProfileField");
        lastName = new TextField("", "성", 20, TextArea.ANY);
        lastName.setUIID("EditProfileField");
        emailAddress = new TextField("", "이메일 주소", 20, TextArea.EMAILADDR);
        emailAddress.setUIID("EditProfileField");  // 이메일 필드는 숨기거나 제거할 수 있습니다.
        password = new TextField("", "비밀번호", 20, TextArea.PASSWORD);
        password.setUIID("EditProfileField");
        phoneNumber = new TextField("", "휴대폰 번호", 20, TextArea.PHONENUMBER);
        phoneNumber.setUIID("EditProfileField");

        // Entity 초기값
        firstName.setText((String) entity.get(firstNameProp));
        lastName.setText((String) entity.get(lastNameProp));
        emailAddress.setText((String) entity.get(emailProp));  // 이메일 초기값 설정
        password.setText((String) entity.get(passwordProp));
        phoneNumber.setText((String) entity.get(mobileNumberProp));

        Container textFields = BoxLayout.encloseY(firstName, lastName, emailAddress, password, phoneNumber);
        wrapper.add(textFields);

        // 유효성 검사
        Validator validator = new Validator();
        validator.addConstraint(firstName, new LengthConstraint(1));
        validator.addConstraint(lastName, new LengthConstraint(1));
        validator.addConstraint(emailAddress, RegexConstraint.validEmail());
        validator.addConstraint(password, new LengthConstraint(8));
        validator.addConstraint(phoneNumber, new LengthConstraint(10));

        // 저장 버튼
        Button saveButton = new Button("저장", "EditProfileSaveButton");
        validator.addSubmitButtons(saveButton);
        saveButton.addActionListener(evt -> {
            evt.consume();

            // entity 값 갱신
            entity.set(firstNameProp, firstName.getText());
            entity.set(lastNameProp, lastName.getText());
            entity.set(emailProp, emailAddress.getText());  // 이메일 업데이트 (나중에 삭제 예정)
            entity.set(passwordProp, password.getText());
            entity.set(mobileNumberProp, phoneNumber.getText());

            // 전화번호 확인
            String phone = phoneNumber.getText();
            if (phone == null || phone.isEmpty()) {
                Dialog.show("오류", "전화번호를 입력하세요", "확인", null);
                return;
            }

            // JSON 생성
            String json = "{"
                    + "\"firstName\":\"" + firstName.getText() + "\","
                    + "\"lastName\":\"" + lastName.getText() + "\","
                    + "\"email\":\"" + emailAddress.getText() + "\","
                    + "\"password\":\"" + password.getText() + "\","
                    + "\"phoneNumber\":\"" + phoneNumber.getText() + "\""
                    + "}";

            // 프로필 업데이트 요청 (전화번호로 변경)
            ConnectionRequest req = new ConnectionRequest();
            req.setUrl("http://172.30.1.32:8080/api/users/profile/" + phone);  // 전화번호 사용
            req.setHttpMethod("PUT");
            req.setContentType("application/json");
            req.setRequestBody(json);
            req.addRequestHeader("Accept", "application/json");

            req.addResponseListener(res -> {
                System.out.println("===> 서버 응답 코드: " + req.getResponseCode());
                System.out.println("===> 서버 응답 본문: " + new String(req.getResponseData()));
                if (req.getResponseCode() == 200) {
                    Dialog.show("성공", "프로필이 성공적으로 수정되었습니다!", "확인", null);
                    ActionSupport.dispatchEvent(new FormController.FormBackEvent(saveButton));
                } else {
                    Dialog.show("오류", "수정 실패: " + new String(req.getResponseData()), "확인", null);
                }
            });

            NetworkManager.getInstance().addToQueue(req);
        });

        wrapper.add(saveButton);
        add(BorderLayout.CENTER, wrapper);

        // 전화번호로 사용자 정보 불러오기
        String phone = (String) entity.get(Account.mobileNumber);
        System.out.println("===> EditProfileView 초기화: entity.get(Account.mobileNumber) = " + phone);
        if (phone != null) {
            getUserProfileFromServer(phone);
        } else {
            System.out.println("===> Entity에 전화번호 없음 - 사용자 정보 불러오기 불가");
        }
    }

    // 사용자 정보 불러오기 (전화번호 사용)
    public void getUserProfileFromServer(String phone) {
        ConnectionRequest req = new ConnectionRequest();
        req.setUrl("http://172.30.1.32:8080/api/users/profile/" + phone);  // 전화번호 사용
        req.setHttpMethod("GET");
        req.addRequestHeader("Accept", "application/json");

        req.addResponseListener(res -> {
            System.out.println("===> 사용자 정보 요청 응답 코드: " + req.getResponseCode());
            String jsonResponse = new String(req.getResponseData());
            System.out.println("===> 사용자 정보 응답 JSON: " + jsonResponse);

            if (req.getResponseCode() == 200) {
                parseUserProfile(jsonResponse);
            } else {
                Dialog.show("오류", "사용자 정보를 불러올 수 없습니다.", "확인", null);
            }
        });

        NetworkManager.getInstance().addToQueue(req);
    }

    private void parseUserProfile(String json) {
        try {
            JSONParser parser = new JSONParser();
            Map<String, Object> userMap = parser.parseJSON(new CharArrayReader(json.toCharArray()));
            System.out.println("===> JSON 파싱 결과: " + userMap);

            entity.set(Account.firstName, (String) userMap.get("firstName"));
            entity.set(Account.lastName, (String) userMap.get("lastName"));
            entity.set(Account.emailAddress, (String) userMap.get("email"));
            entity.set(Account.mobileNumber, (String) userMap.get("phoneNumber"));

            updateUIWithUserProfile();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateUIWithUserProfile() {
        System.out.println("===> updateUIWithUserProfile 호출됨");
        firstName.setText((String) entity.get(Account.firstName));
        lastName.setText((String) entity.get(Account.lastName));
        emailAddress.setText((String) entity.get(Account.emailAddress)); // 이메일을 보여줄 필요가 없다면 이 부분을 지울 수 있습니다.
        phoneNumber.setText((String) entity.get(Account.mobileNumber));
    }

    @Override
    public void update() {}

    @Override
    public void commit() {}

    @Override
    public Node getViewNode() {
        return viewNode;
    }
}
