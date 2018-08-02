package com.l2jserver.util;

import com.google.common.collect.Lists;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

class CollectionUtilTest {

    @Test
    void textConsecutiveForEach() {
        List<Integer> iterationResults = new ArrayList<>();
        CollectionUtil.consecutiveForEach(Lists.newArrayList(1, 2, 3, 4, 5), (left, right) -> iterationResults.add(left + right));

        List<Integer> expected = Arrays.asList(3, 5, 7, 9);
        Assertions.assertIterableEquals(expected, iterationResults);
    }

    @Test
    void textConsecutiveMap() {
        List<Integer> iterationResults = new ArrayList<>();
        CollectionUtil.consecutiveMap(Lists.newArrayList(1, 2, 3, 4, 5), (left, right) -> left + right);


        List<Integer> expected = Arrays.asList(3, 5, 7, 9);
        Assertions.assertIterableEquals(expected, iterationResults);
    }

}