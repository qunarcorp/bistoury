
package qunar.tc.bistoury.serverside.configuration;

/**
 * @author keli.wang
 * @since 2018-11-23
 */
public interface DynamicConfigFactory {
    DynamicConfig create(String name, boolean failOnNotExist);
}
