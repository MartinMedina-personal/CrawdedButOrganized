import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.TableRow;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.File;
import java.time.LocalDate;
import java.util.*;

public class MainApp extends Application {

    private PantryManager pantry = new PantryManager();
    private GroceryQueue groceryQueue = new GroceryQueue();
    private List<Recipe> recipes = new ArrayList<>();
    private AIManager ai = new AIManager();

    private ProfileManager profileManager = new ProfileManager();
    private String currentProfile = null;

    private TableView<PantryItem> table;

    private ObservableList<PantryItem> data = FXCollections.observableArrayList();

    private StackPane screenContainer = new StackPane();

    private VBox mainMenuScreen;
    private VBox pantryScreen;
    private VBox groceryScreen;
    private VBox scanScreen;
    private VBox recipeScreen;

    private Label status = new Label("Ready");
    private TextArea recipeResults = new TextArea();
    private ListView<String> groceryListView = new ListView<>();

    private Button activeNavButton = null;

    private static final String BG = "#020617";

    private BorderPane root;
    private HBox nav;
    private VBox header;

   
    private StackPane wrap(Button b) {
        StackPane p = new StackPane(b);
        p.setPadding(new Insets(6));
        p.setStyle("-fx-background-color: transparent;");
        p.setUserData(b);

        return p;
    }


    public void start(Stage stage) {
    	


        // Sample recipes
        recipes.add(new Recipe("Spaghetti",
                Arrays.asList("pasta", "tomato sauce", "salt"), 30, 2));
        recipes.add(new Recipe("Omelette",
                Arrays.asList("eggs", "milk", "salt"), 10, 1));
        recipes.add(new Recipe("French Toast",
                Arrays.asList("bread", "eggs", "milk"), 15, 2));

        // Build screens + main menu
        buildScreens(stage);
        buildMainMenu(stage);

        screenContainer.getChildren().addAll(
                pantryScreen,
                groceryScreen,
                scanScreen,
                recipeScreen
        );

        // ============================
        //  HEADER
        // ============================
        Label title = new Label("CrowdedButOrganized");
        title.setStyle("""
            -fx-text-fill: #e5e7eb;
            -fx-font-size: 26px;
            -fx-font-weight: bold;
        """);

        Label subtitle = new Label("Smart Pantry, Grocery & Recipe Manager");
        subtitle.setStyle("""
            -fx-text-fill: #9ca3af;
            -fx-font-size: 13px;
        """);

        VBox headerText = new VBox(4, title, subtitle);
        headerText.setAlignment(Pos.CENTER_LEFT);

        status.setStyle("""
            -fx-text-fill: #a5b4fc;
            -fx-font-size: 12px;
        """);

        HBox headerBottom = new HBox(status);
        headerBottom.setAlignment(Pos.CENTER_RIGHT);

        header = new VBox(8, headerText, headerBottom);
        header.setPadding(new Insets(16, 24, 16, 24));
        header.setStyle("""
            -fx-background-color: linear-gradient(to right, #020617, #0f172a);
            -fx-border-color: #1f2937;
            -fx-border-width: 0 0 1 0;
        """);

     // ============================
    //  NAV
    // ============================
    nav = new HBox(28);
    nav.setAlignment(Pos.CENTER);
    nav.setPadding(new Insets(18, 24, 18, 24));
    nav.setSpacing(28);
    nav.setClip(null);
    nav.setPickOnBounds(false);
    nav.setFocusTraversable(false);

    nav.setStyle("""
        -fx-background-color: #020617, transparent, transparent;
        -fx-background-insets: 0, 0, 0;
        -fx-background-radius: 0;
        -fx-border-width: 0;
    """);

    // Create buttons
    Button home = iconButton("🗃️", "Pantry");
    Button grocery = iconButton("🛒", "Grocery");
    Button scan = iconButton("📷", "Scan Receipt");
    Button recipe = iconButton("🍽️", "Recipes");

    // Add wrapped buttons
    StackPane wHome = wrap(home);
    StackPane wGrocery = wrap(grocery);
    StackPane wScan = wrap(scan);
    StackPane wRecipe = wrap(recipe);

    nav.getChildren().addAll(wHome, wGrocery, wScan, wRecipe);

    // Actions
    home.setOnAction(e -> switchScreen(pantryScreen, home));
    grocery.setOnAction(e -> switchScreen(groceryScreen, grocery));
    scan.setOnAction(e -> switchScreen(scanScreen, scan));
    recipe.setOnAction(e -> switchScreen(recipeScreen, recipe));

    nav.setDisable(true);



        // ============================
        //  ROOT
        // ============================
        root = new BorderPane();
        root.setTop(header);
        root.setCenter(mainMenuScreen);
        root.setBottom(nav);
        root.setStyle("-fx-background-color: " + BG + ";");

        Scene scene = new Scene(root, 1100, 720);
        scene.getStylesheets().add(getClass().getResource("neon-table.css").toExternalForm());

        stage.setTitle("CrowdedButOrganized");
        stage.setScene(scene);
        stage.show();

    }


    // ============================
    //  MAIN MENU
    // ============================
    private void buildMainMenu(Stage stage) {

        Label title = new Label("Welcome to CrowdedButOrganized");
        title.setStyle("""
            -fx-text-fill: #e5e7eb;
            -fx-font-size: 28px;
            -fx-font-weight: bold;
        """);

        Label subtitle = new Label("Choose a profile to begin");
        subtitle.setStyle("""
            -fx-text-fill: #9ca3af;
            -fx-font-size: 14px;
        """);

        VBox titleBox = new VBox(6, title, subtitle);
        titleBox.setAlignment(Pos.CENTER);

        ComboBox<String> profileCombo = new ComboBox<>();
        profileCombo.setPromptText("Select a profile");

        profileCombo.setCellFactory(list -> new ListCell<>() {
            
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label icon = new Label("👤");
                    icon.setStyle("-fx-font-size: 18px; -fx-text-fill: #22d3ee;");

                    Label name = new Label(item);
                    name.setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 14px;");

                    HBox box = new HBox(8, icon, name);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        profileCombo.setButtonCell(new ListCell<>() {
            
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setGraphic(null);
                } else {
                    Label icon = new Label("👤");
                    icon.setStyle("-fx-font-size: 18px; -fx-text-fill: #22d3ee;");
                    Label name = new Label(item);
                    name.setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 14px;");
                    HBox box = new HBox(8, icon, name);
                    box.setAlignment(Pos.CENTER_LEFT);
                    setGraphic(box);
                }
            }
        });

        profileCombo.getItems().setAll(profileManager.listProfiles());

        TextField newProfileField = input("New profile name");

        Button createProfileBtn = primaryButton("Create Profile");
        Button loadProfileBtn = primaryButton("Load Profile");
        Button deleteProfileBtn = secondaryButton("Delete Profile");
        Button continueBtn = primaryButton("▶ Continue Last Profile");

        String last = profileManager.getLastProfile();
        continueBtn.setVisible(last != null);

        continueBtn.setOnAction(e -> {
            if (last != null) {
                loadProfileAndEnterApp(last);
                status.setText("Loaded last profile ✔");
            }
        });

        createProfileBtn.setOnAction(e -> {
            String name = newProfileField.getText().trim();
            if (name.isEmpty()) {
                status.setText("Profile name cannot be empty");
                return;
            }
            try {
                if (profileManager.profileExists(name)) {
                    status.setText("Profile already exists");
                    return;
                }
                profileManager.createProfile(name);
                profileCombo.getItems().setAll(profileManager.listProfiles());
                profileCombo.getSelectionModel().select(name);
                loadProfileAndEnterApp(name);
                status.setText("Profile created ✔");
            } catch (Exception ex) {
                ex.printStackTrace();
                status.setText("Error creating profile");
            }
        });

        loadProfileBtn.setOnAction(e -> {
            String selected = profileCombo.getSelectionModel().getSelectedItem();
            if (selected == null) {
                status.setText("Select a profile first");
                return;
            }
            loadProfileAndEnterApp(selected);
            status.setText("Profile loaded ✔");
        });

        deleteProfileBtn.setOnAction(e -> {
            String selected = profileCombo.getSelectionModel().getSelectedItem();
            if (selected == null) {
                status.setText("Select a profile to delete");
                return;
            }
            profileManager.deleteProfile(selected);
            profileCombo.getItems().setAll(profileManager.listProfiles());
            status.setText("Profile deleted ✔");
        });

        VBox form = new VBox(14,
                labelTitle("Existing Profiles"),
                profileCombo,
                continueBtn,
                labelTitle("Create New Profile"),
                newProfileField,
                createProfileBtn,
                loadProfileBtn,
                deleteProfileBtn
        );

        form.setAlignment(Pos.CENTER_LEFT);

        VBox card = card(form);

        VBox layout = new VBox(30, titleBox, card);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(40, 24, 40, 24));

        FadeTransition fade = new FadeTransition(Duration.millis(450), layout);
        fade.setFromValue(0);
        fade.setToValue(1);
        fade.play();

        mainMenuScreen = layout;
    }

    private void loadProfileAndEnterApp(String profileName) {
        this.currentProfile = profileName;

        pantry.clear();
        groceryQueue.clear();

        profileManager.loadProfile(profileName, pantry, groceryQueue);

        refreshTable();
        refreshGroceryList();

        nav.setDisable(false);
        StackPane wrapper = (StackPane) nav.getChildren().get(0);
        Button firstButton = (Button) wrapper.getUserData();
        setActiveNavButton(firstButton);
        root.setCenter(screenContainer);
        showScreen(pantryScreen);
        showDashboardStats();
    }

    private void showDashboardStats() {

        int totalItems = pantry.getAllItems().size();
        int groceryCount = groceryQueue.viewListAsList().size();

        long expiringSoon = pantry.getAllItems().stream()
                .filter(item -> {
                    try {
                        LocalDate exp = LocalDate.parse(item.getExpirationDate());
                        return exp.isBefore(LocalDate.now().plusDays(7));
                    } catch (Exception e) {
                        return false;
                    }
                })
                .count();

        Label statsTitle = labelTitle("Profile Overview");

        Label l1 = new Label("Total Pantry Items: " + totalItems);
        Label l2 = new Label("Expiring Soon (7 days): " + expiringSoon);
        Label l3 = new Label("Grocery Items: " + groceryCount);

        for (Label l : List.of(l1, l2, l3)) {
            l.setStyle("-fx-text-fill: #e5e7eb; -fx-font-size: 13px;");
        }

        VBox statsBox = new VBox(6, statsTitle, l1, l2, l3);
        statsBox.setAlignment(Pos.CENTER_LEFT);

        VBox statsCard = card(statsBox);

        VBox top = new VBox(header, statsCard);
        root.setTop(top);
    }


    // ============================
    //  PAGE WRAPPER
    // ============================
    private VBox page(Node content) {
        VBox box = new VBox(content);
        box.setPadding(new Insets(18, 24, 18, 24));
        box.setAlignment(Pos.TOP_CENTER);
        box.setStyle("-fx-background-color: #020617;");
        return box;
    }

    // ============================
    //  BUILD SCREENS
    // ============================
    private void buildScreens(Stage stage) {

    	// ============================================================
    //  CREATE TABLE
    // ============================================================
    table = new TableView<>();
    table.setItems(data);
    table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

    // ============================================================
    //  CREATE & STYLE COLUMNS
    // ============================================================
    table.getColumns().clear();

    TableColumn<PantryItem, String> nameCol = new TableColumn<>("Item");
    nameCol.setCellValueFactory(new PropertyValueFactory<>("name"));

    TableColumn<PantryItem, Integer> qtyCol = new TableColumn<>("Qty");
    qtyCol.setCellValueFactory(new PropertyValueFactory<>("quantity"));

    TableColumn<PantryItem, String> expCol = new TableColumn<>("Expiration");
    expCol.setCellValueFactory(new PropertyValueFactory<>("expirationDate"));

    TableColumn<PantryItem, String> catCol = new TableColumn<>("Category");
    catCol.setCellValueFactory(new PropertyValueFactory<>("category"));

    // Apply styling
    styleTableColumn(nameCol);
    styleTableColumn(qtyCol);
    styleTableColumn(expCol);
    styleCategoryColumn(catCol);
    styleTableRows();

    table.getColumns().addAll(nameCol, qtyCol, expCol, catCol);

    // ============================================================
    //  APPLY FULL NEON TABLE THEME
    // ============================================================
    table.setStyle("""
    	    -fx-background-color: transparent;
    	    -fx-padding: 0;
    	""");



        // ============================================================
        //  PANTRY SCREEN
        // ============================================================
        TextField name = input("Item name");
        TextField qty = input("Quantity");
        TextField exp = input("Expiration (e.g. 2025-01-01)");
        TextField cat = input("Category");

        Button add = primaryButton("Add to Pantry");

        add.setOnAction(e -> {
            try {
                int q = Integer.parseInt(qty.getText().trim());
                pantry.addItem(name.getText(), q, exp.getText(), cat.getText());

                name.clear();
                qty.clear();
                exp.clear();
                cat.clear();

                refreshTable();
                status.setText("Item added to pantry ✔");
                saveCurrentProfile();
            } catch (NumberFormatException ex) {
                status.setText("Quantity must be a number");
            }
        });

        VBox form = card(
                labelTitle("Add Pantry Item"),
                name, qty, exp, cat, add
        );
        form.setPrefWidth(350);

        
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

     // ============================================================
    //  HYBRID CARD LAYOUT (HEADER CARD + FLOATING TABLE)
    // ============================================================

    
    VBox tableHeaderCard = card(labelTitle("Pantry Items"));
    tableHeaderCard.setPrefWidth(650);

    
    VBox tableContainer = new VBox(8, tableHeaderCard, table);
    tableContainer.setAlignment(Pos.TOP_CENTER);
    tableContainer.setStyle("-fx-background-color: transparent;");
    tableContainer.setPrefWidth(650);

    
    tableContainer.setPickOnBounds(false);

    
    HBox pantryLayout = new HBox(20, form, tableContainer);
    pantryLayout.setAlignment(Pos.TOP_CENTER);
    pantryLayout.setPadding(new Insets(10, 10, 10, 10));

    pantryScreen = page(pantryLayout);


        // ============================================================
        //  GROCERY SCREEN
        // ============================================================
        TextField groceryInput = input("Add grocery item");
        Button addGrocery = primaryButton("Add to Grocery List");

        addGrocery.setOnAction(e -> {
            String item = groceryInput.getText().trim();
            if (!item.isEmpty()) {
                groceryQueue.addItem(item);
                groceryInput.clear();
                refreshGroceryList();
                status.setText("Added to grocery list ✔");
                saveCurrentProfile();
            }
        });

        Button markPurchased = secondaryButton("Mark Next as Purchased");

        markPurchased.setOnAction(e -> {
            String purchased = groceryQueue.pollItem();

            if (purchased != null) {
                status.setText("Purchased: " + purchased);
                refreshGroceryList();
                saveCurrentProfile();
            } else {
                status.setText("No items to purchase");
            }
        });

        groceryListView.setPrefHeight(300);
        groceryListView.setStyle("""
            -fx-background-color: transparent;
            -fx-control-inner-background: #020617;
            -fx-text-fill: #e5e7eb;
        """);

        VBox groceryControls = new VBox(10, groceryInput, addGrocery, markPurchased);

        VBox groceryLayout = new VBox(
                16,
                card(labelTitle("Grocery Controls"), groceryControls),
                card(labelTitle("Grocery Queue"), groceryListView)
        );

        groceryScreen = page(groceryLayout);

        // ============================================================
        //  SCAN SCREEN
        // ============================================================
        scanScreen = page(scanSection(stage));

        // ============================================================
        //  RECIPE SCREEN
        // ============================================================
        recipeScreen = page(recipeSection());
    }



    private VBox scanSection(Stage stage) {

        Button scanImage = primaryButton("Scan Receipt Image");
        Button scanCamera = secondaryButton("Use Camera (Placeholder)");

        Label hint = new Label("Scan a receipt image to auto-add items to your pantry.");
        hint.setStyle("-fx-text-fill: #9ca3af; -fx-font-size: 12px;");

        scanImage.setOnAction(e -> {
            FileChooser fc = new FileChooser();
            fc.setTitle("Select Receipt Image");

            fc.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(
                            "Image Files",
                            "*.png", "*.jpg", "*.jpeg", "*.bmp"
                    )
            );

            File file = fc.showOpenDialog(stage);

            if (file != null) {
                ScannerData scanData = OCRScanner.scan(file);

                List<String> extractedItems =
                        ai.analyzeReceiptImage(file, scanData.getRawText());

                ai.mergeScannedItemsIntoPantry(extractedItems, pantry);

                refreshTable();
                status.setText("Receipt scanned and items merged ✔");
                saveCurrentProfile();
            }
        });

        scanCamera.setOnAction(e ->
                status.setText("Camera capture not yet implemented (placeholder only)")
        );

        VBox buttons = new VBox(10, scanImage, scanCamera, hint);
        buttons.setAlignment(Pos.CENTER_LEFT);

        return card(labelTitle("Receipt Scanner (AI-Ready)"), buttons);
    }

    private VBox recipeSection() {

        Button find = primaryButton("Find Matching Recipes");
        Button sortByTime = secondaryButton("Sort by Prep Time");

        recipeResults.setEditable(false);
        recipeResults.setWrapText(true);
        recipeResults.setPrefHeight(320);

        recipeResults.setStyle("""
            -fx-control-inner-background: #020617;
            -fx-text-fill: #e5e7eb;
            -fx-highlight-fill: #22d3ee;
        """);

        find.setOnAction(e -> {

            StringBuilder result = new StringBuilder();

            for (Recipe r : recipes) {

                int match = 0;

                for (String ing : r.getIngredients()) {
                    if (pantry.hasIngredient(ing)) {
                        match++;
                    }
                }

                if (match == r.getIngredients().size()) {
                    result.append("✔ FULL MATCH: ")
                            .append(r.getName())
                            .append("\n");

                } else if (match > 0) {

                    result.append("➤ PARTIAL MATCH: ")
                            .append(r.getName())
                            .append(" (")
                            .append(match)
                            .append("/")
                            .append(r.getIngredients().size())
                            .append(" ingredients)\n");
                }
            }

            if (result.length() == 0) {
                result.append("No matching recipes found with current pantry items.");
            }

            recipeResults.setText(result.toString());
            status.setText("Recipe scan complete");
        });

        sortByTime.setOnAction(e -> {

            RecipeRecommender.sortByPrepTime(recipes);

            StringBuilder sb =
                    new StringBuilder("Recipes sorted by prep time:\n\n");

            for (Recipe r : recipes) {
                sb.append(r.toString()).append("\n");
            }

            recipeResults.setText(sb.toString());
            status.setText("Recipes sorted by prep time");
        });

        VBox buttons = new VBox(10, find, sortByTime);

        return new VBox(
                16,
                card(labelTitle("Recipe Tools"), buttons),
                card(labelTitle("Recipe Results"), recipeResults)
        );
    }

    private void switchScreen(VBox screen, Button navButton) {

        setActiveNavButton(navButton);

        FadeTransition fade =
                new FadeTransition(Duration.millis(140), screenContainer);

        fade.setFromValue(1);
        fade.setToValue(0.25);

        fade.setOnFinished(e -> {

            showScreen(screen);

            FadeTransition fadeIn =
                    new FadeTransition(Duration.millis(190), screenContainer);

            fadeIn.setFromValue(0.25);
            fadeIn.setToValue(1);
            fadeIn.play();
        });

        fade.play();
    }

    private void setActiveNavButton(Button b) {

        
        if (activeNavButton != null) {
            activeNavButton.setStyle("""
                -fx-background-color: transparent, transparent, transparent;
                -fx-background-insets: 0, 0, 0;
                -fx-background-radius: 16;
                -fx-border-color: transparent;
                -fx-border-radius: 16;
                -fx-border-width: 0;
                -fx-padding: 10 18 10 18;
                -fx-focus-color: transparent;
                -fx-faint-focus-color: transparent;
            """);
        }

        
        b.setStyle("""
            -fx-background-color: #1e293b, #1e293b, #1e293b;
            -fx-background-insets: 0, 0, 0;
            -fx-background-radius: 16;
            -fx-border-color: #22d3ee;
            -fx-border-radius: 16;
            -fx-border-width: 1;
            -fx-effect: dropshadow(gaussian, #22d3ee55, 18, 0.4, 0, 0);
            -fx-padding: 10 18 10 18;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """);

        activeNavButton = b;
    }




    private void showScreen(VBox screen) {
        pantryScreen.setVisible(false);
        groceryScreen.setVisible(false);
        scanScreen.setVisible(false);
        recipeScreen.setVisible(false);

        screen.setVisible(true);
    }

    private VBox card(Node... nodes) {

        VBox inner = new VBox(10, nodes);
        inner.setPadding(new Insets(14));

        inner.setStyle("""
            -fx-background-color: transparent;
            -fx-background-radius: 18;
        """);

        VBox outer = new VBox(inner);
        outer.setPadding(new Insets(4));

        outer.setStyle("""
            -fx-background-color: transparent;
            -fx-background-radius: 22;
        """);

        return outer;
    }


    private TextField input(String placeholder) {

        TextField t = new TextField();
        t.setPromptText(placeholder);

        t.setStyle("""
            -fx-background-color: #020617;
            -fx-text-fill: #e5e7eb;
            -fx-prompt-text-fill: #6b7280;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-border-color: #1f2937;
            -fx-border-width: 1;
            -fx-padding: 6 10 6 10;
        """);

        return t;
    }

    private Label labelTitle(String text) {

        Label l = new Label(text);

        l.setStyle("""
            -fx-text-fill: #e5e7eb;
            -fx-font-size: 14px;
            -fx-font-weight: bold;
        """);

        return l;
    }

    private Button primaryButton(String text) {

        Button b = new Button(text);

        b.setStyle("""
            -fx-background-color: #22d3ee;
            -fx-text-fill: #020617;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-padding: 8 16 8 16;
        """);

        b.setOnMouseEntered(e ->
                b.setStyle("""
                    -fx-background-color: #0ea5e9;
                    -fx-text-fill: #020617;
                    -fx-font-weight: bold;
                    -fx-background-radius: 10;
                    -fx-padding: 8 16 8 16;
                """));

        b.setOnMouseExited(e ->
                b.setStyle("""
                    -fx-background-color: #22d3ee;
                    -fx-text-fill: #020617;
                    -fx-font-weight: bold;
                    -fx-background-radius: 10;
                    -fx-padding: 8 16 8 16;
                """));

        return b;
    }

    private Button secondaryButton(String text) {

        Button b = new Button(text);

        b.setStyle("""
            -fx-background-color: transparent;
            -fx-text-fill: #e5e7eb;
            -fx-border-color: #1f2937;
            -fx-border-width: 1;
            -fx-background-radius: 10;
            -fx-border-radius: 10;
            -fx-padding: 7 14 7 14;
        """);

        b.setOnMouseEntered(e ->
                b.setStyle("""
                    -fx-background-color: #0f172a;
                    -fx-text-fill: #22d3ee;
                    -fx-border-color: #22d3ee;
                    -fx-border-width: 1;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-padding: 7 14 7 14;
                """));

        b.setOnMouseExited(e ->
                b.setStyle("""
                    -fx-background-color: transparent;
                    -fx-text-fill: #e5e7eb;
                    -fx-border-color: #1f2937;
                    -fx-border-width: 1;
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                    -fx-padding: 7 14 7 14;
                """));

        return b;
    }

    private Button iconButton(String icon, String label) {

        // ICON
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("""
            -fx-font-size: 20px;
        """);

        // TEXT
        Label textLabel = new Label(label);
        textLabel.setStyle("""
            -fx-text-fill: #e5e7eb;
            -fx-font-size: 11px;
        """);

        // STACK ICON + TEXT
        VBox content = new VBox(4, iconLabel, textLabel);
        content.setAlignment(Pos.CENTER);
        content.setStyle("""
            -fx-background-color: transparent;
            -fx-background-insets: 0;
            -fx-background-radius: 0;
        """);

        // BUTTON 
        Button b = new Button(null);     
        b.getStyleClass().clear();       
        b.setBackground(Background.EMPTY);
        b.setGraphic(content);
        b.setTooltip(new Tooltip(label));

        // DEFAULT (INACTIVE)
        b.setStyle("""
            -fx-background-color: transparent;
            -fx-background-insets: 0;
            -fx-background-radius: 16;
            -fx-border-color: transparent;
            -fx-border-radius: 16;
            -fx-border-width: 0;
            -fx-padding: 10 18 10 18;
            -fx-focus-color: transparent;
            -fx-faint-focus-color: transparent;
        """);

        // HOVER STYLE
        b.setOnMouseEntered(e -> {
            if (b != activeNavButton) {
                b.setStyle("""
                    -fx-background-color: #0f172a;
                    -fx-background-insets: 0;
                    -fx-background-radius: 16;
                    -fx-border-color: #22d3ee;
                    -fx-border-radius: 16;
                    -fx-border-width: 1;
                    -fx-padding: 10 18 10 18;
                    -fx-focus-color: transparent;
                    -fx-faint-focus-color: transparent;
                """);
            }
        });

        b.setOnMouseExited(e -> {
            if (b != activeNavButton) {
                b.setStyle("""
                    -fx-background-color: transparent;
                    -fx-background-insets: 0;
                    -fx-background-radius: 16;
                    -fx-border-color: transparent;
                    -fx-border-radius: 16;
                    -fx-border-width: 0;
                    -fx-padding: 10 18 10 18;
                    -fx-focus-color: transparent;
                    -fx-faint-focus-color: transparent;
                """);
            }
        });

        return b;
    }




    private void refreshTable() {
        data.clear();
        data.addAll(pantry.getAllItems());
    }

    private void refreshGroceryList() {
        groceryListView.getItems().clear();
        groceryListView.getItems().addAll(groceryQueue.viewListAsList());
    }

    private void saveCurrentProfile() {
        if (currentProfile != null) {
            profileManager.saveProfile(currentProfile, pantry, groceryQueue);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    // ============================================================
    //  TABLE COLUMN STYLING (NEON TEXT + SORTING)
    // ============================================================
    private <T> void styleTableColumn(TableColumn<PantryItem, T> column) {
        column.setSortable(true);

        column.setCellFactory(col -> new TableCell<PantryItem, T>() {
            @Override
            protected void updateItem(T item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item.toString());
                    setTextFill(Color.web("#e5e7eb"));
                    setStyle("-fx-font-size: 13px;");
                }
            }
        });
    }


    // ============================================================
    //  CATEGORY COLOR CODING
    // ============================================================
    private void styleCategoryColumn(TableColumn<PantryItem, String> column) {
        column.setSortable(true);

        column.setCellFactory(col -> new TableCell<PantryItem, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                    return;
                }

                setText(item);
                setStyle("-fx-font-size: 13px; -fx-font-weight: bold;");

                switch (item.toLowerCase()) {
                    case "produce": setTextFill(Color.web("#4ade80")); break;
                    case "dairy": setTextFill(Color.web("#60a5fa")); break;
                    case "meat": setTextFill(Color.web("#f87171")); break;
                    case "snacks": setTextFill(Color.web("#facc15")); break;
                    case "frozen": setTextFill(Color.web("#22d3ee")); break;
                    case "scanned": setTextFill(Color.web("#a78bfa")); break; // purple
                    default: setTextFill(Color.web("#e5e7eb"));
                }
            }
        });
    }

    // ============================================================
    //  ROUNDED CORNERS + HOVER HIGHLIGHT FOR TABLE ROWS
    // ============================================================
    private void styleTableRows() {
        table.setRowFactory(tv -> {
            TableRow<PantryItem> row = new TableRow<>();

            row.setStyle("""
                -fx-background-radius: 10;
                -fx-border-radius: 10;
            """);

            row.setOnMouseEntered(e -> {
                if (!row.isEmpty()) {
                    row.setStyle("""
                        -fx-background-color: #0f172a;
                        -fx-background-radius: 10;
                        -fx-border-radius: 10;
                    """);
                }
            });

            row.setOnMouseExited(e -> {
                row.setStyle("""
                    -fx-background-radius: 10;
                    -fx-border-radius: 10;
                """);
            });

            return row;
        });
    }


}
