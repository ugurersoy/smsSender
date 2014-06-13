package com.acme.customization.shared;
public class ProjectGlobals
{	
	private static String m_ProjectGUID = "{D345DB62-339D-0524-C50B-2672B1F3263B}"; //"{1862F261-EC09-A714-A74F-6760863E8868}";
	public static int USER_TYPE_USER = 0;
	public static int USER_TYPE_ARP = 1;
	public static int USER_TYPE_EMPLOYEE = 2;
	public static int USER_TYPE_OTHER = 3;
	public static int OPERTYPE_SMSALERT = 10;

	public static String getM_ProjectGUID()
	
	{
		return m_ProjectGUID;
	}
	public static void setM_ProjectGUID(String projectGUID)
	{
		m_ProjectGUID = projectGUID;

	}	
}