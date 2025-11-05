package com.toolbelt.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class PdfToolsPage extends BasePage {
    private final String H1_TITLE = "h1";
    private final String TOOLS_DESCRIPTION = "text=/8 powerful PDF tools/i";
    private final String PRIVACY_HEADING = "h3:has-text('Complete Privacy')";
    private final String MERGE_PDFS_BUTTON = "button:has-text('Merge PDFs')";
    private final String SPLIT_PDF_BUTTON = "button:has-text('Split PDF')";
    private final String PDF_TO_WORD_BUTTON = "button:has-text('PDF to Word')";
    private final String WORD_TO_PDF_BUTTON = "button:has-text('Word to PDF')";
    private final String PDF_TO_IMAGES_BUTTON = "button:has-text('PDF to Images')";
    private final String IMAGES_TO_PDF_BUTTON = "button:has-text('Images to PDF')";
    private final String SIGN_PDF_BUTTON = "button:has-text('Sign PDF')";
    private final String ROTATE_PDF_BUTTON = "button:has-text('Rotate PDF')";
    private final String DRAG_DROP_TEXT = "text=/Drag & drop files here/i";
    private final String CHOOSE_FILES_TEXT = "text=/Choose Files/i";
    private final String FILE_INPUT = "input[type=\"file\"]";
    private final String PRIVACY_BROWSER_TEXT = "text=/All files are processed in your browser/i";
    private final String PRIVACY_NO_UPLOAD_TEXT = "text=/No uploads to any server/i";
    private final String UPLOAD_SPLIT_TEXT = "text=/Upload a PDF file you want to split/i";
    private final String UPLOAD_ROTATE_TEXT = "text=/Upload a PDF file you want to rotate/i";
    private final String MERGE_DESCRIPTION = "text=/Combine multiple PDFs into one/i";
    private final String HOW_TO_MERGE = "text=/How to Use Merge PDFs/i";
    private final String UPLOAD_TWO_OR_MORE = "text=/Upload 2 or more PDF files/i";
    private final String HOW_TO_SPLIT = "text=/How to Use Split PDF/i";
    private final String EXTRACT_ALL_PAGES = "text=/extract all pages/i";
    private final String MERGE_PROCESS_BUTTON = "button:has-text('Merge PDFs')";
    private final String ERROR_DISPLAY = ".bg-red-900\\/20";
    private final String FAQ_SECTION = "text=/Frequently Asked Questions/i";
    private final String FAQ_SAFE = "text=/Is it safe to process confidential PDFs/i";
    private final String FAQ_SIZE_LIMIT = "text=/Is there a file size limit/i";
    private final String FAQ_ACCOUNT = "text=/Do I need to create an account/i";
    private final String FAQ_BROWSERS = "text=/What browsers are supported/i";
    private final String SELECT_TOOL_HEADING = "text=/Select PDF Tool/i";
    private final String UPLOAD_HEADING = "h2:has-text('Upload')";
    private final String MINIMUM_TWO_TEXT = "text=/minimum 2/i";
    private final String UPLOAD_PDF_FILE_TEXT = "text=/Upload.*PDF File$/i";
    private final String PROCESSING_COMPLETE_TEXT = "text=/Processing Complete/i";
    private final String MAXIMUM_FILES_TEXT = "text=/Maximum 20 PDF files/i";
    private final String SUPPORTED_FORMATS_TEXT = "text=/JPG, PNG.*supported/i";
    private final String UPLOAD_PDF_FILES_MIN_TWO = "text=/Upload.*PDF Files.*minimum 2/i";
    private final String EDITOR_TEXTAREA = "textarea[placeholder*=\"Enter text to add\"]";
    private final String SIGNATURE_CANVAS = "canvas";
    private final String GRADIENT_BACKGROUND = ".bg-gradient-to-br";

    public PdfToolsPage(Page page) {
        super(page);
    }

    public void clickMergePdfs() {
        page.locator(MERGE_PDFS_BUTTON).click();
    }

    public void clickSplitPdf() {
        page.locator(SPLIT_PDF_BUTTON).click();
    }

    public void clickPdfToWord() {
        page.locator(PDF_TO_WORD_BUTTON).click();
    }

    public void clickWordToPdf() {
        page.locator(WORD_TO_PDF_BUTTON).click();
    }

    public void clickPdfToImages() {
        page.locator(PDF_TO_IMAGES_BUTTON).click();
    }

    public void clickImagesToPdf() {
        page.locator(IMAGES_TO_PDF_BUTTON).click();
    }

    public void clickSignPdf() {
        page.locator(SIGN_PDF_BUTTON).click();
    }

    public void clickRotatePdf() {
        page.locator(ROTATE_PDF_BUTTON).click();
    }

    public String getFileInputAcceptAttribute() {
        return page.locator(FILE_INPUT).getAttribute("accept");
    }

    public String getTitleText() {
        return page.locator(H1_TITLE).textContent();
    }

    public String getRotateButtonClasses() {
        return page.locator(ROTATE_PDF_BUTTON).getAttribute("class");
    }

    public boolean isTitleVisible() {
        return page.locator(H1_TITLE).isVisible();
    }

    public boolean isToolsDescriptionVisible() {
        return page.locator(TOOLS_DESCRIPTION).first().isVisible();
    }

    public boolean isPrivacyHeadingVisible() {
        return page.locator(PRIVACY_HEADING).isVisible();
    }

    public boolean isMergePdfsButtonVisible() {
        return page.locator(MERGE_PDFS_BUTTON).isVisible();
    }

    public boolean isSplitPdfButtonVisible() {
        return page.locator(SPLIT_PDF_BUTTON).isVisible();
    }

    public boolean isPdfToWordButtonVisible() {
        return page.locator(PDF_TO_WORD_BUTTON).isVisible();
    }

    public boolean isWordToPdfButtonVisible() {
        return page.locator(WORD_TO_PDF_BUTTON).isVisible();
    }

    public boolean isPdfToImagesButtonVisible() {
        return page.locator(PDF_TO_IMAGES_BUTTON).isVisible();
    }

    public boolean isImagesToPdfButtonVisible() {
        return page.locator(IMAGES_TO_PDF_BUTTON).isVisible();
    }

    public boolean isSignPdfButtonVisible() {
        return page.locator(SIGN_PDF_BUTTON).isVisible();
    }

    public boolean isRotatePdfButtonVisible() {
        return page.locator(ROTATE_PDF_BUTTON).isVisible();
    }

    public boolean isDragDropTextVisible() {
        return page.locator(DRAG_DROP_TEXT).isVisible();
    }

    public boolean isChooseFilesTextVisible() {
        return page.locator(CHOOSE_FILES_TEXT).isVisible();
    }

    public boolean isPrivacyBrowserTextVisible() {
        return page.locator(PRIVACY_BROWSER_TEXT).isVisible();
    }

    public boolean isPrivacyNoUploadTextVisible() {
        return page.locator(PRIVACY_NO_UPLOAD_TEXT).isVisible();
    }

    public boolean isUploadSplitTextVisible() {
        return page.locator(UPLOAD_SPLIT_TEXT).isVisible();
    }

    public boolean isUploadRotateTextVisible() {
        return page.locator(UPLOAD_ROTATE_TEXT).isVisible();
    }

    public boolean isMergeDescriptionVisible() {
        return page.locator(MERGE_DESCRIPTION).isVisible();
    }

    public boolean isHowToMergeVisible() {
        return page.locator(HOW_TO_MERGE).isVisible();
    }

    public boolean isUploadTwoOrMoreVisible() {
        return page.locator(UPLOAD_TWO_OR_MORE).isVisible();
    }

    public boolean isHowToSplitVisible() {
        return page.locator(HOW_TO_SPLIT).isVisible();
    }

    public boolean isExtractAllPagesVisible() {
        return page.locator(EXTRACT_ALL_PAGES).isVisible();
    }

    public boolean isMergeProcessButtonVisible() {
        return page.locator(MERGE_PROCESS_BUTTON).isVisible();
    }

    public boolean isErrorDisplayVisible() {
        return page.locator(ERROR_DISPLAY).isVisible();
    }

    public boolean isFaqSectionVisible() {
        return page.locator(FAQ_SECTION).isVisible();
    }

    public boolean isFaqSafeVisible() {
        return page.locator(FAQ_SAFE).isVisible();
    }

    public boolean isFaqSizeLimitVisible() {
        return page.locator(FAQ_SIZE_LIMIT).isVisible();
    }

    public boolean isFaqAccountVisible() {
        return page.locator(FAQ_ACCOUNT).isVisible();
    }

    public boolean isFaqBrowsersVisible() {
        return page.locator(FAQ_BROWSERS).isVisible();
    }

    public boolean isSelectToolHeadingVisible() {
        return page.locator(SELECT_TOOL_HEADING).isVisible();
    }

    public boolean isUploadHeadingVisible() {
        return page.locator(UPLOAD_HEADING).isVisible();
    }

    public boolean isMinimumTwoTextVisible() {
        return page.locator(MINIMUM_TWO_TEXT).isVisible();
    }

    public boolean isUploadPdfFileTextVisible() {
        return page.locator(UPLOAD_PDF_FILE_TEXT).first().isVisible();
    }

    public boolean isProcessingCompleteTextVisible() {
        return page.locator(PROCESSING_COMPLETE_TEXT).isVisible();
    }

    public boolean isMaximumFilesTextVisible() {
        return page.locator(MAXIMUM_FILES_TEXT).isVisible();
    }

    public boolean isSupportedFormatsTextVisible() {
        return page.locator(SUPPORTED_FORMATS_TEXT).isVisible();
    }

    public boolean isUploadPdfFilesMinTwoVisible() {
        return page.locator(UPLOAD_PDF_FILES_MIN_TWO).isVisible();
    }

    public boolean isEditorTextareaVisible() {
        return page.locator(EDITOR_TEXTAREA).isVisible();
    }

    public int getSignatureCanvasCount() {
        return page.locator(SIGNATURE_CANVAS).count();
    }

    public int getGradientBackgroundCount() {
        return page.locator(GRADIENT_BACKGROUND).count();
    }

    public boolean isFileInputAttached() {
        return page.locator(FILE_INPUT).count() > 0;
    }
}
