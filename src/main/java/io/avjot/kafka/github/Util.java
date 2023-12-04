package io.avjot.kafka.github;

public class Util {

    public static String getVersion() {
        try {
            return Util.class.getPackage().getImplementationVersion();
        } catch (Exception e) {
            return "0.0.0.0";
        }
    }
}
