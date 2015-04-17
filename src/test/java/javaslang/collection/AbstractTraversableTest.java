/*     / \____  _    ______   _____ / \____   ____  _____
 *    /  \__  \/ \  / \__  \ /  __//  \__  \ /    \/ __  \   Javaslang
 *  _/  // _\  \  \/  / _\  \\_  \/  // _\  \  /\  \__/  /   Copyright 2014-2015 Daniel Dietrich
 * /___/ \_____/\____/\_____/____/\___\_____/_/  \_/____/    Licensed under the Apache License, Version 2.0
 */
package javaslang.collection;

import javaslang.Tuple;
import javaslang.Tuple2;
import javaslang.algebra.Monoid;
import javaslang.control.Match;
import javaslang.control.Option;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.*;
import java.util.function.Function;

import static javaslang.Serializables.deserialize;
import static javaslang.Serializables.serialize;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

/**
 * Tests all methods defined in {@link javaslang.collection.Traversable}.
 */
public abstract class AbstractTraversableTest {

    abstract protected <T> Traversable<T> nil();

    @SuppressWarnings("unchecked")
    abstract protected <T> Traversable<T> of(T... elements);

    // -- average

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenAverageOfNil() {
        nil().average();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenAverageOfNonNumeric() {
        of("1", "2", "3").average();
    }

    @Test
    public void shouldComputeAverageOfBoolean() {
        final boolean actual = of(true, false).average();
        assertThat(actual).isEqualTo(true);
    }

    @Test
    public void shouldComputeAverageOfByte() {
        final byte actual = of((byte) 1, (byte) 2).average();
        assertThat(actual).isEqualTo((byte) 1);
    }

    @Test
    public void shouldComputeAverageOfChar() {
        final char actual = of('a', 'b', 'c').average();
        assertThat(actual).isEqualTo('b');
    }

    @Test
    public void shouldComputeAverageOfDouble() {
        final double actual = of(.1d, .2d, .3d).average();
        assertThat(actual).isBetween(.20d, .21d);
    }

    @Test
    public void shouldComputeAverageOfFloat() {
        final float actual = of(.1f, .2f, .3f).average();
        assertThat(actual).isBetween(.20f, .21f);
    }

    @Test
    public void shouldComputeAverageOfInt() {
        final int actual = of(1, 2, 3).average();
        assertThat(actual).isEqualTo(2);
    }

    @Test
    public void shouldComputeAverageOfLong() {
        final long actual = of(1L, 2L, 3L).average();
        assertThat(actual).isEqualTo(2L);
    }

    @Test
    public void shouldComputeAverageOfShort() {
        final short actual = of((short) 1, (short) 2, (short) 3).average();
        assertThat(actual).isEqualTo((short) 2);
    }

    @Test
    public void shouldComputeAverageOfBigInteger() {
        final BigInteger actual = of(BigInteger.ZERO, BigInteger.ONE).average();
        assertThat(actual).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void shouldComputeAverageOfBigDecimal() {
        final BigDecimal actual = of(BigDecimal.ZERO, BigDecimal.ONE).average();
        assertThat(actual).isEqualTo(new BigDecimal("0.5"));
    }

    // -- clear

    @Test
    public void shouldClearNil() {
        assertThat(nil().clear()).isEqualTo(nil());
    }

    @Test
    public void shouldClearNonNil() {
        assertThat(of(1, 2, 3).clear()).isEqualTo(nil());
    }

    // -- contains

    @Test
    public void shouldRecognizeNilContainsNoElement() {
        final boolean actual = nil().contains(null);
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesNotContainElement() {
        final boolean actual = of(1, 2, 3).contains(0);
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilDoesContainElement() {
        final boolean actual = of(1, 2, 3).contains(2);
        assertThat(actual).isTrue();
    }

    // -- containsAll

    @Test
    public void shouldRecognizeNilNotContainsAllElements() {
        final boolean actual = nil().containsAll(of(1, 2, 3));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilNotContainsAllOverlappingElements() {
        final boolean actual = of(1, 2, 3).containsAll(of(2, 3, 4));
        assertThat(actual).isFalse();
    }

    @Test
    public void shouldRecognizeNonNilContainsAllOnSelf() {
        final boolean actual = of(1, 2, 3).containsAll(of(1, 2, 3));
        assertThat(actual).isTrue();
    }

    // -- distinct

    @Test
    public void shouldComputeDistinctOfEmptyTraversable() {
        assertThat(nil().distinct()).isEqualTo(nil());
    }

    @Test
    public void shouldComputeDistinctOfNonEmptyTraversable() {
        assertThat(of(1, 1, 2, 2, 3, 3).distinct()).isEqualTo(of(1, 2, 3));
    }

    // -- drop

    @Test
    public void shouldDropNoneOnNil() {
        assertThat(nil().drop(1)).isEqualTo(nil());
    }

    @Test
    public void shouldDropNoneIfCountIsNegative() {
        assertThat(of(1, 2, 3).drop(-1)).isEqualTo(of(1, 2, 3));
    }

    @Test
    public void shouldDropAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).drop(2)).isEqualTo(of(3));
    }

    @Test
    public void shouldDropAllIfCountExceedsSize() {
        assertThat(of(1, 2, 3).drop(4)).isEqualTo(nil());
    }

    // -- dropRight

    @Test
    public void shouldDropRightNoneOnNil() {
        assertThat(nil().dropRight(1)).isEqualTo(nil());
    }

    @Test
    public void shouldDropRightNoneIfCountIsNegative() {
        assertThat(of(1, 2, 3).dropRight(-1)).isEqualTo(of(1, 2, 3));
    }

    @Test
    public void shouldDropRightAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).dropRight(2)).isEqualTo(of(1));
    }

    @Test
    public void shouldDropRightAllIfCountExceedsSize() {
        assertThat(of(1, 2, 3).dropRight(4)).isEqualTo(nil());
    }

    // -- dropWhile

    @Test
    public void shouldDropWhileNoneOnNil() {
        assertThat(nil().dropWhile(ignored -> true)).isEqualTo(nil());
    }

    @Test
    public void shouldDropWhileNoneIfPredicateIsFalse() {
        assertThat(of(1, 2, 3).dropWhile(ignored -> false)).isEqualTo(of(1, 2, 3));
    }

    @Test
    public void shouldDropWhileAllIfPredicateIsTrue() {
        assertThat(of(1, 2, 3).dropWhile(ignored -> true)).isEqualTo(nil());
    }

    @Test
    public void shouldDropWhileCorrect() {
        assertThat(of(1, 2, 3).dropWhile(i -> i < 2)).isEqualTo(of(2, 3));
    }

    // -- exists

    @Test
    public void shouldBeAwareOfExistingElement() {
        assertThat(of(1, 2).exists(i -> i == 2)).isTrue();
    }

    @Test
    public void shouldBeAwareOfNonExistingElement() {
        assertThat(this.<Integer>nil().exists(i -> i == 1)).isFalse();
    }

    // -- existsUnique

    @Test
    public void shouldBeAwareOfExistingUniqueElement() {
        assertThat(of(1, 2).existsUnique(i -> i == 1)).isTrue();
    }

    @Test
    public void shouldBeAwareOfNonExistingUniqueElement() {
        assertThat(this.<Integer>nil().existsUnique(i -> i == 1)).isFalse();
    }

    @Test
    public void shouldBeAwareOfExistingNonUniqueElement() {
        assertThat(of(1, 1, 2).existsUnique(i -> i == 1)).isFalse();
    }

    // -- filter

    @Test
    public void shouldFilterEmptyTraversable() {
        assertThat(nil().filter(ignored -> true)).isEqualTo(nil());
    }

    @Test
    public void shouldFilterNonEmptyTraversable() {
        assertThat(of(1, 2, 3, 4).filter(i -> i % 2 == 0)).isEqualTo(of(2, 4));
    }

    // -- findAll

    @Test
    public void shouldFindAllOfNil() {
        assertThat(nil().findAll(ignored -> true)).isEqualTo(nil());
    }

    @Test
    public void shouldFindAllOfNonNil() {
        assertThat(of(1, 2, 3, 4).findAll(i -> i % 2 == 0)).isEqualTo(of(2, 4));
    }

    // -- findFirst

    @Test
    public void shouldFindFirstOfNil() {
        assertThat(nil().findFirst(ignored -> true)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindFirstOfNonNil() {
        assertThat(of(1, 2, 3, 4).findFirst(i -> i % 2 == 0)).isEqualTo(Option.of(2));
    }

    // -- findLast

    @Test
    public void shouldFindLastOfNil() {
        assertThat(nil().findLast(ignored -> true)).isEqualTo(Option.none());
    }

    @Test
    public void shouldFindLastOfNonNil() {
        assertThat(of(1, 2, 3, 4).findLast(i -> i % 2 == 0)).isEqualTo(Option.of(4));
    }

    // -- flatMap

    @Test
    public void shouldFlatMapEmptyTraversable() {
        assertThat(nil().flatMap(this::of)).isEqualTo(nil());
    }

    @Test
    public void shouldFlatMapNonEmptyTraversable() {
        assertThat(of(1, 2, 3).flatMap(this::of)).isEqualTo(of(1, 2, 3));
    }

    @Test
    public void shouldFlatMapTraversableByExpandingElements() {
        assertThat(of(1, 2, 3).flatMap(i -> {
            if (i == 1) {
                return of(1, 2, 3);
            } else if (i == 2) {
                return of(4, 5);
            } else {
                return of(6);
            }
        })).isEqualTo(of(1, 2, 3, 4, 5, 6));
    }

    // -- flatten

    @Test
    public <T> void shouldFlattenEmptyTraversable() {
        final Traversable<? extends Traversable<? extends T>> nil = nil();
        final Traversable<T> actual = nil.flatten();
        assertThat(actual).isEqualTo(nil());
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowOnFlattenTraversableOfPlainElements() {
        of(1, 2, 3).flatten();
    }

    @Test
    public void shouldFlattenTraversableOfTraversables() {
        @SuppressWarnings("unchecked")
        final Traversable<? extends Traversable<? extends Integer>> xs = of(of(1), of(2, 3));
        final Traversable<Integer> actual = xs.flatten();
        final Traversable<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowOnFlattenTraversableOfTraversablesAndPlainElements() {
        of(1, of(2, 3)).flatten();
    }

    @Test(expected = ClassCastException.class)
    public void shouldThrowOnFlattenWithBadImplicitReturnType() {
        of(1, 2, 3).map(Object::toString).flatten();
    }

    // -- flatten(Function1)

    @Test
    public <T> void shouldFlattenEmptyTraversableGivenAFunction() {
        final Traversable<? extends Traversable<T>> nil = nil();
        final Traversable<T> actual = nil.flatten(Function.identity());
        assertThat(actual).isEqualTo(nil());
    }

    @Test
    public void shouldFlattenTraversableOfPlainElementsGivenAFunction() {
        final Traversable<Integer> actual = of(1, 2, 3).flatten(this::of);
        final Traversable<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldFlattenTraversableOfTraversablesGivenAFunction() {
        @SuppressWarnings("unchecked")
        final Traversable<? extends Traversable<Integer>> xs = of(of(1), of(2, 3));
        final Traversable<Integer> actual = xs.flatten(Function.identity());
        final Traversable<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldFlattenTraversableOfTraversablesAndPlainElementsGivenAFunction() {
        final Traversable<?> xs = of(1, of(2, 3));
        final Traversable<Integer> actual = xs.flatten(x -> Match
                .caze((Traversable<Integer> ys) -> ys)
                .caze((Integer i) -> of(i))
                .apply(x));
        final Traversable<Integer> expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldFlattenDifferentElementTypesGivenAFunction() {
        final Traversable<Object> actual = this.<Object>of(1, "2", this.<Object>of(3.1415, 1L))
                .flatten(x -> Match
                        .caze((Traversable<Object> ys) -> ys)
                        .caze((Object i) -> of(i))
                        .apply(x));
        assertThat(actual).isEqualTo(this.<Object>of(1, "2", 3.1415, 1L));
    }

    // -- fold

    @Test
    public void shouldFoldNil() {
        assertThat(this.<String>nil().fold("", (a, b) -> a + b)).isEqualTo("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldNullOperator() {
        this.<String>nil().fold(null, null);
    }

    @Test
    public void shouldFoldNonNil() {
        assertThat(of(1, 2, 3).fold(0, (a, b) -> a + b)).isEqualTo(6);
    }

    // -- foldLeft

    @Test
    public void shouldFoldLeftNil() {
        assertThat(this.<String>nil().foldLeft("", (xs, x) -> xs + x)).isEqualTo("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldLeftNullOperator() {
        this.<String>nil().foldLeft(null, null);
    }

    @Test
    public void shouldFoldLeftNonNil() {
        assertThat(of("a", "b", "c").foldLeft("", (xs, x) -> xs + x)).isEqualTo("abc");
    }

    // -- foldMap

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldMapAndMonoidIsNull() {
        nil().foldMap(null, String::valueOf);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldMapAndMapperIsNull() {
        nil().foldMap(Monoid.endoMonoid(), null);
    }

    @Test
    public void shouldFoldMapNil() {
        nil().foldMap(Monoid.endoMonoid(), o -> Function.identity());
    }

    @Test
    public void shouldFoldMapNonNil() {
        class StringConcat implements Monoid<String> {

            @Override
            public String zero() {
                return "";
            }

            @Override
            public String combine(String a1, String a2) {
                return a1 + a2;
            }
        }
        final Monoid<String> monoid = new StringConcat();
        final String actual = of('a', 'b', 'c').foldMap(monoid, String::valueOf);
        assertThat(actual).isEqualTo("abc");
    }

    // -- foldRight

    @Test
    public void shouldFoldRightNil() {
        assertThat(this.<String>nil().foldRight("", (x, xs) -> x + xs)).isEqualTo("");
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenFoldRightNullOperator() {
        this.<String>nil().foldRight(null, null);
    }

    @Test
    public void shouldFoldRightNonNil() {
        assertThat(of("a", "b", "c").foldRight("", (x, xs) -> x + xs)).isEqualTo("abc");
    }

    // -- forAll

    @Test
    public void shouldBeAwareOfPropertyThatHoldsForAll() {
        assertThat(List.of(2, 4).forAll(i -> i % 2 == 0)).isTrue();
    }

    @Test
    public void shouldBeAwareOfPropertyThatNotHoldsForAll() {
        assertThat(List.of(2, 3).forAll(i -> i % 2 == 0)).isFalse();
    }

    // -- grouped

    @Test
    public void shouldGroupedNil() {
        assertThat(nil().grouped(1)).isEqualTo(nil());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenGroupedWithSizeZero() {
        nil().grouped(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenGroupedWithNegativeSize() {
        nil().grouped(-1);
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGroupedTraversableWithEqualSizedBlocks() {
        assertThat(of(1, 2, 3, 4).grouped(2)).isEqualTo(of(of(1, 2), of(3, 4)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGroupedTraversableWithRemainder() {
        assertThat(of(1, 2, 3, 4, 5).grouped(2)).isEqualTo(of(of(1, 2), of(3, 4), of(5)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldGroupedWhenTraversableLengthIsSmallerThanBlockSize() {
        assertThat(of(1, 2, 3, 4).grouped(5)).isEqualTo(of(of(1, 2, 3, 4)));
    }

    // -- head

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenHeadOnNil() {
        nil().head();
    }

    @Test
    public void shouldReturnHeadOfNonNil() {
        assertThat(of(1, 2, 3).head()).isEqualTo(1);
    }

    // -- init

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenInitOfNil() {
        nil().init();
    }

    @Test
    public void shouldGetInitOfNonNil() {
        assertThat(of(1, 2, 3).init()).isEqualTo(of(1, 2));
    }

    // -- intersperse

    @Test
    public void shouldIntersperseNil() {
        assertThat(this.<Character>nil().intersperse(',')).isEqualTo(nil());
    }

    @Test
    public void shouldIntersperseSingleton() {
        assertThat(of('a').intersperse(',')).isEqualTo(of('a'));
    }

    @Test
    public void shouldIntersperseMultipleElements() {
        assertThat(of('a', 'b').intersperse(',')).isEqualTo(of('a', ',', 'b'));
    }

    // -- isEmpty

    @Test
    public void shouldRecognizeNil() {
        assertThat(nil().isEmpty()).isTrue();
    }

    @Test
    public void shouldRecognizeNonNil() {
        assertThat(of(1).isEmpty()).isFalse();
    }

    // -- iterator

    @Test
    public void shouldNotHasNextWhenNilIterator() {
        assertThat(nil().iterator().hasNext()).isFalse();
    }

    @Test(expected = NoSuchElementException.class)
    public void shouldThrowOnNextWhenNilIterator() {
        nil().iterator().next();
    }

    @Test
    public void shouldIterateFirstElementOfNonNil() {
        assertThat(of(1, 2, 3).iterator().next()).isEqualTo(1);
    }

    @Test
    public void shouldFullyIterateNonNil() {
        final Iterator<Integer> iterator = of(1, 2, 3).iterator();
        int actual;
        for (int i = 1; i <= 3; i++) {
            actual = iterator.next();
            assertThat(actual).isEqualTo(i);
        }
        assertThat(iterator.hasNext()).isFalse();
    }

    // -- join()

    @Test
    public void shouldJoinNil() {
        assertThat(nil().join()).isEqualTo("");
    }

    @Test
    public void shouldJoinNonNil() {
        assertThat(of('a', 'b', 'c').join()).isEqualTo("abc");
    }

    // -- join(delimiter)

    @Test
    public void shouldJoinWithDelimiterNil() {
        assertThat(nil().join(",")).isEqualTo("");
    }

    @Test
    public void shouldJoinWithDelimiterNonNil() {
        assertThat(of('a', 'b', 'c').join(",")).isEqualTo("a,b,c");
    }

    // -- join(delimiter, prefix, suffix)

    @Test
    public void shouldJoinWithDelimiterAndPrefixAndSuffixNil() {
        assertThat(nil().join(",", "[", "]")).isEqualTo("[]");
    }

    @Test
    public void shouldJoinWithDelimiterAndPrefixAndSuffixNonNil() {
        assertThat(of('a', 'b', 'c').join(",", "[", "]")).isEqualTo("[a,b,c]");
    }

    // -- last

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenLastOnNil() {
        nil().last();
    }

    @Test
    public void shouldReturnLastOfNonNil() {
        assertThat(of(1, 2, 3).last()).isEqualTo(3);
    }

    // -- length

    @Test
    public void shouldComputeLengthOfNil() {
        assertThat(nil().length()).isEqualTo(0);
    }

    @Test
    public void shouldComputeLengthOfNonNil() {
        assertThat(of(1, 2, 3).length()).isEqualTo(3);
    }

    // -- map

    @Test
    public void shouldMapNil() {
        assertThat(this.<Integer>nil().map(i -> i + 1)).isEqualTo(nil());
    }

    @Test
    public void shouldMapNonNil() {
        assertThat(of(1, 2, 3).map(i -> i + 1)).isEqualTo(of(2, 3, 4));
    }

    // -- max

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenMaxOfNil() {
        nil().max();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenMaxOfNonNumeric() {
        of("1", "2", "3").max();
    }

    @Test
    public void shouldComputeMaxOfBoolean() {
        final boolean actual = of(true, false).max();
        assertThat(actual).isEqualTo(true);
    }

    @Test
    public void shouldComputeMaxOfByte() {
        final byte actual = of((byte) 1, (byte) 2).max();
        assertThat(actual).isEqualTo((byte) 2);
    }

    @Test
    public void shouldComputeMaxOfChar() {
        final char actual = of('a', 'b', 'c').max();
        assertThat(actual).isEqualTo('c');
    }

    @Test
    public void shouldComputeMaxOfDouble() {
        final double actual = of(.1d, .2d, .3d).max();
        assertThat(actual).isEqualTo(.3d);
    }

    @Test
    public void shouldComputeMaxOfFloat() {
        final float actual = of(.1f, .2f, .3f).max();
        assertThat(actual).isEqualTo(.3f);
    }

    @Test
    public void shouldComputeMaxOfInt() {
        final int actual = of(1, 2, 3).max();
        assertThat(actual).isEqualTo(3);
    }

    @Test
    public void shouldComputeMaxOfLong() {
        final long actual = of(1L, 2L, 3L).max();
        assertThat(actual).isEqualTo(3L);
    }

    @Test
    public void shouldComputeMaxOfShort() {
        final short actual = of((short) 1, (short) 2, (short) 3).max();
        assertThat(actual).isEqualTo((short) 3);
    }

    @Test
    public void shouldComputeMaxOfBigInteger() {
        final BigInteger actual = of(BigInteger.ZERO, BigInteger.ONE).max();
        assertThat(actual).isEqualTo(BigInteger.ONE);
    }

    @Test
    public void shouldComputeMaxOfBigDecimal() {
        final BigDecimal actual = of(BigDecimal.ZERO, BigDecimal.ONE).max();
        assertThat(actual).isEqualTo(BigDecimal.ONE);
    }

    // -- maxBy

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMaxByWithNullComparator() {
        of(1).maxBy(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenMaxByOfNil() {
        nil().maxBy((o1, o2) -> 0);
    }

    @Test
    public void shouldCalculateMaxByOfInts() {
        assertThat(of(1, 2, 3).maxBy((i1, i2) -> i1 - i2)).isEqualTo(3);
    }

    // -- min

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenMinOfNil() {
        nil().min();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenMinOfNonNumeric() {
        of("1", "2", "3").min();
    }

    @Test
    public void shouldComputeMinOfBoolean() {
        final boolean actual = of(true, false).min();
        assertThat(actual).isEqualTo(false);
    }

    @Test
    public void shouldComputeMinOfByte() {
        final byte actual = of((byte) 1, (byte) 2).min();
        assertThat(actual).isEqualTo((byte) 1);
    }

    @Test
    public void shouldComputeMinOfChar() {
        final char actual = of('a', 'b', 'c').min();
        assertThat(actual).isEqualTo('a');
    }

    @Test
    public void shouldComputeMinOfDouble() {
        final double actual = of(.1d, .2d, .3d).min();
        assertThat(actual).isEqualTo(.1d);
    }

    @Test
    public void shouldComputeMinOfFloat() {
        final float actual = of(.1f, .2f, .3f).min();
        assertThat(actual).isEqualTo(.1f);
    }

    @Test
    public void shouldComputeMinOfInt() {
        final int actual = of(1, 2, 3).min();
        assertThat(actual).isEqualTo(1);
    }

    @Test
    public void shouldComputeMinOfLong() {
        final long actual = of(1L, 2L, 3L).min();
        assertThat(actual).isEqualTo(1L);
    }

    @Test
    public void shouldComputeMinOfShort() {
        final short actual = of((short) 1, (short) 2, (short) 3).min();
        assertThat(actual).isEqualTo((short) 1);
    }

    @Test
    public void shouldComputeMinOfBigInteger() {
        final BigInteger actual = of(BigInteger.ZERO, BigInteger.ONE).min();
        assertThat(actual).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void shouldComputeMinOfBigDecimal() {
        final BigDecimal actual = of(BigDecimal.ZERO, BigDecimal.ONE).min();
        assertThat(actual).isEqualTo(BigDecimal.ZERO);
    }

    // -- minBy

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenMinByWithNullComparator() {
        of(1).minBy(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenMinByOfNil() {
        nil().minBy((o1, o2) -> 0);
    }

    @Test
    public void shouldCalculateMinByOfInts() {
        assertThat(of(1, 2, 3).minBy((i1, i2) -> i1 - i2)).isEqualTo(1);
    }

    // -- peek

    @Test
    public void shouldPeekNil() {
        assertThat(nil().peek(t -> {
        })).isEqualTo(nil());
    }

    @Test
    public void shouldPeekNonNilPerformingNoAction() {
        assertThat(of(1).peek(t -> {
        })).isEqualTo(of(1));
    }

    @Test
    public void shouldPeekNonNilPerformingAnAction() {
        assertThat(of(1).peek(System.out::println)).isEqualTo(of(1));
    }

    // -- product

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenProductOfNil() {
        nil().product();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenProductOfNonNumeric() {
        of("1", "2", "3").product();
    }

    @Test
    public void shouldComputeProductOfBoolean() {
        final boolean actual = of(true, false).product();
        assertThat(actual).isEqualTo(false);
    }

    @Test
    public void shouldComputeProductOfByte() {
        final byte actual = of((byte) 1, (byte) 2).product();
        assertThat(actual).isEqualTo((byte) 2);
    }

    @Test
    public void shouldComputeProductOfChar() {
        final char actual = of('a', 'b', 'c').product();
        assertThat(actual).isEqualTo(Character.valueOf((char) ('a' * 'b' * 'c')));
    }

    @Test
    public void shouldComputeProductOfDouble() {
        final double actual = of(.1d, .2d, .3d).product();
        assertThat(actual).isEqualTo(.006d, within(10e-10));
    }

    @Test
    public void shouldComputeProductOfFloat() {
        final float actual = of(.1f, .2f, .3f).product();
        assertThat(actual).isEqualTo(.006f, within(10e-10f));
    }

    @Test
    public void shouldComputeProductOfInt() {
        final int actual = of(1, 2, 3).product();
        assertThat(actual).isEqualTo(6);
    }

    @Test
    public void shouldComputeProductOfLong() {
        final long actual = of(1L, 2L, 3L).product();
        assertThat(actual).isEqualTo(6L);
    }

    @Test
    public void shouldComputeProductOfShort() {
        final short actual = of((short) 1, (short) 2, (short) 3).product();
        assertThat(actual).isEqualTo((short) 6);
    }

    @Test
    public void shouldComputeProductOfBigInteger() {
        final BigInteger actual = of(BigInteger.ZERO, BigInteger.ONE).product();
        assertThat(actual).isEqualTo(BigInteger.ZERO);
    }

    @Test
    public void shouldComputeProductOfBigDecimal() {
        final BigDecimal actual = of(BigDecimal.ZERO, BigDecimal.ONE).product();
        assertThat(actual).isEqualTo(BigDecimal.ZERO);
    }

    // -- reduce

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenReduceNil() {
        this.<String>nil().reduce((a, b) -> a + b);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceNullOperator() {
        this.<String>nil().reduce(null);
    }

    @Test
    public void shouldReduceNonNil() {
        assertThat(of(1, 2, 3).reduce((a, b) -> a + b)).isEqualTo(6);
    }

    // -- reduceLeft

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenReduceLeftNil() {
        this.<String>nil().reduceLeft((a, b) -> a + b);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceLeftNullOperator() {
        this.<String>nil().reduceLeft(null);
    }

    @Test
    public void shouldReduceLeftNonNil() {
        assertThat(of("a", "b", "c").reduceLeft((xs, x) -> xs + x)).isEqualTo("abc");
    }

    // -- reduceRight

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenReduceRightNil() {
        this.<String>nil().reduceRight((a, b) -> a + b);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowWhenReduceRightNullOperator() {
        this.<String>nil().reduceRight(null);
    }

    @Test
    public void shouldReduceRightNonNil() {
        assertThat(of("a", "b", "c").reduceRight((x, xs) -> x + xs)).isEqualTo("abc");
    }

    // -- remove

    @Test
    public void shouldRemoveElementFromNil() {
        assertThat(nil().remove(null)).isEqualTo(nil());
    }

    @Test
    public void shouldRemoveFirstElement() {
        assertThat(of(1, 2, 3).remove(1)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldRemoveLastElement() {
        assertThat(of(1, 2, 3).remove(3)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldRemoveInnerElement() {
        assertThat(of(1, 2, 3).remove(2)).isEqualTo(of(1, 3));
    }

    @Test
    public void shouldRemoveNonExistingElement() {
        assertThat(of(1, 2, 3).remove(4)).isEqualTo(of(1, 2, 3));
    }

    // -- removeAll(Iterable)

    @Test
    public void shouldRemoveAllElementsFromNil() {
        assertThat(nil().removeAll(of(1, 2, 3))).isEqualTo(nil());
    }

    @Test
    public void shouldRemoveAllExistingElementsFromNonNil() {
        assertThat(of(1, 2, 3, 1, 2, 3).removeAll(of(1, 2))).isEqualTo(of(3, 3));
    }

    @Test
    public void shouldNotRemoveAllNonExistingElementsFromNonNil() {
        assertThat(of(1, 2, 3).removeAll(of(4, 5))).isEqualTo(of(1, 2, 3));
    }

    // -- removeAll(Object)

    @Test
    public void shouldRemoveAllObjectsFromNil() {
        assertThat(nil().removeAll(1)).isEqualTo(nil());
    }

    @Test
    public void shouldRemoveAllExistingObjectsFromNonNil() {
        assertThat(of(1, 2, 3, 1, 2, 3).removeAll(1)).isEqualTo(of(2, 3, 2, 3));
    }

    @Test
    public void shouldNotRemoveAllNonObjectsElementsFromNonNil() {
        assertThat(of(1, 2, 3).removeAll(4)).isEqualTo(of(1, 2, 3));
    }

    // -- replace(curr, new)

    @Test
    public void shouldReplaceElementOfNilUsingCurrNew() {
        assertThat(this.<Integer>nil().replace(1, 2)).isEqualTo(nil());
    }

    @Test
    public void shouldReplaceElementOfNonNilUsingCurrNew() {
        assertThat(of(0, 1, 2, 1).replace(1, 3)).isEqualTo(of(0, 3, 2, 1));
    }

    // -- replaceAll(curr, new)

    @Test
    public void shouldReplaceAllElementsOfNilUsingCurrNew() {
        assertThat(this.<Integer>nil().replaceAll(1, 2)).isEqualTo(nil());
    }

    @Test
    public void shouldReplaceAllElementsOfNonNilUsingCurrNew() {
        assertThat(of(0, 1, 2, 1).replaceAll(1, 3)).isEqualTo(of(0, 3, 2, 3));
    }

    // -- replaceAll(UnaryOp)

    @Test
    public void shouldReplaceAllElementsOfNilUsingUnaryOp() {
        assertThat(this.<Integer>nil().replaceAll(i -> i + 1)).isEqualTo(nil());
    }

    @Test
    public void shouldReplaceAllElementsOfNonNilUsingUnaryOp() {
        assertThat(of(1, 2, 3).replaceAll(i -> i + 1)).isEqualTo(of(2, 3, 4));
    }

    // -- retainAll

    @Test
    public void shouldRetainAllElementsFromNil() {
        assertThat(nil().retainAll(of(1, 2, 3))).isEqualTo(nil());
    }

    @Test
    public void shouldRetainAllExistingElementsFromNonNil() {
        assertThat(of(1, 2, 3, 1, 2, 3).retainAll(of(1, 2))).isEqualTo(of(1, 2, 1, 2));
    }

    @Test
    public void shouldNotRetainAllNonExistingElementsFromNonNil() {
        assertThat(of(1, 2, 3).retainAll(of(4, 5))).isEqualTo(nil());
    }

    // -- reverse

    @Test
    public void shouldReverseNil() {
        assertThat(nil().reverse()).isEqualTo(nil());
    }

    @Test
    public void shouldReverseNonNil() {
        assertThat(of(1, 2, 3).reverse()).isEqualTo(of(3, 2, 1));
    }

    // -- sliding(size)

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNilByZeroSize() {
        nil().sliding(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNilByNegativeSize() {
        nil().sliding(-1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNonNilByZeroSize() {
        of(1).sliding(0);
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldThrowWhenSlidingNonNilByNegativeSize() {
        of(1).sliding(-1);
    }

    @Test
    public void shouldSlideNilBySize() {
        assertThat(nil().sliding(1)).isEqualTo(nil());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlideNonNilBySize() {
        assertThat(of(1, 2, 3).sliding(1)).isEqualTo(of(of(1), of(2), of(3)));
    }

    // -- sliding(size, step)

    @Test
    public void shouldSlideNilBySizeAndStep() {
        assertThat(nil().sliding(1, 1)).isEqualTo(nil());
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide5ElementsBySize2AndStep3() {
        assertThat(of(1, 2, 3, 4, 5).sliding(2, 3)).isEqualTo(of(of(1, 2), of(4, 5)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide5ElementsBySize2AndStep4() {
        assertThat(of(1, 2, 3, 4, 5).sliding(2, 4)).isEqualTo(of(of(1, 2), of(5)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide5ElementsBySize2AndStep5() {
        assertThat(of(1, 2, 3, 4, 5).sliding(2, 5)).isEqualTo(of(of(1, 2)));
    }

    @SuppressWarnings("unchecked")
    @Test
    public void shouldSlide4ElementsBySize5AndStep3() {
        assertThat(of(1, 2, 3, 4).sliding(5, 3)).isEqualTo(of(of(1, 2, 3, 4), of(4)));
    }

    // -- span

    @Test
    public void shouldSpanNil() {
        assertThat(this.<Integer>nil().span(i -> i < 2)).isEqualTo(Tuple.of(nil(), nil()));
    }

    @Test
    public void shouldSpanNonNil() {
        assertThat(of(0, 1, 2, 3).span(i -> i < 2)).isEqualTo(Tuple.of(of(0, 1), of(2, 3)));
    }

    // -- spliterator

    @Test
    public void shouldSplitNil() {
        final java.util.List<Integer> actual = new java.util.ArrayList<>();
        this.<Integer>nil().spliterator().forEachRemaining(actual::add);
        assertThat(actual).isEqualTo(Collections.emptyList());
    }

    @Test
    public void shouldSplitNonNil() {
        final java.util.List<Integer> actual = new java.util.ArrayList<>();
        of(1, 2, 3).spliterator().forEachRemaining(actual::add);
        assertThat(actual).isEqualTo(Arrays.asList(1, 2, 3));
    }

    @Test
    public void shouldHaveImmutableSpliterator() {
        assertThat(of(1, 2, 3).spliterator().characteristics() & Spliterator.IMMUTABLE).isNotZero();
    }

    @Test
    public void shouldHaveOrderedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().characteristics() & Spliterator.ORDERED).isNotZero();
    }

    @Test
    public void shouldHaveSizedSpliterator() {
        assertThat(of(1, 2, 3).spliterator().characteristics() & Spliterator.SIZED).isNotZero();
    }

    @Test
    public void shouldReturnSizeWhenSpliterator() {
        assertThat(of(1, 2, 3).spliterator().getExactSizeIfKnown()).isEqualTo(3);
    }

    // -- stderr

    @Test
    public void shouldWriteToStderr() {
        of(1, 2, 3).stderr();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleStderrIOException() {
        final PrintStream originalErr = System.err;
        try (PrintStream failingPrintStream = failingPrintStream()) {
            System.setErr(failingPrintStream);
            of(0).stderr();
        } finally {
            System.setErr(originalErr);
        }
    }

    // -- stdout

    @Test
    public void shouldWriteToStdout() {
        of(1, 2, 3).stdout();
    }

    @Test(expected = IllegalStateException.class)
    public void shouldHandleStdoutIOException() {
        final PrintStream originalOut = System.out;
        try (PrintStream failingPrintStream = failingPrintStream()) {
            System.setOut(failingPrintStream);
            of(0).stdout();
        } finally {
            System.setOut(originalOut);
        }
    }

    // -- sum

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenSumOfNil() {
        nil().sum();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenSumOfNonNumeric() {
        of("1", "2", "3").sum();
    }

    @Test
    public void shouldComputeSumOfBoolean() {
        final boolean actual = of(true, false).sum();
        assertThat(actual).isEqualTo(true);
    }

    @Test
    public void shouldComputeSumOfByte() {
        final byte actual = of((byte) 1, (byte) 2).sum();
        assertThat(actual).isEqualTo((byte) 3);
    }

    @Test
    public void shouldComputeSumOfChar() {
        final char actual = of('a', 'b', 'c').sum();
        assertThat(actual).isEqualTo(Character.valueOf((char) ('a' + 'b' + 'c')));
    }

    @Test
    public void shouldComputeSumOfDouble() {
        final double actual = of(.1d, .2d, .3d).sum();
        assertThat(actual).isEqualTo(.6d, within(10e-10));
    }

    @Test
    public void shouldComputeSumOfFloat() {
        final float actual = of(.1f, .2f, .3f).sum();
        assertThat(actual).isEqualTo(.6f, within(10e-10f));
    }

    @Test
    public void shouldComputeSumOfInt() {
        final int actual = of(1, 2, 3).sum();
        assertThat(actual).isEqualTo(6);
    }

    @Test
    public void shouldComputeSumOfLong() {
        final long actual = of(1L, 2L, 3L).sum();
        assertThat(actual).isEqualTo(6L);
    }

    @Test
    public void shouldComputeSumOfShort() {
        final short actual = of((short) 1, (short) 2, (short) 3).sum();
        assertThat(actual).isEqualTo((short) 6);
    }

    @Test
    public void shouldComputeSumOfBigInteger() {
        final BigInteger actual = of(BigInteger.ZERO, BigInteger.ONE).sum();
        assertThat(actual).isEqualTo(BigInteger.ONE);
    }

    @Test
    public void shouldComputeSumOfBigDecimal() {
        final BigDecimal actual = of(BigDecimal.ZERO, BigDecimal.ONE).sum();
        assertThat(actual).isEqualTo(BigDecimal.ONE);
    }

    // -- take

    @Test
    public void shouldTakeNoneOnNil() {
        assertThat(nil().take(1)).isEqualTo(nil());
    }

    @Test
    public void shouldTakeNoneIfCountIsNegative() {
        assertThat(of(1, 2, 3).take(-1)).isEqualTo(nil());
    }

    @Test
    public void shouldTakeAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).take(2)).isEqualTo(of(1, 2));
    }

    @Test
    public void shouldTakeAllIfCountExceedsSize() {
        assertThat(of(1, 2, 3).take(4)).isEqualTo(of(1, 2, 3));
    }

    // -- takeRight

    @Test
    public void shouldTakeRightNoneOnNil() {
        assertThat(nil().takeRight(1)).isEqualTo(nil());
    }

    @Test
    public void shouldTakeRightNoneIfCountIsNegative() {
        assertThat(of(1, 2, 3).takeRight(-1)).isEqualTo(nil());
    }

    @Test
    public void shouldTakeRightAsExpectedIfCountIsLessThanSize() {
        assertThat(of(1, 2, 3).takeRight(2)).isEqualTo(of(2, 3));
    }

    @Test
    public void shouldTakeRightAllIfCountExceedsSize() {
        assertThat(of(1, 2, 3).takeRight(4)).isEqualTo(of(1, 2, 3));
    }

    // -- takeWhile

    @Test
    public void shouldTakeWhileNoneOnNil() {
        assertThat(nil().takeWhile(x -> true)).isEqualTo(nil());
    }

    @Test
    public void shouldTakeWhileAllOnFalseCondition() {
        assertThat(of(1, 2, 3).takeWhile(x -> false)).isEqualTo(nil());
    }

    @Test
    public void shouldTakeWhileAllOnTrueCondition() {
        assertThat(of(1, 2, 3).takeWhile(x -> true)).isEqualTo(of(1, 2, 3));
    }

    @Test
    public void shouldTakeWhileAsExpected() {
        assertThat(of(2, 4, 5, 6).takeWhile(x -> x % 2 == 0)).isEqualTo(of(2, 4));
    }

    // -- tail

    @Test(expected = UnsupportedOperationException.class)
    public void shouldThrowWhenTailOnNil() {
        nil().tail();
    }

    @Test
    public void shouldReturnTailOfNonNil() {
        final Traversable<Integer> actual = of(1, 2, 3).tail();
        final Traversable<Integer> expected = of(2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    // -- toJavaArray(Class)

    @Test
    public void shouldConvertNilToJavaArray() {
        final Integer[] actual = List.<Integer>nil().toJavaArray(Integer.class);
        final Integer[] expected = new Integer[]{};
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldConvertNonNilToJavaArray() {
        final Integer[] array = List.of(1, 2).toJavaArray(Integer.class);
        final Integer[] expected = new Integer[]{1, 2};
        assertThat(array).isEqualTo(expected);
    }

    // -- toJavaList

    @Test
    public void shouldConvertNilToArrayList() {
        assertThat(this.<Integer>nil().toJavaList()).isEqualTo(new ArrayList<Integer>());
    }

    @Test
    public void shouldConvertNonNilToArrayList() {
        assertThat(of(1, 2, 3).toJavaList()).isEqualTo(Arrays.asList(1, 2, 3));
    }

    // -- toJavaMap(Function)

    @Test
    public void shouldConvertNilToHashMap() {
        assertThat(this.<Integer>nil().toJavaMap(x -> Tuple.of(x, x))).isEqualTo(new HashMap<>());
    }

    @Test
    public void shouldConvertNonNilToHashMap() {
        final java.util.Map<Integer, Integer> expected = new HashMap<>();
        expected.put(1, 1);
        expected.put(2, 2);
        assertThat(of(1, 2).toJavaMap(x -> Tuple.of(x, x))).isEqualTo(expected);
    }

    // -- toJavaSet

    @Test
    public void shouldConvertNilToHashSet() {
        assertThat(this.<Integer>nil().toJavaMap(x -> Tuple.of(x, x))).isEqualTo(new HashMap<>());
    }

    @Test
    public void shouldConvertNonNilToHashSet() {
        final java.util.Set<Integer> expected = new HashSet<>();
        expected.add(2);
        expected.add(1);
        expected.add(3);
        assertThat(of(1, 2, 2, 3).toJavaSet()).isEqualTo(expected);
    }

    // -- unzip

    @Test
    public void shouldUnzipNil() {
        assertThat(nil().unzip(x -> Tuple.of(x, x))).isEqualTo(Tuple.of(nil(), nil()));
    }

    @Test
    public void shouldUnzipNonNil() {
        final Tuple actual = of(0, 1).unzip(i -> Tuple.of(i, (char) ((short) 'a' + i)));
        final Tuple expected = Tuple.of(of(0, 1), this.<Character>of('a', 'b'));
        assertThat(actual).isEqualTo(expected);
    }

    // -- zip

    @Test
    public void shouldZipNils() {
        final Traversable<?> actual = nil().zip(nil());
        assertThat(actual).isEqualTo(nil());
    }

    @Test
    public void shouldZipEmptyAndNonNil() {
        final Traversable<?> actual = nil().zip(of(1));
        assertThat(actual).isEqualTo(nil());
    }

    @Test
    public void shouldZipNonEmptyAndNil() {
        final Traversable<?> actual = of(1).zip(nil());
        assertThat(actual).isEqualTo(nil());
    }

    @Test
    public void shouldZipNonNilsIfThisIsSmaller() {
        final Traversable<Tuple2<Integer, String>> actual = of(1, 2).zip(of("a", "b", "c"));
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipNonNilsIfThatIsSmaller() {
        final Traversable<Tuple2<Integer, String>> actual = of(1, 2, 3).zip(of("a", "b"));
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipNonNilsOfSameSize() {
        final Traversable<Tuple2<Integer, String>> actual = of(1, 2, 3).zip(of("a", "b", "c"));
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(3, "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipWithThatIsNull() {
        nil().zip(null);
    }

    // -- zipAll

    @Test
    public void shouldZipAllNils() {
        final Traversable<?> actual = nil().zipAll(nil(), null, null);
        assertThat(actual).isEqualTo(nil());
    }

    @Test
    public void shouldZipAllEmptyAndNonNil() {
        final Traversable<?> actual = nil().zipAll(of(1), null, null);
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Object, Integer>> expected = of(Tuple.of(null, 1));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonEmptyAndNil() {
        final Traversable<?> actual = of(1).zipAll(nil(), null, null);
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Integer, Object>> expected = of(Tuple.of(1, null));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThisIsSmaller() {
        final Traversable<Tuple2<Integer, String>> actual = of(1, 2).zipAll(of("a", "b", "c"), 9, "z");
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(9, "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsIfThatIsSmaller() {
        final Traversable<Tuple2<Integer, String>> actual = of(1, 2, 3).zipAll(of("a", "b"), 9, "z");
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(3, "z"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldZipAllNonNilsOfSameSize() {
        final Traversable<Tuple2<Integer, String>> actual = of(1, 2, 3).zipAll(of("a", "b", "c"), 9, "z");
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<Integer, String>> expected = of(Tuple.of(1, "a"), Tuple.of(2, "b"), Tuple.of(3, "c"));
        assertThat(actual).isEqualTo(expected);
    }

    @Test(expected = NullPointerException.class)
    public void shouldThrowIfZipAllWithThatIsNull() {
        nil().zipAll(null, null, null);
    }

    // -- zipWithIndex

    @Test
    public void shouldZipNilWithIndex() {
        assertThat(this.<String>nil().zipWithIndex()).isEqualTo(this.<Tuple2<String, Integer>>nil());
    }

    @Test
    public void shouldZipNonNilWithIndex() {
        final Traversable<Tuple2<String, Integer>> actual = of("a", "b", "c").zipWithIndex();
        @SuppressWarnings("unchecked")
        final Traversable<Tuple2<String, Integer>> expected = of(Tuple.of("a", 0), Tuple.of("b", 1), Tuple.of("c", 2));
        assertThat(actual).isEqualTo(expected);
    }

    // ++++++ OBJECT ++++++

    // -- equals

    @Test
    public void shouldEqualSameTraversableInstance() {
        final Traversable<?> traversable = nil();
        assertThat(traversable).isEqualTo(traversable);
    }

    @Test
    public void shouldNilNotEqualsNull() {
        assertThat(nil()).isNotNull();
    }

    @Test
    public void shouldNonNilNotEqualsNull() {
        assertThat(of(1)).isNotNull();
    }

    @Test
    public void shouldEmptyNotEqualsDifferentType() {
        assertThat(nil()).isNotEqualTo("");
    }

    @Test
    public void shouldNonEmptyNotEqualsDifferentType() {
        assertThat(of(1)).isNotEqualTo("");
    }

    @Test
    public void shouldRecognizeEqualityOfNils() {
        assertThat(nil()).isEqualTo(nil());
    }

    @Test
    public void shouldRecognizeEqualityOfNonNils() {
        assertThat(of(1, 2, 3).equals(of(1, 2, 3))).isTrue();
    }

    @Test
    public void shouldRecognizeNonEqualityOfTraversablesOfSameSize() {
        assertThat(of(1, 2, 3).equals(of(1, 2, 4))).isFalse();
    }

    @Test
    public void shouldRecognizeNonEqualityOfTraversablesOfDifferentSize() {
        assertThat(of(1, 2, 3).equals(of(1, 2))).isFalse();
    }

    // -- hashCode

    @Test
    public void shouldCalculateHashCodeOfNil() {
        assertThat(nil().hashCode() == nil().hashCode()).isTrue();
    }

    @Test
    public void shouldCalculateHashCodeOfNonNil() {
        assertThat(of(1, 2).hashCode() == of(1, 2).hashCode()).isTrue();
    }

    @Test
    public void shouldCalculateDifferentHashCodesForDifferentTraversables() {
        assertThat(of(1, 2).hashCode() != of(2, 3).hashCode()).isTrue();
    }

    // -- Serializable interface

    @Test
    public void shouldSerializeDeserializeNil() {
        final Object actual = deserialize(serialize(nil()));
        final Object expected = nil();
        assertThat(actual).isEqualTo(expected);
    }

    @Test
    public void shouldPreserveSingletonInstanceOnDeserialization() {
        final boolean actual = deserialize(serialize(nil())) == nil();
        assertThat(actual).isTrue();
    }

    @Test
    public void shouldSerializeDeserializeNonNil() {
        final Object actual = deserialize(serialize(of(1, 2, 3)));
        final Object expected = of(1, 2, 3);
        assertThat(actual).isEqualTo(expected);
    }

    // helpers

    static PrintStream failingPrintStream() {
        return new PrintStream(new OutputStream() {
            @Override
            public void write(int b) throws IOException {
                throw new IOException();
            }
        });
    }
}
