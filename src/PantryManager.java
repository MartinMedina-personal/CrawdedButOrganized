import java.util.*;

public class PantryManager {

    private final HashMap<String, PantryItem> pantry = new HashMap<>();

    public void addItem(String name, int quantity, String exp, String category) {
        name = name.toLowerCase();

        if (pantry.containsKey(name)) {
            pantry.get(name).addQuantity(quantity);
        } else {
            pantry.put(name, new PantryItem(name, quantity, exp, category));
        }
    }

    /**
     * Safe merge method for AI/OCR imports.
     * Prevents crashes and ensures consistent behavior.
     */
    public void mergeItem(String name) {
        name = name.toLowerCase();
        pantry.putIfAbsent(name, new PantryItem(name, 1, "unknown", "merged"));
    }

    public void removeItem(String name) {
        pantry.remove(name.toLowerCase());
    }

    public boolean hasIngredient(String name) {
        PantryItem item = pantry.get(name.toLowerCase());
        return item != null && item.getQuantity() > 0;
    }

    public Collection<PantryItem> getAllItems() {
        return pantry.values();
    }

    public Map<String, PantryItem> getPantryMap() {
        return pantry;
    }

    public void clear() {
        pantry.clear();
    }

    public List<PantryItemDTO> exportAsDTOs() {
        List<PantryItemDTO> list = new ArrayList<>();
        for (PantryItem item : pantry.values()) {
            list.add(new PantryItemDTO(
                    item.getName(),
                    item.getQuantity(),
                    item.getExpirationDate(),
                    item.getCategory()
            ));
        }
        return list;
    }

    public void loadFromDTOs(List<PantryItemDTO> dtos) {
        pantry.clear();
        for (PantryItemDTO dto : dtos) {
            pantry.put(dto.getName().toLowerCase(),
                    new PantryItem(dto.getName(), dto.getQuantity(),
                            dto.getExpirationDate(), dto.getCategory()));
        }
    }
}
