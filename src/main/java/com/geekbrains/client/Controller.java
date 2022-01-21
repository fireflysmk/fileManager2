package com.geekbrains.client;

import java.io.IOException;
import java.net.Socket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

import com.geekbrains.model.*;

import io.netty.handler.codec.serialization.ObjectDecoderInputStream;
import io.netty.handler.codec.serialization.ObjectEncoderOutputStream;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

public class Controller implements Initializable {

    public ListView<String> clientFiles;
    public ListView<String> serverFiles;
    private Path baseDir;
    private Path serverPath;
    private String userName;
    private ObjectDecoderInputStream is;
    private ObjectEncoderOutputStream os;
    @FXML
    private TextField сlientPathField;
    @FXML
    private TextField serverPathField;

    public TextField input;
    public TextArea output;

    private void read() {
        try {
            while (true) {

                AbstractMessage msg = (AbstractMessage) is.readObject();
                switch (msg.getMessageType()) {
                    case FILE:
                        FileMessage fileMessage = (FileMessage) msg;
                        Files.write(
                                baseDir.resolve(fileMessage.getFileName()),
                                fileMessage.getBytes()
                        );
                        Platform.runLater(() -> fillClientView(getFileNames(baseDir)));
                        break;
                    case FILES_LIST:
                        FilesList files = (FilesList) msg;
                        Platform.runLater(() -> fillServerView(files.getFiles()));
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void fillServerView(List<String> list) {
        serverFiles.getItems().clear();
        serverFiles.getItems().add("...");
        serverFiles.getItems().addAll(list);

    }

    private void fillClientView(List<String> list) {
        clientFiles.getItems().clear();
        clientFiles.getItems().add("...");
        clientFiles.getItems().addAll(list);
    }

    private void fillFirstView() throws IOException {
        //serverPathField.appendText(output.getText().split(" ")[1]);

        try {
            baseDir = Paths.get(System.getProperty("user.dir")).resolve("storage").resolve(output.getText().split(" ")[1]);

            if (!Files.exists(baseDir)) {
                Files.createDirectory(baseDir);
            }
        }
        catch (Exception e) {
            e.printStackTrace();

        }
       getClientFiles(baseDir);
       clientFiles.getItems().add("...");
       clientFiles.getItems().addAll(getFileNames(baseDir));
       System.out.println("baseDir is: " + baseDir);
       сlientPathField.appendText(String.valueOf(baseDir));
       serverPathField.appendText(String.valueOf(serverPath));
       //fillServerView(getFileNames(serverPath));


    }


    private List<FileInfo> getClientFiles(Path path) throws IOException {
        return Files.list(path)
                .map(FileInfo::new)
                .collect(Collectors.toList());
    }

    private List<String> getFileNames(Path path) {
        try {
            return Files.list(path)
                    .map(p -> p.getFileName().toString())
                    .collect(Collectors.toList());
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            //serverPathField.appendText(output.getText());
         //   baseDir = Paths.get(System.getProperty("user.home"));
        //    baseDir = Paths.get(System.getProperty("user.dir")).resolve("storage").resolve(output.getText());
           // String userName = InitialController.getUserName();
            //log.debug("userName is: " + userName);

            Path defaultPath = Paths.get(System.getProperty("user.dir")).resolve("storage").resolve("ServerCommonStorage");
            this.serverPath = defaultPath;

            Platform.runLater(() -> {
                try {
                    fillFirstView();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            // for test


          // clientFiles.getItems().addAll(getFileNames());
         //  System.out.println("baseDir is: " + baseDir);
          //  сlientPathField.appendText(String.valueOf(baseDir));

            clientFiles.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    String file = clientFiles.getSelectionModel().getSelectedItem();
                    System.out.println("selectedItem:" + file);

                    if (file.equals("...")) {
                        baseDir = baseDir.getParent();
                        System.out.println("new PATH = " + baseDir);
                        fillClientView(getFileNames(baseDir));
                        сlientPathField.appendText(baseDir.toString());

                    } else {
                        Path path = baseDir.resolve(file);
                        if (Files.isDirectory(path)) {
                            baseDir = path;
                            fillClientView(getFileNames(baseDir));
                            сlientPathField.appendText(String.valueOf(baseDir));
                        }
                    }

                }
            });
            serverFiles.setOnMouseClicked(e -> {
                if (e.getClickCount() == 2) {
                    String file = serverFiles.getSelectionModel().getSelectedItem();
                    System.out.println("selectedItem:" + file);

                    if (file.equals("...")) {
                        serverPath = serverPath.getParent();
                        System.out.println("new PATH = " + serverPath);
                        fillServerView(getFileNames(serverPath));
                        serverPathField.appendText(serverPath.toString());

                    } else {
                        Path path = serverPath.resolve(file);
                        if (Files.isDirectory(path)) {
                            serverPath = path;
                            fillServerView(getFileNames(serverPath));
                            serverPathField.appendText(String.valueOf(baseDir));
                        }
                    }

                }
            });

            Socket socket = new Socket("localhost", 8189);
            os = new ObjectEncoderOutputStream(socket.getOutputStream());
            is = new ObjectDecoderInputStream(socket.getInputStream());
            Thread thread = new Thread(this::read);
            thread.setDaemon(true);
            thread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void upload(ActionEvent actionEvent) throws IOException {
        String file = clientFiles.getSelectionModel().getSelectedItem();
        Path filePath = baseDir.resolve(file);
        os.writeObject(new FileMessage(filePath));
    }

    public void download(ActionEvent actionEvent) throws IOException {
        String file = serverFiles.getSelectionModel().getSelectedItem();
        os.writeObject(new FileRequest(file));
    }


    public void sendMessage(ActionEvent actionEvent) throws IOException {
        os.writeUTF(input.getText());
    }

    public void sendFile(ActionEvent actionEvent) throws IOException {
    }

    public void getServerPath(ActionEvent actionEvent) throws IOException  {
        //serverPathField.appendText(output.getText());
        Platform.runLater(() -> fillServerView(getFileNames(serverPath)));

    }

    public void getClientPath(ActionEvent actionEvent) throws IOException {
        Platform.runLater(() -> fillClientView(getFileNames(baseDir)));
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void delete(ActionEvent actionEvent) throws IOException {

        String clientFile = clientFiles.getSelectionModel().getSelectedItem();

        try {

        Path clientFilePath = baseDir.resolve(clientFile);
        Files.delete(Paths.get(String.valueOf(clientFilePath)));
        fillClientView(getFileNames(baseDir));

        } catch (NullPointerException e){
            System.out.println("delete: No client files are selected");
        }

        try {
            String serverFile = serverFiles.getSelectionModel().getSelectedItem();

            Path serverFilePath = serverPath.resolve(serverFile);
            os.writeObject(new FileDelete(serverFile));
            fillServerView(getFileNames(serverPath));
        } catch (NullPointerException e) {
            System.out.println("delete: No server files are selected");
        }


    }
}
