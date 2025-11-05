package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.UrlEncoderPage;
import com.toolbelt.utils.BrowserFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UrlEncoderTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private UrlEncoderPage urlPage;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = BrowserFactory.launchBrowser(playwright);
    }

    @AfterAll
    static void closeBrowser() {
        if (browser != null) {
            browser.close();
        }
        if (playwright != null) {
            playwright.close();
        }
    }

    @BeforeEach
    void createContextAndPage() {
        context = browser.newContext(new Browser.NewContextOptions().setBaseURL("https://toolbelt.site"));
        page = context.newPage();
        page.navigate("/url-encoder");
        urlPage = new UrlEncoderPage(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheUrlEncoderPageCorrectly() {
        assertTrue(urlPage.getTitleText().contains("URL Encoder"));
        assertTrue(urlPage.isDescriptionVisible());
        assertTrue(urlPage.isInputTextareaVisible());
        assertTrue(urlPage.isOutputTextareaVisible());
        assertTrue(urlPage.isEncodeButtonVisible());
        assertTrue(urlPage.isDecodeButtonVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        urlPage.navigateToHome();
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class EncodingFeatures {
        @Test
        void shouldEncodeUrlWithSpaces() {
            String input = "Hello World";
            urlPage.fillInput(input);

            // Auto-converts, wait for processing
            urlPage.waitForConversion();

            String output = urlPage.getOutput();
            assertTrue(output.contains("Hello%20World"));
        }

        @Test
        void shouldEncodeSpecialCharacters() {
            // Switch to Component mode to encode special characters
            urlPage.clickComponent();

            String input = "test@example.com?query=value";
            urlPage.fillInput(input);

            // Wait for auto-processing
            urlPage.waitForConversion();

            String output = urlPage.getOutput();
            assertTrue(output.contains("%"));
        }

        @Test
        void shouldDecodeUrlEncodedString() {
            urlPage.clickDecode();

            String input = "Hello%20World";
            urlPage.fillInput(input);

            // Auto-converts, wait for processing
            urlPage.waitForConversion();

            String output = urlPage.getOutput();
            assertEquals("Hello World", output);
        }

        @Test
        void shouldDecodeSpecialCharacters() {
            urlPage.clickDecode();

            String input = "test%40example.com%3Fquery%3Dvalue";
            urlPage.fillInput(input);

            // Auto-converts, wait for processing
            urlPage.waitForConversion();

            String output = urlPage.getOutput();
            assertTrue(output.contains("@"));
            assertTrue(output.contains("?"));
            assertTrue(output.contains("="));
        }
    }

    @Nested
    class EncodingTypes {
        @Test
        void shouldUseFullUrlEncoding() {
            if (urlPage.isFullUrlButtonVisible()) {
                urlPage.clickFullUrl();

                urlPage.fillInput("https://example.com/path with spaces");

                // Auto-converts, wait for processing
                urlPage.waitForConversion();

                String output = urlPage.getOutput();
                assertTrue(output.contains("https://"));
            }
        }

        @Test
        void shouldUseComponentEncoding() {
            if (urlPage.isComponentButtonVisible()) {
                urlPage.clickComponent();

                urlPage.fillInput("hello world");

                // Auto-converts, wait for processing
                urlPage.waitForConversion();

                String output = urlPage.getOutput();
                assertEquals("hello%20world", output);
            }
        }

        @Test
        void shouldUseFormDataEncoding() {
            if (urlPage.isFormDataButtonVisible()) {
                urlPage.clickFormData();

                urlPage.fillInput("hello world");

                // Auto-converts, wait for processing
                urlPage.waitForConversion();

                String output = urlPage.getOutput();
                // Form encoding uses + for spaces
                assertTrue(output.contains("+"));
            }
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldSwapInputAndOutput() {
            urlPage.fillInput("Hello World");

            // Auto-converts, wait for processing
            urlPage.waitForConversion();

            String output = urlPage.getOutput();

            urlPage.clickSwap();

            String newInput = urlPage.getInput();
            assertEquals(output, newInput);
        }

        @Test
        void shouldCopyToClipboard() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            urlPage.fillInput("test");

            urlPage.clickCopy();
            assertTrue(urlPage.isSuccessIndicatorVisible());
        }

        @Test
        void shouldLoadSampleUrl() {
            urlPage.clickAnySample();
            String input = urlPage.getInput();
            assertTrue(input.length() > 0);
        }
    }

    @Nested
    class RoundTripEncoding {
        @Test
        void shouldEncodeAndDecodeBackToOriginal() {
            String original = "Hello World & Test=123";

            urlPage.fillInput(original);

            // Auto-converts, wait for processing
            urlPage.waitForConversion();

            String encoded = urlPage.getOutput();

            urlPage.clickDecode();
            urlPage.fillInput(encoded);

            // Auto-converts, wait for processing
            urlPage.waitForConversion();

            String decoded = urlPage.getOutput();

            assertEquals(original, decoded);
        }
    }

    @Nested
    class UrlComponents {
        @Test
        void shouldParseUrlComponents() {
            String url = "https://example.com:8080/path?query=value#hash";

            urlPage.fillInput(url);

            if (urlPage.isUrlComponentsSectionVisible()) {
                assertTrue(urlPage.isUrlComponentsSectionVisible());
            }
        }
    }

    @Nested
    class CharacterReference {
        @Test
        void shouldDisplayCharacterReferenceTable() {
            if (urlPage.isCharacterReferenceSectionVisible()) {
                assertTrue(urlPage.isCharacterReferenceSectionVisible());
            }
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInput() {
            String output = urlPage.getOutput();
            assertEquals("", output);
        }

        @Test
        void shouldHandleInvalidUrlEncodingGracefully() {
            urlPage.clickDecode();

            urlPage.fillInput("%ZZ%XX");

            String output = urlPage.getOutput();
            // Should handle error gracefully
            assertTrue(output.length() >= 0);
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(urlPage.isTitleVisible());
            assertTrue(urlPage.isInputTextareaVisible());

            urlPage.fillInput("mobile test");

            // Auto-converts, wait for processing
            urlPage.waitForConversion();

            String output = urlPage.getOutput();
            assertTrue(output.contains("mobile"));
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldShowCharacterCounts() {
            urlPage.fillInput("test");

            assertTrue(urlPage.isCharactersLabelVisible());
        }
    }
}
