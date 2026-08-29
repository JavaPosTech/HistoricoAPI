package br.com.fiap.historicoapi.config;

import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.graphql.test.autoconfigure.tester.AutoConfigureGraphQlTester;
import org.springframework.context.annotation.Import;
import org.springframework.graphql.test.tester.GraphQlTester;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@ActiveProfiles("test")
@AutoConfigureGraphQlTester
@Import(value = TestDataBaseConfig.class)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public abstract class AbstractControllerTest {

    @Autowired
    protected GraphQlTester graphQlTester;

    protected GraphQlTester.Response executarQuery(String documento, String nomeVariavel, Object valor) {
        return graphQlTester.document(documento)
                .variable(nomeVariavel, valor)
                .execute();
    }
}