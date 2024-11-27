package io.github.defective4.ham.fmplotter;

public class WavCalculator {

    private WavCalculator() {}

    public static long calculateLength(long size, int sampleRate, int bits) {
        return size / (bits / 8 * sampleRate) * 1000;
    }
}
