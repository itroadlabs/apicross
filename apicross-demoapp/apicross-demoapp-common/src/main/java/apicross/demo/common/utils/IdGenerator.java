package apicross.demo.common.utils;

import java.util.UUID;

public class IdGenerator {
    public static String newId() {
        UUID uuid = UUID.randomUUID();
        long id = uuid.getLeastSignificantBits() ^ uuid.getMostSignificantBits();
        return Long.toHexString(id);
    }
}
