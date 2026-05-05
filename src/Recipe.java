import java.util.*;

public class Recipe {
    private String name;
    private List<String> ingredients;
    private int prepTime; // minutes
    private int difficulty; // 1–5

    public Recipe(String name, List<String> ingredients, int prepTime, int difficulty) {
        this.name = name;
        this.ingredients = ingredients;
        this.prepTime = prepTime;
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }

    public List<String> getIngredients() {
        return ingredients;
    }

    public int getPrepTime() {
        return prepTime;
    }

    public int getDifficulty() {
        return difficulty;
    }

    public String toString() {
        return name + " | Time: " + prepTime + " mins | Difficulty: " + difficulty;
    }
}
