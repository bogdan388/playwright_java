package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.UuidGeneratorPage;
import com.toolbelt.utils.BrowserFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class UuidGeneratorTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private UuidGeneratorPage uuidPage;

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
        page.navigate("/uuid");
        uuidPage = new UuidGeneratorPage(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheUuidGeneratorPageCorrectly() {
        assertTrue(uuidPage.getTitleText().contains("UUID Generator"));
        assertTrue(uuidPage.isDescriptionVisible());
        assertTrue(uuidPage.isGenerateNewButtonVisible());
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
            assertTrue(uuidPage.isUuidVisible());

            String uuid = uuidPage.getFirstUuid();

            // UUID v4 format: 8-4-4-4-12 characters
            assertTrue(uuid.matches("^[\\da-f]{8}-[\\da-f]{4}-4[\\da-f]{3}-[\\da-f]{4}-[\\da-f]{12}$(?i)"));
        }

        @Test
        void shouldGenerateUuidV1() {
            uuidPage.selectVersion("v1");
            uuidPage.clickGenerateNew();

            String uuid = uuidPage.getFirstUuid();
            assertTrue(uuid.matches("^[\\da-f]{8}-[\\da-f]{4}-1[\\da-f]{3}-[\\da-f]{4}-[\\da-f]{12}$(?i)"));
        }

        @Test
        void shouldGenerateNilUuid() {
            uuidPage.selectVersion("nil");
            uuidPage.clickGenerateNew();

            String uuid = uuidPage.getFirstUuid();
            assertEquals("00000000-0000-0000-0000-000000000000", uuid.replaceAll("\\s", ""));
        }

        @Test
        void shouldGenerateMultipleUuids() {
            // Set count to 5
            uuidPage.fillCount("5");
            uuidPage.clickGenerateNew();

            int uuids = uuidPage.getUuidCount();
            assertTrue(uuids >= 5);
        }
    }

    @Nested
    class UuidOptions {
        @Test
        void shouldGenerateUppercaseUuids() {
            if (uuidPage.isUppercaseCheckboxVisible()) {
                uuidPage.clickUppercase();
                uuidPage.clickGenerateNew();

                String uuid = uuidPage.getFirstUuid();
                assertTrue(uuid.matches(".*[A-F0-9].*"));
            }
        }

        @Test
        void shouldGenerateUuidsWithoutHyphens() {
            if (uuidPage.isHyphensCheckboxVisible()) {
                uuidPage.clickHyphens();
                uuidPage.clickGenerateNew();

                String uuid = uuidPage.getFirstUuid();
                assertFalse(uuid.contains("-"));
            }
        }

        @Test
        void shouldGenerateUuidsWithBrackets() {
            if (uuidPage.isBracketsCheckboxVisible()) {
                uuidPage.clickBrackets();
                uuidPage.clickGenerateNew();

                String uuid = uuidPage.getFirstUuid();
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

            uuidPage.clickCopy();
            assertTrue(uuidPage.isSuccessIndicatorVisible());
        }

        @Test
        void shouldCopyAllUuids() {
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            uuidPage.fillCount("3");
            uuidPage.clickGenerateNew();

            if (uuidPage.isCopyAllButtonVisible()) {
                uuidPage.clickCopyAll();
                assertTrue(uuidPage.isSuccessIndicatorVisible());
            }
        }

        @Test
        void shouldDownloadUuids() {
            if (uuidPage.isDownloadButtonVisible()) {
                Download download = page.waitForDownload(() -> {
                    uuidPage.clickDownload();
                });
                assertTrue(download.suggestedFilename().contains("uuid"));
            }
        }
    }

    @Nested
    class Statistics {
        @Test
        void shouldShowUuidCount() {
            uuidPage.fillCount("10");
            uuidPage.clickGenerateNew();

            assertTrue(uuidPage.isUuidCountTextVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(uuidPage.isTitleVisible());
            uuidPage.clickGenerateNew();

            assertTrue(uuidPage.isUuidVisible());
        }
    }
}
