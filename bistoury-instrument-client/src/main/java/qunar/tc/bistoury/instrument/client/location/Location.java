package qunar.tc.bistoury.instrument.client.location;

/**
 * @author keli.wang
 */
public final class Location {
    private final String source;
    private final int line;

    public Location(final String source, final int line) {
        this.source = source;
        this.line = line;
    }

    public String getSource() {
        return source;
    }

    public int getLine() {
        return line;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Location location = (Location) o;
        return line == location.line &&
                java.util.Objects.equals(source, location.source);
    }

    @Override
    public int hashCode() {
        return java.util.Objects.hash(source, line);
    }

    @Override
    public String toString() {
        return "Location{" +
                "source='" + source + '\'' +
                ", line=" + line +
                '}';
    }
}
