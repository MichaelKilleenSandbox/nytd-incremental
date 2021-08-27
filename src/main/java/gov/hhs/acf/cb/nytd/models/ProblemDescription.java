package gov.hhs.acf.cb.nytd.models;

// Generated May 20, 2009 10:16:43 AM by Hibernate Tools 3.2.4.GA

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

/*
 * ProblemDescription generated by hbm2java
 */
public class ProblemDescription extends PersistentObject {

	private Long problemDescriptionId;
	private String name;

	private Set<NytdError> errors = new HashSet<NytdError>(0);

	public ProblemDescription()
	{
	}

	public ProblemDescription(Long problemDescriptionId)
	{
		this.problemDescriptionId = problemDescriptionId;
	}

	public ProblemDescription(Long problemDescriptionId, String name, Calendar createdDate, String createdBy,
			Calendar updateDate, String updateBy, String description, Set<NytdError> errors)
	{
		this.problemDescriptionId = problemDescriptionId;
		this.name = name;
		this.createdDate = createdDate;
		this.createdBy = createdBy;
		this.updateDate = updateDate;
		this.updateBy = updateBy;
		this.description = description;
		this.errors = errors;
	}

	public Long getProblemDescriptionId()
	{
		return this.problemDescriptionId;
	}

	public void setProblemDescriptionId(Long problemDescriptionId)
	{
		this.problemDescriptionId = problemDescriptionId;
	}

	public String getName()
	{
		return this.name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public Set<NytdError> getErrors()
	{
		return this.errors;
	}

	public void setErrors(Set<NytdError> errors)
	{
		this.errors = errors;
	}

}
