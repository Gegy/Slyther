package net.gegy1000.slyther.network.message.client;

import java.util.ArrayList;
import java.util.List;
import java.util.Arrays;

import net.gegy1000.slyther.client.SlytherClient;
import net.gegy1000.slyther.network.MessageByteBuffer;
import net.gegy1000.slyther.network.message.SlytherClientMessageBase;
import net.gegy1000.slyther.server.ConnectedClient;
import net.gegy1000.slyther.server.SlytherServer;


public class MessageClientRiddleAnswer extends SlytherClientMessageBase {
    private String answer;

    public MessageClientRiddleAnswer(String riddle) {
        char[] key = this.getKey(riddle);
        this.answer = this.convertKey(Arrays.copyOfRange(key, 7, 31));
    }

    private char[] getKey(String riddle) {
        List<Character> res = new ArrayList<Character>();
        int d = 0;
        int e = 23;
        int f = 0;
        for (int b: riddle.toCharArray()) {
            if (b <= 96) {
                b += 32;
            }
            b = (b - 97 - e) % 26;
            if (b < 0) {
                b += 26;
            }
            d *= 16;
            d += b;
            e += 17;
            if (f == 1) {
                res.add((char)d);
                f = 0;
                d = 0;
            } else {
                f += 1;
            }
        }
        char[] res2 = new char[res.size()];
        for (int i = 0; i < res.size(); i++) {
            res2[i] = res.get(i);
        }
        return res2;
    }

    private String convertKey(char[] key) {
        StringBuilder res = new StringBuilder();

        int b = 0;
        for (int c = 0; c < key.length; c++) {
            int d = 65;
            char a = key[c];
            if (a >= 97) {
                d += 32;
                a -= 32;
            }
            a -= 65;
            if (c == 0) {
                b = (int)(a + 2);
            }
            int e = ((int)a + b) % 26;
            b += 3 + (int)a;
            res.append((char)(e + d));
        }
        return res.toString();
    }

    @Override
    public void write(MessageByteBuffer buffer, SlytherClient client) {
        buffer.writeASCIIBytes(this.answer);
    }

    @Override
    public void read(MessageByteBuffer buffer, SlytherServer server, ConnectedClient client) {
    }
}