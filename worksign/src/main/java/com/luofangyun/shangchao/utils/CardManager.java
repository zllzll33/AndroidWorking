package com.luofangyun.shangchao.utils;

import android.annotation.TargetApi;
import android.content.IntentFilter;
import android.content.res.Resources;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.NfcA;
import android.nfc.tech.NfcF;
import android.nfc.tech.NfcV;
import android.os.Build;
import android.os.Parcelable;

public final class CardManager {

	public static String[][] TECHLISTS;
	public static IntentFilter[] FILTERS;
	static {
		try {
			TECHLISTS = new String[][] { { IsoDep.class.getName() },
					{ NfcV.class.getName() }, { NfcF.class.getName() },  { NfcA.class.getName() },};

			FILTERS = new IntentFilter[] { new IntentFilter(
					NfcAdapter.ACTION_TECH_DISCOVERED, "*/*") };
		} catch (Exception e) {
		}
	}
	@TargetApi(Build.VERSION_CODES.GINGERBREAD_MR1)
	public static String load(Parcelable parcelable, Resources res) {
		final Tag tag = (Tag) parcelable;

		final IsoDep isodep = IsoDep.get(tag);
		if (isodep != null) {

		}
		final NfcA nfcA = NfcA.get(tag);
		if (nfcA != null) {
			return "M1卡";
		}
		return null;
	}

}