package com.newgen.iforms.user;

public class XmlParser {
	private String parseString;
	private String copyString;
	private int IndexOfPrevSrch;

	public XmlParser() {
	}

//	public XmlParser(String paramString) {
//		this.copyString = new String(paramString);
//		this.parseString = toUpperCase(this.copyString, 0, 0);
//	}
//
//	public void setInputXML(String paramString) {
//		if (paramString != null) {
//			this.copyString = new String(paramString);
//			this.parseString = paramString.toUpperCase(this.copyString, 0, 0);
//			this.IndexOfPrevSrch = 0;
//		} else {
//			this.parseString = null;
//			this.copyString = null;
//			this.IndexOfPrevSrch = 0;
//		}
//	}
//
//	public String getServiceName() {
//		try {
//			return new String(this.copyString.substring(
//					this.parseString.indexOf(toUpperCase("<Option>", 0, 0))
//							+ new String(toUpperCase("<Option>", 0, 0))
//									.length(),
//					this.parseString.indexOf(toUpperCase("</Option>", 0, 0))));
//		} catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
//		}
//		throw new StringIndexOutOfBoundsException();
//	}
//
//	public String getServiceName(char paramChar) {
//		try {
//			if (paramChar == 'A')
//				return new String(this.copyString.substring(
//						this.parseString.indexOf("<AdminOption>".toUpperCase())
//								+ new String("<AdminOption>".toUpperCase())
//										.length(), this.parseString
//								.indexOf("</AdminOption>".toUpperCase())));
//			return "";
//		} catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
//		}
//		return "NoServiceFound";
//	}
//
//	public boolean validateXML() {
//		try {
//			return this.parseString.indexOf("<?xml version=\"1.0\"?>"
//					.toUpperCase()) != -1;
//		} catch (StringIndexOutOfBoundsException localStringIndexOutOfBoundsException) {
//		}
//		return false;
//	}
}