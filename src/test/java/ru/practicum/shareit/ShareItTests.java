package ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;


@SpringBootTest(classes = ShareItApp.class)
class ShareItTests {

    @Test
     void main() {
        ShareItApp.main(new String[]{});
    }

    @Test
    void contextLoads() {
    }



}
