package net.gegy1000.slyther.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonParseException;
import com.google.gson.reflect.TypeToken;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.util.Log;
import net.gegy1000.slyther.util.SystemUtils;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public enum ServerHandler {
    INSTANCE;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    List<Server> serverList;
    List<Server> pingedServers = new ArrayList<>();

    private String encodedServerList;

    private Map<String, String> headers;

    private CountryCodesContainer countryCodes;

    Lock serversAvailable = new ReentrantLock();

    public Map<String, String> getHeaders() {
        if (headers == null) {
            try (InputStreamReader reader = new InputStreamReader(SlytherClient.class.getResourceAsStream("/data/headers.json"))) {
                headers = GSON.fromJson(reader, new TypeToken<Map<String, String>>() {
                }.getType());
            } catch (IOException | JsonParseException e) {
                Log.error("Unable to read headers");
                Log.catching(e);
            } finally {
                // could deserialize null
                if (headers == null) {
                    headers = new HashMap<>();
                }
            }
        }
        return headers;
    }

    public List<Server> getServerList() {
        if (serverList == null) {
            serverList = readServerList();
        }
        return serverList;
    }

    public Map<String, String> getCountryCodes() {
        if (countryCodes == null) {
            try {
                countryCodes = GSON.fromJson(new InputStreamReader(SlytherClient.class.getResourceAsStream("/data/country_codes.json")), CountryCodesContainer.class);
            } catch (Exception e) {
                Log.catching(e);
                return new HashMap<>();
            }
        }
        return countryCodes.codes;
    }

    public Server getServerForPlay() {
        serversAvailable.lock();
        serverList.sort(null);
        Server server = serverList.get(new Random().nextInt(5));
        serversAvailable.unlock();
        return server;
    }

    public List<Server> getPingedServers() {
        return pingedServers;
    }

    private List<Server> readServerList() {
        File cache = new File(SystemUtils.getGameFolder(), "server_list.json");
        List<Server> serverList = new ArrayList<>();
        try {
            Map<String, List<String>> rawServers = decodeServerList(getEncodedServerList());
            for (Map.Entry<String, List<String>> entry : rawServers.entrySet()) {
                serverList.add(new Server(entry.getKey(), entry.getValue()));
            }
        } catch (IOException e) {
            Log.error("Could not access server list, using cache.");
            Log.catching(e);
            if (cache.exists()) {
                try {
                    ClusterListContainer clusters = GSON.fromJson(new FileReader(cache), ClusterListContainer.class);
                    for (ClusterJsonContainer container : clusters.clusters) {
                        serverList.add(new Server(container.ip, container.ports));
                    }
                } catch (Exception cacheError) {
                    Log.error("Could not load server list cache file!");
                    Log.catching(cacheError);
                }
            }
        }
        try (PrintWriter out = new PrintWriter(new FileWriter(cache))) {
            List<ClusterJsonContainer> clusterContainers = new ArrayList<>();
            for (Server server : serverList) {
                ClusterJsonContainer container = new ClusterJsonContainer();
                container.ip = server.getClusterIp();
                container.ports = server.ports;
                clusterContainers.add(container);
            }
            ClusterListContainer clusterListContainer = new ClusterListContainer();
            clusterListContainer.clusters = clusterContainers;
            out.print(GSON.toJson(clusterListContainer));
        } catch (IOException e) {
            Log.catching(e);
        }
        Log.info("Found {} official server clusters.", serverList.size());
        return serverList;
    }

    private Map<String, List<String>> decodeServerList(String encoded) {
        Map<String, List<String>> decoded = new HashMap<>();
        int byteVal = 0, part = 0, read = 0, port = 0;
        boolean hasByte = false;
        ///int ac = 0;
        StringBuilder ipBuf = new StringBuilder();
        char[] chars = encoded.toCharArray();
        //int[] unkDist = new int[256];
        for (int i = 1; i < chars.length; i++) {
            int nibble = (chars[i] - 'a' - (i - 1) * 7) % 26;
            if (nibble < 0) {
                nibble += 26;
            }
            byteVal = byteVal << 4 | nibble;
            if (hasByte) {
                if (part == 0) {
                    ipBuf.append(byteVal);
                    if (++read == 4) {
                        part++;
                        read = 0;
                    } else {
                        ipBuf.append('.');
                    }
                } else if (part == 1) {
                    port = port << 8 | byteVal;
                    if (++read == 3) {
                        part++;
                        read = 0;
                    }
                } else if (part == 2) {
                    //ac = ac << 8 | byteVal;
                    read++;
                    if (read == 3) {
                        part++;
                    }
                } else if (part == 3) {
                    //int clu = byteVal;
                    String ip = ipBuf.toString();
                    List<String> cluster = decoded.get(ip);
                    if (cluster == null) {
                        cluster = new ArrayList<>();
                    }
                    cluster.add(String.valueOf(port));
                    decoded.put(ip, cluster);
                    //unkDist[unk]++;
                    read = part = port = /*ac =*/ 0;
                    ipBuf.setLength(0);
                }
                hasByte = false;
                byteVal = 0;
            } else {
                hasByte = true;
            }
        }
        /*int max = 0;
        for (int i = 0; i < unkDist.length; i++) {
            if (unkDist[i] > max) {
                max = unkDist[i];
            }
        }
        BufferedImage img = new BufferedImage(unkDist.length, max, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = img.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, img.getWidth(), img.getHeight());
        g.setColor(Color.BLACK);
        for (int i = 0; i < unkDist.length; i++) {
            int height = unkDist[i];
            g.drawLine(i, img.getHeight(), i, img.getHeight() - height);
        }
        g.dispose();
        try {
            ImageIO.write(img, "png", new File("unk.png"));
        } catch (IOException e) {}*/
        return decoded;
    }

    private String getEncodedServerList() throws IOException {
        if (encodedServerList == null) {
            URL url = new URL("http://slither.io/i33628.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestProperty("User-Agent", "Slyther");
            InputStream stream = connection.getInputStream();
            encodedServerList = IOUtils.toString(stream);
            stream.close();
        }
        return encodedServerList;
    }

    public void pingServers() {
        new Thread(new ServerPingerDispatcher(), "ServerPingerDispatcher").start();
    }

    public static class Server implements Comparable<Server> {
        private String ip;
        private List<String> ports;
        private int ping = -1;
        private String countryCode;

        public Server(String ip, List<String> ports) {
            this.ip = ip;
            this.ports = ports;
        }

        public String getIp() {
            return ip + ":" + ports.get(new Random().nextInt(ports.size()));
        }

        public String getClusterIp() {
            return ip;
        }

        public List<String> getPorts() {
            return ports;
        }

        public int getPing() {
            return ping;
        }

        public void setPing(int[] pings) {
            ping = 0;
            for (int ping : pings) {
                this.ping += ping;
            }
            ping /= pings.length;
            if (!ServerHandler.INSTANCE.pingedServers.contains(this)) {
                ServerHandler.INSTANCE.pingedServers.add(this);
            }
        }

        public String getCountryCode() {
            if (countryCode == null) {
                new Thread(() -> {
                    try {
                        BufferedReader in = new BufferedReader(new InputStreamReader(new URL("http://api.wipmania.com/" + ip).openStream()));
                        countryCode = in.readLine();
                        Map<String, String> countryCodes = ServerHandler.INSTANCE.getCountryCodes();
                        if (countryCodes.containsKey(countryCode)) {
                            countryCode = countryCodes.get(countryCode);
                        }
                        if (countryCode.equalsIgnoreCase("xx")) {
                            countryCode = "Unknown";
                        }
                    } catch (Exception e) {
                        Log.catching(e);
                    }
                }).start();
                countryCode = "Loading...";
            }
            return countryCode;
        }

        @Override
        public int compareTo(Server server) {
            return Long.compare(ping == -1 ? Long.MAX_VALUE : ping, server.ping == -1 ? Long.MAX_VALUE : server.ping);
        }
    }

    private class ClusterListContainer {
        public List<ClusterJsonContainer> clusters;
    }

    private class ClusterJsonContainer {
        public String ip;
        public List<String> ports;
    }

    private class CountryCodesContainer {
        public Map<String, String> codes;
    }
}
