package java.ru.practicum.shareit;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.shareit.ShareItServer;


@SpringBootTest(classes = ShareItServer.class)
class ShareItTests {

    @Test
    void main() {
        ShareItServer.main(new String[]{});
    }

    @Test
    void contextLoads() {
    }


}
