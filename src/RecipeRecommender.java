import java.util.*;

public class RecipeRecommender {

    /**
     * FUTURE AI:
     * - Score recipes based on pantry availability
     * - Suggest alternatives
     * - Recommend meals based on dietary preferences
     * - Use ML to rank recipes
     */

    // Selection Sort by Prep Time
    public static void sortByPrepTime(List<Recipe> recipes) {
        for (int i = 0; i < recipes.size() - 1; i++) {
            int minIndex = i;

            for (int j = i + 1; j < recipes.size(); j++) {
                if (recipes.get(j).getPrepTime() < recipes.get(minIndex).getPrepTime()) {
                    minIndex = j;
                }
            }

            Collections.swap(recipes, i, minIndex);
        }
    }

    // Console-based recipe matching (still useful for debugging)
    public static void recommendRecipes(List<Recipe> recipes, PantryManager pantry) {
        System.out.println("\nRecipe Recommendations:");

        for (Recipe recipe : recipes) {
            int matchCount = 0;

            for (String ingredient : recipe.getIngredients()) {
                if (pantry.hasIngredient(ingredient)) {
                    matchCount++;
                }
            }

            if (matchCount == recipe.getIngredients().size()) {
                System.out.println("✔ FULL MATCH: " + recipe.getName());
            } else if (matchCount > 0) {
                System.out.println("➤ PARTIAL MATCH: " + recipe.getName() +
                        " (" + matchCount + "/" + recipe.getIngredients().size() + " ingredients)");
            }
        }
    }
}
