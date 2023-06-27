package com.aayar94

import com.aayar94.models.ApiResponse
import com.aayar94.repository.HeroRepository
import com.aayar94.repository.HeroRepositoryImpl
import com.aayar94.repository.NEXT_PAGE_KEY
import com.aayar94.repository.PREV_PAGE_KEY
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.server.testing.*
import kotlin.test.*
import io.ktor.http.*
import io.ktor.server.application.*
import kotlinx.serialization.json.Json
import org.koin.java.KoinJavaComponent.inject

class ApplicationTest {
    private val heroRepository: HeroRepository by inject(HeroRepository::class.java)

    @Test
    fun `'access root endpoint, assert correct information'`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                assertEquals(
                    expected = "Welcome to Baruto API!",
                    actual = response.content
                )
            }
        }

    @Test
    fun `'access all heroes endpoint, assert correct information'`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = null,
                    nextPage = 2,
                    heroes = heroRepository.page1
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("Expected : $expected\n Actual : $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }

    @Test
    fun `'access all heroes endpoint, queryRequest page 2 assert correct information'`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=2").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = 1,
                    nextPage = 3,
                    heroes = heroRepository.page2
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                println("Expected : $expected\n Actual : $actual")
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }


    /**     TEST EVERY PAGE     */
    @Test
    fun `access all heroes endpoint, query all pages, assert correct information`() =
        testApplication {
            val heroRepository = HeroRepositoryImpl()
            val pages = 1..5
            val heroes = listOf(
                heroRepository.page1,
                heroRepository.page2,
                heroRepository.page3,
                heroRepository.page4,
                heroRepository.page5
            )
            pages.forEach { page ->
                val response = client.get("/boruto/heroes?page=$page")
                println("Current Page : $page")
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status
                )
                val actual = Json.decodeFromString<ApiResponse>(response.bodyAsText())
                val expected = ApiResponse(
                    success = true,
                    message = "ok",
                    prevPage = calculatePage(page = page)["prevPage"],
                    nextPage = calculatePage(page = page)["nextPage"],
                    heroes = heroes[page - 1],

                    )
                assertEquals(
                    expected = expected,
                    actual = actual
                )
            }
        }

    @Test
    fun `access all heroes endpoint, query non existing page number , assert error`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=6").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Heroes Not Found",
                )
            }
        }

    @Test
    fun `access all heroes endpoint, query invalid page number , assert error`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes?page=invalid").apply {
                assertEquals(
                    expected = HttpStatusCode.BadRequest,
                    actual = response.status()
                )
                val expected = ApiResponse(
                    success = false,
                    message = "Only Number Allowed",
                )
            }
        }

    @Test
    fun `access search heroes endpoint, query hero name, assert single hero result`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sas").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(expected = 1, actual = actual)
            }
        }

    @Test
    fun `access search heroes endpoint, query hero name, assert multiple hero result`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=sa").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes.size
                assertEquals(expected = 3, actual = actual)
            }
        }

    @Test
    fun `access search heroes endpoint, query an empty hero name, assert empty list for  result`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes
                assertEquals(expected = emptyList(), actual = actual)
            }
        }
    @Test
    fun `access search heroes endpoint, query non existing hero name, assert empty list for  result`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/boruto/heroes/search?name=unknown").apply {
                assertEquals(
                    expected = HttpStatusCode.OK,
                    actual = response.status()
                )
                val actual = Json.decodeFromString<ApiResponse>(response.content.toString())
                    .heroes
                assertEquals(expected = emptyList(), actual = actual)
            }
        }

    @Test
    fun `access non existing endpoint, assert not found`(): Unit =
        withTestApplication(moduleFunction = Application::module) {
            handleRequest(HttpMethod.Get, "/unknown").apply {
                assertEquals(
                    expected = HttpStatusCode.NotFound,
                    actual = response.status()
                )
                assertEquals(expected = "Page Not Found", actual = response.content)
            }
        }
    private fun calculatePage(page: Int): Map<String, Int?> {
        var prevPage: Int? = page
        var nextPage: Int? = page
        if (page in 1..4) {
            nextPage = nextPage?.plus(1)
        }
        if (page in 2..5) {
            prevPage = prevPage?.minus(1)
        }
        if (page == 1) {
            prevPage = null
        }
        if (page == 5) {
            nextPage = null
        }
        return mapOf(PREV_PAGE_KEY to prevPage, NEXT_PAGE_KEY to nextPage)
    }
}
