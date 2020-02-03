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

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static io.restassured.RestAssured.given;
import static io.restassured.RestAssured.when;
import static org.hamcrest.Matchers.*;

public class HarryPotter {
    @BeforeAll
    public static void setup() {
        RestAssured.baseURI = ConfigurationReader.get("harryPotterApiBaseURL");
    }

    @Test
    @DisplayName("Verify sorting hat")
    public void test1() {
        Response response = when().get("/sortingHat");

        String responsStr = response.body().asString().replaceAll("\"", "");
        List<String> list = List.of("Gryffindor", "Ravenclaw", "Slytherin", "Hufflepuff");

        assertEquals(200, response.statusCode());
        assertTrue(list.contains(responsStr));
    }

    @Test
    @DisplayName("Verify bad key")
    public void test2() {
        String bodyStr = "{\"error\":\"API Key Not Found\"}";
        given()
                .accept("application/json")
                .queryParam("key", "invalid")
                .when()
                .get("/characters")
                .then()
                .assertThat()
                .statusCode(401)
                .contentType("application/json; charset=utf-8")
                .statusLine(containsString("Unauthorized"))
                .body(containsString(bodyStr));


    }

    @Test
    @DisplayName("Verify no key")
    public void test3() {
        String bodyStr = "{\"error\":\"Must pass API key for request\"}";
        given()
                .accept("application/json")
                .when()
                .get("/characters")
                .then()
                .assertThat()
                .statusCode(409)
                .contentType("application/json; charset=utf-8")
                .statusLine(containsString("Conflict"))
                .body(containsString(bodyStr));


    }

    @Test
    @DisplayName("Verify number of characters")
    public void test4() {
        Response response =
                given()
                        .accept("application/json")
                        .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                        .when()
                        .get("/characters");
        JsonPath json = response.jsonPath();
        List<Map<String, ?>> characters = json.getList("");
        System.out.println(characters.size());
        assertEquals(200, response.statusCode());
        assertEquals("application/json; charset=utf-8", response.getContentType());
        assertEquals(195, characters.size());
    }

    @Test
    @DisplayName("Verify number of character id and house")
    public void test5() {
        Response response =
                given()
                        .accept("application/json")
                        .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                        .when()
                        .get("/characters");
        JsonPath json = response.jsonPath();
        List<String> IDs = json.getList("_id");
        List<Boolean> dumble = json.getList("dumbledoresArmy");
        List<String> house = json.getList("house");
        System.out.println("house = " + house);
        response.then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json; charset=utf-8")
                .body("_id", everyItem(notNullValue()))
                .body("dumbledoresArmy", everyItem(is(oneOf(true, false))))
                .body("house", everyItem(is(oneOf("Gryffindor", "Ravenclaw", "Slytherin", "Hufflepuff", null))));
    }

    @Test
    @DisplayName("Verify all character information")
    public void test6() {
        Response response1 =
                given()
                        .accept("application/json")
                        .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                        .when()
                        .get("/characters");
        JsonPath json = response1.jsonPath();
        String firstName = json.getString("name[0]");
        Map<String, ?> character1 = json.getMap("[0]");
        System.out.println("character1 = " + character1);

        Response response2 =
                given()
                        .accept("application/json")
                        .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                        .queryParam("name", firstName)
                        .when()
                        .get("/characters");
        json = response2.jsonPath();

        Map<String, ?> character2 = json.getMap("[0]");
        System.out.println("character2 = " + character2);

        assertEquals(character1, character1);
    }

    @Test
    @DisplayName("Verify name search")
    public void test7() {
        given()
                .accept("application/json")
                .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                .queryParam("name", "Harry Potter")
                .when()
                .get("/characters")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json; charset=utf-8")
                .body("name", contains("Harry Potter"));

        given()
                .accept("application/json")
                .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                .queryParam("name", "Marry Potter")
                .when()
                .get("/characters")
                .then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json; charset=utf-8")
                .body(is(empty()));
    }

    @Test
    @DisplayName("Verify house members")
    public void test8() {
        Response response1 =
                given()
                        .accept("application/json")
                        .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                        .when()
                        .get("/houses");

        response1.then()
                .assertThat()
                .statusCode(200)
                .contentType("application/json; charset=utf-8");

        JsonPath json = response1.jsonPath();
        String  gryffindorID= json.getString("find{it.name==\"Gryffindor\"}._id");
        System.out.println("id = " + gryffindorID);
        List<List<String>> memberIDs1 = json.getList("find{it.name==\"Gryffindor\"}.members");
        System.out.println("memberIDs1 = " + memberIDs1);

        Response response2 =
                given()
                        .accept("application/json")
                        .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                        .pathParam("id", gryffindorID)
                        .when()
                        .get("/houses/{id}");
        json = response2.jsonPath();
        List<String> memberIDs2 = json.getList("members._id");
        System.out.println("memberIDs2 = " + memberIDs2);

        assertEquals(memberIDs1, memberIDs2.get(0));
    }

    @Test
    @DisplayName("Verify house with most members")
    public void test9() {
        Response response =
                given()
                        .accept("application/json")
                        .queryParam("key", ConfigurationReader.get("harryPotterApiKey"))
                .when()
                        .get("/houses");
        JsonPath json = response.jsonPath();

        List<String> Gryffindor = json.getList("find{it.name==\"Gryffindor\"}.members");
        List<String> Ravenclaw = json.getList("find{it.name==\"Ravenclaw\"}.members");
        List<String> Slytherin = json.getList("find{it.name==\"Slytherin\"}.members");
        List<String> Hufflepuff = json.getList("find{it.name==\"Hufflepuff\"}.members");

        assertTrue(Gryffindor.size()>Ravenclaw.size()&&Gryffindor.size()>Slytherin.size()&&Gryffindor.size()>Hufflepuff.size());
    }
}
