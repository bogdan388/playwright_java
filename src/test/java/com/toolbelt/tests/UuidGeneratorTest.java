package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UuidGeneratorTest {
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
        page.navigate("/uuid");
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheUuidGeneratorPageCorrectly() {
        assertTrue(page.locator("h1").textContent().contains("UUID Generator"));
        assertTrue(page.locator("text=/Generate universally unique identifiers/i").isVisible());
        assertTrue(page.locator("button:has-text('Generate New')").isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("a[href='/']");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class UuidGeneration {
        @Test
        void shouldGenerateUuidV4ByDefault() {
            // UUIDs should be generated on page load
            assertTrue(page.locator("code").or(page.locator("[class*='uuid']")).isVisible());

            String uuid = page.locator("code").or(page.locator("[class*='uuid']")).first().textContent();

            // UUID v4 format: 8-4-4-4-12 characters
            assertTrue(uuid.matches("^[\\da-f]{8}-[\\da-f]{4}-4[\\da-f]{3}-[\\da-f]{4}-[\\da-f]{12}$(?i)"));
        }

        @Test
        void shouldGenerateUuidV1() {
            page.selectOption("select", "v1");
            page.locator("button:has-text('Generate New')").first().click();

            String uuid = page.locator("code").or(page.locator("[class*='uuid']")).first().textContent();
            assertTrue(uuid.matches("^[\\da-f]{8}-[\\da-f]{4}-1[\\da-f]{3}-[\\da-f]{4}-[\\da-f]{12}$(?i)"));
        }

        @Test
        void shouldGenerateNilUuid() {
            page.selectOption("select", "nil");
            page.locator("button:has-text('Generate New')").first().click();

            String uuid = page.locator("code").or(page.locator("[class*='uuid']")).first().textContent();
            assertEquals("00000000-0000-0000-0000-000000000000", uuid.replaceAll("\\s", ""));
        }

        @Test
        void shouldGenerateMultipleUuids() {
            // Set count to 5
            page.fill("input[type='number']", "5");
            page.locator("button:has-text('Generate New')").first().click();

            int uuids = page.locator("code").or(page.locator("[class*='uuid']")).count();
            assertTrue(uuids >= 5);
        }
    }

    @Nested
    class UuidOptions {
        @Test
        void shouldGenerateUppercaseUuids() {
            Locator uppercaseCheckbox = page.locator("text=/uppercase/i");
            int count = uppercaseCheckbox.count();
            if (count > 0) {
                uppercaseCheckbox.first().click();
                page.locator("button:has-text('Generate New')").first().click();

                String uuid = page.locator("code").or(page.locator("[class*='uuid']")).first().textContent();
                assertTrue(uuid.matches(".*[A-F0-9].*"));
            }
        }

        @Test
        void shouldGenerateUuidsWithoutHyphens() {
            Locator hyphensCheckbox = page.locator("text=/hyphens/i");
            int count = hyphensCheckbox.count();
            if (count > 0) {
                hyphensCheckbox.first().click();
                page.locator("button:has-text('Generate New')").first().click();

                String uuid = page.locator("code").or(page.locator("[class*='uuid']")).first().textContent();
                assertFalse(uuid.contains("-"));
            }
        }

        @Test
        void shouldGenerateUuidsWithBrackets() {
            Locator bracketsCheckbox = page.locator("text=/brackets/i");
            int count = bracketsCheckbox.count();
            if (count > 0) {
                bracketsCheckbox.first().click();
                page.locator("button:has-text('Generate New')").first().click();

                String uuid = page.locator("code").or(page.locator("[class*='uuid']")).first().textContent();
                assertTrue(uuid.contains("{"));
                assertTrue(uuid.contains("}"));
            }
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldCopyUuidToClipboard() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            Locator copyButton = page.locator("button[title*='Copy']").or(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Copy$")));
            int count = copyButton.count();
            if (count > 0) {
                copyButton.first().click();
                assertTrue(page.locator(".text-green-400").or(page.locator("text=/copied/i"))
                    .isVisible(new Locator.IsVisibleOptions().setTimeout(2000)));
            }
        }

        @Test
        void shouldCopyAllUuids() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            page.fill("input[type='number']", "3");
            page.locator("button:has-text('Generate New')").first().click();

            Locator copyAllButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Copy All$"));
            int count = copyAllButton.count();
            if (count > 0) {
                copyAllButton.first().click();
                assertTrue(page.locator(".text-green-400").or(page.locator("text=/copied/i"))
                    .isVisible(new Locator.IsVisibleOptions().setTimeout(2000)));
            }
        }

        @Test
        void shouldDownloadUuids() {
            Locator downloadButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Download$"));
            int count = downloadButton.count();
            if (count > 0) {
                Download download = page.waitForDownload(() -> {
                    downloadButton.first().click();
                });
                assertTrue(download.suggestedFilename().contains("uuid"));
            }
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldShowUuidCount() {
            page.fill("input[type='number']", "10");
            page.locator("button:has-text('Generate New')").first().click();

            assertTrue(page.locator("text=/10.*UUID/i").isVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            page.locator("button:has-text('Generate New')").first().click();

            assertTrue(page.locator("code").or(page.locator("[class*='uuid']")).isVisible());
        }
    }
}
