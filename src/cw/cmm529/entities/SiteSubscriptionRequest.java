package cw.cmm529.entities;

import cmm529.coursework.friend.model.SubscriptionRequest;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBScanExpression;
import com.amazonaws.services.dynamodbv2.datamodeling.PaginatedList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import cw.cmm529.util.Dynamo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Extension to the SubscriptionRequest model
 *
 * @author a.molcanovas@gmail.com
 */
public class SiteSubscriptionRequest extends SubscriptionRequest {

    /**
     * Default constructor
     */
    public SiteSubscriptionRequest() {
        super();
    }

    /**
     * Constructor
     *
     * @param subscriberId The subscribing user
     * @param subscribeTo  The subscription target
     * @param timeStamp    {@link #timeStamp} value
     */
    public SiteSubscriptionRequest(String subscriberId, String subscribeTo, long timeStamp) {
        super(subscriberId, subscribeTo, timeStamp);
    }

    /**
     * Constructor. The {@link #timeStamp} is set to {@link System#currentTimeMillis()}
     *
     * @param subscriberId The subscribing user
     * @param subscribeTo  The subscription target
     */
    public SiteSubscriptionRequest(String subscriberId, String subscribeTo) {
        this(subscriberId, subscribeTo, System.currentTimeMillis());
    }

    /**
     * Check if this subscription request already exists in the database
     *
     * @return True if it does, false if the subscriber or subscribee IDs are null or the subscription request
     * doesn't exist in the database
     */
    public boolean exists() {
        return exists(this.getSubscriberId(), this.getSubscribeTo());
    }

    /**
     * Check if this subscription request already exists in the database
     *
     * @param subscriber The subscribing user's ID
     * @param target     The subscription target's ID
     * @return True if it does, false if the subscriber or subscribee IDs are null or the subscription request
     * doesn't exist in the database
     */
    public static boolean exists(final String subscriber, final String target) {
        if (null == subscriber || null == target) {
            return false;
        } else {
            return Dynamo.newMapper().load(SiteSubscriptionRequest.class, target, subscriber) != null;
        }
    }

    /**
     * Delete a subscription request from the database
     *
     * @param subscriber  The subscribing user
     * @param subscribeTo Subscription target
     */
    public static void delete(final String subscriber, final String subscribeTo) {
        final SiteSubscriptionRequest rq = new SiteSubscriptionRequest(subscriber, subscribeTo);
        Dynamo.newMapper().delete(rq);
    }

    /**
     * Get a list of pending subscription requests the given user hasn't accepted or rejected yet.
     *
     * @param forUser The user ID
     * @return An immutable list of subscription requests
     */
    public static List<SiteSubscriptionRequest> getPendingRequests(final String forUser) {
        if (null == forUser) {
            return ImmutableList.of();
        } else {
            final Map<String, AttributeValue> bindings = ImmutableMap.of(":id", new AttributeValue().withS(forUser));

            final DynamoDBQueryExpression<SiteSubscriptionRequest> expr = new DynamoDBQueryExpression<SiteSubscriptionRequest>()
                    .withKeyConditionExpression("subscribeTo = :id")
                    .withExpressionAttributeValues(bindings);

            final PaginatedList<SiteSubscriptionRequest> out = Dynamo.newMapper()
                    .query(SiteSubscriptionRequest.class, expr);
            out.loadAllResults();

            return out;
        }
    }

    /**
     * Gets a list of pending subscription requests sent be the user which haven't been accepted or rejected yet
     *
     * @param fromUser The sending user
     * @return An immutable list of subscription requests
     */
    public static List<SiteSubscriptionRequest> getOutgoingRequests(final String fromUser) {
        if (null == fromUser) {
            return ImmutableList.of();
        } else {
            final Condition condition = new Condition();

            condition.setComparisonOperator(ComparisonOperator.EQ);
            condition.setAttributeValueList(ImmutableList.of(new AttributeValue().withS(fromUser)));

            final DynamoDBScanExpression sexpr = new DynamoDBScanExpression()
                    .withFilterConditionEntry("subscriber", condition);

            final PaginatedList<SiteSubscriptionRequest> out = Dynamo.newMapper()
                    .scan(SiteSubscriptionRequest.class, sexpr);
            out.loadAllResults();

            return out;
        }
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
        return Objects.hash(super.hashCode(), getTimeStamp(), getSubscriberId(), getSubscribeTo());
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
        if (!(o instanceof SubscriptionRequest)) return false;
        SubscriptionRequest that = (SubscriptionRequest) o;
        return Objects.equals(getTimeStamp(), that.getTimeStamp()) &&
                Objects.equals(getSubscriberId(), that.getSubscriberId()) &&
                Objects.equals(getSubscribeTo(), that.getSubscribeTo());
    }
}
