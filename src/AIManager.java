import java.io.File;
import java.util.*;

/**
 * AIManager
 * ---------------------------------------------------
 * Central hub for future AI integrations:
 * - OCR text interpretation
 * - Ingredient extraction
 * - Receipt parsing
 * - Recipe suggestions
 * - Desktop/mobile shared AI engine
 *
 * This class prepares the project for future upgrades
 * without breaking the current structure.
 */
public class AIManager {

    public AIManager() {
        // FUTURE:
        // Load ML models, initialize OCR engines,
        // connect to Whisper, Tesseract, or cloud AI.
    }

    /**
     * Convert raw OCR text into a list of items.
     * Currently simple, but will later use NLP.
     */
    public List<String> analyzeReceiptText(String rawText) {
        if (rawText == null || rawText.isBlank()) {
            return Collections.emptyList();
        }

        List<String> items = new ArrayList<>();
        String[] lines = rawText.split("\\r?\\n");

        for (String line : lines) {
            String cleaned = line.trim().toLowerCase();
            if (!cleaned.isEmpty() && !cleaned.startsWith("error")) {
                items.add(cleaned);
            }
        }

        return items;
    }

    /**
     * Analyze receipt image.
     * For now, we just reuse the rawText from OCRScanner.
     * FUTURE:
     * - Use computer vision to detect text regions
     * - Use OCR to extract text
     * - Use NLP to normalize item names
     */
    public List<String> analyzeReceiptImage(File imageFile, String rawTextFromScanner) {
        // In a real implementation, imageFile would be processed directly.
        // For now, we rely on the placeholder OCR text.
        return analyzeReceiptText(rawTextFromScanner);
    }

    /**
     * Identify missing ingredients for a recipe.
     */
    public List<String> suggestMissingIngredients(Recipe recipe, PantryManager pantry) {
        List<String> missing = new ArrayList<>();

        for (String ing : recipe.getIngredients()) {
            if (!pantry.hasIngredient(ing)) {
                missing.add(ing);
            }
        }

        return missing;
    }

    /**
     * Merge scanned items into pantry.
     * Future: confidence scoring, duplicate detection.
     */
    public void mergeScannedItemsIntoPantry(List<String> items, PantryManager pantry) {
        for (String item : items) {
            pantry.addItem(item, 1, "unknown", "scanned");
        }
    }
}
