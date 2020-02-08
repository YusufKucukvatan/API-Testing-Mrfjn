package selfStudy;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import pojos.Spartan;
import utilities.ConfigurationReader;
import utilities.SpartanApiUtils;

import java.lang.reflect.Array;
import java.util.*;

import static io.restassured.RestAssured.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.*;

public class HarryPotter2 {

    @BeforeAll
    public static void setUp(){
        baseURI = ConfigurationReader.get("harryPotterApiBaseURL");
    }

    @Test
    @DisplayName("Verify sorting hat")
    public void test1(){

        List<String> expected = new ArrayList<>(Arrays.asList("\"Gryffindor\"", "\"Ravenclaw\"", "\"Slytherin\"", "\"Hufflepuff\""));

        Response response =
                get("/sortingHat").prettyPeek();
        assertEquals(200, response.statusCode());
        assertEquals("application/json; charset=utf-8", response.contentType());
        assertTrue(expected.contains(response.body().asString()));
    }

    @Test
    @DisplayName("Verify bad key")
    public void test2(){


        String expected = "{error=API Key Not Found}";

        given()
                .accept(ContentType.JSON)
                .queryParam("key", "invalid")
        .when()
                .get("/characters")
         .then()
                .assertThat()
                    .statusCode(401)
                    .contentType("application/json; charset=utf-8")
                    .statusLine(containsString("Unauthorized"))
                    .body("",is(expected));

    }

    @Test
    @DisplayName("Verify no key")
    public void test3(){

        String expected = "{\"error\":\"Must pass API key for request\"}";

        given()
                .accept(ContentType.JSON)
        .when()
                .get("/characters").prettyPeek()
        .then()
                .assertThat()
                    .statusCode(409)
                    .contentType("application/json; charset=utf-8")
                    .statusLine(containsString("Conflict"))
                    .body(containsString(expected));
    }

    @Test
    @DisplayName("Verify number of characters")
    public void test4(){

        given()
                .accept(ContentType.JSON)
                .queryParam("key",ConfigurationReader.get("harryPotterApiKey"))
        .when()
                .get("/characters").prettyPeek()
        .then()
                .assertThat()
                    .statusCode(200)
                    .contentType("application/json; charset=utf-8")
                    .body("",hasSize(195));
    }

}
