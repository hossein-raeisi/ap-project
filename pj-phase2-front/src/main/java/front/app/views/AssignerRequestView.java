package front.app.views;

import front.commons.data_class.RequestData;
import front.app.Util;
import front.services.Client;
import javafx.scene.control.Label;
import javafx.scene.layout.Pane;
import javafx.scene.text.Font;

import java.util.UUID;

public class AssignerRequestView extends Pane{

    Label title = new Label();
    Label status = new Label();
    Label assignee = new Label();
    Label id = new Label();
    UUID requestId;


    public AssignerRequestView(RequestData requestData){
        requestId = requestData.id;
        Client client = Client.getInstance();

        setPrefWidth(1500);
        setPrefHeight(40);

        Font f = new Font(18);

        title.setText(requestData.title);
        title.setFont(f);
        getChildren().add(title);
        title.setLayoutX(5);
        title.setLayoutY(5);

        status.setText(requestData.status.toString());
        status.setFont(f);
        getChildren().add(status);
        status.setLayoutX(500);
        status.setLayoutY(5);

        StringBuilder assignees = new StringBuilder();
        for (UUID userId: requestData.assigneeIds){
            assignees.append(Util.getUserFullName(client.getUserData(userId))).append(", ");
        }
        assignee.setText(assignees.toString());
        assignee.setFont(f);
        getChildren().add(assignee);
        assignee.setLayoutX(1000);
        assignee.setLayoutY(5);

        id.setText(String.valueOf(requestData.id));
        id.setFont(f);
        getChildren().add(id);
        id.setLayoutX(1300);
        id.setLayoutY(5);
    }
}
