package com.checkride.support;

/**
 * Single place where the suites learn about their environment.
 * Everything comes from environment variables with local-dev defaults,
 * so the same tests run unchanged on a laptop and in CI.
 */
public final class Config {

    private Config() {
    }

    public static String baseUrl() {
        // matches aerolane's default host port (8090 — its compose maps
        // ${APP_PORT:-8090}:8080). Override with BASE_URL for any other env.
        return env("BASE_URL", "http://localhost:8090");
    }

    public static String officerUser() {
        return env("OFFICER_USER", "officer");
    }

    public static String officerPassword() {
        return env("OFFICER_PASSWORD", "officer123");
    }

    public static String supervisorUser() {
        return env("SUPERVISOR_USER", "supervisor");
    }

    public static String supervisorPassword() {
        return env("SUPERVISOR_PASSWORD", "supervisor123");
    }

    public static String auditorUser() {
        return env("AUDITOR_USER", "auditor");
    }

    public static String auditorPassword() {
        return env("AUDITOR_PASSWORD", "auditor123");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(env("HEADLESS", "true"));
    }

    private static String env(String key, String fallback) {
        String value = System.getenv(key);
        return value == null || value.isBlank() ? fallback : value;
    }
}
