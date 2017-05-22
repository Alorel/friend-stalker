package cw.cmm529.entities;

import cmm529.coursework.friend.model.Subscription;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.transactions.Transaction;
import com.google.common.collect.ImmutableSet;
import cw.cmm529.util.Dynamo;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Stream;

/**
 * Overrides and extensions for the Subscription model
 *
 * @author a.molcanovas@gmail.com
 */
public class SiteSubscription extends Subscription {

    /**
     * Flat-maps the results of {@link DynamoDBMapper#batchLoad(Iterable)}, turning them into a contiguous stream
     */
    @SuppressWarnings("unchecked")
    private static final Function<List, Stream<SiteUser>> siteUserStreamFlatMapper = objects -> ((List<SiteUser>) objects).stream();

    /**
     * Default constructor
     */
    public SiteSubscription() {
        super();
    }

    /**
     * Constructor
     *
     * @param subscriberId The subscribing user
     * @param subscribeTo  A single user that's subscribed to
     */
    public SiteSubscription(String subscriberId, String subscribeTo) {
        super(subscriberId, subscribeTo);
    }

    /**
     * Constructor
     *
     * @param subscriberId The subscribing user
     * @param subscribeTo  A set of users subscribed to
     */
    public SiteSubscription(String subscriberId, Set<String> subscribeTo) {
        super(subscriberId, subscribeTo);
    }

    /**
     * Create a subscription. The subscription request entry is deleted from the database and a new subscription entry
     * is created in its place.
     *
     * @param subscriber  Subscribing user
     * @param subscribeTo Subscription recipient
     * @throws Exception Should the transaction fail.
     */
    public static void create(final String subscriber, final String subscribeTo) throws Exception {
        SiteSubscription subscription = new SiteSubscription();
        subscription.setSubscriberId(subscriber);

        final SiteSubscriptionRequest request = new SiteSubscriptionRequest();
        request.setSubscriberId(subscriber);
        request.setSubscribeTo(subscribeTo);

        final Transaction tx = Dynamo.newTransaction();
        try {
            subscription = Optional.ofNullable(tx.load(subscription)).orElse(subscription);
            subscription.addSubscription(subscribeTo);

            tx.save(subscription);
            tx.delete(request);
            tx.commit();
            tx.delete();
        } catch (final Exception e) {
            tx.rollback();
            throw e;
        }
    }

    /**
     * Fixed your null pointer exceptions, boss
     *
     * @return A set of user IDs we're subscribed to or an empty set if no user IDs match
     */
    @Override
    @DynamoDBAttribute(attributeName = "subscribeTo")
    public Set<String> getSubscribeTo() {
        return Optional.ofNullable(super.getSubscribeTo()).orElse(new HashSet<>());
    }

    /**
     * An extension to {@link #getSubscribeTo()} which also {@link DynamoDBMapper#batchLoad(Iterable) loads} every
     * entity
     *
     * @return An immutable set of SiteUser entities.
     */
    public Set<SiteUser> retrieveSubscribeToAsEntities() {
        if (!getSubscribeTo().isEmpty()) {
            final Set<SiteUser> usersUnloaded = getSubscribeTo()
                    .stream()
                    .map(SiteUser::new)
                    .collect(ImmutableSet.toImmutableSet());

            return Dynamo.newMapper()
                    .batchLoad(usersUnloaded)
                    .values()
                    .stream()
                    .flatMap(siteUserStreamFlatMapper)
                    .collect(ImmutableSet.toImmutableSet());
        }
        return ImmutableSet.of();
    }

    /**
     * Attempts to load a SiteSubscription based on the given subscriber ID
     *
     * @param id The subscriber ID
     * @return An Optional with the SiteSubscription or an empty Optional if the subscription could not be found
     */
    public static Optional<SiteSubscription> getForSubscriberID(final String id) {
        if (null == id) {
            return Optional.empty();
        } else {
            return Optional.ofNullable(Dynamo.newMapper().load(SiteSubscription.class, id));
        }
    }

    /**
     * Adds a new subscription target for the given {@link #getSubscriberId() subscriber}. This object must be
     * {@link DynamoDBMapper#load(Object) loaded} before calling this method.
     *
     * @param subscribeTo The subscription target's ID
     * @return true if subscribeTo is not null and did not already exist as a target
     */
    public boolean addSubscription(final String subscribeTo) {
        if (null != subscribeTo) {
            final Set<String> subs = new HashSet<>(this.getSubscribeTo());
            final boolean ret = subs.add(subscribeTo);
            this.setSubscribeTo(subs);

            return ret;
        }

        return false;
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
        return Objects.hash(super.hashCode(), getSubscriberId(), getSubscribeTo());
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
        if (!(o instanceof Subscription)) return false;
        Subscription that = (Subscription) o;
        return Objects.equals(getSubscriberId(), that.getSubscriberId()) &&
                Objects.equals(getSubscribeTo(), that.getSubscribeTo());
    }

    /**
     * Check if the subscriber is subscribed to the target
     *
     * @param subscriber  The subscriber
     * @param subscribeTo The subscription target
     * @return True if a matching subscription exists, false if any of the arguments are null or no subscriptions match
     * the criteria
     */
    public static boolean exists(final String subscriber, final String subscribeTo) {
        if (null != subscriber && null != subscribeTo) {
            return SiteSubscription.getForSubscriberID(subscriber)
                    .map(SiteSubscription::getSubscribeTo)
                    .map(s -> s.contains(subscribeTo))
                    .orElse(false);
        }

        return false;
    }
}
