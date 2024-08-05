import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;

public class FileProcessorGUI extends JFrame {

    private JTextField inputFilePathField;
    private JTextField outputFilePathField;
    private JTextArea outputArea;
    private JProgressBar progressBar;

    public FileProcessorGUI() {
        setTitle("File Processor");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create UI elements
        JLabel inputLabel = new JLabel("Input File Path:");
        inputFilePathField = new JTextField(20);
        JButton browseButton = new JButton("Browse...");
        JButton processButton = new JButton("Process File");
        outputArea = new JTextArea(10, 50);
        outputArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(outputArea);

        // Create progress bar
        progressBar = new JProgressBar();   
        progressBar.setStringPainted(true);

        // Add ActionListener to the browse button
        browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                selectFile();
            }
        });

        // Add ActionListener to the process button
        processButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                processFile();
            }
        });

        // Layout setup
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10);

        gbc.gridx = 0;
        gbc.gridy = 0;
        add(inputLabel, gbc);

        gbc.gridx = 1;
        add(inputFilePathField, gbc);

        gbc.gridx = 2;
        add(browseButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 1;
        gbc.gridwidth = 3;
        gbc.anchor = GridBagConstraints.CENTER;
        add(processButton, gbc);

        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 3;
        add(scrollPane, gbc);

        gbc.gridx = 0;
        gbc.gridy = 3;
        gbc.gridwidth = 3;
        add(progressBar, gbc);

        // Show the initial prompt when the GUI is opened
        showInitialPrompt();
    }

    private void showInitialPrompt() {
        String[] options = {"Retrieve Data", "Hide Data"};
        int choice = JOptionPane.showOptionDialog(this,
                "Do you want to retrieve or hide the data?",
                "Select Option",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.INFORMATION_MESSAGE,
                null,
                options,
                options[0]);

        // Handle the user's choice
        if (choice == 0) {
            // Retrieve data
            outputArea.setText("Data retrieval option selected.");
            
        } else if (choice == 1) {
            // Hide data
            outputArea.setText("Data hiding option selected.");
        } else {
            // User closed the dialog
            outputArea.setText("No option selected.");
        }
    }

    private void selectFile() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            File selectedFile = fileChooser.getSelectedFile();
            inputFilePathField.setText(selectedFile.getAbsolutePath());
        }
    }

    private void processFile() {
        String inputFilePath = inputFilePathField.getText();
        if (inputFilePath.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please provide the input file path.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // Clear the output area
        outputArea.setText("");

        // Call the processFile method from Main class with progress bar
        SwingWorker<Void, Void> worker = new SwingWorker<>() {
            @Override
            protected Void doInBackground() throws Exception {
                try {
                    Main.processFile(inputFilePath, outputArea, progressBar);
                } catch (IOException e) {
                    e.printStackTrace();
                    outputArea.append("Error during file processing.\n");
                }
                return null;
            }

            @Override
            protected void done() {
                JOptionPane.showMessageDialog(FileProcessorGUI.this, "File processing completed.", "Success", JOptionPane.INFORMATION_MESSAGE);
            }
        };
        worker.execute();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new FileProcessorGUI().setVisible(true);
            }
        });
    }
}

