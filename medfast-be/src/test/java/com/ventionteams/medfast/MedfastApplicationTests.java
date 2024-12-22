package com.ventionteams.medfast;

import com.ventionteams.medfast.config.extension.PostgreContainerExtension;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Tests the MedfastApplication with integration tests.
 */
@SpringBootTest
@ExtendWith(PostgreContainerExtension.class)
public class MedfastApplicationTests {

  @Test
  void main_MethodWorks_SpringIsRunning() {
    MedfastApplication.main(new String[]{});
  }
}
