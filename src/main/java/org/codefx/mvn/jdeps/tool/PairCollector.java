package org.codefx.mvn.jdeps.tool;

import com.google.common.collect.Sets;
import org.codefx.mvn.jdeps.tool.PairCollector.Pair;

import java.util.Set;
import java.util.function.BiConsumer;
import java.util.function.BinaryOperator;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collector;

/**
 * Uses two collectors on a stream of pairs to create a pair of collection results.
 *
 * @param <T>
 * 		the type of input elements to the reduction operation
 * @param <A>
 * 		the result type of the first reduction operation
 * @param <B>
 * 		the result type of the second reduction operation
 * @param <CA>
 * 		the mutable accumulation type of the first reduction operation
 * @param <CB>
 * 		the mutable accumulation type of the second reduction operation
 */
public class PairCollector<T, A, B, CA, CB> implements Collector<T, Pair<CA, CB>, Pair<A, B>> {

	/*
	 * TODO: This class should be polished, tested and placed somewhere else.
	 */

	private final Collector<T, CA, A> firstCollector;
	private final Collector<T, CB, B> secondCollector;

	private PairCollector(Collector<T, CA, A> firstCollector, Collector<T, CB, B> secondCollector) {
		this.firstCollector = firstCollector;
		this.secondCollector = secondCollector;
	}

	public static <T, A, B, CA, CB> Collector<T, ?, Pair<A, B>> pairing(
			Collector<T, CA, A> firstCollector, Collector<T, CB, B> secondCollector) {
		return new PairCollector<>(firstCollector, secondCollector);
	}

	@Override
	public Supplier<Pair<CA, CB>> supplier() {
		return () -> new Pair<>(firstCollector.supplier().get(), secondCollector.supplier().get());
	}

	@Override
	public BiConsumer<Pair<CA, CB>, T> accumulator() {
		return (containers, newValue) -> {
			firstCollector.accumulator().accept(containers.first, newValue);
			secondCollector.accumulator().accept(containers.second, newValue);
		};
	}

	@Override
	public BinaryOperator<Pair<CA, CB>> combiner() {
		return (containers, otherContainers) -> {
			CA firstNewContainer = firstCollector.combiner().apply(containers.first, otherContainers.first);
			CB secondNewContainer = secondCollector.combiner().apply(containers.second, otherContainers.second);
			return new Pair<>(firstNewContainer, secondNewContainer);
		};
	}

	@Override
	public Function<Pair<CA, CB>, Pair<A, B>> finisher() {
		return containers -> {
			A firstResult = firstCollector.finisher().apply(containers.first);
			B secondResult = secondCollector.finisher().apply(containers.second);
			return new Pair<>(firstResult, secondResult);
		};
	}

	@Override
	public Set<Characteristics> characteristics() {
		return Sets.intersection(firstCollector.characteristics(), secondCollector.characteristics());
	}

	public static class Pair<A, B> {

		public final A first;
		public final B second;

		Pair(A first, B second) {
			this.first = first;
			this.second = second;
		}
	}

}
