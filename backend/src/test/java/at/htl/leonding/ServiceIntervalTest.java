/*package at.htl.leonding;

import at.ktm.maintenance.model.ServiceInterval;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNull;

class ServiceIntervalTest {

    @Test
    void testGetterAndSetterForId() {
        ServiceInterval serviceInterval = new ServiceInterval();
        serviceInterval.setId(1L);
        assertEquals(1L, serviceInterval.getId());
    }

    @Test
    void testGetterAndSetterForBikeserviceId() {
        ServiceInterval serviceInterval = new ServiceInterval();
        serviceInterval.setBikeserviceId(100);
        assertEquals(100, serviceInterval.getBikeserviceId());
    }

    @Test
    void testGetterAndSetterForBikeId() {
        ServiceInterval serviceInterval = new ServiceInterval();
        serviceInterval.setBikeId(200);
        assertEquals(200, serviceInterval.getBikeId());
    }

    @Test
    void testGetterAndSetterForInterval() {
        ServiceInterval serviceInterval = new ServiceInterval();
        serviceInterval.setInterval(5000);
        assertEquals(5000, serviceInterval.getInterval());
    }

    @Test
    void testDefaultValuesAreNull() {
        ServiceInterval serviceInterval = new ServiceInterval();

        assertNull(serviceInterval.getId());
        assertNull(serviceInterval.getBikeserviceId());
        assertNull(serviceInterval.getBikeId());
        assertNull(serviceInterval.getInterval());
    }

    @Test
    void testMutatingAllFields() {
        ServiceInterval serviceInterval = new ServiceInterval();

        serviceInterval.setId(10L);
        serviceInterval.setBikeserviceId(101);
        serviceInterval.setBikeId(301);
        serviceInterval.setInterval(10000);

        assertEquals(10L, serviceInterval.getId());
        assertEquals(101, serviceInterval.getBikeserviceId());
        assertEquals(301, serviceInterval.getBikeId());
        assertEquals(10000, serviceInterval.getInterval());
    }

    @Test
    void testEqualityForSameValues() {
        ServiceInterval serviceInterval1 = new ServiceInterval();
        ServiceInterval serviceInterval2 = new ServiceInterval();

        serviceInterval1.setId(1L);
        serviceInterval1.setBikeserviceId(100);
        serviceInterval1.setBikeId(200);
        serviceInterval1.setInterval(5000);

        serviceInterval2.setId(1L);
        serviceInterval2.setBikeserviceId(100);
        serviceInterval2.setBikeId(200);
        serviceInterval2.setInterval(5000);

        assertEquals(serviceInterval1.getId(), serviceInterval2.getId());
        assertEquals(serviceInterval1.getBikeserviceId(), serviceInterval2.getBikeserviceId());
        assertEquals(serviceInterval1.getBikeId(), serviceInterval2.getBikeId());
        assertEquals(serviceInterval1.getInterval(), serviceInterval2.getInterval());
    }

    @Test
    void testInequalityForDifferentValues() {
        ServiceInterval serviceInterval1 = new ServiceInterval();
        ServiceInterval serviceInterval2 = new ServiceInterval();

        serviceInterval1.setId(1L);
        serviceInterval1.setBikeserviceId(100);
        serviceInterval1.setBikeId(200);
        serviceInterval1.setInterval(5000);

        serviceInterval2.setId(2L);
        serviceInterval2.setBikeserviceId(101);
        serviceInterval2.setBikeId(300);
        serviceInterval2.setInterval(6000);

        assertNotEquals(serviceInterval1.getId(), serviceInterval2.getId());
        assertNotEquals(serviceInterval1.getBikeserviceId(), serviceInterval2.getBikeserviceId());
        assertNotEquals(serviceInterval1.getBikeId(), serviceInterval2.getBikeId());
        assertNotEquals(serviceInterval1.getInterval(), serviceInterval2.getInterval());
    }
}
*/