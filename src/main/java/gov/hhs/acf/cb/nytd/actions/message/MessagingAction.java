/**
 * Filename: MessagingAction.java
 * 
 * Copyright 2009, ICF International
 * Created: Aug 26, 2009
 * Author: 16939
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
package gov.hhs.acf.cb.nytd.actions.message;

import com.opensymphony.xwork2.Action;
import com.opensymphony.xwork2.validator.annotations.RegexFieldValidator;
import com.opensymphony.xwork2.validator.annotations.Validations;
import gov.hhs.acf.cb.nytd.actions.SearchAction;
import gov.hhs.acf.cb.nytd.models.Message;
import gov.hhs.acf.cb.nytd.service.MessageService;
import lombok.Getter;
import lombok.Setter;
import org.apache.log4j.Logger;
import org.apache.struts2.interceptor.validation.SkipValidation;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;

/**
 * Messaging action
 * 
 * @author 16939, 13873 (Grant Lewis), 18816 (Adam Russell)
 */
@SuppressWarnings("serial")
public class MessagingAction extends SearchAction<MessageSearch>
{
	// logger
	protected final Logger log = Logger.getLogger(getClass());

	// message service
	@Getter @Setter private MessageService messageServiceP3;

	// message search
	@Getter @Setter private MessageSearch search = null;

	// message object
	@Getter @Setter private Message message;

	// ?
	@Getter @Setter private String sendMessagesList;

	/**
	 * responsible for initializing the search object prior to action invocation
	 * 
	 * @throws Exception
	 */
	public void prepare()
	{
		super.prepare();

		// when using the tab navigation the search object is not created
		if (search == null)
		{
			search = new MessageSearch();
		}

		search.setUser(getUser());
	}

	/**
	 * execute function is used when user clicks on the Messages Tab from the
	 * main nav. It lists Search Criteria , Post New Message and default search
	 * of the latest messages.
	 */
	@SkipValidation
	public final String search()
	{
		String text = null;
		String messageCreatedStartDate = null;
		String messageCreatedEndDate = null;
		Integer pageSize = null;
		
		if (getParameters().containsKey("search.text"))
		{
			text = getParameters().get("search.text").getValue();
		}
		if (getParameters().containsKey("search.messageCreatedStartDate"))
		{
			messageCreatedStartDate = getParameters().get("search.messageCreatedStartDate").getValue();
		}
		if (getParameters().containsKey("search.messageCreatedEndDate"))
		{
			messageCreatedEndDate = getParameters().get("search.messageCreatedEndDate").getValue();
		}
		if (getParameters().containsKey("search.pageSize"))
		{
			pageSize = Integer.parseInt(getParameters().get("search.pageSize").getValue());
		}
		
		// Workaround for an intermittent bug where dates will not already be decoded by Struts
		if (messageCreatedStartDate != null)
		{
			messageCreatedStartDate = urlDecode(messageCreatedStartDate);
		}
		if (messageCreatedEndDate != null)
		{
			messageCreatedEndDate = urlDecode(messageCreatedEndDate);
		}
		
		if (pageSize != null)
		{
			getSearch().setText(text);
			getSearch().setMessageCreatedStartDate(messageCreatedStartDate);
			getSearch().setMessageCreatedEndDate(messageCreatedEndDate);
			getSearch().setPageSize(pageSize);
		}
		
		setSearch(messageServiceP3.search(getSearch()));
		return Action.SUCCESS;
	}

	/**
	 * displays the message detail page. Message object is pre-populated from
	 * request parameters.
	 * 
	 * @return
	 */
	@SkipValidation
	public String messageDetail()
	{
		message = messageServiceP3.getMessage(message.getId());
		messageServiceP3.prepareSystemMessage(message);
		return Action.SUCCESS;
	}
	
	/**
	 * Processes form data for message search.
	 * 
	 * @return SUCCESS or INPUT
	 */
	@Validations(
		regexFields={
			@RegexFieldValidator(fieldName="search.messageCreatedStartDate", regex="\\d{1,2}\\/\\d{1,2}/\\d{4}",
			                     message="The Start Date must be in the MM/DD/YYYY format"),
			@RegexFieldValidator(fieldName="search.messageCreatedEndDate", regex="\\d{1,2}\\/\\d{1,2}/\\d{4}",
			                     message="The End Date must be in the MM/DD/YYYY format")})
	public final String postCriteria()
	{
		if (getSearch().getMessageCreatedStartDate() != null && getSearch().getMessageCreatedEndDate() != null
		 && !getSearch().getMessageCreatedStartDate().isEmpty() && !getSearch().getMessageCreatedEndDate().isEmpty())
		{
			Date messageCreatedStartDate, messageCreatedEndDate;
			try
			{
				messageCreatedStartDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(getSearch().getMessageCreatedStartDate());
				messageCreatedEndDate = DateFormat.getDateInstance(DateFormat.SHORT).parse(getSearch().getMessageCreatedEndDate());
			}
			catch (ParseException e)
			{
				log.error(e.getMessage(), e);
				return ERROR;
			}
			if (messageCreatedStartDate.after(messageCreatedEndDate))
			{
				addActionError("Start Date must not be after End Date");
				return INPUT;
			}
		}
		return SUCCESS;
	}

	@SkipValidation
	public String resetSearch()
	{
		return SUCCESS;
	}

	@Override
	protected MessageSearch getPaginatedSearch()
	{
		return getSearch();
	}
}