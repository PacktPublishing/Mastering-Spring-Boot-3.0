package integrationtests;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.testcontainers.containers.MongoDBContainer;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.utility.DockerImageName;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;
import static com.github.tomakehurst.wiremock.client.WireMock.configureFor;
import static com.github.tomakehurst.wiremock.client.WireMock.get;
import static com.github.tomakehurst.wiremock.client.WireMock.urlEqualTo;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.wireMockConfig;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Testcontainers
@AutoConfigureMockMvc
@ExtendWith(SpringExtension.class)
@ActiveProfiles("integration-test")
public abstract class AbstractIntegrationTest {
    private static final String POSTGRES_IMAGE = "postgres:latest";
    private static final String MONGO_IMAGE = "mongo:4.4.6";
    private static final String DATABASE_NAME = "bookstore";
    private static final String DATABASE_USER = "postgres";
    private static final String DATABASE_PASSWORD = "yourpassword";
    private static final int WIREMOCK_PORT = 8180;
    private static WireMockServer wireMockServer;

    @Container
    static final PostgreSQLContainer<?> postgresqlContainer = initPostgresqlContainer();

    @Container
    static final MongoDBContainer mongoDBContainer = initMongoDBContainer();

    private static PostgreSQLContainer<?> initPostgresqlContainer() {
        PostgreSQLContainer<?> container = new PostgreSQLContainer<>(POSTGRES_IMAGE)
                .withDatabaseName(DATABASE_NAME)
                .withUsername(DATABASE_USER)
                .withPassword(DATABASE_PASSWORD);
        container.start();
        return container;
    }

    private static MongoDBContainer initMongoDBContainer() {
        MongoDBContainer container = new MongoDBContainer(DockerImageName.parse(MONGO_IMAGE));
        container.start();
        return container;
    }

    @DynamicPropertySource
    static void properties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgresqlContainer::getJdbcUrl);
        registry.add("spring.datasource.username", postgresqlContainer::getUsername);
        registry.add("spring.datasource.password", postgresqlContainer::getPassword);
        registry.add("spring.data.mongodb.uri", mongoDBContainer::getReplicaSetUrl);
    }



    @BeforeAll
    public static void setUp() {
        startWireMockServer();
    }

    private static void startWireMockServer() {
        wireMockServer = new WireMockServer(wireMockConfig().port(WIREMOCK_PORT));
        wireMockServer.start();
        configureFor("localhost", WIREMOCK_PORT);
        stubForOpenIDConfiguration();
    }

    private static void stubForOpenIDConfiguration() {
        wireMockServer.stubFor(get(urlEqualTo("/auth/realms/BookStoreRealm/.well-known/openid-configuration"))
                .willReturn(aResponse()
                        .withHeader("Content-Type", "application/json")
                        .withBody("{\n" +
                                "   \"issuer\": \"http://localhost:" + WIREMOCK_PORT + "/auth/realms/BookStoreRealm\",\n" +
                                "   \"authorization_endpoint\": \"http://localhost:" + WIREMOCK_PORT + "/auth/realms/BookStoreRealm/protocol/openid-connect/auth\",\n" +
                                "   \"token_endpoint\": \"http://localhost:" + WIREMOCK_PORT + "/auth/realms/BookStoreRealm/protocol/openid-connect/token\",\n" +
                                "   \"userinfo_endpoint\": \"http://localhost:" + WIREMOCK_PORT + "/auth/realms/BookStoreRealm/protocol/openid-connect/userinfo\",\n" +
                                "   \"jwks_uri\": \"http://localhost:" + WIREMOCK_PORT + "/auth/realms/BookStoreRealm/protocol/openid-connect/certs\",\n" +
                                "   \"response_types_supported\": [\"code\", \"none\", \"id_token\", \"token id_token\"],\n" +
                                "   \"subject_types_supported\": [\"public\"],\n" +
                                "   \"id_token_signing_alg_values_supported\": [\"RS256\"],\n" +
                                "   \"scopes_supported\": [\"openid\", \"profile\", \"email\", \"roles\", \"web-origins\"]\n" +
                                "}")));
    }

    @AfterAll
    public static void tearDown() {
        if (wireMockServer != null && wireMockServer.isRunning()) {
            wireMockServer.stop();
        }
    }
}
