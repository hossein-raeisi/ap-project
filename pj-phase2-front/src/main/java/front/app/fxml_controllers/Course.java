package front.app.fxml_controllers;

import front.app.views.AssignmentView;
import front.app.views.EducationalContentView;
import front.app.views.ExamView;
import front.commons.data_class.AddStudentsData;
import front.services.Client;
import front.services.model_handlers.Assignment;
import front.services.model_handlers.EducationalContent;
import front.services.model_handlers.Exams;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.ArrayList;
import java.util.UUID;

public class Course {

    @FXML
    ListView<EducationalContentView> educationalContents;
    @FXML
    Button newEducationalContent;
    @FXML
    ListView<AssignmentView> assignments;
    @FXML
    Button newAssignment;
    @FXML
    ListView<ExamView> exams;
    @FXML
    Button newExam;
    @FXML
    Button addStudent;
    @FXML
    Button addTA;
    @FXML
    TextField studentIdField;

    @FXML
    public void initialize() {
        var courseData = front.services.model_handlers.Course.getCurrentCourseData();
        var client = Client.getInstance();

        if (!courseData.professorId.equals(client.getCurrentUserData().id)) {
            newEducationalContent.setVisible(false);
            newAssignment.setVisible(false);
            newExam.setVisible(false);

            addStudent.setVisible(false);
            addTA.setVisible(false);
            studentIdField.setVisible(false);
        }

        System.out.println(courseData.educationalContentsId);

        educationalContents.getItems().addAll(
                courseData.educationalContentsId.stream()
                        .map(client::getEducationalContentData)
                        .map(EducationalContentView::new).toList()
        );
        assignments.getItems().addAll(
                client.getCourseAssignments(courseData.id).stream()
                        .map(AssignmentView::new).toList()
        );
        exams.getItems().addAll(
                courseData.examsId.stream()
                        .map(client::getExamData)
                        .map(ExamView::new).toList()
        );


        educationalContents.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, educationalContentView, t1) ->
                        educationalContentSelected(educationalContents.getSelectionModel().getSelectedItem().getEducationalContentId().toString(), courseData.id)
        );
        assignments.getSelectionModel().selectedItemProperty().addListener(
                (observableValue, assignmentView, t1) ->
                        assignmentSelected(assignments.getSelectionModel().getSelectedItem().getAssignmentId().toString())
        );
    }

    public void educationalContentSelected(String educationalContentId, UUID courseId) {
        EducationalContent.showPage(educationalContentId, courseId);
    }

    public void assignmentSelected(String assignmentId) {
        Assignment.showPage(assignmentId);
    }

    public void newEducationalContentButtonAction(ActionEvent ignoredActionEvent) {
        EducationalContent.showNewEducationalContentPage(front.services.model_handlers.Course.getCurrentCourseData().id.toString());
    }

    public void newAssignmentButtonAction(ActionEvent ignoredActionEvent) {
        Assignment.showNewAssignmentPage(front.services.model_handlers.Course.getCurrentCourseData().id.toString());
    }

    public void newExamButtonAction(ActionEvent ignoredActionEvent) {
        Exams.showNewExamPage(front.services.model_handlers.Course.getCurrentCourseData().id.toString());
    }

    public void addStudentButtonAction(ActionEvent ignoredActionEvent) {
        var courseData = front.services.model_handlers.Course.getCurrentCourseData();
        var studentId = UUID.fromString(studentIdField.getText());

        var addStudentsData = new AddStudentsData(
                courseData.id,
                new ArrayList<>() {{
                    add(studentId);
                }}
        );
        Client.getInstance().addStudentToCourse(addStudentsData);

        front.services.model_handlers.Course.showPage(courseData.id.toString());
    }

    public void addTAButtonAction(ActionEvent ignoredActionEvent) {
        var courseData = front.services.model_handlers.Course.getCurrentCourseData();
        var TAId = UUID.fromString(studentIdField.getText());

        var addTAsData = new AddStudentsData(
                courseData.id,
                new ArrayList<>() {{
                    add(TAId);
                }}
        );
        Client.getInstance().addTAToCourse(addTAsData);

        front.services.model_handlers.Course.showPage(courseData.id.toString());
    }
}
