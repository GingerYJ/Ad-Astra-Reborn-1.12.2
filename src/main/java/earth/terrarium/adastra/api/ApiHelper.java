package earth.terrarium.adastra.api;

import java.util.ServiceLoader;

/**
 * Internal helper for loading API implementations via Java's ServiceLoader mechanism.
 * Third-party mods should not use this class directly.
 *
 * @since 1.12.2
 */
public class ApiHelper {

    /**
     * Loads an API implementation using ServiceLoader.
     *
     * @param clazz The API interface class to load
     * @param <T> The API type
     * @return The loaded API implementation
     * @throws NullPointerException if no implementation is found
     */
    public static <T> T load(Class<T> clazz) {
        ServiceLoader<T> loader = ServiceLoader.load(clazz);
        for (T service : loader) {
            return service;
        }
        throw new NullPointerException("Failed to load api for " + clazz.getName());
    }
}
