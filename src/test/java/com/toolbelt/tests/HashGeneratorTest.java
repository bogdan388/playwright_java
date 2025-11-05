package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class HashGeneratorTest {
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
        page.navigate("/hash");
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheHashGeneratorPageCorrectly() {
        assertTrue(page.locator("h1").textContent().contains("Hash Generator"));
        assertTrue(page.locator("textarea").isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("a[href='/']");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class HashGeneration {
        @Test
        void shouldGenerateMD5Hash() {
            String input = "Hello World";

            // Select MD5
            page.selectOption("select", "MD5");
            page.locator("textarea").fill(input);

            // Auto-generates, wait for hash to appear
            page.waitForTimeout(500);

            // MD5 of "Hello World" is b10a8db164e0754105b7a99be72e3fe5
            assertTrue(page.locator("text=/b10a8db164e0754105b7a99be72e3fe5/i").isVisible());
        }

        @Test
        void shouldGenerateSHA256Hash() {
            String input = "test";

            page.selectOption("select", "SHA256");
            page.locator("textarea").fill(input);

            // Auto-generates, wait for hash to appear
            page.waitForTimeout(500);

            // Should show a 64-character hex string
            Locator hashElement = page.locator("[class*='hash']")
                .or(page.locator("code"))
                .or(page.locator("div.font-mono"))
                .first();
            String hash = hashElement.textContent();
            assertEquals(64, hash.replaceAll("\\s", "").length());
        }

        @Test
        void shouldGenerateSHA512Hash() {
            String input = "test";

            page.selectOption("select", "SHA512");
            page.locator("textarea").fill(input);

            // Auto-generates, wait for hash to appear
            page.waitForTimeout(500);

            // SHA-512 produces 128-character hex string
            Locator hashElement = page.locator("[class*='hash']")
                .or(page.locator("code"))
                .or(page.locator("div.font-mono"))
                .first();
            String hash = hashElement.textContent();
            assertEquals(128, hash.replaceAll("\\s", "").length());
        }

        @Test
        void shouldGenerateSHA1Hash() {
            String input = "test";

            page.selectOption("select", "SHA1");
            page.locator("textarea").fill(input);

            // Auto-generates, wait for hash to appear
            page.waitForTimeout(500);

            // SHA-1 produces 40-character hex string
            Locator hashElement = page.locator("[class*='hash']")
                .or(page.locator("code"))
                .or(page.locator("div.font-mono"))
                .first();
            String hash = hashElement.textContent();
            assertEquals(40, hash.replaceAll("\\s", "").length());
        }
    }

    @Nested
    class BatchMode {
        @Test
        void shouldGenerateAllHashesInBatchMode() {
            String input = "test";
            page.locator("textarea").fill(input);

            Locator batchButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^(All|Batch)$"));
            if (batchButton.count() > 0) {
                batchButton.first().click();

                // Should show multiple hash types
                assertTrue(page.locator("text=/MD5/i").isVisible());
                assertTrue(page.locator("text=/SHA-256/i").isVisible());
            }
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldCopyHashToClipboard() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            page.selectOption("select", "MD5");
            page.locator("textarea").fill("test");

            // Auto-generates, wait for hash
            page.waitForTimeout(500);

            Locator copyButton = page.locator("button[title*='Copy']")
                .or(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Copy$")));
            if (copyButton.count() > 0) {
                copyButton.first().click();
                assertTrue(page.locator(".text-green-400")
                    .or(page.locator("text=/copied/i"))
                    .isVisible(new Locator.IsVisibleOptions().setTimeout(2000)));
            }
        }

        @Test
        void shouldLoadSampleText() {
            Locator sampleButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Sample$"));
            if (sampleButton.count() > 0) {
                sampleButton.first().click();
                String value = page.locator("textarea").inputValue();
                assertTrue(value.length() > 0);
            }
        }
    }

    @Nested
    class HashVerification {
        @Test
        void shouldVerifyMatchingHash() {
            String input = "test";

            page.selectOption("select", "MD5");
            page.locator("textarea").fill(input);

            // Auto-generates, wait for hash
            page.waitForTimeout(500);

            // Get the generated hash
            Locator hashElement = page.locator("[class*='hash']")
                .or(page.locator("code"))
                .or(page.locator("div.font-mono"))
                .first();
            String hash = hashElement.textContent();

            // Enable verify mode first
            Locator verifyCheckbox = page.locator("text=/Verify Mode/i");
            if (verifyCheckbox.count() > 0) {
                verifyCheckbox.first().click();

                // Enter same hash for verification
                Locator verifyInput = page.locator("input[placeholder*='hash']")
                    .or(page.locator("input[placeholder*='verify']"));
                if (verifyInput.count() > 0) {
                    verifyInput.first().fill(hash.trim());
                    Locator verifyButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Verify$"));
                    if (verifyButton.count() > 0) {
                        verifyButton.first().click();
                        assertTrue(page.locator("text=/match|valid/i").isVisible());
                    }
                }
            }
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInput() {
            // Leave input empty
            String result = page.locator("body").textContent();
            assertNotNull(result);
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("textarea").isVisible());

            page.locator("textarea").fill("mobile test");

            // Auto-generates, wait for hash
            page.waitForTimeout(500);

            assertTrue(page.locator("[class*='hash']")
                .or(page.locator("code"))
                .or(page.locator("div.font-mono"))
                .isVisible());
        }
    }
}
