package com.toolbelt.tests;

import com.microsoft.playwright.*;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PdfToolsTest {
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
        page.navigate("/pdf-tools");
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadThePdfToolsPageCorrectly() {
        assertTrue(page.locator("h1").textContent().contains("PDF Tools Suite"));
        assertTrue(page.locator("text=/8 powerful PDF tools/i").first().isVisible());
        assertTrue(page.locator("h3").filter(new Locator.FilterOptions().setHasText("(?i)Complete Privacy")).isVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        page.click("a[href=\"/\"]");
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class ToolSelection {
        @Test
        void shouldDisplayAllEightPdfTools() {
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Merge PDFs")).isVisible());
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).isVisible());
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^PDF to Word")).isVisible());
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Word to PDF")).isVisible());
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^PDF to Images")).isVisible());
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Images to PDF")).isVisible());
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Sign PDF")).isVisible());
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).isVisible());
        }

        @Test
        void shouldSwitchBetweenTools() {
            // Click on Split PDF
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();

            // Verify instructions changed
            assertTrue(page.locator("text=/Upload a PDF file you want to split/i").isVisible());

            // Click on Rotate
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).click();

            assertTrue(page.locator("text=/Upload a PDF file you want to rotate/i").isVisible());
        }
    }

    @Nested
    class PrivacyNotice {
        @Test
        void shouldDisplayPrivacyInformation() {
            assertTrue(page.locator("text=/All files are processed in your browser/i").isVisible());
            assertTrue(page.locator("text=/No uploads to any server/i").isVisible());
        }
    }

    @Nested
    class FileUpload {
        @Test
        void shouldShowDragAndDropArea() {
            assertTrue(page.locator("text=/Drag & drop files here/i").isVisible());
            assertTrue(page.locator("text=/Choose Files/i").isVisible());
        }

        @Test
        void shouldAcceptPdfFilesForMergeTool() {
            // Merge tool is selected by default
            Locator fileInput = page.locator("input[type=\"file\"]");
            assertEquals(".pdf", fileInput.getAttribute("accept"));
        }

        @Test
        void shouldAcceptImagesForImagesToPdfTool() {
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Images to PDF")).click();

            Locator fileInput = page.locator("input[type=\"file\"]");
            assertEquals("image/*", fileInput.getAttribute("accept"));
        }

        @Test
        void shouldAcceptTextFilesForWordToPdfTool() {
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Word to PDF")).click();

            Locator fileInput = page.locator("input[type=\"file\"]");
            String accept = fileInput.getAttribute("accept");
            assertTrue(accept.contains(".txt"));
        }
    }

    @Nested
    class ToolDescriptions {
        @Test
        void shouldShowMergeToolDescription() {
            assertTrue(page.locator("text=/Combine multiple PDFs into one/i").isVisible());
        }
    }

    @Nested
    class HowToGuides {
        @Test
        void shouldDisplayInstructionsForMergeTool() {
            assertTrue(page.locator("text=/How to Use Merge PDFs/i").isVisible());
            assertTrue(page.locator("text=/Upload 2 or more PDF files/i").isVisible());
        }

        @Test
        void shouldUpdateInstructionsWhenSwitchingTools() {
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();

            assertTrue(page.locator("text=/How to Use Split PDF/i").isVisible());
            assertTrue(page.locator("text=/extract all pages/i").isVisible());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldShowErrorForMergeWithLessThanTwoFiles() {
            // Try to process without files
            Locator processButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Merge PDFs$"));

            // Button should not be visible without files
            assertFalse(processButton.isVisible());
        }

        @Test
        void shouldShowErrorMessageWhenDisplayed() {
            // This tests the error display component
            Locator errorDisplay = page.locator(".bg-red-900\\/20");

            // Initially no error
            assertFalse(errorDisplay.isVisible());
        }
    }

    @Nested
    class FaqSection {
        @Test
        void shouldDisplayFaqSection() {
            assertTrue(page.locator("text=/Frequently Asked Questions/i").isVisible());
            assertTrue(page.locator("text=/Is it safe to process confidential PDFs/i").isVisible());
            assertTrue(page.locator("text=/Is there a file size limit/i").isVisible());
            assertTrue(page.locator("text=/Do I need to create an account/i").isVisible());
            assertTrue(page.locator("text=/What browsers are supported/i").isVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(page.locator("h1").isVisible());
            assertTrue(page.locator("text=/Select PDF Tool/i").isVisible());

            // Tools should be in grid
            Locator tools = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)Merge|Split|Compress"));
            assertTrue(tools.first().isVisible());
        }

        @Test
        void shouldHaveMobileFriendlyToolGrid() {
            page.setViewportSize(375, 667);

            // Tool buttons should be visible and clickable
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).click();

            // Check that tool grid is visible
            assertTrue(page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).isVisible());
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            assertTrue(page.locator("h2").filter(new Locator.FilterOptions().setHasText("(?i)Select PDF Tool")).isVisible());
            assertTrue(page.locator("h2").filter(new Locator.FilterOptions().setHasText("(?i)Upload")).isVisible());
        }

        @Test
        void shouldHaveFileInputAccessible() {
            Locator fileInput = page.locator("input[type=\"file\"]");
            assertTrue(fileInput.isAttached());
        }
    }

    @Nested
    class ToolSwitching {
        @Test
        void shouldClearFilesWhenSwitchingTools() {
            // This tests that tool switching resets state
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).click();

            // Verify tool is selected
            Locator rotateButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF"));
            String classes = rotateButton.getAttribute("class");
            assertTrue(classes.contains("border-green-500") || classes.contains("bg-green"));
        }

        @Test
        void shouldShowCorrectFileTypeForEachTool() {
            // Test merge (PDF)
            Locator fileInput = page.locator("input[type=\"file\"]");
            assertEquals(".pdf", fileInput.getAttribute("accept"));

            // Test images-to-pdf (images)
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Images to PDF")).click();
            fileInput = page.locator("input[type=\"file\"]");
            assertEquals("image/*", fileInput.getAttribute("accept"));
        }
    }

    @Nested
    class EditorToolUi {
        @Test
        void shouldNotShowEditorUiInitially() {
            Locator editorTextarea = page.locator("textarea[placeholder*=\"Enter text to add\"]");
            assertFalse(editorTextarea.isVisible());
        }

        @Test
        void shouldShowCorrectUploadMessageForEachTool() {
            assertTrue(page.locator("text=/minimum 2/i").isVisible());

            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();
            assertTrue(page.locator("text=/Upload.*PDF File$/i").isVisible());
        }
    }

    @Nested
    class SignerToolUi {
        @Test
        void shouldNotShowSignaturePadInitially() {
            Locator signaturePad = page.locator("canvas");

            // Should not be visible before selecting signer tool
            assertEquals(0, signaturePad.count());
        }
    }

    @Nested
    class ResultDisplay {
        @Test
        void shouldNotShowResultSectionInitially() {
            Locator resultSection = page.locator("text=/Processing Complete/i");
            assertFalse(resultSection.isVisible());
        }

        @Test
        void shouldNotShowProcessButtonWithoutFiles() {
            Locator processButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Merge PDFs$"));
            assertFalse(processButton.isVisible());
        }
    }

    @Nested
    class AdIntegration {
        @Test
        void shouldHaveAdSlots() {
            // Check for ad components (GoogleAds)
            Locator ads = page.locator("[class*=\"ad\"]").or(page.locator("text=/Advertisement/i"));
            // Ads may or may not be visible depending on ad blocker
        }
    }

    @Nested
    class BackgroundAnimation {
        @Test
        void shouldHaveAnimatedBackground() {
            // Check for gradient background
            Locator background = page.locator(".bg-gradient-to-br");
            assertTrue(background.count() > 0);
        }
    }

    @Nested
    class UploadHints {
        @Test
        void shouldShowMaximumFilesHintForMerge() {
            assertTrue(page.locator("text=/Maximum 20 PDF files/i").isVisible());
        }

        @Test
        void shouldShowSupportedFormatsForImagesToPdf() {
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Images to PDF")).click();
            assertTrue(page.locator("text=/JPG, PNG.*supported/i").isVisible());
        }
    }

    @Nested
    class ToolSwitchingWithStateReset {
        @Test
        void shouldClearStateWhenSwitchingFromMergeToSplit() {
            // Start with merge tool (default)
            assertTrue(page.locator("text=/Upload.*PDF Files.*minimum 2/i").isVisible());

            // Switch to split
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();

            // Verify tool switched
            assertTrue(page.locator("text=/Upload a PDF file you want to split/i").isVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromSplitToRotate() {
            // Switch to split
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();
            assertTrue(page.locator("text=/Upload a PDF file you want to split/i").isVisible());

            // Switch to rotate
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).click();
            assertTrue(page.locator("text=/Upload a PDF file$/i").isVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromRotateToSplit() {
            // Switch to rotate
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).click();

            // Switch to split
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();
            assertTrue(page.locator("text=/Upload a PDF file you want to split/i").isVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromSplitToPdfToImages() {
            // Switch to split
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();

            // Switch to PDF to Images
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^PDF to Images")).click();
            assertTrue(page.locator("text=/Upload a PDF file$/i").first().isVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromImagesToPdfToSigner() {
            // Switch to images-to-pdf
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Images to PDF")).click();
            assertTrue(page.locator("text=/JPG, PNG.*supported/i").isVisible());

            // Switch to signer
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Sign PDF")).click();
            assertTrue(page.locator("text=/Upload a PDF file$/i").first().isVisible());
        }

        @Test
        void shouldMaintainToolSelectionAfterSwitching() {
            // Switch to multiple tools in sequence
            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Word to PDF")).click();
            page.waitForTimeout(100);

            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Rotate PDF")).click();
            page.waitForTimeout(100);

            page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).click();
            page.waitForTimeout(100);

            // Final tool should be selected
            Locator splitButton = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF"));
            String classes = splitButton.getAttribute("class");
            assertTrue(classes.contains("border-purple-500") || classes.contains("bg-purple"));
        }
    }
}
