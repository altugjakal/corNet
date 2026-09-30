package com.example.index;

import java.util.HashMap;

public class GlobalStatistics {

    private static HashMap<Integer, Float> avgFieldLengths;

    private void updateFieldLengths(int fieldId, int fieldLength) {
        avgFieldLengths.putIfAbsent(fieldId, avgFieldLengths.getOrDefault(fieldId, 0f) + fieldLength);

    }


}
