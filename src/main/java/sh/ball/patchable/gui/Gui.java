package sh.ball.patchable.gui;

import com.sun.javafx.PlatformUtil;
import java.util.Objects;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyEvent;
import javafx.stage.Stage;
import sh.ball.patchable.audio.engine.ConglomerateAudioEngine;
import sh.ball.patchable.audio.midi.MidiCommunicator;
import sh.ball.patchable.graph.GraphView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Gui extends Application {

  // These need to be global so that we can guarantee they can be accessed by Controllers
  public static final MidiCommunicator midiCommunicator = new MidiCommunicator();
  public static String LOG_DIR = "./logs/";
  public static final Logger logger = Logger.getLogger(Gui.class.getName());

  static {
    try {
      if (PlatformUtil.isWindows()) {
        LOG_DIR = System.getenv("AppData");
      } else if (PlatformUtil.isUnix() || PlatformUtil.isMac()) {
        LOG_DIR = System.getProperty("user.home");
      } else {
        throw new RuntimeException("OS not recognised");
      }

      LOG_DIR += "/patchable/logs/";

      File directory = new File(LOG_DIR);
      if (!directory.exists()){
        directory.mkdirs();
      }
      Handler fileHandler = new FileHandler(LOG_DIR + new SimpleDateFormat("yyyy.MM.dd.HH.mm.ss").format(new Date()) + ".log", 1024 * 1024, 1);
      fileHandler.setLevel(Level.WARNING);
      fileHandler.setFormatter(new SimpleFormatter());

      Logger javafxLogger = Logger.getLogger("javafx");
      logger.addHandler(fileHandler);
      javafxLogger.addHandler(fileHandler);
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
    }
    new Thread(midiCommunicator).start();
  }

  @Override
  public void start(Stage stage) {
    System.setProperty("prism.lcdtext", "false");
    System.setProperty("org.luaj.luajc", "true");

    Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.log(Level.SEVERE, e.getMessage(), e));

    ContextMenu contextMenu = new ContextMenu();
    GraphView graphView = new GraphView(contextMenu, new ConglomerateAudioEngine());
    Scene scene = new Scene(graphView.getParent());
    scene.getStylesheets().add(getClass().getResource("/css/main.css").toExternalForm());
    stage.setScene(scene);
    stage.show();

    EventHandler<KeyEvent> keyEventHandler = e -> {
      if (e.isShiftDown()) {
        contextMenu.show(scene.getWindow(), stage.getX(), stage.getY() + stage.getHeight() / 2 - 20);
      }
      if (e.isControlDown() && e.getCode().getName().equals("S")) {
        graphView.save();
      }
      if (e.isControlDown() && e.getCode().getName().equals("O")) {
        graphView.load();
      }
      if (e.getCode().getName().equals("Esc")) {
        graphView.deselectAll();
      }
    };
    scene.setOnKeyPressed(keyEventHandler);
    scene.setOnKeyReleased(keyEventHandler);

    stage.setOnCloseRequest(t -> {
      Platform.exit();
      System.exit(0);
    });
  }

  public static void main(String[] args) {
    try {
      launch(args);
    } catch (Exception e) {
      logger.log(Level.SEVERE, e.getMessage(), e);
    }
  }
}
