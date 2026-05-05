import javafx.beans.property.*;

public class PantryItem {

    private final StringProperty name;
    private final IntegerProperty quantity;
    private final StringProperty expirationDate;
    private final StringProperty category;

    public PantryItem(String name, int quantity, String expirationDate, String category) {
        this.name = new SimpleStringProperty(name.toLowerCase());
        this.quantity = new SimpleIntegerProperty(quantity);
        this.expirationDate = new SimpleStringProperty(expirationDate);
        this.category = new SimpleStringProperty(category);
    }

    public String getName() {
        return name.get();
    }

    public StringProperty nameProperty() {
        return name;
    }

    public int getQuantity() {
        return quantity.get();
    }

    public void setQuantity(int value) {
        quantity.set(value);
    }

    public IntegerProperty quantityProperty() {
        return quantity;
    }

    public void addQuantity(int amount) {
        quantity.set(quantity.get() + amount);
    }

    public void reduceQuantity(int amount) {
        quantity.set(Math.max(0, quantity.get() - amount));
    }

    public String getExpirationDate() {
        return expirationDate.get();
    }

    public StringProperty expirationDateProperty() {
        return expirationDate;
    }

    public String getCategory() {
        return category.get();
    }

    public StringProperty categoryProperty() {
        return category;
    }

    @Override
    public String toString() {
        return getName() + " | Qty: " + getQuantity()
                + " | Exp: " + getExpirationDate()
                + " | Category: " + getCategory();
    }
}
