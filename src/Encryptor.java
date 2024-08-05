import java.io.File;
import java.nio.file.FileSystems;
import java.nio.file.FileStore;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import javax.swing.filechooser.FileSystemView;


class ClusterNode {
    int clusterAddress;
    ClusterNode next;

    public ClusterNode(int clusterAddress) {
        this.clusterAddress = clusterAddress;
        this.next = null;
    }
}

public class Encryptor {

    public static long getClusterSize(String path) throws IOException {
        Path p = FileSystems.getDefault().getPath(path);
        System.out.println("FilePath: "+p);
        FileStore store = java.nio.file.Files.getFileStore(p);
        long clusterSize = store.getBlockSize();
        System.out.println("Cluster size: " + clusterSize + " bytes"); //Comment this out later
        return clusterSize;
    }

    public static String drive_path()
    {
        FileSystemView fileSystemView = FileSystemView.getFileSystemView();
        File[] roots = File.listRoots();

        //System.out.println("Drives:");

        for (File root : roots) 
        {
            // Print basic information about each root

            // Check if the drive is removable or has other specific descriptions
            String description = fileSystemView.getSystemTypeDescription(root);
            System.out.println(description);
            if (description != null && (description.contains("Removable") || description.contains("USB Drive"))) {
                // Use the path of the removable drive
                String drivePath = root.getAbsolutePath();
                //      System.out.println("Path of removable drive: " + drivePath);
                // Check file system type
                String fileSystemType = getFileSystemType(drivePath);
                return fileSystemType;  
                //   System.out.println("File System Type: " + fileSystemType);
            } 
            // System.out.println();
        }
        return null;
    }

    // Method to get the file system type using fsutil
    private static String getFileSystemType(String drivePath) {
        String fileSystemType = "Unknown";

        try {
            // Ensure the drive path does not have a trailing backslash
            drivePath = drivePath.endsWith("\\") ? drivePath.substring(0, drivePath.length() - 1) : drivePath;

            // Run the 'fsutil fsinfo volumeinfo' command to get file system details
            Process process = Runtime.getRuntime().exec("fsutil fsinfo volumeinfo " + drivePath);
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;

            // Print the output for debugging
            //          System.out.println("Debug output of 'fsutil fsinfo volumeinfo " + drivePath + "':");
            while ((line = reader.readLine()) != null) {
                //              System.out.println(line);  // Print each line of output
                if (line.contains("File System Name")) {
                    fileSystemType = line.split(":")[1].trim();
                }
            }

            process.waitFor();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return fileSystemType;
    }

    public static long readFile(String filePath) {
        RandomAccessFile file = null;
        try {
            file = new RandomAccessFile(filePath, "r");
            long length = file.length();
            return length;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void FileSplitter(String filePath,long clusterSize, int parts, int remSize)
    {
        RandomAccessFile file = null;
        try {
            new File("Files").mkdirs();
            file = new RandomAccessFile(filePath, "r");

            String fileExtension = "";
            int dotIndex = filePath.lastIndexOf('.');
            if (dotIndex > 0 && dotIndex < filePath.length() - 1) {
                fileExtension = filePath.substring(dotIndex);
            }

            for (int i = 1; i <=parts; i++) {
            String outputFile = String.format("./Files/File%04d" + fileExtension, 2*i);
                try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                    byte[] buffer = new byte[(int) clusterSize];
                    int readBuffer = file.read(buffer);
                    outputStream.write(buffer, 0, readBuffer);
                }
            }

            if (remSize > 0) {
            String remFile = String.format("./Files/File%04d" + fileExtension,2*(parts+1));
                try (FileOutputStream rem = new FileOutputStream(remFile)) {
                    byte[] buffer = new byte[remSize];
                    int readBuffer = file.read(buffer);
                    rem.write(buffer, 0, readBuffer);
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (file != null) {
                try {
                    file.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    public static String Extension(String filePath)
    {
        String fileExtension = "";
        int dotIndex = filePath.lastIndexOf('.');
        if (dotIndex > 0 && dotIndex < filePath.length() - 1) 
        {
            fileExtension = filePath.substring(dotIndex);
            return fileExtension;
        }
        return fileExtension;
        
    }

    public static void additionfiles(String filePath, long clusterSize, int parts, int remSize) throws IOException {
        byte[] buffer = new byte[(int) clusterSize];
        new File("FileA").mkdirs();

        String fileExtension = Extension(filePath);

        for (int i = 1; i <=parts; i++) {
            String outputFile = String.format("./FileA/File%04d" + fileExtension, 2*i-1);
            try (FileOutputStream outputStream = new FileOutputStream(outputFile)) {
                for (int j = 0; j < clusterSize; j++) {
                    buffer[j] = 1;
                }
                outputStream.write(buffer);
            }
        }

        if (remSize > 0) {
            int startIndex = 2 * parts + 1;

            String remFile = String.format("./FileA/File%04d" + fileExtension, startIndex);
            try (FileOutputStream rem = new FileOutputStream(remFile)) {
                for (int j = 0; j < clusterSize; j++) {
                    buffer[j] = 1;
                }
                rem.write(buffer);
            }

            String remFile3 = String.format("./FileA/File%04d" + fileExtension, startIndex + 2);
            try (FileOutputStream rem3 = new FileOutputStream(remFile3)) {
                for (int j = 0; j < clusterSize; j++) {
                    buffer[j] = 1;
                }
                rem3.write(buffer);
            }
        }
    }

    public static void dummyfiles(String filePath, long clusterSize, int parts, int remSize) throws IOException {
        byte[] buffer = new byte[(int) clusterSize];
        new File("FileD").mkdirs();

        String fileExtension = Extension(filePath);

        for (int i = 1; i <=parts; i++) {
            String OutputFile = String.format("./FileD/File%04d" + fileExtension, 2*i);
            try (FileOutputStream OutputStream = new FileOutputStream(OutputFile)) {
                for (int j = 0; j < clusterSize; j++) {
                    buffer[j] = 0;
                }
                OutputStream.write(buffer);
            }
        }

        if (remSize > 0) {
            int startIndex = 2 * parts + 1;

            String remFile2 = String.format("./FileD/File%04d" + fileExtension, startIndex + 1);
            try (FileOutputStream rem2 = new FileOutputStream(remFile2)) {
                for (int j = 0; j < clusterSize; j++) {
                    buffer[j] = 0;
                }
                rem2.write(buffer);
            }
        }
    }
    public static void Retrieval(String drivePath){

    }
    {
/*
        public static void DummyFiles(String folderPath, String filePath){
            File inputFile = new File(filePath);
            long criticalFileSize = inputFile.length();
            String fileExtension = getFileExtension(inputFile);

            int sectorSize = getSectorSize();
            int sectorsPerCluster = getSectorsPerCluster();

            int numberOfDummyFiles = calculateNumberOfDummyFiles(criticalFileSize, sectorSize, sectorsPerCluster);

            int dummyFileSize = (int)criticalFileSize/numberOfDummyFiles;
            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            generateDummyFiles(dummyFileSize, folderPath, numberOfDummyFiles, fileExtension);


        }
        private static int calculateNumberOfDummyFiles(long criticalFileSize, int sectorSize, int sectorsPerCluster) {
            return (int) Math.ceil((double) criticalFileSize / (sectorsPerCluster * sectorSize));
        }

        private static void generateDummyFiles(int dummyFileSize, String folderPath, int numberOfFiles, String fileExtension) {
            for (int i = 2; i <= numberOfFiles; i+=2) {
                int fileNumber = i;
                String fileName = String.format("FILE%04d.%s", fileNumber, fileExtension);

                try {
                    createDummyFile(dummyFileSize,folderPath, fileName);
                    System.out.println("Generated file: " + fileName);
                } catch (IOException e) {
                    System.err.println("Error creating file " + fileName + ": " + e.getMessage());
                }
            }
        }

        private static void createDummyFile(int dummyFileSize, String folderPath, String fileName) throws IOException {
            File file = new File(folderPath, fileName);
            try (FileWriter writer = new FileWriter(file)) {
                String content = "a".repeat(dummyFileSize * 1024);
                writer.write(content);
            }
        }

        private static String getFileExtension(File file) {
            String name = file.getName();
            int lastIndexOf = name.lastIndexOf(".");
            if (lastIndexOf == -1) {
                return ""; // empty extension
            }
            return name.substring(lastIndexOf + 1);
        }

        private static int getSectorSize() {
            // Assuming a common sector size, you might need to change this based on your environment
            return 512;
        }

        private static int getSectorsPerCluster() {
            // Assuming a common number of sectors per cluster, you might need to change this based on your environment
            return 8;
        }

        public static void AdditionalFiles(String folderPath, String filePath){
            File inputFile = new File(filePath);
            long criticalFileSize = inputFile.length();
            String fileExtension = getFileExtension(inputFile);

            int sectorSize = getSectorSize();
            int sectorsPerCluster = getSectorsPerCluster();

            int numFiles = calculateNumberOfDummyFiles(criticalFileSize, sectorSize, sectorsPerCluster);

            File folder = new File(folderPath);
            if (!folder.exists()) {
                folder.mkdirs();
            }
            for (int i = 1; i <= numFiles+1; i+=2) {
                int fileNumber = i;
                String fileName = String.format("FILE%04d.%s", fileNumber, fileExtension);
                File file = new File(folderPath, fileName);

                try (FileWriter writer = new FileWriter(file)) {
                    String content = "a".repeat((int)criticalFileSize/(int)numFiles * 1024);
                    writer.write(content);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
*/
    }

    public static void CombineFiles(String externalDrivePath, String dummyFilesPath, String additionalFilesPath){
        File dummyFilesFolder = new File(dummyFilesPath);
        File additionalFilesFolder = new File(additionalFilesPath);

        if (!dummyFilesFolder.exists() || !additionalFilesFolder.exists()) {
            System.err.println("Folders not found.");
            return;
        }

        // List files in the dummyFiles and additionalFiles folders
        List<File> dummyFiles = Arrays.asList(dummyFilesFolder.listFiles());
        List<File> additionalFiles = Arrays.asList(additionalFilesFolder.listFiles());

        // Sort files to ensure correct order
        dummyFiles.sort(Comparator.comparing(File::getName));
        additionalFiles.sort(Comparator.comparing(File::getName));

        // Combine files in an alternating manner
        int maxFiles = Math.max(dummyFiles.size(), additionalFiles.size());
        int dummyIndex = 0, additionalIndex = 0;

        for (int i = 1; i < maxFiles * 2; i++) {
            if (i%2==1 && additionalIndex < additionalFiles.size()) {
                // Write additional file
                writeFileToExternalDrive(additionalFiles.get(additionalIndex++), externalDrivePath);
            } else if (dummyIndex < dummyFiles.size()) {
                // Write dummy file
                writeFileToExternalDrive(dummyFiles.get(dummyIndex++), externalDrivePath);
            }
        }

    }

    private static void writeFileToExternalDrive(File file, String externalDrivePath) {
        try (FileInputStream fis = new FileInputStream(file);
                FileOutputStream fos = new FileOutputStream(externalDrivePath + file.getName())) {

            byte[] buffer = new byte[1024];
            int length;
            while ((length = fis.read(buffer)) > 0) {
                fos.write(buffer, 0, length);
            }

        } catch (IOException e) {
            System.err.println("Error writing file: " + file.getName());
            e.printStackTrace();
        }
    }

    public static void physicalClusterReservation(String externalDrivePath){
        externalDrivePath = externalDrivePath.replace("\\","/");
        int rootDirSize = calcRootdir(externalDrivePath);
        ClusterNode head = FAT32ReadandDelete(externalDrivePath);
    }

    private static int calcRootdir(String externalDrivePath) {
        String filePath = externalDrivePath;
        int sectorSize = 512; // Typical sector size for FAT

        try (RandomAccessFile device = new RandomAccessFile( filePath, "r")) {
            // Read the boot sector
            byte[] bootSector = new byte[sectorSize];
            device.readFully(bootSector);

            // Extract necessary information from the boot sector
            int reservedSectors = ((bootSector[15] & 0xFF) << 8) | (bootSector[14] & 0xFF);
            int numberOfFATs = bootSector[16] & 0xFF;
            int sectorsPerFAT = ((bootSector[39] & 0xFF) << 24) |
                                ((bootSector[38] & 0xFF) << 16) |
                                ((bootSector[37] & 0xFF) << 8) |
                                (bootSector[36] & 0xFF);
            int rootCluster = ((bootSector[47] & 0xFF) << 24) |
                              ((bootSector[46] & 0xFF) << 16) |
                              ((bootSector[45] & 0xFF) << 8) |
                              (bootSector[44] & 0xFF);
            int sectorsPerCluster = bootSector[13] & 0xFF;

            // Start of the first FAT table
            int fatStartSector = reservedSectors;
            int fatSize = sectorsPerFAT * sectorSize;

            // Read the first FAT copy
            device.seek(fatStartSector * sectorSize);
            byte[] fat = new byte[fatSize];
            device.readFully(fat);

            // Traverse the cluster chain for the root directory
            int cluster = rootCluster;
            int clusterCount = 0;

            while (cluster < 0x0FFFFFF8) { // End of cluster chain for FAT32
                clusterCount++;
                int fatIndex = cluster * 4;
                cluster = ((fat[fatIndex + 3] & 0xFF) << 24) |
                          ((fat[fatIndex + 2] & 0xFF) << 16) |
                          ((fat[fatIndex + 1] & 0xFF) << 8) |
                          (fat[fatIndex] & 0xFF);
                cluster &= 0x0FFFFFFF; // Mask to get only 28 bits as FAT32 uses 28 bits for cluster addresses
            }

            // Calculate the size of the root directory
            return clusterCount * sectorsPerCluster * sectorSize/1024;

            //System.out.printf("Root directory size: %d bytes%n", rootDirSize);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return 0;
    }

    private static ClusterNode FAT32ReadandDelete(String externalDrivePath){
        int sectorSize = 512; // Typical sector size for FAT

        try (RandomAccessFile device = new RandomAccessFile(externalDrivePath, "r")) {
            // Read the boot sector
            byte[] bootSector = new byte[sectorSize];
            device.readFully(bootSector);

            // Extract necessary information from the boot sector
            int reservedSectors = ((bootSector[15] & 0xFF) << 8) | (bootSector[14] & 0xFF);
            int numberOfFATs = bootSector[16] & 0xFF;
            int sectorsPerFAT = ((bootSector[39] & 0xFF) << 24) |
                                ((bootSector[38] & 0xFF) << 16) |
                                ((bootSector[37] & 0xFF) << 8) |
                                (bootSector[36] & 0xFF);

            // Start of the first FAT table
            int fatStartSector = reservedSectors;
            int fatSize = sectorsPerFAT * sectorSize;

            // Read the first FAT copy
            device.seek(fatStartSector * sectorSize);
            byte[] fat = new byte[fatSize];
            device.readFully(fat);

            // Read the root directory (for simplicity, assuming it's located after the FAT area)
            int rootDirStartSector = fatStartSector + numberOfFATs * sectorsPerFAT;

            int rootDirSize = calcRootdir(externalDrivePath) * 1024; // 32 KB for the root directory
            //System.out.println(rootDirSize);
            byte[] rootDir = new byte[rootDirSize];
            device.seek(rootDirStartSector * sectorSize);
            device.readFully(rootDir);

            // Process the root directory entries
            ClusterNode head = null;
            ClusterNode current = null;

            for (int i = 128; i < rootDir.length; i += 32) {
                // Read a directory entry
                String fileName = new String(rootDir, i, 11).trim();
                int startingCluster = ((rootDir[i + 21] & 0xFF) << 16) |
                      ((rootDir[i + 27] & 0xFF) << 8) |
                      (rootDir[i + 26] & 0xFF);
                //String fileNumberStr = fileName.substring(5, fileName.length() - 5);
                System.out.println(fileName);

                // Check if the file name matches the pattern "File-(file_Number).txt"
                if (fileName.matches("FILE\\d{4}"+"TXT")) {
                    // Extract the file number
                    String fileNumberStr = fileName.substring(5, 8);
                    System.out.println(fileNumberStr);
                    int fileNumber = Integer.parseInt(fileNumberStr);

                    //System.out.printf("Processing file: %s, Starting cluster: %d%n", fileName, startingCluster);

                    // Check if the file number is even
                    if (fileNumber % 2 == 0) {
                        // Follow the cluster chain in the FAT table
                        int cluster = startingCluster;

                        while (cluster < 0x0FFFFFF8) { // End of cluster chain for FAT32
                            //System.out.printf("Cluster address: %08X%n", cluster);

                            // Create a new node for the cluster address
                            ClusterNode newNode = new ClusterNode(cluster);

                            if (head == null) {
                                head = newNode;
                                current = head;
                            } else {
                                current.next = newNode;
                                current = newNode;
                            }

                            // Get the next cluster from the FAT table
                            int fatIndex = cluster * 4;
                            cluster = ((fat[fatIndex + 3] & 0xFF) << 24) |
                                      ((fat[fatIndex + 2] & 0xFF) << 16) |
                                      ((fat[fatIndex + 1] & 0xFF) << 8) |
                                      (fat[fatIndex] & 0xFF);

                            // Mask to get only 28 bits as FAT32 uses 28 bits for cluster addresses
                            cluster &= 0x0FFFFFFF;
                        }
                    }
                }
            }

            System.out.println("Linked list of clusters for files ending with an even number:");
            ClusterNode temp = head;
            while (temp != null) {
                System.out.printf("Cluster address: %08X%n", temp.clusterAddress);
                temp = temp.next;
            }
            temp = head;
            while (temp != null) {
                int fatIndex = temp.clusterAddress * 4;
                fat[fatIndex] = 0x00;
                fat[fatIndex + 1] = 0x00;
                fat[fatIndex + 2] = 0x00;
                fat[fatIndex + 3] = 0x00;
                temp = temp.next;
            }

            // Write the updated FAT table back to the device
            device.seek(fatStartSector * sectorSize);
            device.write(fat);

            System.out.println("Marked clusters as free and updated FAT table.");
            return temp;

        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;

    }



}
