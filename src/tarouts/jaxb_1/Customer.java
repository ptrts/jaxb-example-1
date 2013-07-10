package tarouts.jaxb_1;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Customer {

	// === NAME ===
	String name;
	public String getName()
	{
		return name;
	}
	@XmlElement
	public void setName(String name)
	{
		this.name = name;
	}

	// === AGE ===
	int age;
	public int getAge()
	{
		return age;
	}
	@XmlElement
	public void setAge(int age)
	{
		this.age = age;
	}

	// === ID ===
	int id;
	public int getId()
	{
		return id;
	}
	@XmlAttribute
	public void setId(int id)
	{
		this.id = id;
	}
}
