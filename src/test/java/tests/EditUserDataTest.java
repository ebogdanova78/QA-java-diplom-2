package tests;

import classes.User;
import io.qameta.allure.Description;
import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import steps.UserSteps;

import static java.net.HttpURLConnection.HTTP_OK;
import static java.net.HttpURLConnection.HTTP_UNAUTHORIZED;
import static org.hamcrest.Matchers.is;
import static steps.UserSteps.editUser;


public class EditUserDataTest {

    public String EMAIL = RandomStringUtils.randomAlphabetic(8) + "@yandex.ru";
    public String PASSWORD = RandomStringUtils.randomAlphanumeric(8);
    public String NAME = "Name101214";
    protected String token;
    private final User user = new User(EMAIL, PASSWORD, NAME);

    @Before
    public void setUp() {
        RestAssured.filters(new RequestLoggingFilter(), new ResponseLoggingFilter());
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site";
    }

    @Test
    @DisplayName("Редактирование авторизованного пользователя")
    @Description("Изменение данных авторизованного пользователя")
    public void editUserAuthorizationTest() {
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        editUser(user, token)
                .then()
                .assertThat()
                .statusCode(HTTP_OK)
                .and()
                .log().all()
                .body("success", is(true))
                .body("user.email", is("emailnew9@mail.ru"))
                .body("user.name", is("namenew9"));

    }

    @Test
    @DisplayName("Редактирование неавторизованного пользователя")
    @Description("Изменение данных неавторизованного пользователя")
    public void editUserNotAuthorizationTest() {
        token = UserSteps.createUser(EMAIL, PASSWORD, NAME).then().extract().path("accessToken");
        editUser(user)
                .then()
                .assertThat()
                .statusCode(HTTP_UNAUTHORIZED)
                .and()
                .body("success", is(false))
                .body("message", is("You should be authorised"));
    }


    @After
    @Description("Delete data")
    public void tearDown() {
      if (token != null)
          UserSteps.deleteUser(token);
    }
}
