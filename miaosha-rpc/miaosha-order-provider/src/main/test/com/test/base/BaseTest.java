package com.test.base;

import com.geekq.provider.OrderProviderApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = OrderProviderApplication.class)
@WebAppConfiguration
public class BaseTest {


}
