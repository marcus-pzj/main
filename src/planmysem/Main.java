package planmysem;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;
import planmysem.logic.LogicManager;
import planmysem.storage.Storage;
import planmysem.storage.StorageFile;
import planmysem.ui.Gui;
import planmysem.ui.Stoppable;

/**
 * Main entry point to the application.
 */
public class Main extends Application implements Stoppable {

    /**
     * Version info of the program.
     */
    public static final String VERSION = "PlanMySem - Version 1.2";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Storage storageFile = new StorageFile();
        Gui gui = new Gui(new LogicManager(storageFile), VERSION);
        gui.start(primaryStage, this);
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        Platform.exit();
    }
}


