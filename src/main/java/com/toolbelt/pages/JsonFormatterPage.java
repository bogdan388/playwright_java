package com.toolbelt.pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.FilePayload;

public class JsonFormatterPage extends BasePage {
    private final String INPUT_TEXTAREA = "textarea[placeholder*='Paste your JSON']";
    private final String OUTPUT_TEXTAREA = "textarea[placeholder*='Formatted JSON']";
    private final String FORMAT_BUTTON = "button:has-text('Format')";
    private final String MINIFY_BUTTON = "button:has-text('Minify')";
    private final String COPY_BUTTON = "button[title='Copy to clipboard']";
    private final String DOWNLOAD_BUTTON = "button[title='Download JSON']";
    private final String UPLOAD_BUTTON = "button[title='Upload JSON file']";
    private final String SAMPLE_BUTTON = "button:has-text('Sample')";
    private final String INDENT_SELECT = "select";
    private final String SORT_KEYS_CHECKBOX = "#sortKeys";
    private final String FILE_INPUT = "input[type='file']";
    private final String H1_TITLE = "h1";
    private final String DESCRIPTION = "text=/Format, validate, and beautify your JSON data/i";
    private final String ERROR_MESSAGE = "text=/Error:/i";
    private final String STATISTICS_SECTION = "text=/JSON Statistics/i";
    private final String TYPE_LABEL = "text=/Type:/i";
    private final String KEYS_LABEL = "text=/Keys:/i";
    private final String DEPTH_LABEL = "text=/Depth:/i";
    private final String SIZE_LABEL = "text=/Size:/i";
    private final String ARRAY_TYPE = "text=Array";
    private final String SUCCESS_INDICATOR = ".text-green-400";
    private final String BACK_TO_TOOLS = "text=/Back to Tools/i";

    public JsonFormatterPage(Page page) {
        super(page);
    }

    public void fillInput(String json) {
        page.locator(INPUT_TEXTAREA).fill(json);
    }

    public void clickFormat() {
        page.locator(FORMAT_BUTTON).click();
    }

    public void clickMinify() {
        page.locator(MINIFY_BUTTON).click();
    }

    public void clickCopy() {
        page.locator(COPY_BUTTON).click();
    }

    public void clickDownload() {
        page.locator(DOWNLOAD_BUTTON).click();
    }

    public void clickUpload() {
        page.locator(UPLOAD_BUTTON).click();
    }

    public void clickSample() {
        page.locator(SAMPLE_BUTTON).click();
    }

    public void selectIndentSize(String size) {
        page.selectOption(INDENT_SELECT, size);
    }

    public void checkSortKeys() {
        page.check(SORT_KEYS_CHECKBOX);
    }

    public void uploadFile(String filename, String content) {
        page.locator(FILE_INPUT).setInputFiles(new FilePayload(filename, "application/json", content.getBytes()));
    }

    public void pressFormatShortcut() {
        page.locator(INPUT_TEXTAREA).press("Control+Enter");
    }

    public String getOutput() {
        return page.locator(OUTPUT_TEXTAREA).inputValue();
    }

    public String getInput() {
        return page.locator(INPUT_TEXTAREA).inputValue();
    }

    public boolean isTitleVisible() {
        return page.locator(H1_TITLE).isVisible();
    }

    public String getTitleText() {
        return page.locator(H1_TITLE).textContent();
    }

    public boolean isDescriptionVisible() {
        return page.locator(DESCRIPTION).isVisible();
    }

    public boolean isInputTextareaVisible() {
        return page.locator(INPUT_TEXTAREA).isVisible();
    }

    public boolean isOutputTextareaVisible() {
        return page.locator(OUTPUT_TEXTAREA).isVisible();
    }

    public boolean isFormatButtonVisible() {
        return page.locator(FORMAT_BUTTON).isVisible();
    }

    public boolean isMinifyButtonVisible() {
        return page.locator(MINIFY_BUTTON).isVisible();
    }

    public boolean isErrorVisible() {
        return page.locator(ERROR_MESSAGE).isVisible();
    }

    public boolean isStatisticsSectionVisible() {
        return page.locator(STATISTICS_SECTION).isVisible();
    }

    public boolean isTypeLabelVisible() {
        return page.locator(TYPE_LABEL).isVisible();
    }

    public boolean isKeysLabelVisible() {
        return page.locator(KEYS_LABEL).isVisible();
    }

    public boolean isDepthLabelVisible() {
        return page.locator(DEPTH_LABEL).isVisible();
    }

    public boolean isSizeLabelVisible() {
        return page.locator(SIZE_LABEL).isVisible();
    }

    public boolean isArrayTypeVisible() {
        return page.locator(ARRAY_TYPE).isVisible();
    }

    public boolean isSuccessIndicatorVisible() {
        return page.locator(SUCCESS_INDICATOR).isVisible();
    }

    public String getDepthText() {
        return page.locator(DEPTH_LABEL).locator("..").textContent();
    }

    public void clickBackToTools() {
        page.click(BACK_TO_TOOLS);
    }
}
