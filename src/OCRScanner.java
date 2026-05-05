import java.io.File;

public class OCRScanner {

    /**
     * OCRScanner
     * ---------------------------------------------------
     * Placeholder for future real OCR engine.
     * Will later integrate:
     * - Tesseract
     * - OpenCV
     * - Cloud OCR APIs
     *
     * For now, returns fake text for testing.
     */
    public static ScannerData scan(File file) {

        try {
            // Placeholder for now (future OCR engine)
            // You can change this fake text to simulate different receipts.
            String fakeOCRText = "milk\nbread\neggs\npasta";

            return new ScannerData(fakeOCRText, file.getName());

        } catch (Exception e) {
            return new ScannerData("ERROR: " + e.getMessage(), file.getName());
        }
    }
}
