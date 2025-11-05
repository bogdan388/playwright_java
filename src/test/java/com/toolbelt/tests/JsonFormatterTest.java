package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.JsonFormatterPage;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class JsonFormatterTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private JsonFormatterPage jsonPage;

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
        jsonPage = new JsonFormatterPage(page);
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
        assertNotNull(jsonPage.getTitleText());
        assertTrue(jsonPage.getTitleText().contains("JSON Formatter"));

        // Check description
        assertTrue(jsonPage.isDescriptionVisible());

        // Check input and output areas
        assertTrue(jsonPage.isInputTextareaVisible());
        assertTrue(jsonPage.isOutputTextareaVisible());

        // Check control buttons
        assertTrue(jsonPage.isFormatButtonVisible());
        assertTrue(jsonPage.isMinifyButtonVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        jsonPage.clickBackToTools();
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class FormattingFeatures {
        @Test
        void shouldFormatValidJson() {
            String inputJson = "{\"name\":\"John\",\"age\":30,\"city\":\"New York\"}";

            jsonPage.fillInput(inputJson);
            jsonPage.clickFormat();

            String formatted = jsonPage.getOutput();

            assertTrue(formatted.contains("{\n"));
            assertTrue(formatted.contains("\"name\": \"John\""));
            assertTrue(formatted.contains("\"age\": 30"));
        }

        @Test
        void shouldMinifyJson() {
            String inputJson = "{\n  \"name\": \"John\",\n  \"age\": 30\n}";

            jsonPage.fillInput(inputJson);
            jsonPage.clickMinify();

            String minified = jsonPage.getOutput();

            assertEquals("{\"name\":\"John\",\"age\":30}", minified);
        }

        @Test
        void shouldHandleNestedJson() {
            String nestedJson = "{\"user\":{\"name\":\"John\",\"address\":{\"city\":\"NYC\",\"zip\":\"10001\"}}}";

            jsonPage.fillInput(nestedJson);
            jsonPage.clickFormat();

            String formatted = jsonPage.getOutput();

            assertTrue(formatted.contains("\"user\": {"));
            assertTrue(formatted.contains("\"address\": {"));
        }

        @Test
        void shouldHandleArraysInJson() {
            String arrayJson = "{\"items\":[\"apple\",\"banana\",\"orange\"],\"count\":3}";

            jsonPage.fillInput(arrayJson);
            jsonPage.clickFormat();

            String formatted = jsonPage.getOutput();

            assertTrue(formatted.contains("\"items\": ["));
            assertTrue(formatted.contains("\"apple\""));
        }

        @Test
        void shouldShowErrorForInvalidJson() {
            String invalidJson = "{name: \"John\", age: 30}"; // Missing quotes

            jsonPage.fillInput(invalidJson);
            jsonPage.clickFormat();

            assertTrue(jsonPage.isErrorVisible());
        }
    }

    @Nested
    class OptionsAndSettings {
        @Test
        void shouldRespectIndentSizeSetting() {
            String inputJson = "{\"name\":\"John\",\"age\":30}";

            jsonPage.fillInput(inputJson);

            // Test with 2 spaces (default)
            jsonPage.clickFormat();
            String formatted = jsonPage.getOutput();
            assertTrue(formatted.contains("  \"name\"")); // 2 spaces

            // Change to 4 spaces
            jsonPage.selectIndentSize("4");
            jsonPage.clickFormat();
            formatted = jsonPage.getOutput();
            assertTrue(formatted.contains("    \"name\"")); // 4 spaces
        }

        @Test
        void shouldSortKeysWhenOptionIsEnabled() {
            String inputJson = "{\"zebra\":\"z\",\"apple\":\"a\",\"banana\":\"b\"}";

            jsonPage.fillInput(inputJson);

            // Enable sort keys
            jsonPage.checkSortKeys();
            jsonPage.clickFormat();

            String formatted = jsonPage.getOutput();

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
            jsonPage.clickSample();

            String value = jsonPage.getInput();

            assertTrue(value.contains("John Doe"));
            assertTrue(value.contains("email"));
        }

        @Test
        void shouldCopyFormattedJsonToClipboard() {
            // Grant clipboard permissions
            context.grantPermissions(java.util.Arrays.asList("clipboard-read", "clipboard-write"));

            String inputJson = "{\"test\":\"value\"}";

            jsonPage.fillInput(inputJson);
            jsonPage.clickFormat();

            // Click copy button
            jsonPage.clickCopy();

            // Check for success indicator
            assertTrue(jsonPage.isSuccessIndicatorVisible());
        }

        @Test
        void shouldDownloadFormattedJson() {
            String inputJson = "{\"download\":\"test\"}";

            jsonPage.fillInput(inputJson);
            jsonPage.clickFormat();

            // Start waiting for download
            Download download = page.waitForDownload(() -> {
                jsonPage.clickDownload();
            });

            assertEquals("formatted.json", download.suggestedFilename());
        }

        @Test
        void shouldUploadJsonFile() {
            String fileContent = "{\"uploaded\": true, \"data\": \"test\"}";

            // Create file input
            jsonPage.clickUpload();
            page.waitForSelector("input[type='file']");

            // Create a file
            jsonPage.uploadFile("test.json", fileContent);

            String value = jsonPage.getInput();

            assertTrue(value.contains("uploaded"));
        }
    }

    @Nested
    class JsonStatistics {
        @Test
        void shouldDisplayStatisticsForFormattedJson() {
            String inputJson = "{\"name\":\"John\",\"age\":30,\"hobbies\":[\"reading\",\"coding\"]}";

            jsonPage.fillInput(inputJson);
            jsonPage.clickFormat();

            // Check statistics section
            assertTrue(jsonPage.isStatisticsSectionVisible());
            assertTrue(jsonPage.isTypeLabelVisible());
            assertTrue(jsonPage.isKeysLabelVisible());
            assertTrue(jsonPage.isDepthLabelVisible());
            assertTrue(jsonPage.isSizeLabelVisible());
        }

        @Test
        void shouldShowCorrectTypeForArrays() {
            String arrayJson = "[\"item1\", \"item2\", \"item3\"]";

            jsonPage.fillInput(arrayJson);
            jsonPage.clickFormat();

            assertTrue(jsonPage.isArrayTypeVisible());
        }

        @Test
        void shouldShowCorrectDepthForNestedObjects() {
            String nestedJson = "{\"a\":{\"b\":{\"c\":{\"d\":\"value\"}}}}";

            jsonPage.fillInput(nestedJson);
            jsonPage.clickFormat();

            String depthText = jsonPage.getDepthText();
            assertTrue(depthText.contains("4"));
        }
    }

    @Nested
    class KeyboardShortcuts {
        @Test
        void shouldFormatOnCtrlEnter() {
            String inputJson = "{\"keyboard\":\"test\"}";

            jsonPage.fillInput(inputJson);
            jsonPage.pressFormatShortcut();

            String formatted = jsonPage.getOutput();

            assertTrue(formatted.contains("{\n"));
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldHandleEmptyInputGracefully() {
            jsonPage.clickFormat();
            assertTrue(jsonPage.isErrorVisible());
        }

        @Test
        void shouldHandleMalformedJsonWithTrailingComma() {
            String malformedJson = "{\"name\":\"John\",\"age\":30,}";

            jsonPage.fillInput(malformedJson);
            jsonPage.clickFormat();

            assertTrue(jsonPage.isErrorVisible());
        }

        @Test
        void shouldHandleUnclosedBrackets() {
            String unclosedJson = "{\"name\":\"John\",\"items\":[1,2,3}";

            jsonPage.fillInput(unclosedJson);
            jsonPage.clickFormat();

            assertTrue(jsonPage.isErrorVisible());
        }
    }

    @Nested
    class SpecialCases {
        @Test
        void shouldHandleSpecialCharactersInStrings() {
            String specialJson = "{\"text\":\"Line 1\\nLine 2\\tTabbed\",\"emoji\":\"😀\"}";

            jsonPage.fillInput(specialJson);
            jsonPage.clickFormat();

            String formatted = jsonPage.getOutput();

            assertTrue(formatted.contains("\\n"));
            assertTrue(formatted.contains("\\t"));
            assertTrue(formatted.contains("😀"));
        }

        @Test
        void shouldHandleNullAndBooleanValues() {
            String mixedJson = "{\"isActive\":true,\"deleted\":false,\"data\":null}";

            jsonPage.fillInput(mixedJson);
            jsonPage.clickFormat();

            String formatted = jsonPage.getOutput();

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

            jsonPage.fillInput(largeJson);
            jsonPage.clickFormat();

            String formatted = jsonPage.getOutput();

            assertNotNull(formatted);
            assertTrue(formatted.length() > largeJson.length());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(jsonPage.isTitleVisible());
            assertTrue(page.locator("textarea").first().isVisible());
            assertTrue(jsonPage.isFormatButtonVisible());
        }
    }
}
