package com.codename1.demos.grub.controllers;

import com.codename1.components.FloatingActionButton;
import com.codename1.demos.grub.interfaces.CompletedOrder;
import com.codename1.demos.grub.interfaces.MainWindow;
import com.codename1.demos.grub.models.AccountModel;
import com.codename1.demos.grub.models.RestaurantModel;
import com.codename1.demos.grub.views.*;
import com.codename1.rad.controllers.Controller;
import com.codename1.rad.controllers.FormController;
import com.codename1.rad.models.Entity;
import com.codename1.rad.models.EntityList;
import com.codename1.rad.nodes.ActionNode;
import com.codename1.rad.nodes.Node;
import com.codename1.rad.nodes.ViewNode;
import com.codename1.rad.ui.UI;
import com.codename1.ui.FontImage;
import com.codename1.ui.Form;
import com.codename1.ui.layouts.BorderLayout;
import com.codename1.ui.plaf.Style;




public class MainWindowController extends FormController {

    MainWindowView mainWindowView;
    Form mainWindowForm;

    public static final ActionNode editProfile = UI.action();
    public static final ActionNode editAddresses = UI.action();
    public static final ActionNode editCreditCards = UI.action();
    public static final ActionNode logOut = UI.action();
    public static final ActionNode updateProfileView = UI.action();

    public static final ActionNode enterRest = UI.action();
    public static final ActionNode recommendedExplore = UI.action();
    public static final ActionNode popularExplore = UI.action();

    public static final ActionNode enterFilter = UI.action();
    public static final ActionNode enterSearch = UI.action();

    public static final ActionNode addFavorite = UI.action();
    public static final ActionNode removeFavorite = UI.action();

    public static final ActionNode addCompletedOrder = UI.action();
    public static final ActionNode orderAgain = UI.action();

    public MainWindowController(Controller parent, Entity appEntity, Node appNode) {
        super(parent);
        MainWindowController.appEntity = appEntity; // static 저장

        mainWindowForm = new Form(new BorderLayout());
        mainWindowForm.getToolbar().hideToolbar();
        Node profileNode = createProfileNode();
        Node homeNode = createHomeNode();

        mainWindowView = new MainWindowView(appEntity, profileNode, homeNode, appNode);
        mainWindowForm.add(BorderLayout.CENTER, mainWindowView);

        setView(mainWindowForm);

        // 글쓰기 플로팅 버튼 추가
        FloatingActionButton writePostBtn = FloatingActionButton.createFAB(FontImage.MATERIAL_ADD);
        writePostBtn.addActionListener(e -> {
            new WritePostController(this).show();
        });
        writePostBtn.getAllStyles().setBgColor(0xE0E0E0);
        writePostBtn.getAllStyles().setFgColor(0x000000);
        writePostBtn.getAllStyles().setMarginUnit(Style.UNIT_TYPE_DIPS);
        writePostBtn.getAllStyles().setMarginBottom(20); // 버튼 높이 조정
        writePostBtn.bindFabToContainer(mainWindowForm.getContentPane());

        addActionListener(editProfile, evt -> {
            evt.consume();
            Entity accountEntity = evt.getEntity();
            new EditProfileController(this, accountEntity, profileNode).getView().show();
        });

        addActionListener(editAddresses, evt -> {
            evt.consume();
            AccountModel accountEntity = (AccountModel)evt.getEntity();
            new EditAddressesController(this, accountEntity).getView().show();
        });

        addActionListener(editCreditCards, evt -> {
            evt.consume();
            AccountModel account = (AccountModel)evt.getEntity();
            new EditCreditCardsController(this, account).getView().show();
        });

        addActionListener(enterRest, evt -> {
            evt.consume();
            Entity restEntity = evt.getEntity();
            new RestaurantController(this, restEntity, appEntity.getEntity(MainWindow.profile), homeNode).getView().show();
        });

        addActionListener(enterFilter, evt -> {
            evt.consume();
            new FilterController(this, appEntity.getEntity(MainWindow.filter), homeNode).getView().show();
        });

        addActionListener(enterSearch, evt -> {
            evt.consume();
            new SearchController(this, appEntity, homeNode).getView().show();
        });

        /*
        addActionListener(enterSearch, evt -> {
            evt.consume();

            Entity filterEntity = (Entity) appEntity.get(MainWindow.filter);
            @SuppressWarnings("unchecked")
            java.util.List<Integer> categories = (java.util.List<Integer>) filterEntity.get(Filter.categories);
            int categoryIndex = categories.isEmpty() ? -1 : categories.get(0);

            String[] categoryNames = { "코딩", "운동", "음악", "요리", "입시", "디자인", "게임", "All" };
            String selectedCategory = (categoryIndex >= 0 && categoryIndex < categoryNames.length)
                    ? categoryNames[categoryIndex] : "";

            ViewNode node = new ViewNode();
            Form form = new Form(new BorderLayout());
            form.add(BorderLayout.CENTER, new HomeView(appEntity, node, selectedCategory));
            form.show();
        });
         */

        addActionListener(addFavorite, evt -> {
            evt.consume();
            if(appEntity.get(MainWindow.profile) instanceof AccountModel){
                ((AccountModel) appEntity.get(MainWindow.profile)).addFavorite(evt.getEntity());
            }
            mainWindowView.addFavorite(evt.getEntity());
        });

        addActionListener(removeFavorite, evt -> {
            evt.consume();
            if(appEntity.get(MainWindow.profile) instanceof AccountModel){
                ((AccountModel) appEntity.get(MainWindow.profile)).removeFavorite(evt.getEntity());
            }
            mainWindowView.removeFavorite(evt.getEntity());
        });

        addActionListener(addCompletedOrder, evt -> {
            evt.consume();
            Entity completedOrder = evt.getEntity();
            Entity rest = completedOrder.getEntity(CompletedOrder.restaurant);
            if (rest instanceof RestaurantModel){
                ((RestaurantModel) rest).clearOrder();
            }
            mainWindowView.addCompletedOrder(completedOrder);
            mainWindowView.moveToOrders();
            this.getView().showBack();
        });

        addActionListener(orderAgain, evt -> {
            evt.consume();
            Entity completedOrder = evt.getEntity();
            RestaurantModel rest = (RestaurantModel) completedOrder.getEntity(CompletedOrder.restaurant);
            if (completedOrder.get(CompletedOrder.order) instanceof EntityList){
                EntityList<Entity> dishesList = (EntityList<Entity>) completedOrder.get(CompletedOrder.order);
                for (Entity dish : dishesList){
                    rest.addToOrder(dish);
                }
            }
            new RestaurantController(this, rest, appEntity.getEntity(MainWindow.profile), homeNode).getView().show();
        });

    }

    private Node createProfileNode(){
        return new ViewNode(
                UI.actions(ProfileView.EDIT_PROFILE, editProfile),
                UI.actions(ProfileView.EDIT_ADDRESSES, editAddresses),
                UI.actions(ProfileView.EDIT_CREDIT_CARDS, editCreditCards),
                UI.actions(ProfileView.UPDATE_VIEW, logOut),
                UI.actions(ProfileView.LOG_OUT, updateProfileView)
        );
    }

    private Node createHomeNode(){
        return new ViewNode(
                UI.actions(HomeView.ENTER_REST, enterRest),
                UI.actions(HomeView.RECOMMENDED_EXPLORE, recommendedExplore),
                UI.actions(HomeView.POPULAR_EXPLORE, popularExplore),
                UI.actions(HomeView.ENTER_FILTER, enterFilter),
                UI.actions(HomeView.ENTER_SEARCH, enterSearch),
                UI.actions(RestaurantView.ADD_TO_FAVORITE, addFavorite),
                UI.actions(RestaurantView.REMOVE_FAVORITE, removeFavorite),
                UI.actions(OrderView.COMPLETE_ORDER, addCompletedOrder),
                UI.actions(CompletedOrderView.ORDER_AGAIN, orderAgain)
        );
    }

    public void updateDefaultAddressView(){
        mainWindowView.updateDefaultAddressView();
    }

    public Form getMainWindowForm() {
        return mainWindowForm;
    }

    public static EntityList<RestaurantModel> getRestaurantsList() {
        return (EntityList<RestaurantModel>) appEntity.get(MainWindow.restaurants);
    }

    private static Entity appEntity;

}