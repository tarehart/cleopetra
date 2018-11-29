package dk.aau.cs.ds306e18.tournament.rlbot;

import rlbot.cppinterop.RLBotDll;
import rlbot.flat.GameTickPacket;

import java.io.IOException;

/** The RLBotStalker is used to fetch game tick packets from Rocket League using the RLBot framework. */
public class RLBotStalker {

    private static final long FETCH_INTERVAL_MS = 2500;

    private boolean isRunning;
    private GameTickPacket lastPacket;

    /** Create a new RLBotStalker */
    public RLBotStalker() throws Exception {
        try {
            // Initialize RLBot dll
            String path = ClassLoader.getSystemResource("RLBot_Core_Interface.dll").getPath();
            if (path.startsWith("/")) {
                path = path.substring(1); // Remove leading '/'
            }
            RLBotDll.initialize(path);
            System.out.println("Initialized RLBot interface dll");

        } catch (IOException e) {
            throw new Exception("Could not initialize RLBot dll : " + e.getMessage());
        }
    }

    /** Start fetch packets regularly so we have an updated packet. */
    public void start() {
        isRunning = true;
        new Thread(() -> {
            while (this.isRunning) {
                try {
                    GameTickPacket packet = RLBotDll.getFlatbufferPacket();
                    if (packet != null) {
                        // We have a packet
                        this.lastPacket = packet;
                        float ballz = packet.ball().physics().location().z();
                        System.out.println("Ball height: " + ballz);
                    }

                    // Wait a bit
                    try {
                        Thread.sleep(FETCH_INTERVAL_MS);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                    break;
                }
            }
            this.isRunning = false;
        }).start();
    }

    /** Stop fetching packets. */
    public void stop() {
        isRunning = false;
    }

    public boolean isRunning() {
        return isRunning;
    }

    public GameTickPacket getLastPacket() {
        return lastPacket;
    }
}
