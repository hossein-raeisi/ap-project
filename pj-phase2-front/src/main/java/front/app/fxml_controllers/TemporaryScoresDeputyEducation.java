package front.app.fxml_controllers;

import front.services.Client;
import front.services.util.AllObjectionRequests;
import front.services.util.CourseDescription;
import front.services.util.ProfessorAllFinalizedScores;
import front.services.util.TemporaryScores;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

import java.util.UUID;

public class TemporaryScoresDeputyEducation {

    @FXML
    Button studentButton;
    @FXML
    TextField studentIdField;
    @FXML
    Button objectionsButton;
    @FXML
    Button professorButton;
    @FXML
    TextField professorIdField;
    @FXML
    Button courseButton;
    @FXML
    TextField courseIdField;

    public void studentButtonAction(ActionEvent ignoredActionEvent) {
        TemporaryScores.showPage(Client.getInstance().getStudentData(UUID.fromString(studentIdField.getText())));
    }

    public void objectionsButtonAction(ActionEvent ignoredActionEvent) {
        AllObjectionRequests.showPage();
    }

    public void professorButtonAction(ActionEvent ignoredActionEvent) {
        ProfessorAllFinalizedScores.showPage(professorIdField.getText());
    }

    public void courseButtonAction(ActionEvent ignoredActionEvent) {
        CourseDescription.showPage(courseIdField.getText());
    }
}
