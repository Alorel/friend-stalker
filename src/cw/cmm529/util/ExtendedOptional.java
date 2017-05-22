package cw.cmm529.util;

import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

/**
 * Extension to the {@link Optional} class; all of Optional's methods are available and delegated.
 *
 * @param <T> Type of optional
 * @author a.molcanovas@gmail.com
 */
public class ExtendedOptional<T> {

    /**
     * The enclosed Optional
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private final Optional<T> enclosed;

    /**
     * A function mapping any exception that occurs in any of the {@link Supplier} to this object's type parameter
     */
    private final Function exceptionHandler;

    /**
     * A prebuilt empty optional
     */
    private static final ExtendedOptional<?> empty = new ExtendedOptional<>(Optional.empty());

    /**
     * Constructor with the enclosed Optional
     *
     * @param enclosed The Optional
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ExtendedOptional(final Optional<T> enclosed) {
        this(enclosed, null);
    }

    /**
     * Constructor with the enclosed Optional & exception mapper
     *
     * @param enclosed         The optional
     * @param exceptionHandler The exception mapper
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    private ExtendedOptional(final Optional<T> enclosed, final Function<Throwable, ? extends T> exceptionHandler) {
        this.enclosed = Objects.requireNonNull(enclosed);
        this.exceptionHandler = exceptionHandler;
    }

    /**
     * Clone this Optional with the given exception mapper
     *
     * @param exceptionHandler The mapper
     * @return The new Optional
     */
    public ExtendedOptional<T> withExceptionMapper(final Function<Throwable, ? extends T> exceptionHandler) {
        return continuePipeline(enclosed, exceptionHandler);
    }

    /**
     * Create a new empty Optional
     *
     * @param <T> Its type
     * @return A new Optional
     */
    @SuppressWarnings("unchecked")
    public static <T> ExtendedOptional<T> empty() {
        return (ExtendedOptional<T>) empty;
    }

    /**
     * Create a new Optional
     *
     * @param something What the Optional contains. Must not be null.
     * @param <T>       The optional's type
     * @return The new optional
     */
    public static <T> ExtendedOptional<T> of(T something) {
        return of(Optional.of(something));
    }

    /**
     * Create a new Optional
     *
     * @param something The Optional's contents. May be null.
     * @param <T>       Optional Type
     * @return The newly created Optional
     */
    public static <T> ExtendedOptional<T> ofNullable(T something) {
        return of(Optional.ofNullable(something));
    }

    /**
     * Create a new Optional with the given Optional's contents
     *
     * @param something The source Optional
     * @param <T>       The optional's type
     * @return The created Optional
     * @throws NullPointerException if the argument is null
     */
    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    public static <T> ExtendedOptional<T> of(Optional<T> something) {
        return new ExtendedOptional<>(Objects.requireNonNull(something));
    }

    /**
     * Delegate for {@link Optional#get()}
     *
     * @return
     */
    @SuppressWarnings("ConstantConditions")
    public T get() {
        return enclosed.get();
    }

    /**
     * Delegate for {@link Optional#isPresent()}
     *
     * @return
     */
    public boolean isPresent() {
        return enclosed.isPresent();
    }

    /**
     * Delegate for {@link Optional#ifPresent(Consumer)}
     *
     * @param consumer
     */
    public void ifPresent(Consumer<? super T> consumer) {
        enclosed.ifPresent(consumer);
    }

    /**
     * Delegate for {@link Optional#filter(Predicate)}
     *
     * @param predicate
     * @return
     */
    public ExtendedOptional<T> filter(Predicate<? super T> predicate) {
        return continuePipeline(enclosed.filter(predicate));
    }

    /**
     * Continue the Optional pipeline with the new Optional and this Optional's exception mapper
     *
     * @param enclosed The new Optional
     * @param <U>      The new Optional's type
     * @return The created Optional
     * @throws NullPointerException If enclosed is null
     */
    @SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
    private <U> ExtendedOptional<U> continuePipeline(final Optional<U> enclosed) {
        return continuePipeline(enclosed, exceptionHandler);
    }

    /**
     * Continue the Optional pipeline with the new Optional and this Optional's exception mapper
     *
     * @param enclosed         The new Optional
     * @param exceptionHandler The new Optional's exception mapper. May be null.
     * @param <U>              The new Optional's type
     * @return The created Optional
     * @throws NullPointerException If enclosed is null
     */
    @SuppressWarnings({"unchecked", "OptionalUsedAsFieldOrParameterType"})
    private <U> ExtendedOptional<U> continuePipeline(final Optional<U> enclosed, Function exceptionHandler) {
        return new ExtendedOptional<U>(Objects.requireNonNull(enclosed), exceptionHandler);
    }

    /**
     * Delegate for {@link Optional#map(Function)}. Discards the exception mapper.
     *
     * @param mapper
     * @param <U>
     * @return
     */
    public <U> ExtendedOptional<U> map(Function<? super T, ? extends U> mapper) {
        return map(mapper, null);
    }

    /**
     * Delegate for {@link Optional#flatMap(Function)}. Discards the exception mapper.
     *
     * @param mapper
     * @param <U>
     * @return
     */
    public <U> ExtendedOptional<U> flatMap(Function<? super T, Optional<U>> mapper) {
        return flatMap(mapper, null);
    }

    /**
     * Delegate for {@link Optional#map(Function)}. The newly created Optional contains the provided exception mapper.
     *
     * @param mapper
     * @param exceptionHandler
     * @param <U>
     * @return
     */
    public <U> ExtendedOptional<U> map(Function<? super T, ? extends U> mapper, Function<Throwable, ? extends U> exceptionHandler) {
        return continuePipeline(enclosed.map(mapper), exceptionHandler);
    }

    /**
     * Delegate for {@link Optional#flatMap(Function)}. The newly created Optional contains the provided exception mapper.
     *
     * @param mapper
     * @param exceptionHandler
     * @param <U>
     * @return
     */
    public <U> ExtendedOptional<U> flatMap(Function<? super T, Optional<U>> mapper, Function<Throwable, ? extends U> exceptionHandler) {
        return continuePipeline(enclosed.flatMap(mapper), exceptionHandler);
    }

    /**
     * Delegate for {@link Optional#orElse(Object)}
     *
     * @param other
     * @return
     */
    public T orElse(T other) {
        return enclosed.orElse(other);
    }

    /**
     * Delegate for {@link Optional#orElseGet(Supplier)}
     *
     * @param other
     * @return
     */
    @SuppressWarnings("unchecked")
    public T orElseGet(Supplier<? extends T> other) {
        if (null == exceptionHandler) {
            return enclosed.orElseGet(other);
        } else {
            try {
                return enclosed.orElseGet(other);
            } catch (final Exception e) {
                return (T) exceptionHandler.apply(e);
            }
        }
    }

    /**
     * Delegate for {@link Optional#orElseThrow(Supplier)}
     *
     * @param exceptionSupplier
     * @param <X>
     * @return
     * @throws X
     */
    public <X extends Throwable> T orElseThrow(Supplier<? extends X> exceptionSupplier) throws X {
        return enclosed.orElseThrow(exceptionSupplier);
    }

    /**
     * Continue the pipeline with the given Supplier if the Optional is empty.
     *
     * @param other The supplier of a new Optional
     * @return this if the Optional isn't empty, a new ExtendedOptional if the supplier executes successfully or
     * a value returned by the {@link #withExceptionMapper(Function) exception mapper} if the mapper is set and
     * an exception occurs in the provided supplier instance.
     */
    @SuppressWarnings("unchecked")
    public ExtendedOptional<T> elseTry(Supplier<Optional<T>> other) {
        if (isPresent()) {
            return this;
        } else if (null == exceptionHandler) {
            return continuePipeline(other.get());
        } else {
            try {
                return continuePipeline(other.get());
            } catch (final Exception e) {
                return continuePipeline(Optional.ofNullable((T) exceptionHandler.apply(e)));
            }
        }
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
     * @see java.util.HashMap
     */
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ExtendedOptional)) return false;
        ExtendedOptional<?> that = (ExtendedOptional<?>) o;
        return Objects.equals(enclosed, that.enclosed) &&
                Objects.equals(exceptionHandler, that.exceptionHandler);
    }

    /**
     * Returns a hash code value for the object. This method is
     * supported for the benefit of hash tables such as those provided by
     * {@link java.util.HashMap}.
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
     * according to the {@link java.lang.Object#equals(java.lang.Object)}
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
     * @see java.lang.Object#equals(java.lang.Object)
     * @see java.lang.System#identityHashCode
     */
    @Override
    public int hashCode() {
        return Objects.hash(enclosed, exceptionHandler);
    }
}
