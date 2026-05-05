import java.util.Locale;

/**
 * VoiceCommandProcessor
 * ---------------------------------------------------
 * Updated with AI-ready structure.
 *
 * FUTURE:
 * - Integrate Whisper for speech-to-text
 * - Natural language understanding
 * - Voice-based pantry and grocery updates
 * - Mobile microphone support
 */
public class VoiceCommandProcessor {

    private GroceryQueue groceryQueue;
    private PantryManager pantryManager;

    public VoiceCommandProcessor(GroceryQueue groceryQueue, PantryManager pantryManager) {
        this.groceryQueue = groceryQueue;
        this.pantryManager = pantryManager;
    }

    /**
     * MAIN ENTRY POINT
     * This simulates receiving voice input as text.
     *
     * Example input:
     * "add milk to grocery"
     * "add eggs to pantry"
     * "buy bread"
     */
    public String processVoiceCommand(String spokenText) {

        if (spokenText == null || spokenText.isBlank()) {
            return "No voice input detected";
        }

        String command = spokenText.toLowerCase(Locale.ROOT).trim();

        // ================= GROCERY COMMAND =================
        if (command.startsWith("add") && command.contains("grocery")) {

            String item = extractItem(command, "add", "to grocery");

            groceryQueue.addItem(item);
            return "Added to grocery: " + item;
        }

        // ================= PANTRY COMMAND =================
        if (command.startsWith("add") && command.contains("pantry")) {

            String item = extractItem(command, "add", "to pantry");

            pantryManager.addItem(item, 1, "unknown", "voice");
            return "Added to pantry: " + item;
        }

        // ================= BUY COMMAND =================
        if (command.startsWith("buy")) {

            String item = command.replace("buy", "").trim();
            groceryQueue.addItem(item);

            return "Queued for purchase: " + item;
        }

        return "Command not recognized: " + spokenText;
    }

    /**
     * Extracts item name from simple voice commands
     */
    private String extractItem(String command, String startWord, String endPhrase) {

        try {
            String cleaned = command
                    .replace(startWord, "")
                    .replace(endPhrase, "")
                    .trim();

            return cleaned.isEmpty() ? "unknown item" : cleaned;

        } catch (Exception e) {
            return "unknown item";
        }
    }
}
