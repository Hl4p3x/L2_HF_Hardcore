package com.l2jserver.util;

import com.google.common.collect.Lists;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

class CollectionUtilTest {

    @Test
    void textConsecutiveForEach() {
        List<Integer> iterationResults = new ArrayList<>();
        CollectionUtil.consecutiveForEach(Lists.newArrayList(1, 2, 3, 4, 5), (left, right) -> iterationResults.add(left + right));

        List<Integer> expected = Arrays.asList(3, 5, 7, 9);
        Assertions.assertThat(iterationResults).isEqualTo(expected);
    }

    @Test
    void textConsecutiveMap() {
        List<Integer> iterationResults = CollectionUtil.consecutiveMap(Lists.newArrayList(1, 2, 3, 4, 5), (left, right) -> left + right);

        List<Integer> expected = Arrays.asList(3, 5, 7, 9);
        Assertions.assertThat(iterationResults).isEqualTo(expected);
    }

    @Test
    void testChunksRegular() {
        List<Integer> testData = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> chunks = Util.chunks(testData, 3);

        Assertions.assertThat(chunks)
                .isEqualTo(
                        Arrays.asList(
                                Arrays.asList(1, 2, 3),
                                Arrays.asList(4, 5, 6),
                                Arrays.asList(7, 8))
                );
    }

    @Test
    void testChunksEmpty() {
        List<List<Integer>> chunks = Util.chunks(Collections.emptyList(), 3);
        Assertions.assertThat(chunks).isEmpty();
    }

    @Test
    void testChunksSizeOfZero() {
        List<Integer> testData = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8);
        List<List<Integer>> chunks = Util.chunks(testData, 0);
        Assertions.assertThat(chunks).isEqualTo(Collections.singletonList(testData));
    }

}