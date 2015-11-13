package com.centro.controllers;

import java.io.IOException;
import static org.junit.Assert.assertEquals;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = {"classpath:mvc-dispatcher-servlet.xml"})
@Controller
public class ApiControllerTest {
    
    @Autowired
    ApiController controller;
    
    @Test
    public void centralTest() throws IOException {
        String query = "[{\"latitude\": 51.5073509, \"longitude\": -0.1277583}, {\"latitude\": 51.7520209, \"longitude\": -1.2577263}]";
        String expectedResponse = "{\"latitude\":51.63104157780866,\"longitude\":-0.691218624225265}";
        
        String actualResponse = controller.central(query);
        assertEquals(expectedResponse, actualResponse);
    }
    
}
