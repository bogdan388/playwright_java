package com.toolbelt.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

public class UuidGeneratorPage extends BasePage {
    private final String GENERATE_NEW_BUTTON = "button:has-text('Generate New')";
    private final String VERSION_SELECT = "select";
    private final String COUNT_INPUT = "input[type='number']";
    private final String UPPERCASE_CHECKBOX = "text=/uppercase/i";
    private final String HYPHENS_CHECKBOX = "text=/hyphens/i";
    private final String BRACKETS_CHECKBOX = "text=/brackets/i";
    private final String COPY_BUTTON = "button[title*='Copy']";
    private final String COPY_TEXT_BUTTON = "button:has-text('Copy')";
    private final String COPY_ALL_BUTTON = "button:has-text('Copy All')";
    private final String DOWNLOAD_BUTTON = "button:has-text('Download')";
    private final String UUID_CODE = "code";
    private final String UUID_CLASS = "[class*='uuid']";
    private final String H1_TITLE = "h1";
    private final String DESCRIPTION = "text=/Generate universally unique identifiers/i";
    private final String UUID_COUNT_TEXT = "text=/10.*UUID/i";
    private final String SUCCESS_INDICATOR = ".text-green-400";
    private final String COPIED_TEXT = "text=/copied/i";

    public UuidGeneratorPage(Page page) {
        super(page);
    }

    public void clickGenerateNew() {
        page.locator(GENERATE_NEW_BUTTON).first().click();
    }

    public void selectVersion(String version) {
        page.selectOption(VERSION_SELECT, version);
    }

    public void fillCount(String count) {
        page.fill(COUNT_INPUT, count);
    }

    public void clickUppercase() {
        if (page.locator(UPPERCASE_CHECKBOX).count() > 0) {
            page.locator(UPPERCASE_CHECKBOX).first().click();
        }
    }

    public void clickHyphens() {
        if (page.locator(HYPHENS_CHECKBOX).count() > 0) {
            page.locator(HYPHENS_CHECKBOX).first().click();
        }
    }

    public void clickBrackets() {
        if (page.locator(BRACKETS_CHECKBOX).count() > 0) {
            page.locator(BRACKETS_CHECKBOX).first().click();
        }
    }

    public void clickCopy() {
        Locator copyButton = page.locator(COPY_BUTTON).or(page.locator(COPY_TEXT_BUTTON));
        if (copyButton.count() > 0) {
            copyButton.first().click();
        }
    }

    public void clickCopyAll() {
        if (page.locator(COPY_ALL_BUTTON).count() > 0) {
            page.locator(COPY_ALL_BUTTON).first().click();
        }
    }

    public void clickDownload() {
        if (page.locator(DOWNLOAD_BUTTON).count() > 0) {
            page.locator(DOWNLOAD_BUTTON).first().click();
        }
    }

    public String getFirstUuid() {
        return page.locator(UUID_CODE).or(page.locator(UUID_CLASS)).first().textContent();
    }

    public int getUuidCount() {
        return page.locator(UUID_CODE).or(page.locator(UUID_CLASS)).count();
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

    public boolean isGenerateNewButtonVisible() {
        return page.locator(GENERATE_NEW_BUTTON).isVisible();
    }

    public boolean isUuidVisible() {
        return page.locator(UUID_CODE).or(page.locator(UUID_CLASS)).isVisible();
    }

    public boolean isUuidCountTextVisible() {
        return page.locator(UUID_COUNT_TEXT).isVisible();
    }

    public boolean isSuccessIndicatorVisible() {
        return page.locator(SUCCESS_INDICATOR).or(page.locator(COPIED_TEXT)).isVisible();
    }

    public boolean isUppercaseCheckboxVisible() {
        return page.locator(UPPERCASE_CHECKBOX).count() > 0;
    }

    public boolean isHyphensCheckboxVisible() {
        return page.locator(HYPHENS_CHECKBOX).count() > 0;
    }

    public boolean isBracketsCheckboxVisible() {
        return page.locator(BRACKETS_CHECKBOX).count() > 0;
    }

    public boolean isCopyAllButtonVisible() {
        return page.locator(COPY_ALL_BUTTON).count() > 0;
    }

    public boolean isDownloadButtonVisible() {
        return page.locator(DOWNLOAD_BUTTON).count() > 0;
    }
}
