package com.extollit.gaming.ai.path.model;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.*;

import static com.extollit.gaming.ai.path.TestingBlocks.*;
import static org.mockito.AdditionalMatchers.leq;
import static org.mockito.AdditionalMatchers.lt;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.Silent.class)
public class OcclusionFieldTests extends AbstractOcclusionFieldTesting {
    @Test
    public void control() {
        occlusionField.loadFrom(centerSpace,  0, 0, 0);

        assertTrue(Element.air.in(occlusionField.elementAt(1, 2, 3)));
    }

    @Test
    public void point() {
        when(centerSpace.blockAt(anyInt(), leq(7), anyInt())).thenReturn(stone);
        occlusionField.loadFrom(centerSpace,  0, 0, 0);
        occlusionField.set(centerSpace, 4, 14, 8, stone);
        verifyNeighborhood(4, 14, 8,
                Element.earth,
                Element.air,
                Element.air,
                Element.air,
                Element.air
        );
    }

    @Test
    public void point2() {
        when(centerSpace.blockAt(anyInt(), lt(4), anyInt())).thenReturn(stone);
        occlusionField.loadFrom(centerSpace,  0, 0, 0);
        occlusionField.set(centerSpace, 15, 4, 3, lava);
        verifyNeighborhood(15, 4,3,
                Element.fire,
                Element.air,
                Element.air,
                Element.air,
                Element.air
        );
    }

    @Test
    public void xPlane() {
        when(centerSpace.blockAt(leq(7), anyInt(), anyInt())).thenReturn(stone);
        occlusionField.loadFrom(centerSpace,  0, 0, 0);

        for (int z = 0; z < 16; ++z)
            for (int y = 0; y < 16; ++y)
                for (int x = 0; x < 16; ++x) {
                    final byte element = occlusionField.elementAt(x, y, z);
                    if (x <= 7)
                        assertFalse(Element.air.in(element));
                    else
                        assertTrue(Element.air.in(element));
                }
    }


    @Test
    public void yPlane() {
        when(centerSpace.blockAt(anyInt(), leq(7), anyInt())).thenReturn(stone);
        occlusionField.loadFrom(centerSpace,  0, 0, 0);

        for (int z = 0; z < 16; ++z)
            for (int y = 0; y < 16; ++y)
                for (int x = 0; x < 16; ++x) {
                    final byte element = occlusionField.elementAt(x, y, z);
                    if (y <= 7)
                        assertFalse(Element.air.in(element));
                    else
                        assertTrue(Element.air.in(element));
                }
    }

    @Test
    public void zPlane() {
        when(centerSpace.blockAt(anyInt(), anyInt(), leq(7))).thenReturn(stone);
        occlusionField.loadFrom(centerSpace,  0, 0, 0);

        for (int z = 0; z < 16; ++z)
            for (int y = 0; y < 16; ++y)
                for (int x = 0; x < 16; ++x) {
                    final byte element = occlusionField.elementAt(x, y, z);
                    if (z <= 7)
                        assertFalse(Element.air.in(element));
                    else
                        assertTrue(Element.air.in(element));
                }
    }

    @Test
    public void lava() {
        when(centerSpace.blockAt(0, 0, 0)).thenReturn(stone);
        occlusionField.loadFrom(centerSpace,  0, 0, 0);
        occlusionField.set(centerSpace, 3, 9, 2, lava);
        final byte element = this.occlusionField.elementAt(3, 9, 2);
        assertTrue(Element.fire.in(element));
    }

    @Test
    public void wall() {
        blockAt(5, 5, 5, wall);

        occlusionField.loadFrom(centerSpace, 0, 0, 0);
        final byte [] wall = {
                occlusionField.elementAt(5, 5, 5),
                occlusionField.elementAt(5, 6, 5),
        };
        assertTrue(Element.earth.in(wall[0]));
        assertTrue(Element.earth.in(wall[1]));
        assertTrue(Logic.fuzzy.in(wall[0]));
        assertTrue(Logic.fuzzy.in(wall[1]));
        assertFalse(Element.earth.in(occlusionField.elementAt(5, 4, 5)));
        assertFalse(Element.earth.in(occlusionField.elementAt(5, 7, 5)));
        for (int y = 5; y <= 6; ++y) {
            assertFalse(Element.earth.in(occlusionField.elementAt(5 + 1, y, 5)));
            assertFalse(Element.earth.in(occlusionField.elementAt(5 - 1, y, 5)));
            assertFalse(Element.earth.in(occlusionField.elementAt(5, y, 5 + 1)));
            assertFalse(Element.earth.in(occlusionField.elementAt(5, y, 5 - 1)));
        }
    }

    @Test
    public void placeFenceGate() {
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        fenceGate(false, 5, 5, 5);
        occlusionField.set(centerSpace, 5, 5, 5, fenceGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.doorway.in(top));
    }

    @Test
    public void removeFenceGate() {
        fenceGate(false, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);
        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.doorway.in(top));

        when(centerSpace.blockAt(5, 5, 5)).thenReturn(air);
        occlusionField.set(centerSpace, 5, 5, 5, air);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.air.in(bottom));
        assertTrue(Element.air.in(top));
        assertTrue(Logic.nothing.in(bottom));
        assertTrue(Logic.nothing.in(top));
    }

    @Test
    public void openFenceGate() {
        fenceGate(false, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.doorway.in(top));

        fenceGate(true, 5, 5, 5);
        occlusionField.set(centerSpace, 5, 5, 5, fenceGate);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.air.in(bottom));
        assertTrue(Element.air.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.doorway.in(top));
    }

    @Test
    public void closeFenceGate() {
        fenceGate(true, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.air.in(bottom));
        assertTrue(Element.air.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.doorway.in(top));

        fenceGate.open = false;
        occlusionField.set(centerSpace, 5, 5, 5, fenceGate);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.doorway.in(top));
    }

    @Test
    public void openCappedFenceGate() {
        fenceGate(false, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(stone);
        occlusionField.set(centerSpace, 5, 6, 5, stone);

        assertTrue(Element.earth.in(occlusionField.elementAt(5, 6, 5)));

        fenceGate.open = true;
        occlusionField.set(centerSpace, 5, 5, 5, fenceGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.air.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.nothing.in(top));
    }

    @Test
    public void closeCappedFenceGate() {
        fenceGate(true, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(stone);
        occlusionField.set(centerSpace, 5, 6, 5, stone);

        assertTrue(Element.earth.in(occlusionField.elementAt(5, 6, 5)));

        fenceGate.open = false;
        occlusionField.set(centerSpace, 5, 5, 5, fenceGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.nothing.in(top));
    }

    @Test
    public void removeClosedCappedFenceGate() {
        fenceGate(false, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(stone);
        occlusionField.set(centerSpace, 5, 6, 5, stone);

        when(centerSpace.blockAt(5, 5, 5)).thenReturn(air);
        occlusionField.set(centerSpace, 5, 5, 5, air);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.air.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.nothing.in(bottom));
        assertTrue(Logic.nothing.in(top));
    }

    @Test
    public void placeClosedCappedFenceGate() {
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(stone);
        occlusionField.set(centerSpace, 5, 6, 5, stone);

        fenceGate(false, 5, 5, 5);
        occlusionField.set(centerSpace, 5, 5, 5, fenceGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.nothing.in(top));
    }

    @Test
    public void fenceGateToWall() {
        fenceGate(true,5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        when(centerSpace.blockAt(5, 5, 5)).thenReturn(wall);
        occlusionField.set(centerSpace, 5, 5, 5, wall);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.fuzzy.in(bottom));
        assertTrue(Logic.fuzzy.in(top));
    }

    @Test
    public void wallToFenceGate() {
        blockAt(5, 5, 5, wall);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        fenceGate(false, 5, 5, 5);
        occlusionField.set(centerSpace, 5, 5, 5, fenceGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
        assertTrue(Logic.doorway.in(bottom));
        assertTrue(Logic.doorway.in(top));
    }

    @Test
    public void torchUp() {
        blockAt(5, 5, 5, wall);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));

        occlusionField.set(centerSpace, 5, 6, 5, torch);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
    }

    @Test
    public void torchDown() {
        blockAt(5, 5, 5, wall);
        blockAt(5, 6, 5, torch);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));

        occlusionField.set(centerSpace, 5, 6, 5, air);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(bottom));
        assertTrue(Element.earth.in(top));
    }

    @Test
    @Ignore("Insufficient information in the occlusion field to determine this reliably, will need to refactor.  Currently this means that two fence gates, the top one closed, will be considered as if both are open.  This is an acceptable trade-off.")
    public void stackedFenceGates() {
        fenceGate.open = false;

        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        final FenceGate
            topGate = new FenceGate(),
            bottomGate = new FenceGate();

        bottomGate.open = true;
        topGate.open = false;

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(fenceGate);
        when(centerSpace.blockAt(5, 5, 5)).thenReturn(fenceGate);
        when(instanceSpace.blockObjectAt(5, 6, 5)).thenReturn(topGate);
        when(instanceSpace.blockObjectAt(5, 5, 5)).thenReturn(bottomGate);

        occlusionField.set(centerSpace, 5, 6, 5, topGate);
        occlusionField.set(centerSpace, 5, 5, 5, bottomGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(top));
        assertFalse(Element.earth.in(bottom));

        topGate.open = true;
        occlusionField.set(centerSpace, 5, 6, 5, topGate);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertFalse(Element.earth.in(top));
        assertFalse(Element.earth.in(bottom));
    }

    @Test
    public void invertedStackedFenceGates() {
        fenceGate.open = false;

        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        final FenceGate
                topGate = new FenceGate(),
                bottomGate = new FenceGate();

        bottomGate.open = false;
        topGate.open = true;

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(fenceGate);
        when(centerSpace.blockAt(5, 5, 5)).thenReturn(fenceGate);
        when(instanceSpace.blockObjectAt(5, 6, 5)).thenReturn(topGate);
        when(instanceSpace.blockObjectAt(5, 5, 5)).thenReturn(bottomGate);

        occlusionField.set(centerSpace, 5, 6, 5, topGate);
        occlusionField.set(centerSpace, 5, 5, 5, bottomGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(top));
        assertTrue(Element.earth.in(bottom));

        topGate.open = false;
        occlusionField.set(centerSpace, 5, 6, 5, topGate);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(top));
        assertTrue(Element.earth.in(bottom));
    }


    @Test
    public void stackedFenceGateTopOpen() {
        fenceGate(false, 5, 5, 5);
        fenceGate(false, 5, 6, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        final FenceGate
                topGate = new FenceGate(),
                bottomGate = new FenceGate();

        bottomGate.open = false;
        topGate.open = true;

        fenceGate.open = false;

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(fenceGate);
        when(centerSpace.blockAt(5, 5, 5)).thenReturn(fenceGate);
        when(instanceSpace.blockObjectAt(5, 6, 5)).thenReturn(topGate);
        when(instanceSpace.blockObjectAt(5, 5, 5)).thenReturn(bottomGate);

        occlusionField.set(centerSpace, 5, 6, 5, topGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(top));
        assertTrue(Element.earth.in(bottom));

        topGate.open = false;
        occlusionField.set(centerSpace, 5, 6, 5, topGate);

        top = occlusionField.elementAt(5, 6, 5);
        bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.earth.in(top));
        assertTrue(Element.earth.in(bottom));
    }

    @Test
    public void stackedFenceGateOpen() {
        fenceGate(false, 5, 5, 5);
        fenceGate(false, 5, 6, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        fenceGate.open = false;

        final FenceGate
                topGate = new FenceGate(),
                bottomGate = new FenceGate();

        bottomGate.open = true;
        topGate.open = true;

        when(centerSpace.blockAt(5, 6, 5)).thenReturn(fenceGate);
        when(centerSpace.blockAt(5, 5, 5)).thenReturn(fenceGate);
        when(instanceSpace.blockObjectAt(5, 6, 5)).thenReturn(topGate);
        when(instanceSpace.blockObjectAt(5, 5, 5)).thenReturn(bottomGate);

        occlusionField.set(centerSpace, 5, 6, 5, topGate);
        occlusionField.set(centerSpace, 5, 5, 5, topGate);

        byte
                top = occlusionField.elementAt(5, 6, 5),
                bottom = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.air.in(top));
        assertTrue(Element.air.in(bottom));
    }

    @Test
    public void lowerBounded() {
        when(instanceSpace.columnarSpaceAt(0, 0)).thenReturn(centerSpace);
        centerSpace.occlusionFields().occlusionFieldAt(0, 0, 0);
        occlusionField.set(centerSpace, 0, 1, 0, stone);
        occlusionField.set(centerSpace, 0, 0, 0, stone);
    }

    @Test
    public void upperBounded() {
        when(instanceSpace.columnarSpaceAt(0, 0)).thenReturn(centerSpace);
        centerSpace.occlusionFields().occlusionFieldAt(0, 15, 0);
        occlusionField.set(centerSpace, 0, 254, 0, stone);
        occlusionField.set(centerSpace, 0, 255, 0, stone);
    }

    @Test
    public void incineratingOverSolid() {
        class SolidIncinerating extends Stone {
            @Override
            public boolean isIncinerating() {
                return true;
            }
        }

        occlusionField.set(centerSpace, 2, 9, 6, new SolidIncinerating());
        final byte element = occlusionField.elementAt(2, 9, 6);
        assertTrue(Element.fire.in(element));
    }

    @Test
    public void openIntractableDoor() {
        ironDoor(true, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        final byte flags = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.air.in(flags));
        assertTrue(Logic.doorway.in(flags));
    }

    @Test
    public void closedIntractableDoor() {
        ironDoor(false, 5, 5, 5);
        occlusionField.loadFrom(centerSpace, 0, 0, 0);

        final byte flags = occlusionField.elementAt(5, 5, 5);

        assertTrue(Element.fire.in(flags));
        assertTrue(Logic.doorway.in(flags));
    }

    @Test
    public void testDynamicHeightBoundaries() {
        int minY = -64;
        int maxY = 320;

        // Initialize the ColumnarOcclusionFieldList with dynamic height range
        ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, minY, maxY);

        // Access the OcclusionField at the minimum chunk index
        OcclusionField minField = fieldList.occlusionFieldAt(0, minY >> 4, 0);
        // Set and retrieve a block at minY
        minField.set(centerSpace, 0, minY, 0, stone);
        assertTrue(Element.earth.in(minField.elementAt(0, 0, 0)));

        // Access the OcclusionField at the maximum chunk index
        OcclusionField maxField = fieldList.occlusionFieldAt(0, (maxY - 1) >> 4, 0);
        // Set and retrieve a block at maxY - 1
        maxField.set(centerSpace, 0, maxY - 1, 0, stone);
        assertTrue(Element.earth.in(maxField.elementAt(0, 15, 0)));
    }


    @Test(expected = IndexOutOfBoundsException.class)
    public void testBoundaryValidation() {
        ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, -64, 320);

        // Access out-of-bounds chunk index
        fieldList.occlusionFieldAt(0, Math.floorDiv(321, 16), 0);
    }


    @Test
    public void testDynamicHeightRangeWithEdgeCases() {
        int[][] testRanges = {
                {-64, 320},    // Overworld range
                {0, 256},      // Nether range
                {-1000, 1000}, // Large custom range
                {-30, 30},     // Small range
                {1000, 1016}   // High altitude small range
        };

        for (int[] range : testRanges) {
            int minY = range[0];
            int maxY = range[1];

            System.out.println("\nTesting range: [" + minY + ", " + maxY + "]");

            ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, minY, maxY);

            System.out.println("minY chunk index: " + (minY >> 4));
            System.out.println("maxY - 1 chunk index: " + ((maxY - 1) >> 4));

            OcclusionField minField = fieldList.occlusionFieldAt(0, minY >> 4, 0);
            int minDy = minY & 15; // Normalize Y within chunk
            minField.set(centerSpace, 0, minY, 0, stone);
            assertTrue("Min field failed at minY=" + minY, Element.earth.in(minField.elementAt(0, minDy, 0)));

            OcclusionField maxField = fieldList.occlusionFieldAt(0, (maxY - 1) >> 4, 0);
            int maxDy = (maxY - 1) & 15; // Normalize Y within chunk
            maxField.set(centerSpace, 0, maxY - 1, 0, stone);
            assertTrue("Max field failed at maxY=" + maxY, Element.earth.in(maxField.elementAt(0, maxDy, 0)));
        }
    }

    @Test
    public void testDynamicHeightVariousRanges() {
        int[][] testRanges = {
                {-64, 320},   // Standard Overworld range
                {0, 256},     // Nether-like range
                {-1000, 1000},// High custom range
                {-30, 30},    // Small custom range
        };

        for (int[] range : testRanges) {
            int minY = range[0];
            int maxY = range[1];

            System.out.println("\nTesting range: [" + minY + ", " + maxY + "]");

            // Initialize the ColumnarOcclusionFieldList
            ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, minY, maxY);

            // Log chunk indices for debugging
            System.out.println("minY chunk index: " + (minY >> 4));
            System.out.println("maxY - 1 chunk index: " + ((maxY - 1) >> 4));

            // Test minY
            OcclusionField minField = fieldList.occlusionFieldAt(0, minY >> 4, 0);
            int minDy = minY & 15; // Normalize Y within the chunk
            minField.set(centerSpace, 0, minY, 0, stone);
            assertTrue("Failed for minY: " + minY, Element.earth.in(minField.elementAt(0, minDy, 0)));

            // Test maxY - 1
            OcclusionField maxField = fieldList.occlusionFieldAt(0, (maxY - 1) >> 4, 0);
            int maxDy = (maxY - 1) & 15; // Normalize Y within the chunk
            maxField.set(centerSpace, 0, maxY - 1, 0, stone);
            assertTrue("Failed for maxY: " + maxY, Element.earth.in(maxField.elementAt(0, maxDy, 0)));
        }
    }

    @Test
    public void testOptOcclusionFieldAt() {
        int minY = -64;
        int maxY = 320;

        // Initialize the ColumnarOcclusionFieldList
        ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, minY, maxY);

        // Test valid range
        OcclusionField validField = fieldList.optOcclusionFieldAt((maxY - 1) >> 4);
        assertNull("Expected null for uncreated field in valid range", validField);

        // Test out-of-bounds range
        OcclusionField outOfBoundsField = fieldList.optOcclusionFieldAt(Math.floorDiv(maxY, 16) + 1);
        assertNull("Expected null for out-of-bounds field", outOfBoundsField);
    }

    @Test
    public void testMidRangePlacements() {
        int[][] testRanges = {
                {-64, 320},
                {0, 256},
                {-1000, 1000},
                {1000, 2000}
        };

        for (int[] range : testRanges) {
            int minY = range[0];
            int maxY = range[1];
            int midY = (minY + maxY) / 2;

            ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, minY, maxY);

            OcclusionField midField = fieldList.occlusionFieldAt(0, midY >> 4, 0);
            int midDy = midY & 15; // Normalize Y within the chunk

            midField.set(centerSpace, 0, midY, 0, stone);

            assertTrue("Failed at midY=" + midY, Element.earth.in(midField.elementAt(0, midDy, 0)));
        }
    }

    @Test
    public void testEdgeCasesForCustomRanges() {
        int[][] edgeCases = {
                {-16, 16}, // Very small range
                {-2032, -2020}, // Small negative range
                {0, 16}, // Single chunk range
                {1000, 1016}, // High altitude small range
        };

        for (int[] range : edgeCases) {
            int minY = range[0];
            int maxY = range[1];

            // Initialize the ColumnarOcclusionFieldList
            ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, minY, maxY);

            // Validate the fields for minY and maxY - 1
            OcclusionField minField = fieldList.occlusionFieldAt(0, minY >> 4, 0);
            OcclusionField maxField = fieldList.occlusionFieldAt(0, (maxY - 1) >> 4, 0);

            assertNotNull("Min field should be initialized", minField);
            assertNotNull("Max field should be initialized", maxField);
        }
    }

    @Test
    public void testEnhancedOptOcclusionFieldAt() {
        int minY = -64;
        int maxY = 320;

        // Initialize the ColumnarOcclusionFieldList
        ColumnarOcclusionFieldList fieldList = new ColumnarOcclusionFieldList(centerSpace, minY, maxY);

        // Test valid range but uninitialized field
        int validChunkIndex = (maxY - 1) >> 4;
        OcclusionField validField = fieldList.optOcclusionFieldAt(validChunkIndex);
        assertNull("Expected null for uninitialized field in valid range", validField);

        // Create and retrieve a field
        fieldList.occlusionFieldAt(0, validChunkIndex, 0);
        validField = fieldList.optOcclusionFieldAt(validChunkIndex);
        assertNotNull("Expected initialized field in valid range", validField);

        // Test out-of-bounds negative range
        int outOfBoundsLow = Math.floorDiv(minY, 16) - 1;
        OcclusionField outOfBoundsFieldLow = fieldList.optOcclusionFieldAt(outOfBoundsLow);
        assertNull("Expected null for out-of-bounds low range", outOfBoundsFieldLow);

        // Test out-of-bounds positive range
        int outOfBoundsHigh = Math.floorDiv(maxY, 16) + 1;
        OcclusionField outOfBoundsFieldHigh = fieldList.optOcclusionFieldAt(outOfBoundsHigh);
        assertNull("Expected null for out-of-bounds high range", outOfBoundsFieldHigh);

        // Test mid-range retrieval
        int midChunkIndex = (minY + maxY) / 32; // Average Y normalized to chunk index
        OcclusionField midField = fieldList.optOcclusionFieldAt(midChunkIndex);
        assertNull("Expected null for uninitialized mid-range field", midField);

        // Initialize mid-range and verify retrieval
        fieldList.occlusionFieldAt(0, midChunkIndex, 0);
        midField = fieldList.optOcclusionFieldAt(midChunkIndex);
        assertNotNull("Expected initialized field in mid-range", midField);
    }
}
