package com.test;

import com.quill.OperationType;
import com.quill.Quill;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class QuillTest {

    @Test
    public void testSetAdditionCost() {
        Quill quill = new Quill();
        quill.setAdditionCost(10);
        quill.setTranspositionCost(6);
        assertEquals(33, quill.computeCost("oat", "boat", OperationType.DEFAULT));
    }

    @Test
    public void testSetDeletionCost() {
        Quill quill = new Quill();
        quill.setDeletionCost(10);
        quill.setTranspositionCost(6);
        assertEquals(33, quill.computeCost("oats", "oat", OperationType.DEFAULT));
    }

    @Test
    public void testSetSubstitutionCost() {
        Quill quill = new Quill();
        quill.setSubstitutionCost(0.5);
        assertEquals(0.5, quill.computeCost("goat", "boat", OperationType.SUBSTITUTION));
    }

    @Test
    public void testSetTranspositionCost() {
        Quill quill = new Quill();
        quill.setTranspositionCost(0.5);
        quill.setAdditionCost(0.2);
        quill.setDeletionCost(0.2);
        assertEquals(0.4, quill.computeCost("ogat", "goat", OperationType.TRANSPOSITION));
    }

    @Test
    public void testAddSubstitutionCostRules() {
        Quill quill = new Quill();
        quill.addSubstitutionCostRules('g', 'b', 0.5);
        assertEquals(0.5, quill.computeCost("goat", "boat", OperationType.SUBSTITUTION));
    }

    @Test
    public void testComputeCost() {
        Quill quill = new Quill();
        assertEquals(1, quill.computeCost("goat", "boat", OperationType.SUBSTITUTION));
    }
}