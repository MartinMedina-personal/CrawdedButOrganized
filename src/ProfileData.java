import java.util.List;

public class ProfileData {
    private List<PantryItemDTO> pantryItems;
    private List<String> groceryItems;

    public ProfileData() {
    }

    public ProfileData(List<PantryItemDTO> pantryItems, List<String> groceryItems) {
        this.pantryItems = pantryItems;
        this.groceryItems = groceryItems;
    }

    public List<PantryItemDTO> getPantryItems() {
        return pantryItems;
    }

    public List<String> getGroceryItems() {
        return groceryItems;
    }
}
