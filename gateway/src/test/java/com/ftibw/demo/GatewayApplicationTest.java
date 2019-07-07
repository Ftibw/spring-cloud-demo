
package com.ftibw.demo;

import org.junit.Test;
import org.junit.Before;
import org.junit.After;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.adapter.DefaultServerWebExchange;

/**
 * GatewayApplication Tester.
 *
 * @author <Ftibw>
 * @version 1.0
 * @since <pre>06/26/2019</pre>
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class GatewayApplicationTest /*extends AbstractShiroMock*/ {

    @Before
    public void before() throws Exception {
        //super.before();
        //login("domain,account", "password");
    }

    @After
    public void after() throws Exception {
        //super.after();
    }


    @Test
    public void testMain() throws Exception {

    }


} 
