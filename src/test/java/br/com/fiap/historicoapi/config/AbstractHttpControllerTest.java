package br.com.fiap.historicoapi.config;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureHttpGraphQlTester;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.graphql.test.tester.HttpGraphQlTester;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@AutoConfigureHttpGraphQlTester
@Import(value = TestDataBaseConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractHttpControllerTest {

    @Autowired
    protected HttpGraphQlTester graphQlTester;

    protected GraphQlTester.Response executarQuery(String documento, String nomeVariavel, Object valor) {
        return graphQlTester.document(documento)
                .variable(nomeVariavel, valor)
                .execute();
    }
}
