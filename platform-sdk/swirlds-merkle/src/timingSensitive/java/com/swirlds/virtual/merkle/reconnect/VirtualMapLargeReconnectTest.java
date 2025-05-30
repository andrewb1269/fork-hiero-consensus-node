// SPDX-License-Identifier: Apache-2.0
package com.swirlds.virtual.merkle.reconnect;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import com.swirlds.virtual.merkle.TestKey;
import com.swirlds.virtual.merkle.TestValue;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Stream;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Tags;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

@DisplayName("Virtual Map MerkleDB Large Reconnect Test")
class VirtualMapLargeReconnectTest extends VirtualMapReconnectTestBase {

    @ParameterizedTest
    @MethodSource("provideLargeTreePermutations")
    @Tags({@Tag("VirtualMerkle"), @Tag("Reconnect"), @Tag("VMAP-003"), @Tag("VMAP-003.14")})
    @DisplayName("Permutations of very large trees reconnecting")
    void largeTeacherLargerLearnerPermutations(int teacherStart, int teacherEnd, int learnerStart, int learnerEnd) {

        for (int i = teacherStart; i < teacherEnd; i++) {
            teacherMap.put(new TestKey(i), new TestValue(i));
        }

        for (int i = learnerStart; i < learnerEnd; i++) {
            learnerMap.put(new TestKey(i), new TestValue(i));
        }

        assertDoesNotThrow(this::reconnect, "Should not throw a Exception");
    }

    @ParameterizedTest
    @MethodSource("provideLargeTreePermutations")
    @Tags({@Tag("VirtualMerkle"), @Tag("Reconnect"), @Tag("VMAP-005"), @Tag("VMAP-006")})
    @DisplayName("Reconnect aborts 3 times before success")
    void multipleAbortedReconnectsCanSucceed(int teacherStart, int teacherEnd, int learnerStart, int learnerEnd) {
        for (int i = teacherStart; i < teacherEnd; i++) {
            teacherMap.put(new TestKey(i), new TestValue(i));
        }

        for (int i = learnerStart; i < learnerEnd; i++) {
            learnerMap.put(new TestKey(i), new TestValue(i));
        }

        learnerBuilder.setNumCallsBeforeThrow((teacherEnd - teacherStart) / 2);
        learnerBuilder.setNumTimesToBreak(2);

        reconnectMultipleTimes(3);
    }

    @Test
    void reconnectBench() {
        final long N = 1_000_000;
        final long U = 10_000;

        final Random rand = new Random(N / 2);

        for (long i = 0; i < N; i++) {
            teacherMap.put(new TestKey(i), new TestValue(i));
            learnerMap.put(new TestKey(i), new TestValue(i));
        }

        for (long i = 0; i < U; i++) {
            final long toUpdate = rand.nextLong(N);
            teacherMap.put(new TestKey(toUpdate), new TestValue(toUpdate + N)); // update
            teacherMap.put(new TestKey(i + N), new TestValue(i + N)); // add
            final long toDelete = rand.nextLong(N + i);
            teacherMap.remove(new TestKey(toDelete));
        }

        long start = System.currentTimeMillis();
        assertDoesNotThrow(this::reconnect, "Should not throw a Exception");
        long end = System.currentTimeMillis();
        System.err.println("Time: " + (end - start) / 1000.0);
    }

    static Stream<Arguments> provideLargeTreePermutations() {
        final List<Arguments> args = new ArrayList<>();
        // Two million leaf trees that have no intersection
        args.add(Arguments.of(0, 1_000_000, 1_000_000, 2_000_000));
        // Two million leaf trees that intersect
        args.add(Arguments.of(0, 1_000_000, 500_000, 1_500_000));
        // A smaller tree and larger tree that do not intersect
        args.add(Arguments.of(0, 10_000, 1_000_000, 2_000_000));
        args.add(Arguments.of(1_000_000, 2_000_000, 0, 10_000));
        // A smaller tree and larger tree that do intersect
        args.add(Arguments.of(0, 10_000, 5_000, 1_005_000));
        args.add(Arguments.of(5_000, 1_005_000, 0, 10_000));

        // Two million leaf trees that intersect
        args.add(Arguments.of(50_000, 250_000, 0, 100_000));
        args.add(Arguments.of(50_000, 249_999, 0, 100_000));
        args.add(Arguments.of(50_000, 250_001, 0, 100_000));
        return args.stream();
    }
}
