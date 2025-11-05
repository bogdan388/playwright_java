package com.toolbelt.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class TextCaseConverterPage extends BasePage {
    private final String INPUT_TEXTAREA = "textarea:nth-of-type(1)";
    private final String OUTPUT_TEXTAREA = "textarea:nth-of-type(2)";
    private final String UPPERCASE_BUTTON = "button:has-text('UPPERCASE')";
    private final String LOWERCASE_BUTTON = "button:has-text('lowercase')";
    private final String TITLE_CASE_BUTTON = "button:has-text('Title Case')";
    private final String CAMEL_CASE_BUTTON = "button:has-text('camelCase')";
    private final String PASCAL_CASE_BUTTON = "button:has-text('PascalCase')";
    private final String SNAKE_CASE_BUTTON = "button:has-text('snake_case')";
    private final String KEBAB_CASE_BUTTON = "button:has-text('kebab-case')";
    private final String CONSTANT_CASE_BUTTON = "button:has-text('CONSTANT_CASE')";
    private final String SWAP_BUTTON = "button:has-text('Swap')";
    private final String COPY_BUTTON = "button:has-text('Copy')";
    private final String SAMPLE_BUTTON = "button:has-text('Sample')";
    private final String DOWNLOAD_BUTTON = "button:has-text('Download')";
    private final String H1_TITLE = "h1";
    private final String DESCRIPTION = "text=/Convert text between different case formats/i";
    private final String CHARACTERS_LABEL = "text=/characters/i";
    private final String WORDS_LABEL = "text=/words/i";
    private final String SUCCESS_INDICATOR = ".text-green-400";
    private final String COPIED_TEXT = "text=/copied/i";
    private final String HISTORY_SECTION = "text=/Recent|History/i";

    public TextCaseConverterPage(Page page) {
        super(page);
    }

    public void fillInput(String text) {
        page.locator(INPUT_TEXTAREA).fill(text);
    }

    public void clickUppercase() {
        page.locator(UPPERCASE_BUTTON).click();
    }

    public void clickLowercase() {
        page.locator(LOWERCASE_BUTTON).click();
    }

    public void clickTitleCase() {
        page.locator(TITLE_CASE_BUTTON).click();
    }

    public void clickCamelCase() {
        page.locator(CAMEL_CASE_BUTTON).click();
    }

    public void clickPascalCase() {
        page.locator(PASCAL_CASE_BUTTON).click();
    }

    public void clickSnakeCase() {
        page.locator(SNAKE_CASE_BUTTON).click();
    }

    public void clickKebabCase() {
        page.locator(KEBAB_CASE_BUTTON).click();
    }

    public void clickConstantCase() {
        page.locator(CONSTANT_CASE_BUTTON).click();
    }

    public void clickSwap() {
        if (page.locator(SWAP_BUTTON).isVisible()) {
            page.locator(SWAP_BUTTON).click();
        }
    }

    public void clickCopy() {
        if (page.locator(COPY_BUTTON).isVisible()) {
            page.locator(COPY_BUTTON).click();
        }
    }

    public void clickSample() {
        if (page.locator(SAMPLE_BUTTON).count() > 0) {
            page.locator(SAMPLE_BUTTON).first().click();
        }
    }

    public void clickDownload() {
        if (page.locator(DOWNLOAD_BUTTON).count() > 0) {
            page.locator(DOWNLOAD_BUTTON).first().click();
        }
    }

    public void waitForConversion() {
        page.waitForTimeout(300);
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

    public boolean isCharactersLabelVisible() {
        return page.locator(CHARACTERS_LABEL).first().isVisible();
    }

    public boolean isWordsLabelVisible() {
        return page.locator(WORDS_LABEL).first().isVisible();
    }

    public boolean isSuccessIndicatorVisible() {
        return page.locator(SUCCESS_INDICATOR).or(page.locator(COPIED_TEXT)).isVisible();
    }

    public boolean isHistorySectionVisible() {
        if (page.locator(HISTORY_SECTION).count() > 0) {
            return page.locator(HISTORY_SECTION).first().isVisible();
        }
        return false;
    }

    public boolean isSampleButtonVisible() {
        return page.locator(SAMPLE_BUTTON).count() > 0;
    }

    public boolean isDownloadButtonVisible() {
        return page.locator(DOWNLOAD_BUTTON).count() > 0;
    }
}
