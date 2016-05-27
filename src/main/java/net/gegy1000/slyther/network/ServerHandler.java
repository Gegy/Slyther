package net.gegy1000.slyther.network;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.util.SystemUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

public enum ServerHandler {
    INSTANCE;

    private volatile int pingedCount;
    private List<Server> serverList;
    private String encodedServerList;
    private Map<String, String> headers;

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();

    public Map<String, String> getHeaders() throws IOException {
        if (headers == null) {
            headers = GSON.fromJson(new InputStreamReader(SlytherClient.class.getResourceAsStream("/data/headers.json")), new TypeToken<Map<String, String>>(){}.getType());
        }
        return headers;
    }

    public List<Server> getServerList() throws IOException {
        if (serverList == null) {
            File cache = new File(SystemUtils.getGameFolder(), "server_list.json");
            try {
                serverList = new ArrayList<>();
                Map<String, List<String>> rawServers = decodeServerList(getEncodedServerList());
                for (Map.Entry<String, List<String>> entry : rawServers.entrySet()) {
                    serverList.add(new Server(entry.getKey(), entry.getValue()));
                }
            } catch (Exception e) {
                System.err.println("Could not access server list, using cache.");
                e.printStackTrace();
                if (cache.exists()) {
                    try {
                        ClusterListContainer clusters = GSON.fromJson(new FileReader(cache), ClusterListContainer.class);
                        for (ClusterJsonContainer container : clusters.clusters) {
                            serverList.add(new Server(container.ip, container.ports));
                        }
                    } catch (Exception cacheError) {
                        System.err.println("Could not load server list cache file!");
                        cacheError.printStackTrace();
                    }
                }
            }
            try {
                cache.createNewFile();
                PrintWriter out = new PrintWriter(new FileWriter(cache));
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
                out.close();
            } catch (IOException e) {
            }
            System.out.println("Found " + serverList.size() + " official server clusters.");
        }
        return serverList;
    }

    private Map<String, List<String>> decodeServerList(String encoded) {
        Map<String, List<String>> decoded = new HashMap<>();
        int e = 0;
        int u = 0;
        int f = 0;
        int c = 0;
        List<Integer> octets = new ArrayList<>();
        List<Integer> portParts = new ArrayList<>();
        int t = 0;
        for (int characterIndex = 1; characterIndex < encoded.length(); characterIndex++) {
            int w = (encoded.charAt(characterIndex) - 97 - e) % 26;
            if (w < 0) {
                w += 26;
            }
            u *= 16;
            u += w;
            e += 7;
            if (c == 1) {
                if (f == 0) {
                    octets.add(u);
                    if (octets.size() == 4) {
                        f++;
                    }
                } else if (f == 1) {
                    portParts.add(u);
                    if (portParts.size() == 3) {
                        f++;
                    }
                } else if (f == 2) {
                    t++;
                    if (t == 3) {
                        f++;
                    }
                } else if (f == 3) {
                    int port = 0;
                    for (Integer portPart : portParts) {
                        port *= 256;
                        port += portPart;
                    }
                    String ip = "";
                    for (int octet : octets) {
                        ip += octet + ".";
                    }
                    ip = ip.substring(0, ip.length() - 1);
                    List<String> cluster = decoded.get(ip);
                    if (cluster == null) {
                        cluster = new ArrayList<>();
                    }
                    cluster.add(String.valueOf(port));
                    decoded.put(ip, cluster);
                    octets.clear();
                    portParts.clear();
                    t = 0;
                    f = 0;
                }
                c = u = 0;
            } else {
                c++;
            }
        }
        return decoded;
    }

    private String getEncodedServerList() throws IOException {
        if (encodedServerList == null) {
            URL url = new URL("http://slither.io/i49526.txt");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("User-Agent", "Slyther");
            Scanner scanner = new Scanner(connection.getInputStream());
            encodedServerList = "";
            while (scanner.hasNextLine()) {
                encodedServerList += scanner.nextLine();
            }
            scanner.close();
        }
        return encodedServerList;
    }

    public int getPingedCount() {
        return pingedCount;
    }

    public void resetPingedServerCount() {
        pingedCount = 0;
    }

    public class Server implements Comparable<Server> {
        private String ip;
        private List<String> ports;
        private long ping = -1;

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

        public long getPing() {
            return ping;
        }

        public void setPing(long[] pings) {
            ping = 0;
            for (long ping : pings) {
                this.ping += ping;
            }
            ping /= pings.length;
            ServerHandler.INSTANCE.pingedCount++;
        }

        @Override
        public int compareTo(Server server) {
            return Long.compare(ping != -1 ? ping : Long.MAX_VALUE, server.ping != -1 ? server.ping : Long.MAX_VALUE);
        }
    }

    private class ClusterListContainer {
        public List<ClusterJsonContainer> clusters;
    }

    private class ClusterJsonContainer {
        public String ip;
        public List<String> ports;
    }
}
