package com.toolbelt.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.SelectOption;

public class RegexTesterPage extends BasePage {
    private final String PATTERN_INPUT = "input[placeholder*='regex pattern']";
    private final String TEST_STRING_TEXTAREA = "textarea[placeholder*='text to test']";
    private final String BACK_TO_TOOLBELT_BUTTON = "button:has-text('Back to Toolbelt')";
    private final String FLAG_G_BUTTON = "button:has-text('g')";
    private final String FLAG_I_BUTTON = "button:has-text('i')";
    private final String FLAG_M_BUTTON = "button:has-text('m')";
    private final String FLAG_S_BUTTON = "button:has-text('s')";
    private final String FLAG_U_BUTTON = "button:has-text('u')";
    private final String PATTERN_SELECT = "select:has-text('Select a pattern')";
    private final String FLAVOR_SELECT = "select:nth-of-type(2)";
    private final String SHOW_INFO_BUTTON = "button:has-text('Show Info')";
    private final String HIDE_INFO_BUTTON = "button:has-text('Hide Info')";
    private final String COPY_PATTERN_BUTTON = "button[title='Copy pattern']";
    private final String CLEAR_ALL_BUTTON = "button[title='Clear all']";
    private final String H1_TITLE = "h1:has-text('REGEX TESTER')";
    private final String DESCRIPTION = "text=Test and validate regular expressions";
    private final String TOOLBELT_H1 = "h1:has-text('TOOLBELT')";
    private final String MATCHES_FOUND = "text=/\\d+ match(es)? found/i";
    private final String NO_MATCHES = "text=No matches found";
    private final String ECMA_SCRIPT_ENGINE = "text=ECMAScript regex engine";
    private final String PYTHON_RE_MODULE = "text=Python re module";
    private final String RE2_ENGINE = "text=RE2 engine";
    private final String REGEX_CRATE = "text=regex crate";
    private final String CAPTURED_GROUPS = "text=Captured Groups";
    private final String GROUP_1 = "text=Group 1";
    private final String GROUP_2 = "text=Group 2";
    private final String POSITION_TEXT = "text=/Position: \\d+/i";
    private final String ENTER_PATTERN_MESSAGE = "text=Enter a pattern and test string";
    private final String PATTERN_LABEL = "label:has-text('Pattern')";
    private final String TEST_STRING_LABEL = "label:has-text('Test String')";
    private final String FLAGS_LABEL = "label:has-text('Flags')";
    private final String RESULTS_LABEL = "label:has-text('Results')";
    private final String COPY_PATTERN_SVG = "button[title='Copy pattern'] svg";

    public RegexTesterPage(Page page) {
        super(page);
    }

    public void fillPattern(String pattern) {
        page.fill(PATTERN_INPUT, pattern);
    }

    public void fillTestString(String text) {
        page.fill(TEST_STRING_TEXTAREA, text);
    }

    public void clickBackToToolbelt() {
        page.click(BACK_TO_TOOLBELT_BUTTON);
    }

    public void clickFlagG() {
        page.click(FLAG_G_BUTTON);
    }

    public void clickFlagI() {
        page.click(FLAG_I_BUTTON);
    }

    public void clickFlagM() {
        page.click(FLAG_M_BUTTON);
    }

    public void clickFlagS() {
        page.click(FLAG_S_BUTTON);
    }

    public void clickFlagU() {
        page.click(FLAG_U_BUTTON);
    }

    public void selectQuickExample(String example) {
        page.selectOption(PATTERN_SELECT, example);
    }

    public void selectLanguageFlavor(String flavor) {
        page.locator(FLAVOR_SELECT).selectOption(new SelectOption().setLabel(flavor));
    }

    public void selectLanguageFlavorByValue(String value) {
        page.locator(FLAVOR_SELECT).selectOption(value);
    }

    public void clickShowInfo() {
        page.click(SHOW_INFO_BUTTON);
    }

    public void clickHideInfo() {
        page.click(HIDE_INFO_BUTTON);
    }

    public void clickCopyPattern() {
        page.click(COPY_PATTERN_BUTTON);
    }

    public void clickClearAll() {
        page.click(CLEAR_ALL_BUTTON);
    }

    public void waitForResults() {
        page.waitForTimeout(500);
    }

    public void waitForResults(int milliseconds) {
        page.waitForTimeout(milliseconds);
    }

    public String getPatternValue() {
        return page.locator(PATTERN_INPUT).inputValue();
    }

    public String getTestStringValue() {
        return page.locator(TEST_STRING_TEXTAREA).inputValue();
    }

    public String getFlavorSelectValue() {
        return page.locator(FLAVOR_SELECT).inputValue();
    }

    public String getMatchesFoundText() {
        return page.locator(MATCHES_FOUND).textContent();
    }

    public boolean isTitleVisible() {
        return page.locator(H1_TITLE).isVisible();
    }

    public boolean isDescriptionVisible() {
        return page.locator(DESCRIPTION).isVisible();
    }

    public boolean isPatternInputVisible() {
        return page.locator(PATTERN_INPUT).isVisible();
    }

    public boolean isTestStringTextareaVisible() {
        return page.locator(TEST_STRING_TEXTAREA).isVisible();
    }

    public boolean isBackToToolbeltButtonVisible() {
        return page.locator(BACK_TO_TOOLBELT_BUTTON).isVisible();
    }

    public boolean isToolbeltH1Visible() {
        return page.locator(TOOLBELT_H1).isVisible();
    }

    public boolean isMatchesFoundVisible() {
        return page.locator(MATCHES_FOUND).isVisible();
    }

    public boolean isNoMatchesVisible() {
        return page.locator(NO_MATCHES).isVisible();
    }

    public boolean isEcmaScriptEngineVisible() {
        return page.locator(ECMA_SCRIPT_ENGINE).isVisible();
    }

    public boolean isPythonReModuleVisible() {
        return page.locator(PYTHON_RE_MODULE).isVisible();
    }

    public boolean isRe2EngineVisible() {
        return page.locator(RE2_ENGINE).isVisible();
    }

    public boolean isRegexCrateVisible() {
        return page.locator(REGEX_CRATE).isVisible();
    }

    public boolean isCapturedGroupsVisible() {
        return page.locator(CAPTURED_GROUPS).isVisible();
    }

    public boolean isGroup1Visible() {
        return page.locator(GROUP_1).isVisible();
    }

    public boolean isGroup2Visible() {
        return page.locator(GROUP_2).isVisible();
    }

    public boolean isPositionTextVisible() {
        return page.locator(POSITION_TEXT).isVisible();
    }

    public boolean isEnterPatternMessageVisible() {
        return page.locator(ENTER_PATTERN_MESSAGE).isVisible();
    }

    public boolean isPatternLabelVisible() {
        return page.locator(PATTERN_LABEL).isVisible();
    }

    public boolean isTestStringLabelVisible() {
        return page.locator(TEST_STRING_LABEL).isVisible();
    }

    public boolean isFlagsLabelVisible() {
        return page.locator(FLAGS_LABEL).isVisible();
    }

    public boolean isResultsLabelVisible() {
        return page.locator(RESULTS_LABEL).isVisible();
    }

    public boolean isCopyPatternSvgVisible() {
        return page.locator(COPY_PATTERN_SVG).isVisible();
    }

    public boolean hasMatchesOrFound() {
        return page.locator(MATCHES_FOUND).count() > 0 || page.locator("text=/found/i").count() > 0;
    }

    public void typePattern(String pattern) {
        page.locator(PATTERN_INPUT).click();
        page.keyboard().type(pattern);
    }

    public void typeTestString(String text) {
        page.locator(TEST_STRING_TEXTAREA).click();
        page.keyboard().type(text);
    }
}
