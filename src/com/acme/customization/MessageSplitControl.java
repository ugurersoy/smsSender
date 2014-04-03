package com.acme.customization;

import com.acme.entity.entityParameter;
import com.lbs.util.StringUtil;

public class MessageSplitControl {

	private String[] par;

	public String[] splitControl(String message) {
		par = message.split("([\'<'\'>'])");

		return par;
	}

	public boolean controlParams(String[] strlist) {
		boolean status = true;
		for (int i = 0; i < strlist.length; i++) {
			if (!StringUtil.equals(strlist[i], ".ad�.")
					&& !StringUtil.equals(strlist[i], ".Soyad�.")
					&& !StringUtil.equals(strlist[i], ".Telfon Numaras�.")
					&& !StringUtil.equals(strlist[i], ".Tarih.")
					&& !StringUtil.equals(strlist[i], ".Saat.")
					&& !StringUtil.equals(strlist[i], ".Cari Hesap Kodu.")
					&& !StringUtil.equals(strlist[i], ".Cari Hesap Unvan�.")) {
				status = false;
			} else {
				status = true;
				break;
			}
		}
		return status;
	}

	public boolean controlParamsText(String text) {
		boolean status = true;

		if (text.equals(".ad�.")) {
			status = false;
		} else if (text.equals(".Soyad�.")) {
			status = false;
		} else if (text.equals(".Telfon Numaras�.")) {
			status = false;
		} else if (text.equals(".Tarih.")) {
			status = false;
		} else if (text.equals(".Saat.")) {
			status = false;
		} else if (text.equals(".Cari Hesap Kodu.")) {
			status = false;
		} else if (text.equals(".Cari Hesap Unvan�.")) {
			status = false;
		}
		else 
		{
			status = true;
		}

		return status;
	}

}
