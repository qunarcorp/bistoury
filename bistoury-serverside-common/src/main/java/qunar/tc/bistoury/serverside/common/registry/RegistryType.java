package qunar.tc.bistoury.serverside.common.registry;

/**
 * @author cai.wen created on 2019/9/2 17:20
 */
public enum RegistryType {

    MOCK(-1), ZOOKEEPER(0), ETCD_V2(1);

    public final int code;

    RegistryType(int code) {
        this.code = code;
    }

    public static RegistryType fromCode(int code) {
        for (RegistryType status : values()) {
            if (status.code == code) {
                return status;
            }
        }
        throw new IllegalArgumentException("no code found in RegistryType");
    }

}
