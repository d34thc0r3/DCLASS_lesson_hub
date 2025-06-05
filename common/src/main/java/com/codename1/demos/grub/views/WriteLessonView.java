
package com.codename1.demos.grub.views;

import com.codename1.components.FloatingActionButton;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.rad.models.Entity;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractEntityView;

public class WriteLessonView extends AbstractEntityView {

    public WriteLessonView(Entity entity, Node viewNode) {
        super(entity);
        setLayout(new BoxLayout(BoxLayout.Y_AXIS));
        setScrollableY(true);

        com.codename1.ui.TextField titleField = new com.codename1.ui.TextField("", "제목");
        com.codename1.ui.TextArea descriptionArea = new com.codename1.ui.TextArea(5, 20);
        descriptionArea.setHint("레슨 내용");

        com.codename1.ui.Button submitButton = new com.codename1.ui.Button("작성 완료");
        submitButton.addActionListener(e -> {
            com.codename1.ui.Dialog.show("알림", "작성된 내용을 저장했습니다.", "확인", null);
        });

        addAll(titleField, descriptionArea, submitButton);
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
