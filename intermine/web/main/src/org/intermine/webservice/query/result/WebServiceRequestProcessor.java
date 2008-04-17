package org.intermine.webservice.query.result;

/*
 * Copyright (C) 2002-2008 FlyMine
 *
 * This code may be freely distributed and modified under the
 * terms of the GNU Lesser General Public Licence.  This should
 * be distributed with the code.  See the LICENSE file for more
 * information or http://www.gnu.org/copyleft/lesser.html.
 *
 */

import javax.servlet.http.HttpServletRequest;

import org.intermine.webservice.PagedServiceInput;
import org.intermine.webservice.WebServiceInput;


/**
 * Base request parser that is used by advanced web service parsers. 
 * @author Jakub Kulaviak
 **/
public class WebServiceRequestProcessor
{

    private static final String START_PARAMETER = "firstResult";
    private static final String LIMIT_PARAMETER = "limit";
    protected static final String OUTPUT_PARAMETER = "output";
    private static final int DEFAULT_START = 1;
    private static final int DEFAULT_MAX_COUNT = 1000;
    private static final int MAX_COUNT_LIMIT = 1000000;
    /** Value of parameter when user wants xml output to be returned. **/
    public static final String OUTPUT_PARAMETER_XML = "xml";
    /** Value of parameter when user wants tab separated output to be returned. **/
    public static final String OUTPUT_PARAMETER_TAB = "tab";
    /** Value of parameter when user wants html output to be returned. **/
    public static final String OUTPUT_PARAMETER_HTML = "html";

    /**
     * Parses common parameters for all web services. Must be called from parseRequest
     * method in subclass else the parameters won't be set. 
     * @param request request
     * @param input web service input in which the parameters are set
     */
    public void parseRequest(HttpServletRequest request, PagedServiceInput input) {
        input.setMaxCount(DEFAULT_MAX_COUNT);
        input.setStart(DEFAULT_START);
        
        Integer start = parseInteger(request.getParameter(START_PARAMETER), START_PARAMETER, 1, 
                Integer.MAX_VALUE, input);
        if (start != null) {
            input.setStart(start);
        }
    
        Integer maxCount = parseInteger(request.getParameter(LIMIT_PARAMETER), 
                LIMIT_PARAMETER, 1, MAX_COUNT_LIMIT, input);
        if (maxCount != null) {
            input.setMaxCount(maxCount);
        }        
    }
    
    private Integer parseInteger(String stringValue, String name, int minValue, int maxValue, 
            WebServiceInput input) {
        Integer ret = null;
        if (stringValue != null && !stringValue.equals("")) {
            try {
                ret = new Integer(stringValue);
                if (ret < minValue || ret > maxValue) {
                    input.addError("Invalid value of " + name + " parameter: " + ret 
                            + " Parameter should have value from " + minValue + " to " 
                            + maxValue + ".");
                }
            } catch (Exception ex) {
                String value = stringValue;
                input.addError("invalid " + name + " parameter: " + value);
            }
        }
        return ret;
    }
}
