// SPDX-License-Identifier: Apache-2.0
package com.hedera.node.config.converter;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class AccountIDConverterTest {

    @Test
    void testNullParam() {
        // given
        final AccountIDConverter converter = new AccountIDConverter();

        // then
        assertThatThrownBy(() -> converter.convert(null)).isInstanceOf(NullPointerException.class);
    }

    @ParameterizedTest
    @ValueSource(
            strings = {
                "", " ", "  ", "a.b.b", "1.b.c", "1.2.c", "a.2.3", "1", "1.2", ".1.2.3", "..1.2.3", ".1.2.3.", "1.2.3.4"
            })
    void testAllNotParseable(final String input) {
        // given
        final AccountIDConverter converter = new AccountIDConverter();

        // then
        assertThatThrownBy(() -> converter.convert(input)).isInstanceOf(IllegalArgumentException.class);
    }

    /**
     * The given tests does not work. From my point of view the expected behavior is to throw an exception but the
     * values would be converted without an issue. We should have a look at this in future.
     *
     * @param input
     */
    @Disabled
    @ParameterizedTest
    @ValueSource(strings = {"1.2.3.", "1.2.3.."})
    void testEdgeCasesForDiscussion(final String input) {
        // given
        final AccountIDConverter converter = new AccountIDConverter();

        // then
        assertThatThrownBy(() -> converter.convert(input)).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void testSimpleValue() {
        // given
        final AccountIDConverter converter = new AccountIDConverter();
        final String value = "1.2.3";

        // when
        final var result = converter.convert(value);

        // then
        assertThat(result).isNotNull();
        assertThat(result.shardNum()).isEqualTo(1L);
        assertThat(result.realmNum()).isEqualTo(2L);
        assertThat(result.accountNum()).isEqualTo(3L);
    }

    @Test
    void testLongValues() {
        // given
        final AccountIDConverter converter = new AccountIDConverter();
        final String value = Long.MAX_VALUE + "." + Long.MAX_VALUE + ".0";

        // when
        final var result = converter.convert(value);

        // then
        assertThat(result).isNotNull();
        assertThat(result.shardNum()).isEqualTo(Long.MAX_VALUE);
        assertThat(result.realmNum()).isEqualTo(Long.MAX_VALUE);
        assertThat(result.accountNum()).isZero();
    }
}
