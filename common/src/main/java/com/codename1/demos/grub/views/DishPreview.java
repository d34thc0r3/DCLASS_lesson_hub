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

import com.codename1.components.MultiButton;
import com.codename1.components.ScaleImageLabel;
import com.codename1.demos.grub.Util;
import com.codename1.demos.grub.interfaces.Dish;  // ✅ Dish 변수를 유지
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.Property;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.image.RoundRectImageRenderer;
import com.codename1.ui.Button;
import com.codename1.ui.EncodedImage;
import com.codename1.ui.Image;
import com.codename1.ui.layouts.BorderLayout;

import static com.codename1.ui.ComponentSelector.$;
import static com.codename1.ui.util.Resources.getGlobalResources;

public class DishPreview<T extends Entity> extends AbstractEntityView<T> {

    private Node viewNode;
    private Property nameProp, descriptionProp, pictureUrlProp, priceProp;

    public static final ActionNode.Category DISH_CLICKED = new ActionNode.Category();  // ✅ 유지

    private static EncodedImage placeHolder = EncodedImage.createFromImage(getGlobalResources().getImage("placeholder.png"), false);

    public DishPreview(T entity, Node viewNode) {
        super(entity);
        setUIID("LessonPreview");  // ✅ UI ID 변경 (Dish → Lesson)
        setLayout(new BorderLayout());

        this.viewNode = viewNode;
        nameProp = entity.findProperty(Dish.name);
        descriptionProp = entity.findProperty(Dish.description);
        pictureUrlProp = entity.findProperty(Dish.pictureUrl);
        priceProp = entity.findProperty(Dish.price);

        PropertySelector imagePropertySelector = new PropertySelector(entity, pictureUrlProp);
        RoundRectImageRenderer renderer = new RoundRectImageRenderer(300, 300, 2);

        Image lessonImage = renderer.createImage(imagePropertySelector);
        ScaleImageLabel lessonImageLabel = new ScaleImageLabel(lessonImage);

        Button lead = new Button();
        lead.setVisible(false);
        add(BorderLayout.SOUTH, lead);
        setLeadComponent(lead);

        MultiButton lessonPreview = new MultiButton("📚 " + entity.getText(nameProp));  // ✅ 레슨명 앞에 아이콘 추가
        lessonPreview.setTextLine2("📖 설명: " + entity.getText(descriptionProp));  // ✅ 설명 추가
        lessonPreview.setTextLine3("💰 수강료: " + Util.getPriceAsString(entity.getDouble(priceProp)));  // ✅ 수강료 변경
        lessonPreview.setUIID("LessonPreviewInfo");  // ✅ UI ID 변경
        lessonPreview.setUIIDLine1("LessonPreviewName");
        lessonPreview.setUIIDLine2("LessonPreviewDescription");
        lessonPreview.setUIIDLine3("LessonPreviewPrice");

        add(BorderLayout.NORTH, lessonImageLabel);
        add(BorderLayout.CENTER, lessonPreview);

        $(lead, lessonPreview).addActionListener(evt -> {
            evt.consume();
            ActionNode action = viewNode.getInheritedAction(DISH_CLICKED);  // ✅ 기능은 그대로 유지
            if (action != null) {
                action.fireEvent(entity, DishPreview.this);
            }
        });
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
