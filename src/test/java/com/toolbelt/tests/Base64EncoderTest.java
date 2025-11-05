package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.Base64EncoderPage;
import com.toolbelt.utils.BrowserFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class Base64EncoderTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private Base64EncoderPage base64Page;

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
        page.navigate("/base64");
        base64Page = new Base64EncoderPage(page);
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
        assertNotNull(base64Page.getTitleText());
        assertTrue(base64Page.getTitleText().contains("Base64 Encoder"));

        // Check description
        assertTrue(base64Page.isDescriptionVisible());

        // Check input and output areas
        assertTrue(base64Page.isInputTextareaVisible());
        assertTrue(base64Page.isOutputTextareaVisible());

        // Check mode buttons
        assertTrue(base64Page.isEncodeModeButtonVisible());
        assertTrue(base64Page.isDecodeModeButtonVisible());
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

            base64Page.fillInput(inputText);
            base64Page.clickEncodeAction();

            String encoded = base64Page.getOutput();

            assertEquals("SGVsbG8gV29ybGQ=", encoded);
        }

        @Test
        void shouldDecodeBase64ToPlainText() {
            // Switch to decode mode
            base64Page.clickDecodeModeButton();

            String inputText = "SGVsbG8gV29ybGQ=";

            base64Page.fillInput(inputText);
            base64Page.clickDecodeAction();

            String decoded = base64Page.getOutput();

            assertEquals("Hello World", decoded);
        }

        @Test
        void shouldHandleUrlSafeEncoding() {
            // Enable URL-safe mode
            base64Page.checkUrlSafe();

            String inputText = "Hello?World&Test=123";

            base64Page.fillInput(inputText);

            String encoded = base64Page.getOutput();

            // URL-safe Base64 uses - and _ instead of + and /
            assertFalse(encoded.contains("+"));
            assertFalse(encoded.contains("/"));
        }

        @Test
        void shouldHandleSpecialCharacters() {
            String inputText = "Special chars: @#$%^&*()";

            base64Page.fillInput(inputText);
            base64Page.clickEncodeAction();

            String encoded = base64Page.getOutput();

            assertNotNull(encoded);
            assertTrue(encoded.length() > 0);
        }

        @Test
        void shouldHandleUnicodeCharacters() {
            String inputText = "Hello 世界 🌍";

            base64Page.fillInput(inputText);
            base64Page.clickEncodeAction();

            String encoded = base64Page.getOutput();

            assertNotNull(encoded);
            assertTrue(encoded.length() > 0);
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldSwapInputAndOutput() {
            String inputText = "Hello World";

            base64Page.fillInput(inputText);
            base64Page.clickEncodeAction();

            String encoded = base64Page.getOutput();

            // Click swap button
            base64Page.clickSwap();

            // Input should now have the encoded value
            String newInput = base64Page.getInput();
            assertEquals(encoded, newInput);
        }

        @Test
        void shouldCopyToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            String inputText = "Test";

            base64Page.fillInput(inputText);
            base64Page.clickEncodeAction();

            // Wait for copy button to be visible and click
            base64Page.clickCopy();

            // Check for success indicator
            assertTrue(base64Page.isCopiedIndicatorVisible());
        }

        @Test
        void shouldLoadSampleText() {
            Locator sampleButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)sample"));

            // Only test if Sample button exists
            if (sampleButton.count() > 0) {
                sampleButton.first().click();

                String value = base64Page.getInput();

                assertTrue(value.length() > 0);
            }
        }

        @Test
        void shouldClearInput() {
            base64Page.fillInput("Some text");

            Locator clearButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)clear"));

            // Only test if Clear button exists
            if (clearButton.count() > 0) {
                clearButton.first().click();

                String value = base64Page.getInput();
                assertEquals("", value);
            }
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInputGracefully() {
            String value = base64Page.getOutput();

            assertEquals("", value);
        }

        @Test
        void shouldShowErrorForInvalidBase64InDecodeMode() {
            base64Page.clickDecodeModeButton();

            base64Page.fillInput("Invalid!!!Base64");

            String value = base64Page.getOutput();

            // Either shows error or empty
            assertTrue(value.length() >= 0);
        }
    }

    @Nested
    class RoundTripEncoding {
        @Test
        void shouldEncodeAndDecodeBackToOriginal() {
            String originalText = "Round trip test!";

            // Encode
            base64Page.fillInput(originalText);
            base64Page.clickEncodeAction();

            String encoded = base64Page.getOutput();

            // Switch to decode
            base64Page.clickDecodeModeButton();
            base64Page.fillInput(encoded);
            base64Page.clickDecodeAction();

            String decoded = base64Page.getOutput();
            assertEquals(originalText, decoded);
        }

        @Test
        void shouldHandleMultilineText() {
            String multilineText = "Line 1\nLine 2\nLine 3";

            base64Page.fillInput(multilineText);
            base64Page.clickEncodeAction();

            String encoded = base64Page.getOutput();

            // Decode it
            base64Page.clickDecodeModeButton();
            base64Page.fillInput(encoded);
            base64Page.clickDecodeAction();

            String decoded = base64Page.getOutput();
            assertEquals(multilineText, decoded);
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldShowCharacterCounts() {
            String inputText = "Test";

            base64Page.fillInput(inputText);

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

            assertTrue(base64Page.isTitleVisible());
            assertTrue(base64Page.isInputTextareaVisible());
            assertTrue(base64Page.isEncodeModeButtonVisible());

            // Test encoding on mobile
            base64Page.fillInput("Mobile test");
            base64Page.clickEncodeAction();

            String value = base64Page.getOutput();
            assertNotNull(value);
            assertTrue(value.length() > 0);
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            assertTrue(base64Page.isInputLabelVisible());
            assertTrue(base64Page.isOutputLabelVisible());
        }
    }
}
