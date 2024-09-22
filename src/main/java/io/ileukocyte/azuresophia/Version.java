package io.ileukocyte.azuresophia;

/**
 * An object that is used for better program version organization
 *
 * @param major
 * @param minor
 * @param patch
 * @param stability stability suffix (is not displayed in case {@link Stability#STABLE} is chosen)
 * @param unstable a number of an unstable version
 */
public record Version(int major, int minor, int patch, Stability stability, int unstable) {
    public Version(int major, int minor, int patch, Stability stability) {
        this(major, minor, patch, stability, 0);
    }

    public Version(int major, int minor, int patch) {
        this(major, minor, patch, Stability.STABLE);
    }

    public Version(int major, int minor) {
        this(major, minor, 0);
    }

    @Override
    public String toString() {
        var version = new StringBuilder();

        version.append(major);
        version.append(".");
        version.append(minor);

        if (patch > 0) {
            version.append(".");
            version.append(patch);
        }

        if (stability != Stability.STABLE) {
            version.append("-");
            version.append(stability);

            if (unstable > 0) {
                version.append(unstable);
            }
        }

        return new String(version);
    }

    public enum Stability {
        STABLE,
        RC,
        BETA,
        ALPHA
    }
}