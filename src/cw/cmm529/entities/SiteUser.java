package cw.cmm529.entities;

import cmm529.coursework.friend.model.Location;
import cmm529.coursework.friend.model.User;
import cw.cmm529.util.Dynamo;

import java.util.HashMap;
import java.util.Objects;
import java.util.Optional;

/**
 * Extension for the User model
 *
 * @author a.molcanovas@gmail.com
 */
public class SiteUser extends User {

    /**
     * Default constructor
     */
    public SiteUser() {
        super();
    }

    /**
     * Constructor
     *
     * @param id          The user's ID
     * @param location    The user's location
     * @param lastUpdated Last update timestamp
     */
    public SiteUser(String id, Location location, long lastUpdated) {
        super(id, location, lastUpdated);
    }

    /**
     * Constructor
     *
     * @param id The user's ID
     */
    public SiteUser(final String id) {
        this();
        setId(id);
    }

    /**
     * Check if the user exists
     *
     * @param id The user's ID
     * @return True if they exist, false if ID is null or doesn't exist in the database
     */
    public static boolean exists(final String id) {
        return null != id && Dynamo.newMapper().load(SiteUser.class, id) != null;

    }

    /**
     * Shorthand for loading this entity from the database
     *
     * @param id The user ID
     * @return An Optional containing the loaded user, or an empty optional if ID is null or doesn't exist in the
     * database
     */
    public static Optional<SiteUser> load(final String id) {
        return null == id ? Optional.empty() : Optional.ofNullable(Dynamo.newMapper().load(SiteUser.class, id));
    }

    /**
     * Set the user's coordinates
     *
     * @param latitude  The check-in latitude
     * @param longitude The check-in longitude
     */
    public void setCoordinates(final double latitude, final double longitude) {
        setLocation(new Location(longitude, latitude));
    }

    /**
     * Set the user's coordinates
     *
     * @param latitude  The check-in latitude
     * @param longitude The check-in longitude
     * @throws NumberFormatException if lat/long aren't valid {@link Double}s
     */
    public void setCoordinates(final String latitude, final String longitude) {
        final double lat = Double.valueOf(Objects.requireNonNull(latitude));
        final double lng = Double.valueOf(Objects.requireNonNull(longitude));
        setCoordinates(lat, lng);
    }

    /**
     * Check if this user exists in the database
     *
     * @return True if they exist, false if ID is null or doesn't exist in the database
     */
    public boolean exists() {
        return exists(this.getId());
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link HashMap}.
     * <p>
     * The general contract of {@code hashCode} is:
     * <ul>
     * <li>Whenever it is invoked on the same object more than once during
     * an execution of a Java application, the {@code hashCode} method
     * must consistently return the same integer, provided no information
     * used in {@code equals} comparisons on the object is modified.
     * This integer need not remain consistent from one execution of an
     * application to another execution of the same application.
     * <li>If two objects are equal according to the {@code equals(Object)}
     * method, then calling the {@code hashCode} method on each of
     * the two objects must produce the same integer result.
     * <li>It is <em>not</em> required that if two objects are unequal
     * according to the {@link Object#equals(Object)}
     * method, then calling the {@code hashCode} method on each of the
     * two objects must produce distinct integer results.  However, the
     * programmer should be aware that producing distinct integer results
     * for unequal objects may improve the performance of hash tables.
     * </ul>
     * <p>
     * As much as is reasonably practical, the hashCode method defined by
     * class {@code Object} does return distinct integers for distinct
     * objects. (This is typically implemented by converting the internal
     * address of the object into an integer, but this implementation
     * technique is not required by the
     * Java&trade; programming language.)
     *
     * @return a hash code value for this object.
     * @see Object#equals(Object)
     * @see System#identityHashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(
                super.hashCode(),
                getLastUpdated(),
                getId(),
                locationHash()
        );
    }

    /**
     * Indicates whether some other object is "equal to" this one.
     * <p>
     * The {@code equals} method implements an equivalence relation
     * on non-null object references:
     * <ul>
     * <li>It is <i>reflexive</i>: for any non-null reference value
     * {@code x}, {@code x.equals(x)} should return
     * {@code true}.
     * <li>It is <i>symmetric</i>: for any non-null reference values
     * {@code x} and {@code y}, {@code x.equals(y)}
     * should return {@code true} if and only if
     * {@code y.equals(x)} returns {@code true}.
     * <li>It is <i>transitive</i>: for any non-null reference values
     * {@code x}, {@code y}, and {@code z}, if
     * {@code x.equals(y)} returns {@code true} and
     * {@code y.equals(z)} returns {@code true}, then
     * {@code x.equals(z)} should return {@code true}.
     * <li>It is <i>consistent</i>: for any non-null reference values
     * {@code x} and {@code y}, multiple invocations of
     * {@code x.equals(y)} consistently return {@code true}
     * or consistently return {@code false}, provided no
     * information used in {@code equals} comparisons on the
     * objects is modified.
     * <li>For any non-null reference value {@code x},
     * {@code x.equals(null)} should return {@code false}.
     * </ul>
     * <p>
     * The {@code equals} method for class {@code Object} implements
     * the most discriminating possible equivalence relation on objects;
     * that is, for any non-null reference values {@code x} and
     * {@code y}, this method returns {@code true} if and only
     * if {@code x} and {@code y} refer to the same object
     * ({@code x == y} has the value {@code true}).
     * <p>
     * Note that it is generally necessary to override the {@code hashCode}
     * method whenever this method is overridden, so as to maintain the
     * general contract for the {@code hashCode} method, which states
     * that equal objects must have equal hash codes.
     *
     * @param o the reference object with which to compare.
     * @return {@code true} if this object is the same as the obj
     * argument; {@code false} otherwise.
     * @see #hashCode()
     * @see HashMap
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof SiteUser)) return false;
        SiteUser that = (SiteUser) o;
        return Objects.equals(getLastUpdated(), that.getLastUpdated()) &&
                Objects.equals(getId(), that.getId()) &&
                locationEquals(o);
    }

    /**
     * Can't extend the {@link Location} class to override its {@link Object#equals(Object) equals} and
     * {@link Object#hashCode() hashCode}, so doing it here instead.
     *
     * @return Whether the locations equal
     */
    private boolean locationEquals(Object o) {
        if (getLocation() == o) return true;
        if ((null == getLocation() && null != o) || (null != getLocation() && null == o) || !(o instanceof Location))
            return false;
        Location that = (Location) o;
        return Objects.equals(getLocation().getLatitude(), that.getLatitude()) &&
                Objects.equals(getLocation().getLongitude(), that.getLongitude());
    }

    /**
     * Can't extend the {@link Location} class to override its {@link Object#equals(Object) equals} and
     * {@link Object#hashCode() hashCode}, so doing it here instead.
     *
     * @return The hashCode of {@link #getLocation()}
     */
    private int locationHash() {
        if (null != getLocation()) {
            return Objects.hash(getLocation().getLatitude(), getLocation().getLongitude());
        }
        return 0;
    }
}
