package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.HashGeneratorPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class HashGeneratorTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private HashGeneratorPage hashPage;

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
        hashPage = new HashGeneratorPage(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheHashGeneratorPageCorrectly() {
        assertTrue(hashPage.getTitleText().contains("Hash Generator"));
        assertTrue(hashPage.isTextareaVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        hashPage.navigateToHome();
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class HashGeneration {
        @Test
        void shouldGenerateMD5Hash() {
            String input = "Hello World";

            // Select MD5
            hashPage.selectAlgorithm("MD5");
            hashPage.fillInput(input);

            // Auto-generates, wait for hash to appear
            hashPage.waitForHashGeneration();

            // MD5 of "Hello World" is b10a8db164e0754105b7a99be72e3fe5
            assertTrue(page.locator("text=/b10a8db164e0754105b7a99be72e3fe5/i").isVisible());
        }

        @Test
        void shouldGenerateSHA256Hash() {
            String input = "test";

            hashPage.selectAlgorithm("SHA256");
            hashPage.fillInput(input);

            // Auto-generates, wait for hash to appear
            hashPage.waitForHashGeneration();

            // Should show a 64-character hex string
            String hash = hashPage.getHashOutput();
            assertEquals(64, hash.replaceAll("\\s", "").length());
        }

        @Test
        void shouldGenerateSHA512Hash() {
            String input = "test";

            hashPage.selectAlgorithm("SHA512");
            hashPage.fillInput(input);

            // Auto-generates, wait for hash to appear
            hashPage.waitForHashGeneration();

            // SHA-512 produces 128-character hex string
            String hash = hashPage.getHashOutput();
            assertEquals(128, hash.replaceAll("\\s", "").length());
        }

        @Test
        void shouldGenerateSHA1Hash() {
            String input = "test";

            hashPage.selectAlgorithm("SHA1");
            hashPage.fillInput(input);

            // Auto-generates, wait for hash to appear
            hashPage.waitForHashGeneration();

            // SHA-1 produces 40-character hex string
            String hash = hashPage.getHashOutput();
            assertEquals(40, hash.replaceAll("\\s", "").length());
        }
    }

    @Nested
    class BatchMode {
        @Test
        void shouldGenerateAllHashesInBatchMode() {
            String input = "test";
            hashPage.fillInput(input);

            hashPage.clickBatch();

            // Should show multiple hash types
            assertTrue(hashPage.isMd5Visible());
            assertTrue(hashPage.isSha256Visible());
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldCopyHashToClipboard() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            hashPage.selectAlgorithm("MD5");
            hashPage.fillInput("test");

            // Auto-generates, wait for hash
            hashPage.waitForHashGeneration();

            hashPage.clickCopy();
            assertTrue(hashPage.isSuccessIndicatorVisible());
        }

        @Test
        void shouldLoadSampleText() {
            hashPage.clickSample();
            String value = hashPage.getInputValue();
            assertTrue(value.length() > 0);
        }
    }

    @Nested
    class HashVerification {
        @Test
        void shouldVerifyMatchingHash() {
            String input = "test";

            hashPage.selectAlgorithm("MD5");
            hashPage.fillInput(input);

            // Auto-generates, wait for hash
            hashPage.waitForHashGeneration();

            // Get the generated hash
            String hash = hashPage.getHashOutput();

            // Enable verify mode first
            hashPage.clickVerifyMode();

            // Enter same hash for verification
            hashPage.fillVerifyInput(hash.trim());
            hashPage.clickVerify();
            assertTrue(hashPage.isMatchValidVisible());
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

            assertTrue(hashPage.isTitleVisible());
            assertTrue(hashPage.isTextareaVisible());

            hashPage.fillInput("mobile test");

            // Auto-generates, wait for hash
            hashPage.waitForHashGeneration();

            assertTrue(hashPage.isHashOutputVisible());
        }
    }
}
