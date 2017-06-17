package com.itransition.pmanager;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

@RunWith(SpringRunner.class)
@SpringBootTest
public class ProjectManagerApplicationTests {

	@Test
	public void contextLoads() {



		String s = "Test TheBestProject";
		String[] ss=s.split(" ");
        for (String m:ss
             ) {
            System.out.print(m+" ");
        }
    }

}
