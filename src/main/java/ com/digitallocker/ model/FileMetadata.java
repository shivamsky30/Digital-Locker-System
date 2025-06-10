// Model class: FileMetadata.java
package com.digitallocker.model;

/**
 * Represents the metadata of a file stored in the digital locker.
 * This information is stored in the user's metadata file, not the actual file content.
 */
public class FileMetadata {
    private String id; // Unique identifier for the file within the locker
    private String originalFilename; // Original name of the file when uploaded
    private String storedFilename;   // Name of the file as stored on disk (e.g., a timestamp or UUID)
    private String uploadDate;       // Date and time of upload
    private long fileSize;           // Size of the file in bytes

    public FileMetadata(String id, String originalFilename, String storedFilename, String uploadDate, long fileSize) {
        this.id = id;
        this.originalFilename = originalFilename;
        this.storedFilename = storedFilename;
        this.uploadDate = uploadDate;
        this.fileSize = fileSize;
    }

    public String getId() {
        return id;
    }

    public String getOriginalFilename() {
        return originalFilename;
    }

    public String getStoredFilename() {
        return storedFilename;
    }

    public String getUploadDate() {
        return uploadDate;
    }

    public long getFileSize() {
        return fileSize;
    }

    // Method to convert FileMetadata object to a string format for file storage.
    public String toFileString() {
        return String.join("|", id, originalFilename, storedFilename, uploadDate, String.valueOf(fileSize));
    }

    // Static method to parse a string from file back into a FileMetadata object.
    public static FileMetadata fromFileString(String fileString) {
        String[] parts = fileString.split("\\|");
        if (parts.length == 5) {
            return new FileMetadata(parts[0], parts[1], parts[2], parts[3], Long.parseLong(parts[4]));
        }
        throw new IllegalArgumentException("Invalid file metadata string format.");
    }
}
