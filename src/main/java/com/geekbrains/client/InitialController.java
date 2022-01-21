package com.geekbrains.client;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import java.io.IOException;
import java.lang.management.PlatformLoggingMXBean;


public class InitialController  {

    public Label welcome;
    public TextField userName;
    public TextArea console;
    private String user;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }


    private Scene loadMainScene() throws IOException {
       Parent parent = FXMLLoader.load(getClass().getResource("my_client.fxml"));
       return new Scene(parent);
    }
/*
    private void setUserName(String name) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("my_client.fxml"));
        Parent root = loader.load();

        Controller mainSceneController = loader.getController();
        mainSceneController.setUserName(name);
    }
*/
    public void startMainAppWindow (ActionEvent actionEvent) throws IOException {

        this.console.clear();

        String inputUserName = userName.getText();

        if (!"".equals(inputUserName)) {


            FXMLLoader loader = new FXMLLoader(getClass().getResource("my_client.fxml"));
            Parent root = loader.load();
            Controller mainSceneController = loader.getController();
            mainSceneController.setUserName(inputUserName);

            mainSceneController.output.appendText("welcome " + inputUserName);

            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();


            System.out.println("HELLO");

            //setUser(inputUserName);
            /*
            Stage primary = (Stage) this.welcome.getScene().getWindow();
            primary.setScene(loadMainScene());
            primary.show();
            */
        } else {
            this.console.appendText("please Enter UserName");
        }


    }
}
