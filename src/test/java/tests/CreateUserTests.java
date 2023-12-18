package tests;

import classes.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.qameta.allure.Description;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static java.net.HttpURLConnection.*;
import static org.hamcrest.Matchers.is;
import static steps.UserSteps.createUser;


public class CreateUserTests {

    public String EMAIL = RandomStringUtils.randomAlphabetic(8) + "@yandex.ru";
    public String PASSWORD = RandomStringUtils.randomAlphanumeric(8);
    public String NAME = "Name123";
    private final User user = new User(EMAIL, PASSWORD, NAME);
    protected String token;

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Создание пользователя")
    @Description("Создание нового уникального пользователя")
    public void createNewUserTest() {

        token = createUser(user)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .assertThat()
                .body("success", is(true))
                .extract().path("accessToken");

    }
    @Test
    @DisplayName("Создание пользователя, который уже зарегестрирован")
    @Description("Повторная регистрация уже зарегистрированного пользователя")
    public void createDuplicateUserTest() {
        token = createUser(user)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .body("success", is(true))
                .extract().path("accessToken");
        createUser(user)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", is(false))
                .body("message", is("User already exists"));
    }

    @Test
    @DisplayName("Создание пользователя без поля name")
    @Description("Создание нового пользователя без поля name")
    public void createUserWithoutNameTest() {
        createUser(EMAIL, PASSWORD, null)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без поля password")
    @Description("Создание нового пользователя без поля password")
    public void createUserWithoutPasswordTest() {
        createUser(EMAIL, null, NAME)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @Test
    @DisplayName("Создание пользователя без поля email")
    @Description("Создание нового пользователя без поля email")
    public void createUserWithoutEmailTest() {
        createUser(null, PASSWORD, NAME)
                .then()
                .assertThat()
                .statusCode(HTTP_FORBIDDEN)
                .and()
                .body("success", is(false))
                .body("message", is("Email, password and name are required fields"));
    }

    @After
    @Description("Delete data")
    public void tearDown() {
        if (token != null)
            UserSteps.deleteUser(token);
    }
}
