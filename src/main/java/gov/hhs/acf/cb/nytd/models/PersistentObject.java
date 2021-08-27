/**
 * Filename: PersistentObject.java
 *
 * Copyright 2009, ICF International Created: May 20, 2009 Author: 15178
 *
 * COPYRIGHT STATUS: This work, authored by ICF International employees, was
 * funded in whole or in part under U.S. Government contract, and is, therefore,
 * subject to the following license: The Government is granted for itself and
 * others acting on its behalf a paid-up, nonexclusive, irrevocable worldwide
 * license in this work to reproduce, prepare derivative works, distribute
 * copies to the public, and perform publicly and display publicly, by or on
 * behalf of the Government. All other rights are reserved by the copyright
 * owner.
 */
package gov.hhs.acf.cb.nytd.models;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author 15178
 */
public abstract class PersistentObject implements Serializable {
    // regular expression pattern for named parameters in messages
    // parameter format in messages is ${parameterName}
    private static Pattern paramPattern = Pattern.compile("(\\$\\{(\\w+)\\})");

    protected Long id;
    protected Calendar createdDate;
    protected String createdBy;
    protected Calendar updateDate;
    protected String updateBy;
    protected String description;

    /**
     * @return the createdDate
     */
    public Calendar getCreatedDate() {
        return createdDate;
    }

    /**
     * @param createdDate the createdDate to set
     */
    public void setCreatedDate(Calendar createdDate) {
        this.createdDate = createdDate;
    }

    /**
     * @return the createdBy
     */
    public String getCreatedBy() {
        return createdBy;
    }

    /**
     * @param createdBy the createdBy to set
     */
    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    /**
     * @return the updatedDate
     */
    public Calendar getUpdateDate() {
        return updateDate;
    }

    /**
     * @param updateDate
     */
    public void setUpdateDate(Calendar updateDate) {
        this.updateDate = updateDate;
    }

    /**
     * @return the updatedBy
     */
    public String getUpdateBy() {
        return updateBy;
    }

    /**
     * @param updateBy
     */
    public void setUpdateBy(String updateBy) {
        this.updateBy = updateBy;
    }

    /**
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description the description to set
     */
    public void setDescription(String description) {
        this.description = description;
    }

    public Long getId() {
        return this.id;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public String toString() {
        return getClass().getSimpleName()
                + "<" + Integer.toHexString(System.identityHashCode(this)) + ">"
                + " id=" + getId();
    }

    public String formatText(String text, Map params) {
        Matcher paramMatcher = paramPattern.matcher(text);
        StringBuffer sb = new StringBuffer();
        while (paramMatcher.find()) {
            String namedParameter = paramMatcher.group(2);

            if (params.containsKey(namedParameter)) {
                paramMatcher.appendReplacement(
                        sb, params.get(namedParameter).toString());
            } else {
                throw new RuntimeException(
                        "parameter map missing named parmater " + namedParameter);
            }
        }
        paramMatcher.appendTail(sb);
        return sb.toString();
    }
}

