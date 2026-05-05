import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class ProfileManager {

    private final Path profilesDir;
    private final Gson gson;

    public ProfileManager() {
        this.profilesDir = Paths.get("profiles");
        this.gson = new GsonBuilder().setPrettyPrinting().create();
        ensureProfilesDir();
    }

    private void ensureProfilesDir() {
        try {
            if (!Files.exists(profilesDir)) {
                Files.createDirectories(profilesDir);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Path profileDir(String name) {
        return profilesDir.resolve(name);
    }

    private Path profileFile(String name) {
        return profileDir(name).resolve("data.json");
    }

    private Path lastProfileFile() {
        return profilesDir.resolve("lastProfile.txt");
    }

    public List<String> listProfiles() {
        List<String> result = new ArrayList<>();
        if (Files.exists(profilesDir) && Files.isDirectory(profilesDir)) {
            try {
                Files.list(profilesDir)
                        .filter(Files::isDirectory)
                        .forEach(p -> result.add(p.getFileName().toString()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return result;
    }

    public boolean profileExists(String name) {
        Path p = profileDir(name);
        return Files.exists(p) && Files.isDirectory(p);
    }

    public void createProfile(String name) throws IOException {
        Path p = profileDir(name);
        if (!Files.exists(p)) {
            Files.createDirectories(p);
        }
        saveProfile(name, new PantryManager(), new GroceryQueue());
        rememberLastProfile(name);
    }

    public void saveProfile(String name, PantryManager pantry, GroceryQueue grocery) {
        try {
            List<PantryItemDTO> pantryDTOs = pantry.exportAsDTOs();
            List<String> groceryItems = grocery.viewListAsList();

            ProfileData data = new ProfileData(pantryDTOs, groceryItems);

            Path file = profileFile(name);
            Files.createDirectories(file.getParent());

            try (Writer writer = Files.newBufferedWriter(file)) {
                gson.toJson(data, writer);
            }

            rememberLastProfile(name);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void loadProfile(String name, PantryManager pantry, GroceryQueue grocery) {
        Path file = profileFile(name);
        if (!Files.exists(file)) {
            return;
        }

        try (Reader reader = Files.newBufferedReader(file)) {
            ProfileData data = gson.fromJson(reader, ProfileData.class);
            if (data != null) {
                pantry.loadFromDTOs(
                        data.getPantryItems() != null ? data.getPantryItems() : new ArrayList<>()
                );
                grocery.loadFromList(
                        data.getGroceryItems() != null ? data.getGroceryItems() : new ArrayList<>()
                );
            }
            rememberLastProfile(name);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void deleteProfile(String name) {
        try {
            Path dir = profileDir(name);
            if (Files.exists(dir)) {
                deleteRecursive(dir);
            }
            String last = getLastProfile();
            if (last != null && last.equals(name)) {
                Files.deleteIfExists(lastProfileFile());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void deleteRecursive(Path path) throws IOException {
        if (Files.isDirectory(path)) {
            try (var stream = Files.list(path)) {
                for (Path p : (Iterable<Path>) stream::iterator) {
                    deleteRecursive(p);
                }
            }
        }
        Files.deleteIfExists(path);
    }

    public void rememberLastProfile(String name) {
        try {
            Files.createDirectories(profilesDir);
            try (Writer w = Files.newBufferedWriter(lastProfileFile())) {
                w.write(name);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getLastProfile() {
        Path f = lastProfileFile();
        if (!Files.exists(f)) return null;
        try {
            return Files.readString(f).trim();
        } catch (IOException e) {
            return null;
        }
    }
}
