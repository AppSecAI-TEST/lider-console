package tr.org.liderahenk.liderconsole.core.model;

import java.io.Serializable;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import tr.org.liderahenk.liderconsole.core.enums.ParameterType;

/**
 * This class represents a report parameter defined in a report template.
 * 
 * @author <a href="mailto:emre.akkaya@agem.com.tr">Emre Akkaya</a>
 *
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReportTemplateParameter implements Serializable {

	private static final long serialVersionUID = -5961536845769096042L;

	private Long id;

	private String key;

	private String label;

	private ParameterType type;

	public ReportTemplateParameter() {
	}

	public ReportTemplateParameter(Long id, String key, String label, ParameterType type) {
		this.id = id;
		this.key = key;
		this.label = label;
		this.type = type;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public ParameterType getType() {
		return type;
	}

	public void setType(ParameterType type) {
		this.type = type;
	}

}
