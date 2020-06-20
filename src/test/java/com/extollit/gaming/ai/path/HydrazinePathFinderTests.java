package com.extollit.gaming.ai.path;

import com.extollit.gaming.ai.path.model.*;
import com.extollit.linalg.immutable.Vec3i;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class HydrazinePathFinderTests extends AbstractHydrazinePathFinderTests {

    @Test
    public void stepUp() {
        solid(0, -1, 0);
        solid(1, 0, 0);
        solid(1, 3, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNotNull(actual);
        assertEquals(Passibility.passible, actual.passibility());
        assertEquals(1, actual.key.y);
    }

    @Test
    public void noStepUp() {
        solid(0, -1, 0);
        solid(1, -1, 0);
        solid(1, 0, 0);
        solid(1, 1, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNull(actual);
    }

    @Test
    public void narrowAtHead() {
        solid(1, 0, 0);
        solid(1, 2, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNull(actual);
    }

    @Test
    public void narrowAtFeet() {
        clear(1, 0, 0);
        slabUp(1, 1, 0);
        solid(1, -1, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNull(actual);
    }

    @Test
    public void narrowAtFeetDown1() {
        solid(0, -1, 0);
        clear(1, 0, 0);
        slabUp(1, 1, 0);
        clear(1, -1, 0);
        solid(1, -2, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNull(actual);
    }

    @Test
    public void barelyHeadSpace() {
        solid(0, -1, 0);
        clear(0, 0, 0);
        clear(0, 1, 0);
        slabDown(0, 0, 1);
        clear(0, 1, 1);
        slabUp(0, 2, 1);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(0, 0, 1), ORIGIN);
        assertEquals(1, actual.key.y);
    }

    @Test
    public void climbLadder() {
        solid(1, 0, 0);
        solid(1, 1, 0);
        solid(1, 2, 0);
        solid(1, 3, 0);
        climb(0, 0, 0);
        climb(0, 1, 0);
        climb(0, 2, 0);
        climb(0, 3, 0);
        solid(0, -1, 0);
        solid(1, -1, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);

        assertNotNull(actual);
        assertEquals(4, actual.key.y);
        assertEquals(1, actual.key.x);
    }

    @Test
    public void noClimbDiagonal() {
        solid(1, 0, 1);
        solid(1, 1, 1);
        solid(1, 2, 1);
        solid(1, 3, 1);
        climb(0, 0, 0);
        climb(0, 1, 0);
        climb(0, 2, 0);
        climb(0, 3, 0);
        solid(0, -1, 0);
        solid(1, -1, 1);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 1), ORIGIN);

        assertNull(actual);
    }

    @Test
    public void diagonalFences() {
        solid(0, 0, 0);
        solid(1, 0, 0);
        solid(0, 0, 1);
        solid(1, 0, 1);
        longFence(0, 1, 0);
        longFence(1, 1, 1);
        pos(0, 1, 1);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 1, 0), new Vec3i(0, 1, 1));

        assertNotNull(actual);
        assertEquals(new Vec3i(1, 1, 0), actual.key);
    }

    @Test
    public void ladderOneUp() {
        solid(1, 0, 0);
        solid(1, 1, 0);
        solid(1, 2, 0);
        solid(1, 3, 0);
        climb(0, 0, 0);
        climb(0, 1, 0);
        climb(0, 2, 0);
        solid(0, -1, 0);
        solid(1, -1, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);

        assertNotNull(actual);
        assertEquals(4, actual.key.y);
        assertEquals(1, actual.key.x);
    }

    @Test
    public void ohSoHoppySlab() {
        slabDown(0, 0, 0);
        solid(0, 0, 1);
        solid(0, 1, 1);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(0, 1, 1), new Vec3i(0, 1, 0));

        assertNull(actual);
    }

    @Test
    public void noHoppySlab() {
        slabDown(0, 0, 0);
        solid(0, 0, 1);
        slabDown(0, 1, 1);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(0, 1, 1), new Vec3i(0, 1, 0));

        assertNotNull(actual);
        assertEquals(2, actual.key.y);
    }

    @Test
    public void ladderOneTooManyUp() {
        solid(1, 0, 0);
        solid(1, 1, 0);
        solid(1, 2, 0);
        solid(1, 3, 0);
        slabDown(1, 4, 0);
        climb(0, 0, 0);
        climb(0, 1, 0);
        climb(0, 2, 0);
        solid(0, -1, 0);
        solid(1, -1, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);

        assertNull(actual);
    }

    @Test
    public void ladderHalfUp() {
        solid(1, 0, 0);
        solid(1, 1, 0);
        solid(1, 2, 0);
        slabDown(1, 3, 0);
        climb(0, 0, 0);
        climb(0, 1, 0);
        climb(0, 2, 0);
        solid(0, -1, 0);
        solid(1, -1, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);

        assertNotNull(actual);
        assertEquals(4, actual.key.y);
        assertEquals(1, actual.key.x);
    }

    @Test
    public void slabUpDown() {
        cautious(false);

        slabDown(0, 0, 0);
        solid(0, -1, 0);
        solid(1, -2, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 1, 0), new Vec3i(0, 1, 0));

        assertNotNull(actual);
        assertEquals(Passibility.risky, actual.passibility());
        assertEquals(-1, actual.key.y);
    }

    @Test
    public void safeFall() {
        solid(1, -1, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNotNull(actual);
        assertEquals(Passibility.passible, actual.passibility());
    }

    @Test
    public void riskyFall() {
        solid(1, -3, 0);

        cautious(false);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNotNull(actual);
        assertEquals(Passibility.risky, actual.passibility());
    }

    @Test
    public void veryRiskyFall() {
        solid(1, -5, 0);

        cautious(false);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNotNull(actual);
        assertEquals(Passibility.risky, actual.passibility());
    }

    @Test
    public void dangerousFall() {
        solid(1, -21, 0);

        cautious(false);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNotNull(actual);
        assertEquals(Passibility.dangerous, actual.passibility());
    }

    @Test
    public void swimming() {
        solid(1, -3, 0);
        water(1, -2, 0);
        water(1, -1, 0);
        solid(0, -3, 0);
        water(0, -2, 0);
        water(0, -1, 0);

        diver();

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertNotNull(actual);
        assertEquals(0, actual.key.y);
    }

    @Test
    public void drown() {
        for (int y = 20; y > -20; --y)
            for (int z = -1; z <= +1; ++z)
                for (int x = -1; x <= +1; ++x)
                    water(x, y, z);

        diver();

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertEquals(Passibility.risky, actual.passibility());
        assertEquals(0, actual.key.y);
    }

    @Test
    public void cesa() {
        for (int y = 12; y > -20; --y)
            for (int z = -1; z <= 1; ++z)
                for (int x = -1; x <= 1; ++x)
                    water(x, y, z);

        diver();

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertEquals(Passibility.risky, actual.passibility());
        assertEquals(13, actual.key.y);
    }

    @Test
    public void dive() {
        for (int y = -5; y > -10; --y)
            for (int z = -1; z <= 1; ++z)
                for (int x = -1; x <= 1; ++x)
                    water(x, y, z);

        diver();

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);
        assertEquals(Passibility.risky, actual.passibility());
        assertEquals(-4, actual.key.y);
    }

    @Test
    public void outPool() {
        water(0, -1, 0);
        clear(0, 0, 0);
        clear(0, 1, 0);
        clear(0, 2, 0);

        solid(1, -1, 0);
        solid(1, 0, 0);
        clear(1, 1, 0);
        clear(1, 2, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);

        assertNull(actual);
    }

    @Test
    public void climbOutPool() {
        water(0, -1, 0);
        climb(0, 0, 0);
        climb(0, 1, 0);
        climb(0, 2, 0);

        solid(1, -1, 0);
        solid(1, 0, 0);
        clear(1, 1, 0);
        clear(1, 2, 0);

        final HydrazinePathPoint actual = pathFinder.passiblePointNear(new Vec3i(1, 0, 0), ORIGIN);

        assertEquals(Passibility.passible, actual.passibility());
        assertEquals(new Vec3i(1, 1, 0), actual.key);
    }

    @Test
    public void refinePassibilityGate() {
        solid(0, -1, 0);
        solid(1, -1, 0);
        solid(-1, -1, 0);
        pos(0.2f, 0, 0);

        longFence(0, 0, 0);
        when(occlusionProvider.elementAt(0, 0, 0)).thenReturn(Logic.doorway.to(Element.earth.mask));

        final PathObject path = pathFinder.initiatePathTo(1, 0, 0);

        assertNull(path);

        assertTrue(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(+1, 0, 0)));
        assertFalse(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(-1, 0, 0)));
    }

    @Test
    public void refinePassibilityEast() {
        solid(0, -1, 0);
        solid(1, -1, 0);
        solid(-1, -1, 0);
        pos(0.2f, 0, 0);

        longFence(0, 0, 0);

        final PathObject path = pathFinder.initiatePathTo(1, 0, 0);

        assertNull(path);

        assertTrue(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(+1, 0, 0)));
        assertFalse(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(-1, 0, 0)));
    }

    @Test
    public void refinePassibilityWest() {
        solid(0, -1, 0);
        solid(1, -1, 0);
        solid(-1, -1, 0);
        pos(0.8f, 0, 0);

        longFence(0, 0, 0);

        final PathObject path = pathFinder.initiatePathTo(-1, 0, 0);

        assertNull(path);

        assertTrue(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(-1, 0, 0)));
        assertFalse(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(+1, 0, 0)));
    }

    @Test
    public void refinePassibilitySouth() {
        solid(0, -1, 0);
        solid(0, -1, +1);
        solid(0, -1, -1);
        pos(0, 0, 0.2f);

        latFence(0, 0, 0);

        final PathObject path = pathFinder.initiatePathTo(0, 0, 1);

        assertNull(path);

        assertTrue(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(0, 0, +1)));
        assertFalse(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(0, 0, -1)));
    }

    @Test
    public void refinePassibilityNorth() {
        solid(0, -1, 0);
        solid(0, -1, +1);
        solid(0, -1, -1);
        pos(0, 0, 0.8f);

        latFence(0, 0, 0);

        final PathObject path = pathFinder.initiatePathTo(0, 0, -1);

        assertNull(path);

        assertTrue(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(0, 0, -1)));
        assertFalse(pathFinder.unreachableFromSource(Vec3i.ZERO, new Vec3i(0, 0, +1)));
    }
}
