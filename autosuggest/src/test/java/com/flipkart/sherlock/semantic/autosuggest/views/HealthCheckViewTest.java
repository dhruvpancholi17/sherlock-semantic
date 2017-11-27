package com.flipkart.sherlock.semantic.autosuggest.views;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.ws.rs.Path;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.UriInfo;
import java.net.URI;

import static com.flipkart.sherlock.semantic.autosuggest.views.HealthCheckView.BIR;
import static com.flipkart.sherlock.semantic.autosuggest.views.HealthCheckView.OOR;

/**
 * Created by dhruv.pancholi on 10/10/17.
 */
public class HealthCheckViewTest {

    @Mock
    private UriInfo uriInfo;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
    }

    @After
    public void tearDown() throws Exception {

    }

    @Test
    public void testAPIPaths() throws NoSuchMethodException {
        Assert.assertEquals("/flash", HealthCheckView.class.getAnnotation(Path.class).value());
        Assert.assertEquals("rotation-status", HealthCheckView.class.getMethod("getStatus", UriInfo.class).getAnnotation(Path.class).value());
        Assert.assertEquals("rotation", HealthCheckView.class.getMethod("flipRotation", String.class, String.class, UriInfo.class).getAnnotation(Path.class).value());
        Assert.assertEquals("elb-healthcheck", HealthCheckView.class.getMethod("getElbHealthCheckStatus", UriInfo.class).getAnnotation(Path.class).value());
    }

    @Test
    public void testValues() {
        Assert.assertEquals("oor", OOR);
        Assert.assertEquals("bir", BIR);
    }

    /**
     * 1. When the box is restarted, it is not in rotation
     * 2. We put the box BIR, and then check the status
     * 3. We again put the box OOR, and then check the status
     * <p>
     * 301 MOVED_PERMANENTLY for OOR, and 200 OK for in rotation is returned for rotation-status
     *
     * @throws Exception
     */
    @Test
    public void getStatus() throws Exception {
        HealthCheckView healthCheckView = new HealthCheckView();
        Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(),
                healthCheckView.getStatus(null).getStatus());

        URI uri = new URI("http://localhost:9007");
        Mockito.when(uriInfo.getBaseUri()).thenReturn(uri);

        healthCheckView.flipRotation(BIR, null, uriInfo);

        Assert.assertEquals(Response.Status.OK.getStatusCode(),
                healthCheckView.getStatus(null).getStatus());

        healthCheckView.flipRotation(OOR, null, uriInfo);
        Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(),
                healthCheckView.getStatus(null).getStatus());
    }

    /**
     * 1. Validate if the change in rotation is working for localhost
     * 2. Check if the request is rejected, if an external host tries to change the rotation status
     *
     * @throws Exception
     */
    @Test
    public void flipRotation() throws Exception {

        URI uri;

        uri = new URI("http://127.0.0.1:9007");
        Mockito.when(uriInfo.getBaseUri()).thenReturn(uri);

        HealthCheckView healthCheckView = new HealthCheckView();
        Response response = null;

        response = healthCheckView.flipRotation(BIR, null, uriInfo);
        Assert.assertEquals(Response.Status.OK.getStatusCode(),
                response.getStatus());
        Assert.assertEquals(Response.Status.OK.getStatusCode(),
                healthCheckView.getStatus(null).getStatus());

        response = healthCheckView.flipRotation(OOR, null, uriInfo);
        Assert.assertEquals(Response.Status.OK.getStatusCode(),
                response.getStatus());
        Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(),
                healthCheckView.getStatus(null).getStatus());

        uri = new URI("http://192.168.0.1:9007");
        Mockito.when(uriInfo.getBaseUri()).thenReturn(uri);

        healthCheckView = new HealthCheckView();
        response = healthCheckView.flipRotation(BIR, null, uriInfo);
        Assert.assertEquals(Response.Status.EXPECTATION_FAILED.getStatusCode(),
                response.getStatus());
        Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(),
                healthCheckView.getStatus(null).getStatus());
    }


    /**
     * 1. When the box is restarted, it is not in rotation
     * 2. We put the box BIR, and then check the status
     * 3. We again put the box OOR, and then check the status
     * <p>
     * 301 MOVED_PERMANENTLY for OOR, and 200 OK for in rotation is returned for elb-healthcheck
     *
     * @throws Exception
     */
    @Test
    public void getElbHealthCheckStatus() throws Exception {
        HealthCheckView healthCheckView = new HealthCheckView();
        Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(),
                healthCheckView.getElbHealthCheckStatus(null).getStatus());

        URI uri = new URI("http://localhost:9007");
        Mockito.when(uriInfo.getBaseUri()).thenReturn(uri);

        healthCheckView.flipRotation(BIR, null, uriInfo);

        Assert.assertEquals(Response.Status.OK.getStatusCode(),
                healthCheckView.getElbHealthCheckStatus(null).getStatus());

        healthCheckView.flipRotation(OOR, null, uriInfo);
        Assert.assertEquals(Response.Status.MOVED_PERMANENTLY.getStatusCode(),
                healthCheckView.getElbHealthCheckStatus(null).getStatus());
    }

}
