import java.util.*;

public class GroceryQueue {
    private Queue<String> groceryList;

    public GroceryQueue() {
        groceryList = new LinkedList<>();
    }

    public void addItem(String item) {
        groceryList.offer(item.toLowerCase());
    }

    public String pollItem() {
        return groceryList.poll();
    }

    public void markPurchased() {
        if (!groceryList.isEmpty()) {
            System.out.println("Purchased: " + groceryList.poll());
        } else {
            System.out.println("No items to purchase.");
        }
    }

    public void requeueItem(String item) {
        groceryList.offer(item.toLowerCase());
    }

    public List<String> viewListAsList() {
        return new ArrayList<>(groceryList);
    }

    public void clear() {
        groceryList.clear();
    }

    public void loadFromList(List<String> items) {
        groceryList.clear();
        for (String s : items) {
            groceryList.offer(s.toLowerCase());
        }
    }
}
