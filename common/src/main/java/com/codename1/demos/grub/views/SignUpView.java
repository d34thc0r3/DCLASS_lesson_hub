/**
 * Licensed to Codename One LTD under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  Codename One licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.codename1.demos.grub.views;

import com.codename1.components.ScaleImageLabel;
import com.codename1.demos.grub.interfaces.Account;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.NetworkManager;
import com.codename1.ui.Dialog;


import static com.codename1.ui.CN.convertToPixels;
import static com.codename1.ui.util.Resources.getGlobalResources;

public class SignUpView extends AbstractEntityView {

    public SignUpView(Entity entity, Node applicationControllerViewNode, Node accountViewNode) {
        super(entity);
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setUIID("SignUpCnt");
        setScrollableY(true);
        Container wrapper = new Container(new BorderLayout());

        Label signUpHeader = new Label("계정 만들기", "SignUpHeader");
        Image grubLogo = getGlobalResources().getImage("grub-logo.png");
        ScaleImageLabel logoLabel = new ScaleImageLabel(grubLogo);
        Container signUpTopView = BoxLayout.encloseY(logoLabel, signUpHeader);
        signUpTopView.setUIID("SignUpTopView");
        wrapper.add(BorderLayout.NORTH, signUpTopView);


        TextField firstName = new TextField("", "성", 20, TextArea.ANY);
        firstName.setUIID("SignUpField");
        firstName.getHintLabel().setUIID("SignUpFieldHint");

        TextField lastName = new TextField("", "이름", 20, TextArea.ANY);
        lastName.setUIID("SignUpField");
        lastName.getHintLabel().setUIID("SignUpFieldHint");

        TextField emailAddress = new TextField("", "Email 입력", 20, TextArea.EMAILADDR);
        emailAddress.setUIID("SignUpField");
        emailAddress.getHintLabel().setUIID("SignUpFieldHint");

        TextField password = new TextField("", "비밀번호", 20, TextArea.PASSWORD);
        password.setUIID("SignUpField");
        password.getHintLabel().setUIID("SignUpFieldHint");

        TextField phoneNumber = new TextField("", "전화번호", 20, TextArea.PHONENUMBER);
        phoneNumber.setUIID("SignUpField");
        phoneNumber.getHintLabel().setUIID("SignUpFieldHint");
        wrapper.add(BorderLayout.CENTER, BoxLayout.encloseY(firstName, lastName, emailAddress, password, phoneNumber));

        Validator validator = new Validator();
        validator.addConstraint(firstName, new LengthConstraint(1));
        validator.addConstraint(lastName, new LengthConstraint(1));
        validator.addConstraint(emailAddress, RegexConstraint.validEmail());
        validator.addConstraint(password, new LengthConstraint(8));
        validator.addConstraint(phoneNumber, new LengthConstraint(10));

        Button confirmSignUp = new Button("회원가입", "SignUpConfirmButton");
        validator.addSubmitButtons(confirmSignUp);
        confirmSignUp.addActionListener(evt -> {
            evt.consume();
            entity.set(Account.firstName, firstName.getText());
            entity.set(Account.lastName, lastName.getText());
            entity.set(Account.emailAddress, emailAddress.getText());
            entity.set(Account.password, password.getText());
            entity.set(Account.mobileNumber, phoneNumber.getText());

            ActionNode action = applicationControllerViewNode.getInheritedAction(SignInView.COMPLETE_SIGNING_IN);
            if (action != null) {
                action.fireEvent(entity, SignUpView.this);
            }

            // 🔥 여기가 백엔드 연동하는 부분
            String json = "{"
                    + "\"firstName\":\"" + firstName.getText() + "\","
                    + "\"lastName\":\"" + lastName.getText() + "\","
                    + "\"email\":\"" + emailAddress.getText() + "\","
                    + "\"password\":\"" + password.getText() + "\","
                    + "\"phoneNumber\":\"" + phoneNumber.getText() + "\""
                    + "}";

            ConnectionRequest req = new ConnectionRequest();
            req.setUrl("http://172.30.1.32:8080/api/users"); // IP로 입력!
            req.setPost(true);
            req.setHttpMethod("POST");
            req.setContentType("application/json");
            req.setRequestBody(json);
            req.addRequestHeader("Accept", "application/json");

            req.addResponseListener(res -> {
                String response = new String(req.getResponseData());
                Dialog.show("알림", "회원가입 완료! 😊", "확인", null);
                // new SignInView().show(); // 회원가입 후 로그인 이동할 경우
            });

            NetworkManager.getInstance().addToQueue(req);
        });


        Label goToSignInLabel = new Label("계정이 이미 있어요", "SignUpLabel");
        Button goToSignInButton = new Button("로그인", "GoToSighInButton");

        goToSignInButton.addActionListener(evt->{
            evt.consume();
            ActionNode action = accountViewNode.getInheritedAction(AccountView.SIGN_IN);
            if (action != null) {
                action.fireEvent(entity, SignUpView.this);
            }
        });

        Label continueWith = new Label("-----------------------", "SignUpLabel");
        Button forgotPassword = new Button("비밀번호가 뭐더라..?", "SignUpLabel");

        Image facebookIconImage = getGlobalResources().getImage("facebook-icon.png").scaled(convertToPixels(5), convertToPixels(5));
        Image googleIconImage = getGlobalResources().getImage("google-icon.png").scaled(convertToPixels(5), convertToPixels(5));
        Image appleIconImage = getGlobalResources().getImage("apple-icon.png").scaled(convertToPixels(5), convertToPixels(5));

        Button faceBookIconButton = new Button(facebookIconImage);
        Button googleIconButton = new Button(googleIconImage);
        Button appleIconButton = new Button(appleIconImage);


        Container signUpOptionsCnt = BoxLayout.encloseY(confirmSignUp,
                FlowLayout.encloseCenter(goToSignInLabel, goToSignInButton),
                continueWith,
                FlowLayout.encloseCenter(faceBookIconButton, googleIconButton, appleIconButton),
                forgotPassword);
        signUpOptionsCnt.setUIID("SignUpOptionsCnt");

        wrapper.add(BorderLayout.SOUTH, signUpOptionsCnt);
        if (CN.isTablet()){
            setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
            add(BorderLayout.CENTER, wrapper);
        }else{
            add(wrapper);
        }
    }

    @Override
    public void update() {

    }

    @Override
    public void commit() {

    }

    @Override
    public Node getViewNode() {
        return null;
    }
}
