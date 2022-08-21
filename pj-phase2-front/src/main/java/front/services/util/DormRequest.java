package front.services.util;

import front.app.SceneControl;
import front.services.Client;

public class DormRequest {

    public static void showPage(){
        SceneControl.changeScene(SceneControl.SceneType.dormRequestPage);
    }

    public static void requestDorm() {
        Client.getInstance().requestDorm();
    }
}
