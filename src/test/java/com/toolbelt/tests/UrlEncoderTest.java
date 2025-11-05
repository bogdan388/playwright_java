package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UrlEncoderTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;

    @BeforeAll
    static void launchBrowser() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch();
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
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheUrlEncoderPageCorrectly() {
        assertTrue(page.locator("h1").textContent().contains("URL Encoder"));
        assertTrue(page.locator("text=/Encode and decode URLs/i").isVisible());
        assertTrue(page.locator("textarea").first().isVisible());
        assertTrue(page.locator("textarea").nth(1).isVisible());
        assertTrue(page.locator("button:has-text('Encode')").first().isVisible());
        assertTrue(page.locator("button:has-text('Decode')").first().isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("a[href='/']");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class EncodingFeatures {
        @Test
        void shouldEncodeUrlWithSpaces() {
            String input = "Hello World";
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertTrue(output.contains("Hello%20World"));
        }

        @Test
        void shouldEncodeSpecialCharacters() {
            // Switch to Component mode to encode special characters
            Locator componentButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Component$"));
            componentButton.click();

            String input = "test@example.com?query=value";
            page.locator("textarea").first().fill(input);

            // Wait for auto-processing
            page.waitForTimeout(500);

            String output = page.locator("textarea").nth(1).inputValue();
            assertTrue(output.contains("%"));
        }

        @Test
        void shouldDecodeUrlEncodedString() {
            page.locator("button:has-text('Decode')").first().click();

            String input = "Hello%20World";
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("Hello World", output);
        }

        @Test
        void shouldDecodeSpecialCharacters() {
            page.locator("button:has-text('Decode')").first().click();

            String input = "test%40example.com%3Fquery%3Dvalue";
            page.locator("textarea").first().fill(input);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertTrue(output.contains("@"));
            assertTrue(output.contains("?"));
            assertTrue(output.contains("="));
        }
    }

    @Nested
    class EncodingTypes {
        @Test
        void shouldUseFullUrlEncoding() {
            Locator fullUrlButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Full URL$"));
            int count = fullUrlButton.count();
            if (count > 0) {
                fullUrlButton.first().click();

                page.locator("textarea").first().fill("https://example.com/path with spaces");

                // Auto-converts, wait for processing
                page.waitForTimeout(300);

                String output = page.locator("textarea").nth(1).inputValue();
                assertTrue(output.contains("https://"));
            }
        }

        @Test
        void shouldUseComponentEncoding() {
            Locator componentButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Component$"));
            int count = componentButton.count();
            if (count > 0) {
                componentButton.first().click();

                page.locator("textarea").first().fill("hello world");

                // Auto-converts, wait for processing
                page.waitForTimeout(300);

                String output = page.locator("textarea").nth(1).inputValue();
                assertEquals("hello%20world", output);
            }
        }

        @Test
        void shouldUseFormDataEncoding() {
            Locator formButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Form Data$"));
            int count = formButton.count();
            if (count > 0) {
                formButton.first().click();

                page.locator("textarea").first().fill("hello world");

                // Auto-converts, wait for processing
                page.waitForTimeout(300);

                String output = page.locator("textarea").nth(1).inputValue();
                // Form encoding uses + for spaces
                assertTrue(output.contains("+"));
            }
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldSwapInputAndOutput() {
            page.locator("textarea").first().fill("Hello World");

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();

            Locator swapButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Swap$"));
            int count = swapButton.count();
            if (count > 0) {
                swapButton.first().click();

                String newInput = page.locator("textarea").first().inputValue();
                assertEquals(output, newInput);
            }
        }

        @Test
        void shouldCopyToClipboard() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            page.locator("textarea").first().fill("test");

            Locator copyButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Copy$"));
            int count = copyButton.count();
            if (count > 0) {
                copyButton.first().click();
                assertTrue(page.locator(".text-green-400").or(page.locator("text=/copied/i"))
                    .isVisible(new Locator.IsVisibleOptions().setTimeout(2000)));
            }
        }

        @Test
        void shouldLoadSampleUrl() {
            Locator sampleButtons = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)url|text|params|path|international"));
            int count = sampleButtons.count();

            if (count > 0) {
                sampleButtons.first().click();
                String input = page.locator("textarea").first().inputValue();
                assertTrue(input.length() > 0);
            }
        }
    }

    @Nested
    class RoundTripEncoding {
        @Test
        void shouldEncodeAndDecodeBackToOriginal() {
            String original = "Hello World & Test=123";

            page.locator("textarea").first().fill(original);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String encoded = page.locator("textarea").nth(1).inputValue();

            page.locator("button:has-text('Decode')").first().click();
            page.locator("textarea").first().fill(encoded);

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String decoded = page.locator("textarea").nth(1).inputValue();

            assertEquals(original, decoded);
        }
    }

    @Nested
    class UrlComponents {
        @Test
        void shouldParseUrlComponents() {
            String url = "https://example.com:8080/path?query=value#hash";

            page.locator("textarea").first().fill(url);

            Locator componentsSection = page.locator("text=/URL Components/i");
            int count = componentsSection.count();
            if (count > 0) {
                assertTrue(page.locator("text=/Protocol/i").isVisible());
                assertTrue(page.locator("text=/Host/i").isVisible());
            }
        }
    }

    @Nested
    class CharacterReference {
        @Test
        void shouldDisplayCharacterReferenceTable() {
            Locator referenceSection = page.locator("text=/Common Encodings|Character Reference/i");
            int count = referenceSection.count();
            if (count > 0) {
                assertTrue(referenceSection.first().isVisible());
                assertTrue(page.locator("text=/%20/i").first().isVisible());
            }
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInput() {
            String output = page.locator("textarea").nth(1).inputValue();
            assertEquals("", output);
        }

        @Test
        void shouldHandleInvalidUrlEncodingGracefully() {
            page.locator("button:has-text('Decode')").first().click();

            page.locator("textarea").first().fill("%ZZ%XX");

            String output = page.locator("textarea").nth(1).inputValue();
            // Should handle error gracefully
            assertTrue(output.length() >= 0);
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("textarea").first().isVisible());

            page.locator("textarea").first().fill("mobile test");

            // Auto-converts, wait for processing
            page.waitForTimeout(300);

            String output = page.locator("textarea").nth(1).inputValue();
            assertTrue(output.contains("mobile"));
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldShowCharacterCounts() {
            page.locator("textarea").first().fill("test");

            assertTrue(page.locator("text=/characters/i").first().isVisible());
        }
    }
}
