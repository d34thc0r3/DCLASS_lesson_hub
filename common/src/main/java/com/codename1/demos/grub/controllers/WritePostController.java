package com.codename1.demos.grub.controllers;

import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.FormController;
import com.codename1.ui.Form;
import com.codename1.ui.TextField;
import com.codename1.ui.TextArea;
import com.codename1.ui.ComboBox;
import com.codename1.ui.Button;
import com.codename1.ui.Dialog;
import com.codename1.ui.layouts.BoxLayout;
import com.codename1.demos.grub.models.*;
import com.codename1.demos.grub.interfaces.Restaurant;
import com.codename1.demos.grub.interfaces.MainWindow;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;

import java.util.ArrayList;
import java.util.List;

public class WritePostController extends FormController {

    private Form form;

    public WritePostController(Controller parent) {
        super(parent);
        form = new Form("레슨 등록", BoxLayout.y());

        TextField titleField = new TextField("", "제목 입력");
        TextArea contentArea = new TextArea("", 5, 20);
        TextField priceField = new TextField("", "가격 입력");
        ComboBox<String> categoryComboBox = new ComboBox<>("코딩", "운동", "음악", "요리", "입시", "디자인", "게임");

        Button submitButton = new Button("등록하기");

        form.addAll(titleField, contentArea, priceField, categoryComboBox, submitButton);

        submitButton.addActionListener(evt -> {
            String title = titleField.getText().trim();
            String content = contentArea.getText().trim();
            String category = categoryComboBox.getSelectedItem();
            String price = priceField.getText().trim();

            if (title.isEmpty() || content.isEmpty() || price.isEmpty()) {
                Dialog.show("오류", "모든 필드를 입력하세요.", "확인", null);
                return;
            }

            int deliveryTime;
            try {
                deliveryTime = Integer.parseInt(price);
            } catch (NumberFormatException e) {
                Dialog.show("오류", "가격은 숫자만 입력하세요.", "확인", null);
                return;
            }

            // 레슨 생성
            DishModel lessonDish = new DishModel(
                    title,
                    content,
                    "https://via.placeholder.com/150",
                    deliveryTime,
                    new ArrayList<>()
            );

            // ★ ArrayList로 dishes 준비
            List<Entity> dishes = new ArrayList<>();
            dishes.add(lessonDish);

            // 카테고리 생성
            FoodCategoryModel categoryModel = new FoodCategoryModel(category, dishes);

            // 메뉴 생성
            RestaurantModel.RestaurantMenu menu = new RestaurantModel.RestaurantMenu();
            menu.add(categoryModel);

            // 레스토랑 생성
            RestaurantModel newRestaurant = new RestaurantModel();
            newRestaurant.setEntityType(RestaurantModel.getRestaurantEntityType());
            newRestaurant.set(Restaurant.name, title);
            newRestaurant.set(Restaurant.description, content);
            newRestaurant.set(Restaurant.rating, 5.0);
            newRestaurant.set(Restaurant.deliveryFee, 0.0);
            newRestaurant.set(Restaurant.category, category);
            newRestaurant.set(Restaurant.deliveryTime, deliveryTime);
            newRestaurant.set(Restaurant.reviews, 1);
            newRestaurant.set(Restaurant.menu, menu);
            newRestaurant.set(Restaurant.picture, "https://via.placeholder.com/150");
            newRestaurant.set(Restaurant.icon, "https://via.placeholder.com/64");
            newRestaurant.set(Restaurant.restDiscount, 0.0); // 기본 할인율
            newRestaurant.set(Restaurant.distance, 0); // 기본 거리
            newRestaurant.set(Restaurant.order, new RestaurantModel.RestaurantOrder());


            MainWindowController.getRestaurantsList().add(newRestaurant);

            Dialog.show("등록 완료", "레슨이 성공적으로 등록되었습니다.", "확인", null);
            ((FormController)getParent()).getView().showBack();
        });

        setView(form);
    }
}
