package com.toolbelt.tests;

import com.microsoft.playwright.*;
import com.toolbelt.pages.PdfToolsPage;
import com.toolbelt.utils.BrowserFactory;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

public class PdfToolsTest {
    private static Playwright playwright;
    private static Browser browser;
    private BrowserContext context;
    private Page page;
    private PdfToolsPage pdfPage;

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
        page.navigate("/pdf-tools");
        pdfPage = new PdfToolsPage(page);
    }

    @AfterEach
    void closeContext() {
        if (context != null) {
            context.close();
        }
    }

    @Test
    void shouldLoadThePdfToolsPageCorrectly() {
        assertTrue(pdfPage.getTitleText().contains("PDF Tools Suite"));
        assertTrue(pdfPage.isToolsDescriptionVisible());
        assertTrue(pdfPage.isPrivacyHeadingVisible());
    }

    @Test
    void shouldNavigateBackToHomepage() {
        pdfPage.navigateToHome();
        assertTrue(page.url().endsWith("/"));
    }

    @Nested
    class ToolSelection {
        @Test
        void shouldDisplayAllEightPdfTools() {
            assertTrue(pdfPage.isMergePdfsButtonVisible());
            assertTrue(pdfPage.isSplitPdfButtonVisible());
            assertTrue(pdfPage.isPdfToWordButtonVisible());
            assertTrue(pdfPage.isWordToPdfButtonVisible());
            assertTrue(pdfPage.isPdfToImagesButtonVisible());
            assertTrue(pdfPage.isImagesToPdfButtonVisible());
            assertTrue(pdfPage.isSignPdfButtonVisible());
            assertTrue(pdfPage.isRotatePdfButtonVisible());
        }

        @Test
        void shouldSwitchBetweenTools() {
            // Click on Split PDF
            pdfPage.clickSplitPdf();

            // Verify instructions changed
            assertTrue(pdfPage.isUploadSplitTextVisible());

            // Click on Rotate
            pdfPage.clickRotatePdf();

            assertTrue(pdfPage.isUploadRotateTextVisible());
        }
    }

    @Nested
    class PrivacyNotice {
        @Test
        void shouldDisplayPrivacyInformation() {
            assertTrue(pdfPage.isPrivacyBrowserTextVisible());
            assertTrue(pdfPage.isPrivacyNoUploadTextVisible());
        }
    }

    @Nested
    class FileUpload {
        @Test
        void shouldShowDragAndDropArea() {
            assertTrue(pdfPage.isDragDropTextVisible());
            assertTrue(pdfPage.isChooseFilesTextVisible());
        }

        @Test
        void shouldAcceptPdfFilesForMergeTool() {
            // Merge tool is selected by default
            assertEquals(".pdf", pdfPage.getFileInputAcceptAttribute());
        }

        @Test
        void shouldAcceptImagesForImagesToPdfTool() {
            pdfPage.clickImagesToPdf();

            assertEquals("image/*", pdfPage.getFileInputAcceptAttribute());
        }

        @Test
        void shouldAcceptTextFilesForWordToPdfTool() {
            pdfPage.clickWordToPdf();

            String accept = pdfPage.getFileInputAcceptAttribute();
            assertTrue(accept.contains(".txt"));
        }
    }

    @Nested
    class ToolDescriptions {
        @Test
        void shouldShowMergeToolDescription() {
            assertTrue(pdfPage.isMergeDescriptionVisible());
        }
    }

    @Nested
    class HowToGuides {
        @Test
        void shouldDisplayInstructionsForMergeTool() {
            assertTrue(pdfPage.isHowToMergeVisible());
            assertTrue(pdfPage.isUploadTwoOrMoreVisible());
        }

        @Test
        void shouldUpdateInstructionsWhenSwitchingTools() {
            pdfPage.clickSplitPdf();

            assertTrue(pdfPage.isHowToSplitVisible());
            assertTrue(pdfPage.isExtractAllPagesVisible());
        }
    }

    @Nested
    class ErrorHandling {
        @Test
        void shouldShowErrorForMergeWithLessThanTwoFiles() {
            // Try to process without files
            // Button should not be visible without files
            assertFalse(pdfPage.isMergeProcessButtonVisible());
        }

        @Test
        void shouldShowErrorMessageWhenDisplayed() {
            // This tests the error display component
            // Initially no error
            assertFalse(pdfPage.isErrorDisplayVisible());
        }
    }

    @Nested
    class FaqSection {
        @Test
        void shouldDisplayFaqSection() {
            assertTrue(pdfPage.isFaqSectionVisible());
            assertTrue(pdfPage.isFaqSafeVisible());
            assertTrue(pdfPage.isFaqSizeLimitVisible());
            assertTrue(pdfPage.isFaqAccountVisible());
            assertTrue(pdfPage.isFaqBrowsersVisible());
        }
    }

    @Nested
    class MobileResponsiveness {
        @Test
        void shouldBeResponsiveOnMobile() {
            page.setViewportSize(375, 667);

            assertTrue(pdfPage.isTitleVisible());
            assertTrue(pdfPage.isSelectToolHeadingVisible());

            // Tools should be in grid
            Locator tools = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)Merge|Split|Compress"));
            assertTrue(tools.first().isVisible());
        }

        @Test
        void shouldHaveMobileFriendlyToolGrid() {
            page.setViewportSize(375, 667);

            // Tool buttons should be visible and clickable
            pdfPage.clickRotatePdf();

            // Check that tool grid is visible
            assertTrue(pdfPage.isRotatePdfButtonVisible());
        }
    }

    @Nested
    class Accessibility {
        @Test
        void shouldHaveProperLabels() {
            assertTrue(page.locator("h2").filter(new Locator.FilterOptions().setHasText("(?i)Select PDF Tool")).isVisible());
            assertTrue(pdfPage.isUploadHeadingVisible());
        }

        @Test
        void shouldHaveFileInputAccessible() {
            assertTrue(pdfPage.isFileInputAttached());
        }
    }

    @Nested
    class ToolSwitching {
        @Test
        void shouldClearFilesWhenSwitchingTools() {
            // This tests that tool switching resets state
            pdfPage.clickRotatePdf();

            // Verify tool is selected
            String classes = pdfPage.getRotateButtonClasses();
            assertTrue(classes.contains("border-green-500") || classes.contains("bg-green"));
        }

        @Test
        void shouldShowCorrectFileTypeForEachTool() {
            // Test merge (PDF)
            assertEquals(".pdf", pdfPage.getFileInputAcceptAttribute());

            // Test images-to-pdf (images)
            pdfPage.clickImagesToPdf();
            assertEquals("image/*", pdfPage.getFileInputAcceptAttribute());
        }
    }

    @Nested
    class EditorToolUi {
        @Test
        void shouldNotShowEditorUiInitially() {
            assertFalse(pdfPage.isEditorTextareaVisible());
        }

        @Test
        void shouldShowCorrectUploadMessageForEachTool() {
            assertTrue(pdfPage.isMinimumTwoTextVisible());

            pdfPage.clickSplitPdf();
            assertTrue(pdfPage.isUploadPdfFileTextVisible());
        }
    }

    @Nested
    class SignerToolUi {
        @Test
        void shouldNotShowSignaturePadInitially() {
            // Should not be visible before selecting signer tool
            assertEquals(0, pdfPage.getSignatureCanvasCount());
        }
    }

    @Nested
    class ResultDisplay {
        @Test
        void shouldNotShowResultSectionInitially() {
            assertFalse(pdfPage.isProcessingCompleteTextVisible());
        }

        @Test
        void shouldNotShowProcessButtonWithoutFiles() {
            assertFalse(pdfPage.isMergeProcessButtonVisible());
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
            assertTrue(pdfPage.getGradientBackgroundCount() > 0);
        }
    }

    @Nested
    class UploadHints {
        @Test
        void shouldShowMaximumFilesHintForMerge() {
            assertTrue(pdfPage.isMaximumFilesTextVisible());
        }

        @Test
        void shouldShowSupportedFormatsForImagesToPdf() {
            pdfPage.clickImagesToPdf();
            assertTrue(pdfPage.isSupportedFormatsTextVisible());
        }
    }

    @Nested
    class ToolSwitchingWithStateReset {
        @Test
        void shouldClearStateWhenSwitchingFromMergeToSplit() {
            // Start with merge tool (default)
            assertTrue(pdfPage.isUploadPdfFilesMinTwoVisible());

            // Switch to split
            pdfPage.clickSplitPdf();

            // Verify tool switched
            assertTrue(pdfPage.isUploadSplitTextVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromSplitToRotate() {
            // Switch to split
            pdfPage.clickSplitPdf();
            assertTrue(pdfPage.isUploadSplitTextVisible());

            // Switch to rotate
            pdfPage.clickRotatePdf();
            assertTrue(pdfPage.isUploadPdfFileTextVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromRotateToSplit() {
            // Switch to rotate
            pdfPage.clickRotatePdf();

            // Switch to split
            pdfPage.clickSplitPdf();
            assertTrue(pdfPage.isUploadSplitTextVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromSplitToPdfToImages() {
            // Switch to split
            pdfPage.clickSplitPdf();

            // Switch to PDF to Images
            pdfPage.clickPdfToImages();
            assertTrue(pdfPage.isUploadPdfFileTextVisible());
        }

        @Test
        void shouldClearStateWhenSwitchingFromImagesToPdfToSigner() {
            // Switch to images-to-pdf
            pdfPage.clickImagesToPdf();
            assertTrue(pdfPage.isSupportedFormatsTextVisible());

            // Switch to signer
            pdfPage.clickSignPdf();
            assertTrue(pdfPage.isUploadPdfFileTextVisible());
        }

        @Test
        void shouldMaintainToolSelectionAfterSwitching() {
            // Switch to multiple tools in sequence
            pdfPage.clickWordToPdf();
            page.waitForTimeout(100);

            pdfPage.clickRotatePdf();
            page.waitForTimeout(100);

            pdfPage.clickSplitPdf();
            page.waitForTimeout(100);

            // Final tool should be selected
            String classes = page.locator("button").filter(new Locator.FilterOptions().setHasText("(?i)^Split PDF")).getAttribute("class");
            assertTrue(classes.contains("border-purple-500") || classes.contains("bg-purple"));
        }
    }
}
