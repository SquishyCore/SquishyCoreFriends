package dev.mqrio.squishycorefriends.paginator;

import java.util.Arrays;

public class Paginator {
    public String[][] Paginator(String[] lines, int linesPerPage) {
        if(linesPerPage <= 0) {
            return null;
        }

        int rest = lines.length % linesPerPage;
        int chunks = lines.length / linesPerPage + (rest > 0 ? 1 : 0);
        String[][] arrays = new String[chunks][];
        for(int i = 0; i < (rest > 0 ? chunks - 1 : chunks); i++) {
            arrays[i] = Arrays.copyOfRange(lines, i * linesPerPage, i * linesPerPage + linesPerPage);
        }
        if(rest > 0) {
            arrays[chunks - 1] = Arrays.copyOfRange(lines, (chunks - 1) * linesPerPage, (chunks - 1) * linesPerPage + rest);
        }
        return arrays;
    }
}
