
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.Timeout;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import io.vertx.reactivex.core.Vertx;
import io.vertx.reactivex.redis.RedisClient;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

/**
 *
 * @author vinay
 */
@ExtendWith(VertxExtension.class)
@Testcontainers
@Timeout(value = 10, timeUnit = TimeUnit.MINUTES)
public class RedisSelectNotWorkingTest {

  @Container
  private final GenericContainer REDIS_CONTAINER = new GenericContainer("redis:5").withExposedPorts(6379);
  private final RedisClient client;

  public RedisSelectNotWorkingTest() {
    REDIS_CONTAINER.getPortBindings().add("6379:6379");
    client = RedisClient.create(Vertx.vertx(),new JsonObject().put("select", 0));
  }

  @Test
  public void brokenConnection(VertxTestContext testContext) {

    client
            .rxGet("test")
            .subscribe((l) -> testContext.completeNow(), err -> testContext.failNow(err), () -> testContext.completeNow());

  }
}
