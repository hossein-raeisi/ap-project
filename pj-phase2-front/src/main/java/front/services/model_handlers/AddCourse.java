package front.services.model_handlers;

import front.app.SceneControl;

public class AddCourse {

    public static void showPage() {
        SceneControl.changeScene(SceneControl.SceneType.addCoursePage);
    }
}
