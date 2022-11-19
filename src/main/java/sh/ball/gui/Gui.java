package sh.ball.gui;

import com.sun.javafx.PlatformUtil;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import sh.ball.audio.engine.AudioDevice;
import sh.ball.audio.engine.AudioEngine;
import sh.ball.audio.engine.ConglomerateAudioEngine;
import sh.ball.audio.midi.MidiCommunicator;
import sh.ball.graph.GraphView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.*;

public class Gui extends Application {

  // These need to be global so that we can guarantee they can be accessed by Controllers
  public static final MidiCommunicator midiCommunicator = new MidiCommunicator();
  public static String LOG_DIR = "./logs/";
  public static final Logger logger = Logger.getLogger(Gui.class.getName());
  public static AudioEngine audioEngine;
  public static AudioDevice defaultOutputDevice;
  public static AudioDevice defaultInputDevice;

  static {
    try {
      audioEngine = new ConglomerateAudioEngine();
      defaultOutputDevice = audioEngine.getDefaultOutputDevice();
      defaultInputDevice = audioEngine.getDefaultInputDevice();

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
    // testing audio engine
    new Thread(() -> {
      var ref = new Object() {
        double phase = 0;
      };
      try {
        audioEngine.play(() -> new float[]{
            (float) Math.sin(++ref.phase / 100),
            (float) Math.cos(ref.phase / 100)
        }, defaultOutputDevice);
      } catch (Exception e) {
        logger.log(Level.SEVERE, e.getMessage(), e);
      }
    }).start();
  }

  @Override
  public void start(Stage stage) {
    System.setProperty("prism.lcdtext", "false");
    System.setProperty("org.luaj.luajc", "true");

    Thread.setDefaultUncaughtExceptionHandler((t, e) -> logger.log(Level.SEVERE, e.getMessage(), e));

    ContextMenu contextMenu = new ContextMenu();
    Scene scene = new Scene(new GraphView(contextMenu).getParent());
    stage.setScene(scene);
    stage.show();

    scene.setOnKeyPressed(e -> {
      if (e.getCode() == KeyCode.SHIFT) {
        contextMenu.show(scene.getWindow(), stage.getX(), stage.getY() + stage.getHeight() / 2 - 20);
      }
    });

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
