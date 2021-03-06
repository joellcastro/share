/*
 * #%L
 * share-po
 * %%
 * Copyright (C) 2005 - 2016 Alfresco Software Limited
 * %%
 * This file is part of the Alfresco software. 
 * If the software was purchased under a paid Alfresco license, the terms of 
 * the paid license agreement will prevail.  Otherwise, the software is 
 * provided under the following open source license terms:
 * 
 * Alfresco is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * Alfresco is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Lesser General Public License for more details.
 * 
 * You should have received a copy of the GNU Lesser General Public License
 * along with Alfresco. If not, see <http://www.gnu.org/licenses/>.
 * #L%
 */

package org.alfresco.po.share.dashlet;

import static org.alfresco.po.RenderElement.getVisibleRenderElement;

import java.util.ArrayList;
import java.util.List;

import org.alfresco.po.HtmlPage;
import org.alfresco.po.RenderTime;
import org.alfresco.po.exception.PageException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

@FindBy(css="div[id*='DASHLET']")
/**
 * TopSiteContributorDashlet page object for top site contributor report dashlet
 * 
 * @author jcule
 */
public class TopSiteContributorDashlet extends AbstractDashlet implements Dashlet
{
    private static Log logger = LogFactory.getLog(TopSiteContributorDashlet.class);

    //chart
    private static final String TOP_SITE_CONTRIBUTOR_REPORT_DASHLET = "div[id*='DASHLET']";
    private static final String PIE_CHART_SLICES = "path[transform]";
    private static final String TOOLTIP_DATA = "div[id^='tipsyPvBehavior']";
    private static final String ORIGINAL_TITLE_ATTRIBUTE = "original-title";
    
    //date picker
    private static final String DATE_PICKER_DROP_DOWN =  "input[class='dijitReset dijitInputField dijitArrowButtonInner']";
    private static final String TODAY = "td[id='dijit_MenuItem_0_text']";
    private static final String LAST_SEVEN_DAYS = "td[id='dijit_MenuItem_1_text']";
    private static final String LAST_THIRTY_DAYS = "td[id='dijit_MenuItem_2_text']";
    private static final String PAST_YEAR = "td[id='dijit_MenuItem_3_text']";
    private static final String DATE_RANGE = "td[id='dijit_MenuItem_4_text']";   
    private static final String FROM_DATE_INPUT_FIELD = "";
    private static final String TO_DATE_INPUT_FIELD = "";
    private static final String DATE_INPUT_FIELDS = "input[id^='alfresco_forms_controls_DojoDateTextBox']";

    @SuppressWarnings("unchecked")
    public TopSiteContributorDashlet render(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(TOP_SITE_CONTRIBUTOR_REPORT_DASHLET)));
        return this;
    }
    
    public TopSiteContributorDashlet renderDateDropDown(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector("div[id^='alfresco_forms_controls_DojoDateTextBox']")));
        return this;
    }
    
    /**
    public TopSiteContributorDashlet renderNoDataMessage(RenderTime timer)
    {
        elementRender(timer, getVisibleRenderElement(By.cssSelector(NO_DATA_FOUND)));
        return this;
    }
**/
    
    /**
     * Gets the list of user data appearing in tooltips (file type-count) 
     * @return List<String>
     */
    public List<String> getTooltipUserData() throws Exception
    {
        List<WebElement> pieChartSlices = getPieChartSlices();
        List<String> toolTipData = new ArrayList<String>();
        for (WebElement pieChartSlice : pieChartSlices)
        {
            mouseOver(pieChartSlice);
            WebElement tooltipElement = findAndWait(By.cssSelector(TOOLTIP_DATA));
            String user = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/table/tr/td/div/strong");
            String items = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/table/tr/td/div/text()[preceding-sibling::br]");
            String [] counts = items.split(" ");
            String fileCount = counts[0];
            StringBuilder builder = new StringBuilder();
            builder.append(user).append("-").append(fileCount);
            toolTipData.add(builder.toString());
        }   
        return toolTipData;
    }
    
    
    /**
     * Gets the list of usernames appearing in tooltips 
     * @return List<String>
     */
    public List<String> getTooltipUsers() throws Exception
    {
        List<WebElement> pieChartSlices = getPieChartSlices();
        List<String> users = new ArrayList<String>();
        for (WebElement pieChartSlice : pieChartSlices)
        {
            mouseOver(pieChartSlice);
            mouseOver(pieChartSlice);
            WebElement tooltipElement = findAndWait(By.cssSelector(TOOLTIP_DATA));
            String user = getElement(tooltipElement.getAttribute(ORIGINAL_TITLE_ATTRIBUTE), "/table/tr/td/div/strong");
            users.add(user);
        }   
        return users;
    }
    
    
    
    /**
     * Gets the list of pie chart slices elements
     * 
     * @return List<WebElement>
     */
    private List<WebElement> getPieChartSlices()
    {
        List<WebElement> pieChartSlices = new ArrayList<WebElement>();
        try
        {
            pieChartSlices = driver.findElements(By.cssSelector(PIE_CHART_SLICES));

        }
        catch (NoSuchElementException nse)
        {
            logger.error("No Top Contributor Report pie chart slices " + nse);
        }
        return pieChartSlices;
    }
     
    
    /**
     * Enters from date into calendar
     * 
     * @param fromDate String
     * @return HtmlPage
     */
    public HtmlPage enterFromDate(final String fromDate)
    {
        if (fromDate == null || fromDate.isEmpty())
        {
            throw new UnsupportedOperationException("From date is required");
        }
        try
        {
            WebElement input = findAndWait(By.cssSelector(FROM_DATE_INPUT_FIELD));
            input.clear();
            input.sendKeys(fromDate);
            
            if (logger.isTraceEnabled())
            {
                logger.trace("From date entered: " + fromDate);
            }
            
            return getCurrentPage();
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Calendar from date drop down not displayed.");
        }
    }

    /**
     * Enters to date into calendar
     * 
     * @param toDate String
     * @return HtmlPage
     */
    public HtmlPage enterToDate(final String toDate)
    {
        if (toDate == null || toDate.isEmpty())
        {
            throw new UnsupportedOperationException("To date is required");
        }
        try
        {
            WebElement input = findAndWait(By.cssSelector(TO_DATE_INPUT_FIELD));
            input.clear();
            input.sendKeys(toDate);
            if (logger.isTraceEnabled())
            {
                logger.trace("To date entered: " + toDate);
            }
            return getCurrentPage();
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Calendar to date drop down not displayed.");
        }
    }

    /**
     * Clicks on calendar right handside arrow
     * 
     */
    public void clickOnCalendarDropdown()
    {
        try
        {
            findAndWait(By.cssSelector(DATE_PICKER_DROP_DOWN)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click the date picker dropdown arrow.", e);
            }
        }
    }

    /**
     * Selects TODAY option from the calendar dropdown  
     * 
     */
    public void clickCalendarTodayOption()
    {
        try
        {
            findAndWait(By.cssSelector(TODAY)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click on the TODAY option from dropdown.", e);
            }
        }
    }
    
    /**
     * Selects LAST 7 DAYS option from the calendar dropdown 
     *  
     */
    public void clickCalendarLastSevenDaysOption()
    {
        try
        {
            findAndWait(By.cssSelector(LAST_SEVEN_DAYS)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click on the LAST 7 DAYS option from dropdown.", e);
            }
        }
    }
    
    /**
     * Selects LAST 30 DAYS option from the calendar dropdown  
     */
    public void clickCalendarLastThirtyDaysOption()
    {
        try
        {
            findAndWait(By.cssSelector(LAST_THIRTY_DAYS)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click on the LAST 30 DAYS option from dropdown.", e);
            }
        }
    }
    
    /**
     * Selects PAST YEAR option from the calendar dropdown 
     *  
     */
    public void clickCalendarPastYearOption()
    {
        try
        {
            findAndWait(By.cssSelector(PAST_YEAR)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click on the PAST YEAR option from dropdown.", e);
            }
        }
    }
    
    /**
     * Selects Date Range option from the calendar dropdown 
     *  
     */
    public void clickCalendarDateRangeOption()
    {
        try
        {
            findAndWait(By.cssSelector(DATE_RANGE)).click();
        }
        catch (TimeoutException e)
        {
            if (logger.isTraceEnabled())
            {
                logger.trace("Exceeded time to find and click on the Date Range option from dropdown.", e);
            }
        }
    }
     
    /**
     * Enters to an from date into calendar
     * 
     * @param fromDate String
     * @param toDate String
     * @return HtmlPage
     */
    public HtmlPage enterFromToDate(final String fromDate, final String toDate)
    {
        if (fromDate == null || fromDate.isEmpty())
        {
            throw new UnsupportedOperationException("From date is required");
        }
        
        if (toDate == null || toDate.isEmpty())
        {
            throw new UnsupportedOperationException("To date is required");
        }
        
        try
        {
            List<WebElement> inputFields = getDateInputFields();
            
            WebElement fromInput = inputFields.get(0);
            WebElement toInput = inputFields.get(1);
            
            fromInput.clear();
            fromInput.sendKeys(fromDate);
            
            if (logger.isTraceEnabled())
            {
                logger.trace("From date entered: " + toDate);
            }
            
            toInput.clear();
            toInput.sendKeys(toDate);
            
            if (logger.isTraceEnabled())
            {
                logger.trace("To date entered: " + toDate);
            }
            return getCurrentPage();
        }
        catch (TimeoutException nse)
        {
            throw new PageException("Calendar to date drop down not displayed.");
        }
    }
    
    
    /**
     * 
     * Returns the list of date input fields elements
     * 
     * @return List<WebElement>
     */
    public List<WebElement> getDateInputFields()
    {
        try
        {
            List<WebElement> elements = findDisplayedElements(By.cssSelector(DATE_INPUT_FIELDS));
            return elements;
        }
        catch (NoSuchElementException nse)
        {
            logger.error("No date input fields " + nse);
            throw new PageException("Unable to find date input fields.", nse);
            
        }
    }
    @SuppressWarnings("unchecked")
    @Override
    public TopSiteContributorDashlet render()
    {
        return render(new RenderTime(maxPageLoadingTime));
    }
}
