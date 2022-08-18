package com.test.base;

import com.geekq.provider.GoodsProviderApplication;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = GoodsProviderApplication.class)
@WebAppConfiguration
public class BaseTest {


}
