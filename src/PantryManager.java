import java.time.LocalDate;
import java.util.*;

public class PantryManager {

    private final HashMap<String, PantryItem> pantry = new HashMap<>();

    // ================================
    // ADD / REMOVE / MERGE
    // ================================
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

    // ================================
    // ACCESSORS
    // ================================
    public Collection<PantryItem> getAllItems() {
        return pantry.values();
    }

    public Map<String, PantryItem> getPantryMap() {
        return pantry;
    }

    public void clear() {
        pantry.clear();
    }

    // ================================
    // EXPORT / IMPORT
    // ================================
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

    // ================================
    // Sort pantry items by expiration date (ascending)
    // ================================
    public List<PantryItem> getSortedByExpiration() {
        List<PantryItem> list = new ArrayList<>(pantry.values());
        insertionSortByExpiration(list);
        return list;
    }

    private void insertionSortByExpiration(List<PantryItem> items) {
        for (int i = 1; i < items.size(); i++) {
            PantryItem key = items.get(i);

            LocalDate keyDate;
            try {
                keyDate = LocalDate.parse(key.getExpirationDate());
            } catch (Exception e) {
                keyDate = LocalDate.MAX; // invalid dates go last
            }

            int j = i - 1;

            while (j >= 0) {
                LocalDate jDate;
                try {
                    jDate = LocalDate.parse(items.get(j).getExpirationDate());
                } catch (Exception e) {
                    jDate = LocalDate.MAX;
                }

                if (jDate.isAfter(keyDate)) {
                    items.set(j + 1, items.get(j));
                    j--;
                } else {
                    break;
                }
            }

            items.set(j + 1, key);
        }
    }
}
