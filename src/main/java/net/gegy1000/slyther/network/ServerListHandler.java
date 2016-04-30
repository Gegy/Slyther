package net.gegy1000.slyther.network;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public enum ServerListHandler {
    INSTANCE;

    private List<String> serverList;

    public List<String> getServerList() throws IOException {
        if (serverList == null) {
            serverList = decodeServerList(getEncodedServerList());
        }
        return serverList;
    }

    /**
     * Ported from minimalized JavaScript, don't blame me for this mess
     */
    private List<String> decodeServerList(String encoded) {
        List<String> decoded = new ArrayList<>();
        int e = 0;
        int u = 0;
        int f = 0;
        int c = 0;
        List<Integer> octets = new ArrayList<>();
        List<Integer> E = new ArrayList<>();
        List<Integer> t = new ArrayList<>();
        List<Integer> x = new ArrayList<>();
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
                    E.add(u);
                    if (E.size() == 3) {
                        f++;
                    }
                } else if (f == 2) {
                    t.add(u);
                    if (t.size() == 3) {
                        f++;
                    }
                } else if (f == 3 && x.size() == 0) {
                    x.add(u);
                    for (f = w = 0; f < E.size(); f++) {
                        w *= 256;
                        w += E.get(f);
                    }
                    int EE;
                    for (f = EE = 0; f < t.size(); f++) {
                        EE *= 256;
                        EE += t.get(f);
                    }
                    String ip = "";
                    for (int octet : octets) {
                        ip += octet + ".";
                    }
                    ip = ip.substring(0, ip.length() - 1);
                    decoded.add(ip + ":" + w);
                    octets.clear();
                    E.clear();
                    t.clear();
                    x.clear();
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
        URL url = new URL("http://slither.io/i49526.txt");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.setRequestProperty("User-Agent", "Slyther");
        Scanner scanner = new Scanner(connection.getInputStream());
        String encodedServerList = "";
        while (scanner.hasNextLine()) {
            encodedServerList += scanner.nextLine();
        }
        scanner.close();
        return encodedServerList;
    }
}
