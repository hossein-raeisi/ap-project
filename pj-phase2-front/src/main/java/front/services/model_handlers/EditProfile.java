package front.services.model_handlers;

import front.app.SceneControl;

public class EditProfile {
    public static void showPage() {
        SceneControl.changeScene(SceneControl.SceneType.editProfilePage);
    }
}
