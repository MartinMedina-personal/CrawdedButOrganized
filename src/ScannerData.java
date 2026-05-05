public class ScannerData {

    private String rawText;
    private String sourceFile;
    private long timestamp;

    public ScannerData(String rawText, String sourceFile) {
        this.rawText = rawText;
        this.sourceFile = sourceFile;
        this.timestamp = System.currentTimeMillis();
    }

    public String getRawText() {
        return rawText;
    }

    public String getSourceFile() {
        return sourceFile;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "ScannerData{" +
                "rawText='" + rawText + '\'' +
                ", sourceFile='" + sourceFile + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
