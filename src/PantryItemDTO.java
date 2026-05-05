public class PantryItemDTO {
    private String name;
    private int quantity;
    private String expirationDate;
    private String category;

    public PantryItemDTO() {
    }

    public PantryItemDTO(String name, int quantity, String expirationDate, String category) {
        this.name = name;
        this.quantity = quantity;
        this.expirationDate = expirationDate;
        this.category = category;
    }

    public String getName() { return name; }
    public int getQuantity() { return quantity; }
    public String getExpirationDate() { return expirationDate; }
    public String getCategory() { return category; }
}
