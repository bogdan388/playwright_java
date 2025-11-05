package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFormatterTest {
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
        page.navigate("/json-formatter");
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadTheJsonFormatterPageCorrectly() {
        // Check page title
        assertNotNull(page.locator("h1").textContent());
        assertTrue(page.locator("h1").textContent().contains("JSON Formatter"));

        // Check description
        assertTrue(page.locator("text=/Format, validate, and beautify your JSON data/i").isVisible());

        // Check input and output areas
        assertTrue(page.locator("textarea[placeholder*='Paste your JSON']").isVisible());
        assertTrue(page.locator("textarea[placeholder*='Formatted JSON']").isVisible());

        // Check control buttons
        assertTrue(page.locator("button:has-text('Format')").isVisible());
        assertTrue(page.locator("button:has-text('Minify')").isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("text=/Back to Tools/i");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class FormattingFeatures {
        @Test
        void shouldFormatValidJson() {
            String inputJson = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);
            page.click("button:has-text('Format')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            assertTrue(formatted.contains("{\n"));
            assertTrue(formatted.contains("\"name\": \"John\""));
            assertTrue(formatted.contains("\"age\": 30"));
        }

        @Test
        void shouldMinifyJson() {
            String inputJson = "{\n  \"name\": \"John\",\n  \"age\": 30\n}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);
            page.click("button:has-text('Minify')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String minified = outputArea.inputValue();

            assertEquals("{\"name\":\"John\",\"age\":30}", minified);
        }

        @Test
        void shouldHandleNestedJson() {
            String nestedJson = "{\"user\":{\"name\":\"John\",\"address\":{\"city\":\"NYC\",\"zip\":\"10001\"}}}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(nestedJson);
            page.click("button:has-text('Format')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            assertTrue(formatted.contains("\"user\": {"));
            assertTrue(formatted.contains("\"address\": {"));
        }

        @Test
        void shouldHandleArraysInJson() {
            String arrayJson = "{\"items\":[\"apple\",\"banana\",\"orange\"],\"count\":3}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(arrayJson);
            page.click("button:has-text('Format')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            assertTrue(formatted.contains("\"items\": ["));
            assertTrue(formatted.contains("\"apple\""));
        }

        @Test
        void shouldShowErrorForInvalidJson() {
            String invalidJson = "{name: \"John\", age: 30}"; // Missing quotes
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(invalidJson);
            page.click("button:has-text('Format')");

            assertTrue(page.locator("text=/Error:/i").isVisible());
        }
    }

    @Nested
    class OptionsAndSettings {
        @Test
        void shouldRespectIndentSizeSetting() {
            String inputJson = "{\"name\":\"John\",\"age\":30}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);

            // Test with 2 spaces (default)
            page.click("button:has-text('Format')");
            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();
            assertTrue(formatted.contains("  \"name\"")); // 2 spaces

            // Change to 4 spaces
            page.selectOption("select", "4");
            page.click("button:has-text('Format')");
            formatted = outputArea.inputValue();
            assertTrue(formatted.contains("    \"name\"")); // 4 spaces
        }

        @Test
        void shouldSortKeysWhenOptionIsEnabled() {
            String inputJson = "{\"zebra\":\"z\",\"apple\":\"a\",\"banana\":\"b\"}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);

            // Enable sort keys
            page.check("#sortKeys");
            page.click("button:has-text('Format')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            // Check that keys are sorted
            int appleIndex = formatted.indexOf("\"apple\"");
            int bananaIndex = formatted.indexOf("\"banana\"");
            int zebraIndex = formatted.indexOf("\"zebra\"");

            assertTrue(appleIndex < bananaIndex);
            assertTrue(bananaIndex < zebraIndex);
        }
    }

    @Nested
    class UIControls {
        @Test
        void shouldLoadSampleJson() {
            page.click("button:has-text('Sample')");

            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");
            String value = inputArea.inputValue();

            assertTrue(value.contains("John Doe"));
            assertTrue(value.contains("email"));
        }

        @Test
        void shouldCopyFormattedJsonToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            String inputJson = "{\"test\":\"value\"}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);
            page.click("button:has-text('Format')");

            // Click copy button
            page.click("button[title='Copy to clipboard']");

            // Check for success indicator
            assertTrue(page.locator(".text-green-400").isVisible());
        }

        @Test
        void shouldDownloadFormattedJson() {
            String inputJson = "{\"download\":\"test\"}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);
            page.click("button:has-text('Format')");

            // Start waiting for download
            Download download = page.waitForDownload(() -> {
                page.click("button[title='Download JSON']");
            });

            assertEquals("formatted.json", download.suggestedFilename());
        }

        @Test
        void shouldUploadJsonFile() {
            String fileContent = "{\"uploaded\": true, \"data\": \"test\"}";

            // Create file input
            page.click("button[title='Upload JSON file']");
            Locator fileInput = page.waitForSelector("input[type='file']");

            // Create a file
            fileInput.setInputFiles(new FilePayload(
                "test.json",
                "application/json",
                fileContent.getBytes()
            ));

            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");
            String value = inputArea.inputValue();

            assertTrue(value.contains("uploaded"));
        }
    }

    @Nested
    class JsonStatistics {
        @Test
        void shouldDisplayStatisticsForFormattedJson() {
            String inputJson = "{\"name\":\"John\",\"age\":30,\"hobbies\":[\"reading\",\"coding\"]}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);
            page.click("button:has-text('Format')");

            // Check statistics section
            assertTrue(page.locator("text=/JSON Statistics/i").isVisible());
            assertTrue(page.locator("text=/Type:/i").isVisible());
            assertTrue(page.locator("text=/Keys:/i").isVisible());
            assertTrue(page.locator("text=/Depth:/i").isVisible());
            assertTrue(page.locator("text=/Size:/i").isVisible());
        }

        @Test
        void shouldShowCorrectTypeForArrays() {
            String arrayJson = "[\"item1\", \"item2\", \"item3\"]";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(arrayJson);
            page.click("button:has-text('Format')");

            assertTrue(page.locator("text=Array").isVisible());
        }

        @Test
        void shouldShowCorrectDepthForNestedObjects() {
            String nestedJson = "{\"a\":{\"b\":{\"c\":{\"d\":\"value\"}}}}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(nestedJson);
            page.click("button:has-text('Format')");

            String depthText = page.locator("text=/Depth:/i").locator("..").textContent();
            assertTrue(depthText.contains("4"));
        }
    }

    @Nested
    class KeyboardShortcuts {
        @Test
        void shouldFormatOnCtrlEnter() {
            String inputJson = "{\"keyboard\":\"test\"}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(inputJson);
            inputArea.press("Control+Enter");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            assertTrue(formatted.contains("{\n"));
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInputGracefully() {
            page.click("button:has-text('Format')");
            assertTrue(page.locator("text=/Error:/i").isVisible());
        }

        @Test
        void shouldHandleMalformedJsonWithTrailingComma() {
            String malformedJson = "{\"name\":\"John\",\"age\":30,}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(malformedJson);
            page.click("button:has-text('Format')");

            assertTrue(page.locator("text=/Error:/i").isVisible());
        }

        @Test
        void shouldHandleUnclosedBrackets() {
            String unclosedJson = "{\"name\":\"John\",\"items\":[1,2,3}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(unclosedJson);
            page.click("button:has-text('Format')");

            assertTrue(page.locator("text=/Error:/i").isVisible());
        }
    }

    @Nested
    class SpecialCases {
        @Test
        void shouldHandleSpecialCharactersInStrings() {
            String specialJson = "{\"text\":\"Line 1\\nLine 2\\tTabbed\",\"emoji\":\"😀\"}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(specialJson);
            page.click("button:has-text('Format')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            assertTrue(formatted.contains("\\n"));
            assertTrue(formatted.contains("\\t"));
            assertTrue(formatted.contains("😀"));
        }

        @Test
        void shouldHandleNullAndBooleanValues() {
            String mixedJson = "{\"isActive\":true,\"deleted\":false,\"data\":null}";
            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");

            inputArea.fill(mixedJson);
            page.click("button:has-text('Format')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            assertTrue(formatted.contains("true"));
            assertTrue(formatted.contains("false"));
            assertTrue(formatted.contains("null"));
        }

        @Test
        void shouldHandleLargeJsonFiles() {
            // Generate large JSON
            StringBuilder largeJsonBuilder = new StringBuilder("{\"data\":[");
            for (int i = 0; i < 100; i++) {
                if (i > 0) largeJsonBuilder.append(",");
                largeJsonBuilder.append("{\"id\":").append(i)
                    .append(",\"name\":\"Item ").append(i).append("\"")
                    .append(",\"value\":").append(Math.random()).append("}");
            }
            largeJsonBuilder.append("]}");
            String largeJson = largeJsonBuilder.toString();

            Locator inputArea = page.locator("textarea[placeholder*='Paste your JSON']");
            inputArea.fill(largeJson);
            page.click("button:has-text('Format')");

            Locator outputArea = page.locator("textarea[placeholder*='Formatted JSON']");
            String formatted = outputArea.inputValue();

            assertNotNull(formatted);
            assertTrue(formatted.length() > largeJson.length());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("textarea").first().isVisible());
            assertTrue(page.locator("button:has-text('Format')").isVisible());
        }
    }
}
