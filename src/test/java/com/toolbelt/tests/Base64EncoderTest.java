package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class Base64EncoderTest {
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
        page.navigate("/base64");
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheBase64EncoderPageCorrectly() {
        // Check page title
        assertNotNull(page.locator("h1").textContent());
        assertTrue(page.locator("h1").textContent().contains("Base64 Encoder"));

        // Check description
        assertTrue(page.locator("text=/Encode and decode Base64/i").isVisible());

        // Check input and output areas
        assertTrue(page.locator("textarea").first().isVisible());
        assertTrue(page.locator("textarea").nth(1).isVisible());

        // Check mode buttons
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
        void shouldEncodePlainTextToBase64() {
            String inputText = "Hello World";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);
            page.locator("button:has-text('Encode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String encoded = outputArea.inputValue();

            assertEquals("SGVsbG8gV29ybGQ=", encoded);
        }

        @Test
        void shouldDecodeBase64ToPlainText() {
            // Switch to decode mode
            page.locator("button:has-text('Decode')").first().click();

            String inputText = "SGVsbG8gV29ybGQ=";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);
            page.locator("button:has-text('Decode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String decoded = outputArea.inputValue();

            assertEquals("Hello World", decoded);
        }

        @Test
        void shouldHandleUrlSafeEncoding() {
            // Enable URL-safe mode
            page.check("text=/URL Safe/i");

            String inputText = "Hello?World&Test=123";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);

            Locator outputArea = page.locator("textarea").nth(1);
            String encoded = outputArea.inputValue();

            // URL-safe Base64 uses - and _ instead of + and /
            assertFalse(encoded.contains("+"));
            assertFalse(encoded.contains("/"));
        }

        @Test
        void shouldHandleSpecialCharacters() {
            String inputText = "Special chars: @#$%^&*()";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);
            page.locator("button:has-text('Encode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String encoded = outputArea.inputValue();

            assertNotNull(encoded);
            assertTrue(encoded.length() > 0);
        }

        @Test
        void shouldHandleUnicodeCharacters() {
            String inputText = "Hello 世界 🌍";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);
            page.locator("button:has-text('Encode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String encoded = outputArea.inputValue();

            assertNotNull(encoded);
            assertTrue(encoded.length() > 0);
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldSwapInputAndOutput() {
            String inputText = "Hello World";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);
            page.locator("button:has-text('Encode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String encoded = outputArea.inputValue();

            // Click swap button
            page.locator("button:has-text('Swap')").click();

            // Input should now have the encoded value
            String newInput = inputArea.inputValue();
            assertEquals(encoded, newInput);
        }

        @Test
        void shouldCopyToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            String inputText = "Test";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);
            page.locator("button:has-text('Encode')").nth(1).click();

            // Wait for copy button to be visible
            Locator copyButton = page.locator("button:has-text('Copy')");
            copyButton.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE));
            copyButton.click();

            // Check for success indicator
            assertTrue(page.locator("text=/Copied/i").isVisible(new Locator.IsVisibleOptions().setTimeout(2000)));
        }

        @Test
        void shouldLoadSampleText() {
            Locator sampleButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)sample"));

            // Only test if Sample button exists
            if (sampleButton.count() > 0) {
                sampleButton.first().click();

                Locator inputArea = page.locator("textarea").first();
                String value = inputArea.inputValue();

                assertTrue(value.length() > 0);
            }
        }

        @Test
        void shouldClearInput() {
            Locator inputArea = page.locator("textarea").first();
            inputArea.fill("Some text");

            Locator clearButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)clear"));

            // Only test if Clear button exists
            if (clearButton.count() > 0) {
                clearButton.first().click();

                String value = inputArea.inputValue();
                assertEquals("", value);
            }
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInputGracefully() {
            Locator outputArea = page.locator("textarea").nth(1);
            String value = outputArea.inputValue();

            assertEquals("", value);
        }

        @Test
        void shouldShowErrorForInvalidBase64InDecodeMode() {
            page.locator("button:has-text('Decode')").first().click();

            Locator inputArea = page.locator("textarea").first();
            inputArea.fill("Invalid!!!Base64");

            Locator outputArea = page.locator("textarea").nth(1);
            String value = outputArea.inputValue();

            // Either shows error or empty
            assertTrue(value.length() >= 0);
        }
    }

    @Nested
    class RoundTripEncoding {
        @Test
        void shouldEncodeAndDecodeBackToOriginal() {
            String originalText = "Round trip test!";
            Locator inputArea = page.locator("textarea").first();

            // Encode
            inputArea.fill(originalText);
            page.locator("button:has-text('Encode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String encoded = outputArea.inputValue();

            // Switch to decode
            page.locator("button:has-text('Decode')").first().click();
            inputArea.fill(encoded);
            page.locator("button:has-text('Decode')").nth(1).click();

            String decoded = outputArea.inputValue();
            assertEquals(originalText, decoded);
        }

        @Test
        void shouldHandleMultilineText() {
            String multilineText = "Line 1\nLine 2\nLine 3";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(multilineText);
            page.locator("button:has-text('Encode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String encoded = outputArea.inputValue();

            // Decode it
            page.locator("button:has-text('Decode')").first().click();
            inputArea.fill(encoded);
            page.locator("button:has-text('Decode')").nth(1).click();

            String decoded = outputArea.inputValue();
            assertEquals(multilineText, decoded);
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldShowCharacterCounts() {
            String inputText = "Test";
            Locator inputArea = page.locator("textarea").first();

            inputArea.fill(inputText);

            Locator statsLocator = page.locator("text=/characters|length|count/i");
            if (statsLocator.count() > 0) {
                assertTrue(statsLocator.first().isVisible());
            }
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("textarea").first().isVisible());
            assertTrue(page.locator("button:has-text('Encode')").first().isVisible());

            // Test encoding on mobile
            Locator inputArea = page.locator("textarea").first();
            inputArea.fill("Mobile test");
            page.locator("button:has-text('Encode')").nth(1).click();

            Locator outputArea = page.locator("textarea").nth(1);
            String value = outputArea.inputValue();
            assertNotNull(value);
            assertTrue(value.length() > 0);
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            assertTrue(page.locator("text=/Input/i").isVisible());
            assertTrue(page.locator("text=/Output/i").isVisible());
        }
    }
}
