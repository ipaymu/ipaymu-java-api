/**
 * 
 */
package com.ipaymu.java;

/**
 * @author abdur
 *
 */
public class Config {
	public String balance, transaction, history, banklist, redirectpayment, directpayment, codarea, codrate, codpickup,
			codpayment, codawb, codtracking, codhistory;

	public Config(boolean prod) {
		String base = prod ? "https://my.ipaymu.com/api/v2" : "https://sandbox.ipaymu.com/api/v2";

		/**
		 * General API
		 **/
		this.balance = base + "/balance";
		this.transaction = base + "/transaction";
		this.history = base + "/history";
		this.banklist = base + "/banklist";

		/**
		 * Payment API
		 **/
		this.redirectpayment = base + "/payment";
		this.directpayment = base + "/payment/direct";

		/**
		 * COD Payment
		 **/
		this.codarea = base + "/cod/getarea";
		this.codrate = base + "/cod/getrate";
		this.codpickup = base + "/cod/pickup";
		this.codpayment = base + "/payment/cod";

		/**
		 * COD Tracking
		 **/
		this.codawb = base + "/cod/getawb";
		this.codtracking = base + "/cod/tracking";
		this.codhistory = base + "/cod/history";
	}
}
