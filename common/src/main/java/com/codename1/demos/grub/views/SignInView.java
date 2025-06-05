package com.codename1.demos.grub.views;

import com.codename1.components.ScaleImageLabel;
import com.codename1.demos.grub.interfaces.Account;
import com.codename1.io.CharArrayReader;
import com.codename1.io.ConnectionRequest;
import com.codename1.io.JSONParser;
import com.codename1.io.NetworkManager;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.ui.*;
import com.codename1.ui.events.ActionEvent;
import com.codename1.ui.events.ActionListener;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.FlowLayout;
import com.codename1.ui.validation.LengthConstraint;
import com.codename1.ui.validation.RegexConstraint;
import com.codename1.ui.validation.Validator;

import java.util.Map;

import static com.codename1.ui.CN.convertToPixels;
import static com.codename1.ui.util.Resources.getGlobalResources;

public class SignInView<T extends Entity> extends AbstractEntityView<T> {

    public static final ActionNode.Category COMPLETE_SIGNING_IN = new ActionNode.Category();

    public SignInView(T entity, Node applicationControllerViewNode, Node accountViewNode) {
        super(entity);
        setUIID("SignInCnt");

        Container wrapper = new Container(new BorderLayout());
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollVisible(false);
        setScrollableY(true);

        Label signInHeader = new Label("Welcome Back!", "SignInHeader");
        Image grubLogo = getGlobalResources().getImage("grub-logo.png");
        ScaleImageLabel logoLabel = new ScaleImageLabel(grubLogo);
        Container signInTopView = BoxLayout.encloseY(logoLabel, signInHeader);
        signInTopView.setUIID("SignInTopView");
        wrapper.add(BorderLayout.NORTH, signInTopView);

        TextField emailAddress = new TextField("", "Email 입력", 20, TextArea.EMAILADDR);
        emailAddress.setUIID("SignInField");
        emailAddress.getHintLabel().setUIID("SignInFieldHint");

        TextField password = new TextField("", "비밀번호", 20, TextArea.PASSWORD);
        password.setUIID("SignInField");
        password.getHintLabel().setUIID("SignInFieldHint");

        Validator validator = new Validator();
        validator.addConstraint(emailAddress, RegexConstraint.validEmail());
        validator.addConstraint(password, new LengthConstraint(8));

        wrapper.add(BorderLayout.CENTER, BoxLayout.encloseY(emailAddress, password));

        Button confirmSignIn = new Button("로그인", "SignInConfirmButton");
        validator.addSubmitButtons(confirmSignIn);

        confirmSignIn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                evt.consume();
                String email = emailAddress.getText();
                String pw = password.getText();
                String jsonBody = "{\"email\":\"" + email + "\", \"password\":\"" + pw + "\"}";

                ConnectionRequest req = new ConnectionRequest() {
                    protected void handleErrorResponseCode(int code, String message) {
                        Dialog.show("오류", "서버 오류: " + code, "확인", null);
                    }
                };

                req.setUrl("http://172.30.1.32:8080/api/users/login"); // IP입력!
                req.setPost(true);
                req.setContentType("application/json");
                req.setRequestBody(jsonBody);
                req.addRequestHeader("Accept", "application/json");

                req.addResponseListener(new ActionListener() {
                    public void actionPerformed(ActionEvent evt) {
                        try {
                            String response = new String(req.getResponseData(), "UTF-8");
                            System.out.println("로그인 응답: " + response);

                            if (response.contains("로그인 실패")) {
                                Dialog.show("로그인 실패", "이메일/비밀번호 확인", "확인", null);
                                return;
                            }

                            JSONParser parser = new JSONParser();
                            Map<String, Object> data = (Map<String, Object>) parser.parseJSON(
                                    new CharArrayReader(response.toCharArray())
                            );

                            String firstName = (String) data.get("firstName");
                            String lastName = (String) data.get("lastName");
                            String email = (String) data.get("email");

                            entity.set(Account.firstName, firstName);
                            entity.set(Account.lastName, lastName);
                            entity.set(Account.emailAddress, email);

                            ActionNode action = applicationControllerViewNode.getInheritedAction(COMPLETE_SIGNING_IN);
                            if (action != null) {
                                action.fireEvent(entity, SignInView.this);
                            }

                        } catch (Exception e) {
                            Dialog.show("에러", "응답 처리 중 오류 발생", "확인", null);
                        }
                    }
                });

                NetworkManager.getInstance().addToQueue(req);
            }
        });

        Label goToSignUpLabel = new Label("계정이 없으신가요?", "SignInLabel");
        Button goToSignUpButton = new Button("회원가입", "GoToSighUpButton");

        goToSignUpButton.addActionListener(evt->{
            evt.consume();
            ActionNode action = accountViewNode.getInheritedAction(AccountView.SIGN_UP);
            if (action != null) {
                action.fireEvent(entity, SignInView.this);
            }
        });

        Label continueWith = new Label("-----------------------", "SignInLabel");
        Button forgotPassword = new Button("비밀번호가 뭐더라..?", "SignInLabel");

        Image facebookIconImage = getGlobalResources().getImage("facebook-icon.png").scaled(convertToPixels(5), convertToPixels(5));
        Image googleIconImage = getGlobalResources().getImage("google-icon.png").scaled(convertToPixels(5), convertToPixels(5));
        Image appleIconImage = getGlobalResources().getImage("apple-icon.png").scaled(convertToPixels(5), convertToPixels(5));

        Button faceBookIconButton = new Button(facebookIconImage);
        Button googleIconButton = new Button(googleIconImage);
        Button appleIconButton = new Button(appleIconImage);

        Container signInOptionsCnt = BoxLayout.encloseY(confirmSignIn,
                FlowLayout.encloseCenter(goToSignUpLabel, goToSignUpButton),
                continueWith,
                FlowLayout.encloseCenter(faceBookIconButton, googleIconButton, appleIconButton),
                forgotPassword
        );
        signInOptionsCnt.setUIID("SignUpOptionsCnt");
        wrapper.add(BorderLayout.SOUTH, signInOptionsCnt);

        if (CN.isTablet()){
            setLayout(new BorderLayout(BorderLayout.CENTER_BEHAVIOR_CENTER_ABSOLUTE));
            add(BorderLayout.CENTER, wrapper);
        }else{
            add(wrapper);
        }
    }

    @Override
    public void update() {}

    @Override
    public void commit() {}

    @Override
    public Node getViewNode() {
        return null;
    }
}