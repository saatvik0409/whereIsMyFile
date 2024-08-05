import java.io.File;

public class DeleteEvenNumberedFiles {
    public static void main(String[] args) {
        // Specify the directory where the files are located
        String directoryPath = Encryptor.drive_path();
        
        // Create a File object representing the directory
        File directory = new File(directoryPath);
        
        // Get all files in the directory
        File[] files = directory.listFiles();
        
        if (files != null) {
            for (File file : files) {
                // Get the file name
                String fileName = file.getName();
                
                // Check if the file name matches the pattern "FileXXXX"
                if (fileName.matches("File\\d{4}")) {
                    // Extract the number XXXX from the file name
                    String numberPart = fileName.substring(4, 8);
                    int number = Integer.parseInt(numberPart);
                    
                    // Check if the number is even
                    if (number % 2 == 0) {
                        // Delete the file
                        if (file.delete()) {
                            System.out.println("Deleted: " + fileName);
                        } else {
                            System.out.println("Failed to delete: " + fileName);
                        }
                    }
                }
            }
        } else {
            System.out.println("The directory is empty or does not exist.");
        }
    }
}
