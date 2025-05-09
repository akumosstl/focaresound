import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Properties;
import java.util.concurrent.Executors;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

public class CommandLine {
    static Logger logger = Logger.getLogger(CommandLine.class.getName());
    static FileHandler fh;
    private static Mixer.Info infoCable;
    private static Mixer.Info infoMic;
    private static Mixer.Info infoSpeakers;
    private static Clip clip;
    private static Thread server;
    public static String path;
    static Properties prop;

    public static void main(String[] args) {
        loadProperties();
        loadLog();
        HttpServer server = null;
        try {
            int port = Integer.parseInt(prop.getProperty("focare.server.port"));
            server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
        server.createContext("/play", new PlayHandler());
        server.createContext("/stop", new StopHandler());

        server.setExecutor(Executors.newFixedThreadPool(100));
        server.start();

        init();

    }

    static void loadLog(){
            SimpleDateFormat format = new SimpleDateFormat("M-d_HHmmss");
            try {
                fh = new FileHandler(prop.getProperty("focare.log.path") + "\\focare_"
                        + format.format(Calendar.getInstance().getTime()) + ".log");
            } catch (Exception e) {
                e.printStackTrace();
            }

            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);

            logger.info("log engine carregado...");

    }

    static void loadProperties() {
        prop = new Properties();
        InputStream stream = null;
        try {
            stream = Files.newInputStream(Paths.get("focare.properties"));
            prop.load(stream);
        } catch (IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    public static void init() {
        path = prop.getProperty("focare.path");

        logger.info("Exibindo todos os devices de in/out: ");
        Mixer.Info[] infos = AudioSystem.getMixerInfo();
        for (int i = 0; i < infos.length; i++) {
            String name = infos[i].getName();

            logger.info(i + " - " + name);
            if (name.contains(prop.get("focare.mic.name").toString())) {
                infoMic = (infos[i]);
            } else if (name.contains(prop.get("focare.cable.name").toString())) {
                infoCable = (infos[i]);
            } else if (name.contains(prop.get("focare.speakers.name").toString())) {
                infoSpeakers = (infos[i]);
            }
        }
        logger.info("==========================================");
        logger.info("Exibindo devices selecionados:");
        logger.info("Cable: " + infoCable);
        logger.info("Speakers: " + infoSpeakers);
        logger.info("Mic: " + infoMic);
        logger.info("==========================================");
    }

    public static void play(File file) {
        if (file.getName().replaceAll("^.*\\.(.*)$", "$1").equals("wav")) {
            try {
                playClip(AudioSystem.getAudioInputStream(file), infoCable);
                playClip(AudioSystem.getAudioInputStream(file), infoSpeakers);
            } catch (UnsupportedAudioFileException | IOException e) {
                logger.log(Level.SEVERE, e.getMessage());
                throw new RuntimeException(e);
            }

        }
    }

    public static void stop() {
        clip.stop();
    }

    private static void playClip(AudioInputStream stream, Mixer.Info info) {
        try {
            clip = AudioSystem.getClip(info);

            clip.addLineListener(e -> {
                LineEvent.Type t = e.getType();
                if (t == LineEvent.Type.STOP) {
                    if (clip.getMicrosecondLength() == clip.getMicrosecondPosition())
                        clip.close();
                }
            });
            clip.open(stream);
            clip.start();
        } catch (LineUnavailableException | IOException e) {
            logger.log(Level.SEVERE, e.getMessage());
            throw new RuntimeException(e);
        }
    }

    static class PlayHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Play aceito!";

            play(new File(path));

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

    static class StopHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange t) throws IOException {
            String response = "Stop aceito!";

            stop();

            t.sendResponseHeaders(200, response.length());
            OutputStream os = t.getResponseBody();
            os.write(response.getBytes());
            os.close();
        }
    }

}