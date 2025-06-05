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
import com.codename1.demos.grub.interfaces.Address;
import com.codename1.demos.grub.interfaces.CompletedOrder;
import com.codename1.demos.grub.interfaces.Restaurant;
import com.codename1.demos.grub.models.RestaurantModel;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.PropertySelector;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.ui.AbstractEntityView;
import com.codename1.rad.ui.image.RoundRectImageRenderer;
import com.codename1.ui.*;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.ui.layouts.GridLayout;
import com.codename1.ui.plaf.UIManager;

import static com.codename1.ui.util.Resources.getGlobalResources;

public class CompletedOrderView extends AbstractEntityView {

    private static EncodedImage placeHolder = EncodedImage.createFromImage(getGlobalResources().getImage("placeholder.png"), false).
            scaledEncoded(100, 100);

    public static final ActionNode.Category ORDER_AGAIN = new ActionNode.Category();

    public CompletedOrderView(Entity order, Node viewNode) {
        super(order);
        setUIID("CompletedOrderView");
        setLayout(new BorderLayout());
        String instructorName = order.getEntity(CompletedOrder.restaurant).getText(Restaurant.name); // 강사 이름

        Label instructorLabel = new Label(instructorName, "CompletedOrderRestName");
        Label lessonIdLabel = new Label("레슨 ID: " + order.getInt(CompletedOrder.orderId), "CompletedOrderIdLabel");

        int lessonLocation = order.getInt(CompletedOrder.deliveredTo);
        String lessonLocationText = "개인";  // 기본값

        if (lessonLocation == Address.WORK){
            lessonLocationText = "학원";
        } else if(lessonLocation == Address.OTHER){
            lessonLocationText = "기타";
        }

        RestaurantModel instructor = (RestaurantModel) order.getEntity(CompletedOrder.restaurant);

        PropertySelector imagePropertySelector = new PropertySelector(instructor, Restaurant.icon);
        RoundRectImageRenderer renderer = new RoundRectImageRenderer(100, 100, 1);

        Image instructorImage = renderer.createImage(imagePropertySelector);
        ScaleImageLabel instructorIconLabel = new ScaleImageLabel(instructorImage);

        Label locationHeaderLabel = new Label("수강 장소:", "CompletedOrderDeliveredToHeaderLabel");
        Label locationLabel = new Label(lessonLocationText, "CompletedOrderDeliveredToLabel");

        Label dateHeaderLabel = new Label("수강 날짜:", "CompletedOrderDateHeaderLabel");
        Label dateLabel = new Label(order.getText(CompletedOrder.date), "CompletedOrderDateLabel");

        Button applyAgain = new Button("다시 신청하기", "CompletedOrderOrderAgainButton");
        Button writeReview = new Button("리뷰 작성하기", "CompletedOrderRateButton");

        applyAgain.addActionListener(evt -> {
            evt.consume();
            ActionNode action = viewNode.getInheritedAction(ORDER_AGAIN);
            if (action != null) {
                action.fireEvent(order, CompletedOrderView.this);
            }
        });

        Label completedIcon = new Label(FontImage.createMaterial(FontImage.MATERIAL_CHECK_CIRCLE, UIManager.getInstance().getComponentStyle("CompletedOrderIcon")));
        Container orderTopView = new Container(new BorderLayout());
        orderTopView.add(BorderLayout.NORTH, BorderLayout.centerEastWest(BoxLayout.encloseY(instructorLabel, lessonIdLabel), completedIcon, instructorIconLabel));
        orderTopView.add(BorderLayout.WEST, BoxLayout.encloseY(dateHeaderLabel, dateLabel));
        orderTopView.add(BorderLayout.EAST, BoxLayout.encloseY(locationHeaderLabel, locationLabel));
        add(BorderLayout.NORTH, orderTopView);
        add(BorderLayout.CENTER, GridLayout.encloseIn(2, applyAgain, writeReview));
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
