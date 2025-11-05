package com.toolbelt.pages;

import com.microsoft.playwright.Page;

public class DiffCheckerPage extends BasePage {
    private final String TEXT1_TEXTAREA = "textarea:nth-of-type(1)";
    private final String TEXT2_TEXTAREA = "textarea:nth-of-type(2)";
    private final String SPLIT_VIEW_BUTTON = "button:has-text('Split View')";
    private final String UNIFIED_VIEW_BUTTON = "button:has-text('Unified View')";
    private final String IGNORE_WHITESPACE = "text=/Ignore.*Whitespace/i";
    private final String IGNORE_CASE = "text=/Ignore.*Case/i";
    private final String SAMPLE_BUTTON = "button:has-text('Sample')";
    private final String CLEAR_BUTTON = "button:has-text('Clear')";
    private final String SWAP_BUTTON = "button[title*='Swap']";
    private final String H1_TITLE = "h1";
    private final String IDENTICAL_MESSAGE = "text=/identical|no differences|unchanged/i";
    private final String ADDED_MESSAGE = "text=/added|\\+1|differences|changes/i";
    private final String REMOVED_MESSAGE = "text=/removed|-1|differences|changes/i";
    private final String MODIFIED_MESSAGE = "text=/modified|~1|differences|changes/i";
    private final String STATISTICS = "text=/added|removed|modified|total lines/i";
    private final String ORIGINAL_LABEL = "text=/Original|Text 1|First/i";
    private final String MODIFIED_LABEL = "text=/Modified|Text 2|Second/i";

    public DiffCheckerPage(Page page) {
        super(page);
    }

    public void fillText1(String text) {
        page.locator(TEXT1_TEXTAREA).fill(text);
    }

    public void fillText2(String text) {
        page.locator(TEXT2_TEXTAREA).fill(text);
    }

    public void clickSplitView() {
        if (page.locator(SPLIT_VIEW_BUTTON).count() > 0) {
            page.locator(SPLIT_VIEW_BUTTON).first().click();
        }
    }

    public void clickUnifiedView() {
        if (page.locator(UNIFIED_VIEW_BUTTON).count() > 0) {
            page.locator(UNIFIED_VIEW_BUTTON).first().click();
        }
    }

    public void clickIgnoreWhitespace() {
        if (page.locator(IGNORE_WHITESPACE).count() > 0) {
            page.locator(IGNORE_WHITESPACE).first().click();
        }
    }

    public void clickIgnoreCase() {
        if (page.locator(IGNORE_CASE).count() > 0) {
            page.locator(IGNORE_CASE).first().click();
        }
    }

    public void clickSample() {
        if (page.locator(SAMPLE_BUTTON).count() > 0) {
            page.locator(SAMPLE_BUTTON).first().click();
        }
    }

    public void clickClear() {
        if (page.locator(CLEAR_BUTTON).count() > 0) {
            page.locator(CLEAR_BUTTON).first().click();
        }
    }

    public void clickSwap() {
        if (page.locator(SWAP_BUTTON).count() > 0) {
            page.locator(SWAP_BUTTON).first().click();
        }
    }

    public String getText1() {
        return page.locator(TEXT1_TEXTAREA).inputValue();
    }

    public String getText2() {
        return page.locator(TEXT2_TEXTAREA).inputValue();
    }

    public void waitForDiffCalculation() {
        page.waitForTimeout(500);
    }

    public boolean isTitleVisible() {
        return page.locator(H1_TITLE).isVisible();
    }

    public String getTitleText() {
        return page.locator(H1_TITLE).textContent();
    }

    public boolean isText1TextareaVisible() {
        return page.locator(TEXT1_TEXTAREA).isVisible();
    }

    public boolean isText2TextareaVisible() {
        return page.locator(TEXT2_TEXTAREA).isVisible();
    }

    public boolean isIdenticalMessageVisible() {
        return page.locator(IDENTICAL_MESSAGE).isVisible();
    }

    public boolean isAddedMessageVisible() {
        return page.locator(ADDED_MESSAGE).isVisible();
    }

    public boolean isRemovedMessageVisible() {
        return page.locator(REMOVED_MESSAGE).isVisible();
    }

    public boolean isModifiedMessageVisible() {
        return page.locator(MODIFIED_MESSAGE).isVisible();
    }

    public boolean isStatisticsVisible() {
        return page.locator(STATISTICS).isVisible();
    }

    public boolean isOriginalLabelVisible() {
        return page.locator(ORIGINAL_LABEL).first().isVisible();
    }

    public boolean isModifiedLabelVisible() {
        return page.locator(MODIFIED_LABEL).first().isVisible();
    }
}
