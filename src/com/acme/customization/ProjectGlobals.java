package com.acme.customization;
public class ProjectGlobals
{	
	private static String m_ProjectGUID = "{1862F261-EC09-A714-A74F-6760863E8868}";
	public static int USER_TYPE_USER = 0;
	public static int USER_TYPE_ARP = 1;
	public static int USER_TYPE_OTHER = 2;

	public static String getM_ProjectGUID()
	
	{
		return m_ProjectGUID;
	}
	public static void setM_ProjectGUID(String projectGUID)
	{
		m_ProjectGUID = projectGUID;

	}	
}